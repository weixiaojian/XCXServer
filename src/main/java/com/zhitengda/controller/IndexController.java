package com.zhitengda.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhitengda.entity.*;
import com.zhitengda.service.IndexService;
import com.zhitengda.util.RetResult;
import com.zhitengda.vo.PageVo;
import com.zhitengda.vo.RetPage;
import com.zhitengda.web.exception.GlobalException;
import com.zhitengda.wx.WXConfig;
import com.zhitengda.ztdCloud.MatchAddressUtil;
import com.zhitengda.ztdCloud.cloudVo.MatchAddress.MatchAddressLisrResponse;
import com.zhitengda.ztdCloud.cloudVo.MatchAddress.MatchAddressResponse;
import com.zhitengda.ztdCloud.cloudVo.MatchAddress.MatchParsImgResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author langao_q
 * @since 2021-01-31 17:29
 */
@Slf4j
@RestController
@RequestMapping("/index")
public class IndexController extends BaseControoler{

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private IndexService indexService;

    /**
     * 获取AccessToken
     * @return
     */
    @GetMapping("/getAccessToken")
    public RetResult getAccessToken(){
        WxToken wxToken = indexService.getWxToken(wxConfig.getAppId());
        return RetResult.success(wxToken.getToken());
    }

    /**
     * 发送订阅消息（下单成功）
     * 模板id：liHLQynBfGrxej6VxnD5GrT0BbCbdoZHsWwafrKg1s8
     * @param orderBill 订单号
     * @param createDate 下单时间
     * @return
     */
    @GetMapping("/subscribeMsg")
    public RetResult subscribeMsg(@RequestParam String orderBill,
                                  @RequestParam String createDate){
        WxToken wxToken = indexService.getWxToken(wxConfig.getAppId());
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token="+wxToken.getToken();
        String templateId = wxConfig.getTemplateId();
        String openId = getToken();
        JSONObject params = new JSONObject();
        JSONObject paramValue = new JSONObject();
        JSONObject orderParam = new JSONObject();
        JSONObject timeParam = new JSONObject();

        orderParam.putOpt("value", orderBill);
        timeParam.putOpt("value", createDate);
        paramValue.putOpt("character_string1", orderParam);
        paramValue.putOpt("time2", timeParam);

        params.putOpt("touser", openId);
        params.putOpt("template_id", templateId);
        params.putOpt("page", "pages/tabbar/inquire/index");
        params.putOpt("data", paramValue);

        log.info("【小程序发送订阅消息请求】" + params);
        String result = HttpUtil.post(url, params.toString());
        JSONObject josnResult = JSONUtil.parseObj(result);
        log.info("【小程序发送订阅消息响应】" + josnResult);
        if(0 == josnResult.getInt("errcode")){
            return RetResult.success();
        }else{
            log.error("消息发送失败: " + result);
            return RetResult.success();
        }
    }

    /**
     * 地址解析接口
     * @param address
     * @return
     */
    @GetMapping("/addressAnalysis")
    public RetResult addressAnalysis(@RequestParam String address){
        MatchAddressResponse ydResponse = null;
        MatchAddressResponse ztdResponse = null;
        try {
            //韵达的地址解析接口
            ydResponse = MatchAddressUtil.analysis(address);
            if (!"1".equals(ydResponse.getCode())) {
                log.error("韵达云地址解析失败:" + ydResponse.getMsg());
                throw new GlobalException("云地址解析失败:" + ydResponse.getMsg());
            }else if(ydResponse.getData() == null){
                log.error("韵达云地址解析Data为空:" + JSONUtil.toJsonStr(ydResponse));
                throw new GlobalException("云地址解析Data为空:" + ydResponse.getMsg());
            }else if(ydResponse.getData().getProvince() == null){
                log.error("韵达云地址解析失败:未解析出省-市-区！");
                throw new GlobalException("未解析出省-市-区！");
            }
            return RetResult.success(ydResponse.getData());
        }catch (Exception e){
            //智腾达的地址解析
            ztdResponse = MatchAddressUtil.ztdAnalysis(address);
            if (ztdResponse == null || !"1".equals(ztdResponse.getCode())) {
                log.error("ZTD云地址解析失败:" + ztdResponse.getMsg());
                throw new GlobalException(e.getMessage());
            }
            //当我们自己的解析没有姓名 韵达的有姓名，这时候取韵达的姓名
            if(ydResponse != null && ydResponse.getData() != null && StrUtil.isNotBlank(ydResponse.getData().getName())
                    && StrUtil.isEmpty(ztdResponse.getData().getName())){
                ztdResponse.getData().setName(ydResponse.getData().getName());
            }
            return RetResult.success(ztdResponse.getData());
        }
    }

    /**
     * 批量地址解析接口，地址以"；"分割（中文分号）
     * @param address
     * @return
     */
    @GetMapping("/addressAnalysisList")
    public RetResult addressAnalysisList(@RequestParam String address){
        MatchAddressLisrResponse ydResponse = null;
        MatchAddressLisrResponse ztdResponse = null;
        List<String> addressList = Arrays.asList(address.split("；"));
        try {
            ydResponse = MatchAddressUtil.analysisList(addressList);
            if (!"1".equals(ydResponse.getCode())) {
                log.error("韵达云地址解析失败:" + ydResponse.getMsg());
                throw new GlobalException("云地址解析失败:" + ydResponse.getMsg());
            }else if(ydResponse.getData().isEmpty()){
                log.error("韵达云地址解析Data为空:" + JSONUtil.toJsonStr(ydResponse));
                throw new GlobalException("云地址解析Data为空:" + ydResponse.getMsg());
            }else if(ydResponse.getData().get(0).getProvince() == null){
                log.error("韵达云地址解析失败:未解析出省-市-区！");
                throw new GlobalException("未解析出省-市-区！");
            }
            return RetResult.success(ydResponse.getData());
        }catch (Exception e){
            ztdResponse = MatchAddressUtil.ztdAnalysisList(addressList);
            if (ztdResponse == null || !"1".equals(ztdResponse.getCode())) {
                log.error("ZTD云地址解析失败:" + ztdResponse.getMsg());
                throw new GlobalException(e.getMessage());
            }
            return RetResult.success(ztdResponse.getData());
        }
    }

    /**
     * 图片识别文字
     * @param img base64的图片
     * @return
     */
    @PostMapping("/imgAnalysis")
    public RetResult imgAnalysis(@RequestBody String img){
        MatchParsImgResponse response = MatchAddressUtil.analysisImg(img);
        if (!"1".equals(response.getCode())) {
            throw new GlobalException("云图片识别文字失败:" + response.getMsg());
        }else if(response.getData() == null){
            throw new GlobalException("云图片识别文字Data为空:" + response.getMsg());
        }
        return RetResult.success(response.getData());
    }

    /**
     * 获取地址分页数据（寄/收）
     * @param pageVo
     * @return RetResult
     */
    @GetMapping("/getAddress")
    public RetResult getAddress(PageVo pageVo){
        pageVo.setOpenId(getToken());
        RetPage<WxAddress> page = indexService.getAddressPage(pageVo);
        return RetResult.success(page);
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/qryAddressByDefault")
    public RetResult qryAddressByDefault(){
        String openId = getToken();
        WxAddress address = indexService.findAddressByDefault(openId);
        return RetResult.success(address);
    }

    /**
     * 保存地址簿数据
     * @param address
     * @return
     */
    @PostMapping("/addAddress")
    public RetResult addAddress(@RequestBody @Validated WxAddress address){
        address.setOpenId(getToken());
        boolean boo = indexService.addAddress(address);
        if(boo){
            return RetResult.success(address.getGuid());
        }else{
            return RetResult.warn("添加地址失败！");
        }
    }

    /**
     * 更新地址簿数据
     * @param address
     * @return
     */
    @PostMapping("/editAddress")
    public RetResult editAddress(@RequestBody @Validated WxAddress address){
        if(StrUtil.isEmpty(address.getGuid())){
            return RetResult.warn("id不能为空！");
        }
        address.setOpenId(getToken());
        indexService.updateAddress(address);
        return RetResult.success();
    }

    /**
     * 修改用户默认地址
     * @param guid
     * @return
     */
    @GetMapping("/editDefault")
    public RetResult editDefault(@RequestParam String guid){
        if(StrUtil.isEmpty(guid)){
            return RetResult.warn("id不能为空！");
        }
        WxAddress address = new WxAddress();
        address.setGuid(guid);
        address.setOpenId(getToken());
        indexService.updateDefault(address);
        return RetResult.success();
    }

    /**
     * 删除地址簿数据
     * @param guid
     * @return
     */
    @GetMapping("/delAddress")
    public RetResult delAddress(@RequestParam String guid){
        if (StrUtil.isEmpty(guid)) {
            return RetResult.warn("id不能为空！");
        }
        indexService.deleteAddress(guid);
        return RetResult.success();
    }

    /**
     * 校验省-市-区是否合法，并返回对应的code
     * @param province
     * @param city
     * @param county
     * @param town
     * @return
     */
    @GetMapping("/checkProvince")
    public RetResult checkProvince(@RequestParam String province,
                                   @RequestParam String city,
                                   @RequestParam String county,
                                   String town) {
        Town t = indexService.checkProvince(province, city, county, town);
        if(t == null){
            return RetResult.warn("省-市-区数据不合法：["+province+"]["+city+"]["+county+"]");
        }
        return RetResult.success(t);
    }

    /**
     * 获取省-市-区集合数据（树形结构）
    * @return
     */
    @GetMapping("/getProvinceList")
    public RetResult getProvinceList() {
        List<Province> list = indexService.getProvinceList();
        return RetResult.success(list);
    }

    /**
     * 根据区code获取街道
     * @param countyCode
     * @return
     */
    @GetMapping("/getTown")
    public RetResult getTown(@RequestParam String countyCode) {
        List<Town> list = indexService.getTown(countyCode);
        return RetResult.success(list);
    }


    /**
     * 根据编码查询网点
     * @param siteCode
     * @return
     */
    @GetMapping("/getSite")
    public RetResult getSite(@RequestParam String siteCode) {
        Site site = indexService.getSite(siteCode);
        return RetResult.success(site);
    }


    /**
     * 根据编码查询员工
     * @param empCode 员工编码
     * @return 员工
     */
    @GetMapping("/getEmployee")
    public RetResult getEmployee(@RequestParam String empCode) {
        Employee emp = indexService.getEmployee(empCode);
        return RetResult.success(emp);
    }

    /**
     * 区域查询
     * @param province 省
     * @param city 市
     * @param county 区
     * @param name 网点名称
     * @return 网点集合
     */
    @GetMapping("/getSiteByAddress")
    public RetResult getSiteByAddress(String province,
                                      String city,
                                      String county,
                                      String name){
        Site site = new Site();
        site.setProvince(province);
        site.setCity(city);
        site.setCountry(county);
        site.setSiteName(name);
        List<Site> res = indexService.getSiteByAddress(site);
        return RetResult.success(res);
    }

    /*--------------------------------------------团队相关-------------------------------------------------------------*/

    /**
     * 查询当前待审核的总数（只有超级管理员才有这个权限）
     * @return
     */
    @GetMapping("/getAuditCount")
    public RetResult getAuditCount(@RequestParam String groupId){
        Integer auditCount = indexService.getAuditCount(groupId, getToken());
        return RetResult.success(auditCount);
    }

    /**
     * 查询当前用户是否有团队信息
     * @return
     */
    @GetMapping("/getWxGroupByOpenId")
    public RetResult getWxGroupByOpenId(){
        //1.查询团队数据
        WxGroup wxGroup = indexService.getWxGroupByOpenId(getToken());
        return RetResult.success(wxGroup);
    }

    /**
     * 根据团队ID或名称查询团队信息（查询团队ID、团队名称、是否共享电子面单）
     * @param groupId
     * @return
     */
    @GetMapping("/getWxGroupByIdOrName")
    public RetResult getWxGroupByIdOrName(String groupId){
        //1.查询团队数据
        WxGroup wxGroup = indexService.getWxGroupByIdOrName(groupId);
        return RetResult.success(wxGroup);
    }

    /**
     * 根据昵称查询团队成员信息
     * @param groupId
     * @param nickName
     * @return
     */
    @GetMapping("/getWxGroupByName")
    public RetResult getWxGroupByName(@RequestParam String groupId,
                                      @RequestParam String nickName){
        //1.查询团队成员数据
        WxGroup wxGroup = indexService.getWxGroupByName(groupId, nickName);
        return RetResult.success(wxGroup);
    }

    /**
     * 根据团队ID获取团队成员列表
     * @param groupId
     * @return
     */
    @GetMapping("/getWxGroupList")
    public RetResult getWxGroupList(@RequestParam String groupId, String nickName){
        List<WxGroup> resultList = new ArrayList<>();
        //1.封装查询条件(查询所有成员)
        WxGroup queryWxGroup = new WxGroup();
        queryWxGroup.setGroupId(groupId);
        queryWxGroup.setNickName(nickName);
        //2.查询团队数据
        List<WxGroup> list = indexService.getWxGroup(queryWxGroup);
        if(list == null){
            throw new GlobalException("未查询到团队成员信息！");
        }
        //2.1赋值网点数据
        for(WxGroup wxGroup : list){
            Site siteDb = indexService.getSite(wxGroup.getSiteCode());
            if(siteDb != null){
                wxGroup.setSiteName(siteDb.getSiteName());
            }
        }
        //3.权限区分：团长和管理员展示所有、团员只能看到自己和团长（包括待审核）
        WxGroup wxGroup = indexService.getWxGroupByOpenId(getToken());
        if(wxGroup == null){
            throw new GlobalException("当前用户未加入任何团队！");
        }
        //3.1团长和管理员展示所有
        if(wxGroup.getStatus() == 1 || wxGroup.getStatus() == 2){
            resultList = list;
        }
        //3.2团员只能看到自己和团长（包括待审核）
        if(wxGroup.getStatus() == 0 || wxGroup.getStatus() == 3){
            //取出团长数据
            for(WxGroup group : list){
                if (group.getStatus() == 1) {
                    resultList.add(group);
                }
            }
            //添加自己数据
            resultList.add(wxGroup);
        }
        return RetResult.success(resultList);
    }

    /**
     * 创建团队
      * @param wxGroup
     * @return
     */
    @PostMapping("/createWxGroup")
    public RetResult createWxGroup(@RequestBody @Validated WxGroup wxGroup){
        boolean bool = indexService.createWxGroup(wxGroup);
        if(bool){
            return RetResult.success(wxGroup.getGroupId());
        }else{
            return RetResult.warn("创建团队失败！");
        }
    }

    /**
     * 加入团队
     * @param wxGroup
     * @return
     */
    @PostMapping("/addWxGroup")
    public RetResult addWxGroup(@RequestBody @Validated WxGroup wxGroup) {
        boolean bool = indexService.addWxGroup(wxGroup);
        if(bool){
            return RetResult.success(wxGroup.getGroupId());
        }else{
            return RetResult.warn("加入团队失败！");
        }
    }

    /**
     * 审核团队成员
     * @param groupId
     * @param openId
     * @return
     */
    @GetMapping("/auditWxGroup")
    public RetResult auditWxGroup(@RequestParam String groupId, @RequestParam String openId){
        //1.获取当前请求的token，校验当前token是超级管理员
        indexService.checkAuthOpenId(groupId, getToken());
        //2.通过审核：创建客户、更新团队成员数据
        indexService.auditWxGroup(openId);
        return RetResult.success();
    }

    /**
     * 拒绝团队成员
     * @param groupId
     * @param openId
     * @return
     */
    @GetMapping("/turnDownWxGroup")
    public RetResult turnDownWxGroup(@RequestParam String groupId, @RequestParam String openId){
        indexService.turnDownWxGroup(groupId,  getToken(), openId);
        return RetResult.success();
    }

    /**
     * 修改自己或团队成员的昵称
     * @param openId
     * @param nickName
     * @param groupId
     * @return
     */
    @GetMapping("/updateNickName")
    public RetResult updateNickName(@RequestParam String openId,
                                    @RequestParam String nickName,
                                    @RequestParam String groupId){
        //1.如果不是修改自己的昵称，需要校验当前token是超级管理员
        if(!getToken().equals(openId)){
            indexService.checkAuthOpenId(groupId, getToken());
        }
        //2.更新用户昵称
        WxGroup dbWxGroup = new WxGroup();
        dbWxGroup.setOpenId(openId);
        dbWxGroup.setNickName(nickName);
        dbWxGroup.setUpdateDate(new Date());
        indexService.updateWxGroupById(dbWxGroup);
        return RetResult.success();
    }

    /**
     * 修改团队成员的权限状态
     * @param openId
     * @param status
     * @param groupId
     * @return
     */
    @GetMapping("/updateSatus")
    public RetResult updateSatus(@RequestParam String openId,
                                 @RequestParam Integer status,
                                 @RequestParam String groupId){
        //1.需要校验当前token是超级管理员
        indexService.checkAuthOpenId(groupId, getToken());
        //2.校验权限状态值
        if(status == 1){
            throw new GlobalException("不能赋于超级管理员的权限！");
        }
        //3.更新用户权限
        WxGroup dbWxGroup = new WxGroup();
        dbWxGroup.setOpenId(openId);
        dbWxGroup.setStatus(status);
        dbWxGroup.setUpdateDate(new Date());
        indexService.updateWxGroupById(dbWxGroup);
        return RetResult.success();
    }

    /**
     * 解散团队
     * @param groupId
     * @return
     */
    @GetMapping("/emptyWxGroup")
    public RetResult emptyWxGroup(@RequestParam String groupId){
        indexService.emptyWxGroup(getToken(), groupId);
        return RetResult.success();
    }

    /**
     * 移除团队成员/退出团队
     * @param groupId
     * @param openId
     * @return
     */
    @GetMapping("/deleteWxGroup")
    public RetResult deleteWxGroup(@RequestParam String groupId,
                                   @RequestParam String openId){
        if(!getToken().equals(openId)){
            //1.如果不是当前登陆用户操作需要校验当前token是超级管理员，如果是则无需校验（自己退出）
            indexService.checkAuthOpenId(groupId, getToken());
        }else{
            //2.如果当前用户是超级管理员则不能操作退出团队
            WxGroup queryWxGroup = new WxGroup();
            queryWxGroup.setOpenId(openId);
            queryWxGroup.setStatus(1);
            List<WxGroup> list = indexService.getWxGroup(queryWxGroup);
            if(list.size() > 0){
                throw new GlobalException("超级管理员无法退出自己的团队！");
            }
        }
        //2.移除团队成员
        indexService.deleteWxGroupById(openId);
        return RetResult.success();
    }

    /**
     * 修改团队名称（超级管理员权限）
     * @param groupId
     * @param groupName
     * @return
     */
    @GetMapping("/editGroupName")
    public RetResult editGroupName(@RequestParam String groupId,
                                   @RequestParam String groupName){
        WxGroup wxGroup = new WxGroup();
        wxGroup.setOpenId(getToken());
        wxGroup.setGroupId(groupId);
        wxGroup.setGroupName(groupName);
        indexService.editGroupName(wxGroup);
        return RetResult.success();
    }

    /**
     * 更改共享电子面单开关（超级管理员权限）
     * @param groupId
     * @param blShare 0关闭 1打开
     * @return
     */
    @GetMapping("/editGroupBlShare")
    public RetResult editGroupBlShare(@RequestParam String groupId,
                                   @RequestParam Integer blShare){
        WxGroup wxGroup = new WxGroup();
        wxGroup.setOpenId(getToken());
        wxGroup.setGroupId(groupId);
        wxGroup.setBlShare(blShare);
        indexService.editGroupBlShare(wxGroup);
        return RetResult.success();
    }

    /**
     * 更改是否需要审批开关（管理员权限）
     * @param groupId
     * @param blAudit 1需要审批 0不需要审批
     * @return
     */
    @GetMapping("/editGroupBlAudit")
    public RetResult editGroupBlAudit(@RequestParam String groupId,
                                      @RequestParam Integer blAudit){
        WxGroup wxGroup = new WxGroup();
        wxGroup.setOpenId(getToken());
        wxGroup.setGroupId(groupId);
        wxGroup.setBlAudit(blAudit);
        indexService.editGroupBlAudit(wxGroup);
        return RetResult.success();
    }

    /**
     * 生成二维码(https://cityexp-mp.yundasys.com/YDTCPXCXServer/scanGroup/)
     * @param groupId
     * @return
     */
    @GetMapping("/generateGroupQrCode")
    public RetResult generateGroupQrCode(@RequestParam String groupId) throws IOException {
        final String URL = "https://cityexp-mp.yundasys.com/YDTCPXCXServer/scanGroup/";
        BufferedImage image = QrCodeUtil.generate(URL + groupId, 300, 300);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        String base64 = Base64.encode(stream.toByteArray());
        stream.flush();
        stream.close();
        return RetResult.success(base64);
    }

}
