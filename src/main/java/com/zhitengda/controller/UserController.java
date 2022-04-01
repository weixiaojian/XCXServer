package com.zhitengda.controller;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhitengda.entity.*;
import com.zhitengda.service.IndexService;
import com.zhitengda.service.UserService;
import com.zhitengda.util.CharacterUtils;
import com.zhitengda.util.RetResult;
import com.zhitengda.vo.PageVo;
import com.zhitengda.vo.RetPage;
import com.zhitengda.web.exception.GlobalException;
import com.zhitengda.wx.WXConfig;
import com.zhitengda.wx.WxApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author langao_q
 * @since 2021-01-31 17:29
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseControoler {


    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private IndexService indexService;

    /**
     * 根据wx.login()的code 获取openid、session_key、unionid
     * @param code
     * @return openid、unionid、session_key
     */
    @GetMapping("/getOpenIdByCode")
    public RetResult getOpenIdByCode(@RequestParam("code") String code){
        //发起请求：根据code获取openId
        try {
            StringBuilder urlPath = new StringBuilder(wxConfig.getUrlPath());
            urlPath.append(String.format("?appid=%s", wxConfig.getAppId()))
                    .append(String.format("&secret=%s", wxConfig.getAppSecret()))
                    .append(String.format("&js_code=%s", code))
                    .append(String.format("&grant_type=%s", "authorization_code"));
            String data = HttpUtil.get(urlPath.toString());
            JSONObject result = JSONUtil.parseObj(data);
            if (StrUtil.isEmpty(result.getStr("openid"))) {
                String errCode = result.getStr("errcode");
                if ("-1".equals(errCode)) {
                    throw new GlobalException("系统繁忙,请稍后再试");
                }
                if ("40029".equals(errCode)) {
                    throw new GlobalException("code 无效");
                }
                if ("45011".equals(errCode)) {
                    throw new GlobalException("频率限制，每个用户每分钟100次");
                }
                if ("40163".equals(errCode)) {
                    throw new GlobalException("code 已经使用");
                }
                return RetResult.warn("获取openId失败！");
            }else{
                return RetResult.success(result);
            }
        }catch (Exception e){
            log.error("获取微信用户标识失败：", e);
            return RetResult.warn("获取微信用户标识失败：" + e.getMessage());
        }
    }

    /**
     * 解密用户手机号(同时保存用户数据)
     * @param openId
     * @param unionId
     * @param sessionKey
     * @param encryptedData
     * @param iv
     * @return
     */
    @GetMapping("/decryptPhone")
    public RetResult decryptPhone(@RequestParam("openId") String openId,
                                  @RequestParam("unionId") String unionId,
                                  @RequestParam("sessionKey") String sessionKey,
                                  @RequestParam("encryptedData") String encryptedData,
                                  @RequestParam("iv") String iv){
        try {
            //1.解密用户手机号
            String decryptResult = WxApiUtil.decrypt(wxConfig.getAppId(), encryptedData, sessionKey, iv);
            JSONObject decrypt = JSONUtil.parseObj(decryptResult);
            String phone = decrypt.getStr("phoneNumber");

            //2.保存/更新用户数微信api获取的unionid可能为空 就要从加密的数据中取
            unionId = unionId == null ? decrypt.getStr("unionid") : unionId;
            //新增或修改用户信息
            WxUser user = new WxUser(openId, unionId, phone, sessionKey, 1);
            userService.saveOrUpdate(user);
            //3.返回用户数据
            WxUser dbUser = userService.findByOpneId(getToken());
            return RetResult.success(dbUser);
        }catch (Exception e){
            log.error("解密用户手机号失败：", e);
            return RetResult.warn("解密用户手机号失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户的头像 昵称
     * @param openId
     * @param headImgUrl
     * @param nickName
     * @return
     */
    @GetMapping("/updateUser")
    public RetResult updateUser(@RequestParam("openId") String openId,
                                  @RequestParam("headImgUrl") String headImgUrl,
                                  @RequestParam("nickName") String nickName){
        try {
            //1.去掉昵称中的特殊字符
            String nickNameNew = CharacterUtils.replaceStr(nickName);
            //2.更新用户信息（头像和昵称）
            WxUser user = new WxUser();
            user.setOpenId(openId);
            user.setHeadimgurl(headImgUrl);
            user.setNickName(nickName);
            userService.saveOrUpdate(user);
            //3.返回用户数据
            WxUser dbUser = userService.findByOpneId(getToken());
            return RetResult.success(dbUser);
        }catch (Exception e){
            log.error("更新用户的头像 昵称败：", e);
            return RetResult.warn("更新用户的头像 昵称失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/getUser")
    public RetResult getUser() {
        WxUser user = userService.findByOpneId(getToken());
        return RetResult.success(user);
    }

    /**
     * 根据手机号获取用户信息
     * @return
     */
    @GetMapping("/getUserByPhone")
    public RetResult getUserByPhone(@RequestParam String phone) {
        WxUser user = userService.findByPhone(phone);
        return RetResult.success(user);
    }

    /**
     * 解密小程序的用户信息  此处的unionid一定不为空
     *
     * @param encryptedData 加密的数据
     * @param iv            密钥
     * @return 返回手机号
     */
    @GetMapping("/getUserInfo")
    public RetResult getUserInfo(@RequestParam("encryptedData") String encryptedData,
                                 @RequestParam("iv") String iv) {
        String openId = getToken();
        String appId = wxConfig.getAppId();
        WxUser user = userService.findByOpneId(openId);
        //获取openid和session_key
        String sessionKey = user.getSessionKey();
        //开始解密
        String decryptResult = WxApiUtil.decrypt(appId, encryptedData, sessionKey, iv);
        //返回json对象
        JSONObject decrypt = JSONUtil.parseObj(decryptResult);
        return RetResult.success(decrypt);
    }

    /**
     * 退出登陆
     * @return 用户数据
     */
    @GetMapping("/logOut")
    public RetResult editLogin() {
        String openId = getToken();
        WxUser dbUser = new WxUser();
        //更新用户信息
        dbUser.setOpenId(openId);
        dbUser.setIsLogin(0);
        userService.saveOrUpdate(dbUser);
        return RetResult.success();
    }

    /**
     * 实名认证分页数据
     * @param pageVo
     * @return
     */
    @GetMapping("/realIdPage")
    public RetResult realIdPage(PageVo pageVo) {
        pageVo.setOpenId(getToken());

        RetPage<WxReal> page = userService.findRealIdPage(pageVo);
        return RetResult.success(page);
    }

    /**
     * 获取实名认证
     * @param phone
     * @return
     */
    @GetMapping("/getRealId")
    public RetResult getRealId(String phone) {
        WxReal wxReal = new WxReal();
        wxReal.setOpenId(getToken());

        WxReal realId = userService.findRealId(wxReal);
        return RetResult.success(realId);
    }

    /**
     * 添加/修改 实名认证
     *
     * @param realName
     * @return
     */
    @PostMapping("/addRealId")
    public RetResult addRealId(@Valid @RequestBody WxReal realName) {
        //1.校验身份证是否合法
        if(!IdcardUtil.isValidCard(realName.getRealIdCode())){
            throw new GlobalException("身份证号码不合法！");
        }
        //2.保存或更新微信实名认证
        realName.setOpenId(getToken());
        userService.saveOrUpdateRealId(realName);
        return RetResult.success();
    }

    /**
     * 删除实名认证
     *
     * @param guid
     * @return
     */
    @GetMapping("/delRealId")
    public RetResult delRealId(@RequestParam String guid) {
        if (StrUtil.isEmpty(guid)) {
            return RetResult.warn("id不能为空！");
        }
        userService.deleteRealId(guid);
        return RetResult.success();
    }

    /**
     * 模糊查询员工数据
     * @param emp
     * @return
     */
    @PostMapping("/getEmpBylike")
    public RetResult binDingEmp(@RequestBody Employee emp){
        List<Employee> list = indexService.getEmpBylike(emp);
        return RetResult.success(list);
    }

    /**
     * 绑定专属快递员
     * @param user
     * @return
     */
    @PostMapping("/binDingEmp")
    public RetResult binDingEmp(@RequestBody WxUser user){
        if (StrUtil.isEmpty(user.getSiteCode()) && StrUtil.isEmpty(user.getSiteName())) {
            return RetResult.warn("网点不能为空！");
        }
        if (StrUtil.isEmpty(user.getEmployeeCode()) && StrUtil.isEmpty(user.getEmployeeName())) {
            return RetResult.warn("员工不能为空！");
        }
        user.setOpenId(getToken());
        userService.saveOrUpdate(user);
        return RetResult.success();
    }

    /**
     * 扫码绑定专属快递员
     * @param empCode
     * @return
     */
    @GetMapping("/scanBinDingEmp")
    public RetResult scanBinDingEmp(@RequestParam  String empCode){
        String openId = getToken();
        //根据员工Code查询数据  绑定到user表
        Employee employee = userService.scanBinDingEmp(openId, empCode);
        //绑定成功后返回员工
        return RetResult.success(employee);
    }

    /**
     * 解绑专属快递员
     * @return
     */
    @GetMapping("/unbindEmp")
    public RetResult unbindEmp(){
        WxUser user = new WxUser();
        user.setOpenId(getToken());
        user.setSiteCode("");
        user.setSiteName("");
        user.setEmployeeCode("");
        user.setEmployeeName("");
        userService.saveOrUpdate(user);

        return RetResult.success();
    }

    /**
     * 获取用户消息设置信息
     * @return
     */
    @GetMapping("/getMessagesSet")
    public RetResult getMessagesSet(){
        WxMessagesSet set = userService.getMessagesSet(getToken());
        return RetResult.success(set);
    }

    /**
     * 保存/更新消息设置信息
     * @param set
     * @return
     */
    @PostMapping("/saveMsgSet")
    public RetResult saveMsgSet(@RequestBody WxMessagesSet set){
        boolean bool = userService.saveMsgSet(set);
        if(bool){
            return RetResult.success(set);
        }else{
            return RetResult.warn("保存失败！");
        }
    }

    /**
     * 用户绑定月结客户账号
     * @param cust
     * @return
     */
    @PostMapping("/bindCustomer")
    public RetResult bindCustomer(@RequestBody Customer cust){
        if(StrUtil.isEmpty(cust.getCustomerCode()) || StrUtil.isEmpty(cust.getBalancePassword())) {
            throw new GlobalException("账号、密码不能为空！");
        }
        WxUser user = userService.bindCustomer(cust.getCustomerCode(), cust.getBalancePassword(), getToken());
        return RetResult.success(user);
    }

    /**
     * 解除月结客户账号绑定
     * @param customerCode
     * @return
     */
    @GetMapping("/unBindCustomer")
    public RetResult unBindCustomer(@RequestParam String customerCode){
        WxUser dbUser = userService.findByOpneId(getToken());
        if(StrUtil.isEmpty(dbUser.getCustomerCode())){
            throw new GlobalException("当前用户未绑定月结客户！");
        }
        if(!customerCode.equals(dbUser.getCustomerCode())){
            throw new GlobalException("客户账号["+customerCode+"]与用户绑定的不一致！");
        }
        dbUser.setCustomerName("");
        dbUser.setCustomerCode("");
        userService.saveOrUpdate(dbUser);
        return RetResult.success(dbUser);
    }

    /**
     * 添加投诉建议
     * @param wxAdvice
     * @return
     */
    @PostMapping("/addWxAdvice")
    public RetResult addWxAdvice(@Valid @RequestBody WxAdvice wxAdvice){
        userService.addWxAdvice(wxAdvice);
        return RetResult.success();
    }

}
