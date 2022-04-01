package com.zhitengda.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhitengda.entity.*;
import com.zhitengda.mapper.*;
import com.zhitengda.util.ZtdAESUtils;
import com.zhitengda.vo.PageVo;
import com.zhitengda.vo.RetPage;
import com.zhitengda.web.exception.GlobalException;
import com.zhitengda.wx.WXConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户Service
 * @author langao_q
 * @since 2021-02-02 10:41
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private WxUserMapper userMapper;

    @Autowired
    private WxRealMapper realMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private WxMessagesSetMapper wxMessagesSetMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private WXConfig wxConfig;


    /**
     * 校验用户是否登陆
     * @param token
     * @return
     */
    public Integer checkUserByOpneId(String token) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("OPEN_ID", token);
        wrapper.eq("IS_LOGIN", 1);
        return userMapper.selectCount(wrapper);
    }

    /**
     * 根据openid查询用户数据
     * @param openId
     * @return
     */
    public WxUser findByOpneId(String openId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("OPEN_ID", openId);
        wrapper.eq("IS_LOGIN", 1);
        //只取一条记录
        wrapper.eq("ROWNUM", 1);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 插入/更新
     * @param user
     * @return
     */
    public boolean saveOrUpdate(WxUser user) {
        if(user == null){
            return false;
        }
        //先查询用户是否存在 不存在就保存 存在就更新
        WxUser dbUser = userMapper.selectById(user.getOpenId());

        if(dbUser == null){
            userMapper.insert(user);
        }else{
            user.setUpdateDate(new Date());
            userMapper.updateById(user);
        }
        return true;
    }

    /**
     * 保存或更新实名认证
     * @param realName
     * @return
     */
    public boolean saveOrUpdateRealId(WxReal realName) {
        //1.走韵达大网的接口 校验实名认证信息是否正确
        //1.1校验用户是否已经在通过大网验证的实名表中TAB_REAL_NAME
        BigNetRealName param = new BigNetRealName();
        param.setCustomerName(realName.getRealName());
        param.setSid(realName.getRealIdCode());
        param.setBlAudit(1);
        BigNetRealName bigNetRealName = commonMapper.getCheckRealName(param);
        //1.2不存在实名表 走大网验证接口
        if(bigNetRealName == null){
            //1.3调用韵达大网的验证接口
            Map<String, Object> requestParam = new HashMap<>(4);
            requestParam.put("name",realName.getRealName());
            requestParam.put("identityNo",realName.getRealIdCode());
            log.info("大网实名校验参数：{}", JSONUtil.toJsonStr(requestParam));
            String result = HttpUtil.post(wxConfig.getRealNamePath(), JSONUtil.toJsonStr(requestParam));
            log.info("大网实名校验结果：{}", result);
            if(StrUtil.isBlank(result)){
                throw new GlobalException("大网实名校验服务失败，请稍后重试！");
            }
            //1.4解析大网实名校验结果
            JSONObject realObj = JSONUtil.parseObj(result);
            if(realObj.getBool("data")){
                //1.5验证通过回写大网验证的实名表
                param.setBlAudit(0);
                bigNetRealName = commonMapper.getCheckRealName(param);
                //1.6添加或更新大网验证的实名表
                if(bigNetRealName != null){
                    bigNetRealName.setBlAudit(1);
                    bigNetRealName.setAuditDate(new Date());
                    bigNetRealName.setModifyMan("微信");
                    bigNetRealName.setModifyDate(new Date());
                    commonMapper.uptCheckRealName(bigNetRealName);
                }else{
                    BigNetRealName bigNetRealNameDb = new BigNetRealName();
                    bigNetRealNameDb.setType("散客");
                    bigNetRealNameDb.setCardType("身份证");
                    bigNetRealNameDb.setSid(realName.getRealIdCode());
                    bigNetRealNameDb.setSex(realName.getSex()==1?"男":"女");
                    bigNetRealNameDb.setNationality(realName.getNational());
                    bigNetRealNameDb.setCardAddress(realName.getRealIdAddress());
                    bigNetRealNameDb.setCustomerName(realName.getRealName());
                    bigNetRealNameDb.setCustomerPhone(realName.getPhone());
                    bigNetRealNameDb.setBlAudit(1);
                    bigNetRealNameDb.setAuditDate(new Date());
                    bigNetRealNameDb.setCreateMan("微信");
                    bigNetRealNameDb.setVSource("微信");
                    bigNetRealNameDb.setOpenId(realName.getOpenId());
                    commonMapper.addCheckRealName(bigNetRealNameDb);
                }
            }else{
                throw new GlobalException("大网实名校验服务失败["+realObj.getStr("respCode")+"]：实名信息不正确！");
            }
        }
        //2.查询是否已经实名认证
        WxReal realDb = findRealId(realName);
        //3.保存或更新实名认证
        if(null == realDb){
            return realMapper.insert(realName)>0;
        }else{
            UpdateWrapper wrapper = new UpdateWrapper();
            wrapper.eq("OPEN_ID", realName.getOpenId());
            realName.setUpdateDate(new Date());
            return realMapper.update(realName, wrapper)>0;
        }
    }

    /**
     * 删除实名认证数据
     * @param guid
     * @return
     */
    public boolean deleteRealId(String guid) {
        return realMapper.deleteById(guid)>0;
    }

    /**
     * 获取实名认证数据
     * @param realName
     * @return
     */
    public WxReal findRealId(WxReal realName) {
        QueryWrapper wrapper = new QueryWrapper();
        if(StrUtil.isNotBlank(realName.getOpenId())){
            wrapper.eq("OPEN_ID", realName.getOpenId());
        }
        if(StrUtil.isNotBlank(realName.getPhone())){
            wrapper.eq("PHONE", realName.getPhone());
        }
        //只取一条记录
        wrapper.eq("ROWNUM", 1);

        return realMapper.selectOne(wrapper);
    }

    /**
     * 获取实名分页数据
     * @param vo
     * @return
     */
    public RetPage<WxReal> findRealIdPage(PageVo vo) {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq("OPEN_ID", vo.getOpenId());
        //姓名模糊匹配
        if(StrUtil.isNotBlank(vo.getName())){
            wrapper.like("REAL_NAME", vo.getName());
        }
        //手机号模糊匹配
        if(StrUtil.isNotBlank(vo.getPhone())){
            wrapper.like("PHONE", vo.getPhone());
        }
        wrapper.orderByDesc("CREATE_DATE");
        //根据指定列排序
        if(StrUtil.isNotBlank(vo.getOrderByColumn())){
            wrapper.orderByDesc(vo.getOrderByColumn());
        }
        IPage<WxReal> page = new Page<>(vo.getPageNum(), vo.getPageSize());
        page = realMapper.selectPage(page, wrapper);
        //封装返回结果RetPage
        RetPage retPage = new RetPage(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), page.getRecords());
        return retPage;
    }

    /**
     * 扫码绑定专属业务员
     * @param openId
     * @param empCode
     * @return
     */
    public Employee scanBinDingEmp(String openId, String empCode) {
        //查询员工信息
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq("EMPLOYEE_CODE", empCode);
        wrapper.eq("BL_OPEN", 1);
        Employee emp = employeeMapper.selectOne(wrapper);
        if(emp == null){
            throw new GlobalException("员工不存在或未启用！");
        }

        //更新用户信息
        WxUser user = new WxUser();
        user.setOpenId(openId);
        user.setSiteCode(emp.getOwnerSiteCode());
        user.setSiteName(emp.getOwnerSite());
        user.setEmployeeCode(emp.getEmployeeCode());
        user.setEmployeeName(emp.getEmployeeName());
        user.setUpdateDate(new Date());
        userMapper.updateById(user);

        //返回员工数据
        return emp;
    }

    /**
     * 获取用户消息设置
     * @param openId
     * @return
     */
    public WxMessagesSet getMessagesSet(String openId) {
        return wxMessagesSetMapper.selectById(openId);
    }

    /**
     * 保存/更新消息设置信息
     * @param set
     * @return
     */
    public boolean saveMsgSet(WxMessagesSet set) {
        int mark = 0;
        //1.先判断设置是否存在，不存在就插入 存在就更新
        if(null == wxMessagesSetMapper.selectById(set.getOpenId())){
            mark = wxMessagesSetMapper.insert(set);
        }else{
            set.setUpdateDate(new Date());
            mark = wxMessagesSetMapper.updateById(set);
        }
        return mark > 0;
    }

    /**
     * 根据手机号获取用户信息
     * @param phone
     * @return
     */
    public WxUser findByPhone(String phone) {
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("PHONE", phone);
        wrapper.eq("ROWNUM", 1);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 用户绑定月结客户账号
     * @param customerCode
     * @param loginPw
     * @return
     */
    public WxUser bindCustomer(String customerCode, String loginPw, String openId) {
        //1.根据账号查询数据
        Map<String, String> sysUser = commonMapper.getUserMonthly(customerCode);
        if(sysUser == null || sysUser.isEmpty() || sysUser.size() < 3){
            throw new GlobalException("未查询到客户数据！");
        }
        //2.比对密码
        if(!ZtdAESUtils.encodeOmsPwd(loginPw).equals(sysUser.get("LOGIN_PW"))){
            throw new GlobalException("用户名密码不正确！");
        }
        //3.校验客户是否支持月结 支持就绑定到用户信息中
        Customer dbCustomer = findCustomerByCode(customerCode);
        if(dbCustomer == null || !dbCustomer.getCustomerBalanceMode().contains("月结")){
            throw new GlobalException("该客户不支持月结！");
        }
        if(dbCustomer == null || !"客户".equals(dbCustomer.getCustomerType())){
            throw new GlobalException("该客户类型["+dbCustomer.getCustomerType()+"]不允许在小程序上绑定！");
        }
        WxUser user = userMapper.selectById(openId);
        user.setCustomerCode(customerCode);
        user.setCustomerName(ZtdAESUtils.aesDecryptString(sysUser.get("USER_NAME")));
        userMapper.updateById(user);
        return user;
    }

    /**
     * 根据客户编码查询客户信息
     * @param customerCode
     * @return
     */
    public Customer findCustomerByCode(String customerCode) {
        QueryWrapper<Customer> query = new QueryWrapper<>();
        query.select("CUSTOMER_CODE", "CUSTOMER_CODE", "CUSTOMER_OWNER_SITE", "CUSTOMER_OWNER_SITE_CODE", "CUSTOMER_BALANCE_MODE", "CUSTOMER_TYPE");
        query.eq("CUSTOMER_CODE", customerCode)
                .eq("BL_OPEN", "1");
        return customerMapper.selectOne(query);
    }

    /**
     * 添加投诉建议
     * @param wxAdvice
     * @return
     */
    public boolean addWxAdvice(WxAdvice wxAdvice) {
        return commonMapper.addWxAdvice(wxAdvice) > 0;
    }

}
