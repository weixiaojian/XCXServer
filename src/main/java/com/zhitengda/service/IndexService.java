package com.zhitengda.service;

import cn.hutool.core.util.StrUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author langao_q
 * @since 2021-02-04 15:43
 */
@Slf4j
@Service
public class IndexService {

    @Autowired
    private WxTokenMapper wxTokenMapper;

    @Autowired
    private WxAddressMapper addressMapper;

    @Autowired
    private ProvinceMapper provinceMapper;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private CountyMapper countyMapper;

    @Autowired
    private TownMapper townMapper;

    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private WxGroupMapper wxGroupMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private UserService userService;

    @Autowired
     private WxMessagesMapper wxMessagesMapper;

    /**
     * 获取微信AccessToken
     * @param appId
     * @return
     */
    public WxToken getWxToken(String appId){
        return wxTokenMapper.selectById(appId);
    }

    /**
     * 获取地址簿分页数据
     * @param pageVo
     * @return
     */
    public RetPage<WxAddress> getAddressPage(PageVo pageVo) {
        QueryWrapper<WxAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("OPEN_ID", pageVo.getOpenId())
                .eq("BL_TYPE", pageVo.getBlType())
                //姓名模糊匹配 - 手机号模糊匹配
                .and(StrUtil.isNotBlank(pageVo.getName())|| StrUtil.isNotBlank(pageVo.getPhone()),
                        i -> i.like(StrUtil.isNotBlank(pageVo.getName()), "NAME", pageVo.getName())
                        .or().like(StrUtil.isNotBlank(pageVo.getPhone()), "PHONE", pageVo.getPhone()))
                .orderByDesc("CREATE_DATE");
        //根据指定列排序
        if(StrUtil.isNotBlank(pageVo.getOrderByColumn())){
            wrapper.orderByDesc(pageVo.getOrderByColumn());
        }
        IPage<WxAddress> page = new Page<>(pageVo.getPageNum(), pageVo.getPageSize());
        page = addressMapper.selectPage(page, wrapper);
        //封装返回结果RetPage
        RetPage retPage = new RetPage(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), page.getRecords());
        return retPage;
    }

    /**
     * 查询默认地址
     * @param openId
     * @return
     */
    public WxAddress findAddressByDefault(String openId) {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq("OPEN_ID", openId);
        wrapper.eq("DEFAULT_VALUE", 1);
        //只取一条记录
        wrapper.eq("ROWNUM", 1);
        return addressMapper.selectOne(wrapper);
    }

    /**
     * 添加地址簿数据
     * 限制：每个用户最多添加一百条寄件地址、一百条收件地址
     * @param address
     * @return
     */
    public boolean addAddress(WxAddress address) {
        //限制：每个用户最多添加一万条寄件地址、一万条收件地址
        Integer count = 0;
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("OPEN_ID", address.getOpenId());
        if(null != address.getBlType() && 0 == address.getBlType()){
            wrapper.eq("BL_TYPE", 0);
            count = addressMapper.selectCount(wrapper);
            if(count > 10000){
                throw new GlobalException("寄件地址总条数不能超过10000！");
            }
        }else{
            wrapper.eq("BL_TYPE", 1);
            count = addressMapper.selectCount(wrapper);
            if(count > 10000){
                throw new GlobalException("收件地址总条数不能超过10000！");
            }
        }
        return addressMapper.insert(address)>0;
    }

    /**
     * 修改地址簿数据
     * @param address
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAddress(WxAddress address) {
        address.setUpdateDate(new Date());

        //如果要设置成默认地址
        if(address.getDefaultValue() == 1){
            //1.先将指定openId的地址簿数据都改为非默认
            WxAddress dbAddress = new WxAddress();
            UpdateWrapper wrapper = new UpdateWrapper<>();
            dbAddress.setDefaultValue(0);
            wrapper.eq("OPEN_ID", address.getOpenId());
            addressMapper.update(dbAddress, wrapper);
        }
        //2.再更新指定地址
        return addressMapper.updateById(address)>0;
    }

    /**
     * 修改默认地址
     * 1.先将指定openId的地址簿数据都改为非默认
     * 2.设置指定id为默认地址
     * @param address
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDefault(WxAddress address) {
        //1.先将指定openId的地址簿数据都改为非默认
        UpdateWrapper wrapper = new UpdateWrapper<>();
        address.setDefaultValue(0);
        wrapper.eq("OPEN_ID", address.getOpenId());
        addressMapper.update(address, wrapper);
        //2.设置指定id为默认地址
        address.setDefaultValue(1);
        return addressMapper.updateById(address)>0;
    }

    /**
     * 删除地址簿数据
     * @param guid
     */
    public boolean deleteAddress(String guid) {
        return addressMapper.deleteById(guid)>0;
    }

    /**
     * 获取省-市-区集合数据（树形结构）
     * @return
     */
    public List<Province> getProvinceList() {
        long start = System.currentTimeMillis();
        QueryWrapper proQuery = new QueryWrapper();
        proQuery.select("PROVINCE_CODE","PROVINCE");
        List<Province> provinces = provinceMapper.selectList(proQuery);

        QueryWrapper cityQuery = new QueryWrapper();
        cityQuery.select("CITY_CODE","CITY_NAME", "PROVINCE_CODE", "PROVINCE");
        List<City> cities = cityMapper.selectList(cityQuery);

        QueryWrapper counQuery = new QueryWrapper();
        counQuery.select("COUNTY_CODE","COUNTY_NAME", "CITY_CODE", "CITY_NAME");
        List<County> counties = countyMapper.selectList(counQuery);
        long end = System.currentTimeMillis();
        log.info("【省-市-区sql】耗时：" + (end - start));

        /*QueryWrapper townQuery = new QueryWrapper();
        townQuery.select("TOWN_CODE","TOWN_NAME", "COUNTY_CODE", "COUNTY_NAME");
        List<Town> towns = townMapper.selectList(townQuery);

        //封装区-街道树
        Map<String, County> countyMap = new HashMap<>(3400);
        for (County county : counties) {
            countyMap.put(county.getCountyCode(), county);
        }
        for (Town town : towns) {
            String countyCode = town.getCountyCode();
            if (countyMap.containsKey(countyCode)) {
                County tabCounty = countyMap.get(countyCode);
                if (tabCounty.getTownList() == null) {
                    tabCounty.setTownList(new ArrayList<>());
                }
                tabCounty.getTownList().add(town);
            }
        }*/
        //封装市-区树
        Map<String, City> cityMap = new HashMap<>(400);
        for (City city : cities) {
            cityMap.put(city.getCityCode(), city);
        }
        for (County county : counties) {
            String cityCode = county.getCityCode();
            if (cityMap.containsKey(cityCode)) {
                City tabCity = cityMap.get(cityCode);
                if (tabCity.getCountyList() == null) {
                    tabCity.setCountyList(new ArrayList<>());
                }
                tabCity.getCountyList().add(county);
            }
        }
        //封装省-市树
        Map<String, Province> provinceMap = new HashMap<>(50);
        for (Province province : provinces) {
            provinceMap.put(province.getProvinceCode(), province);
        }
        for (City city : cities) {
            String provinceCode = city.getProvinceCode();
            Province tabProvince = provinceMap.get(provinceCode);
            if (tabProvince != null) {
                if (tabProvince.getCityList() == null) {
                    tabProvince.setCityList(new ArrayList<>());
                }
                tabProvince.getCityList().add(city);
            }
        }
        long end1 = System.currentTimeMillis();
        log.info("【省-市-区数据】耗时：" + (end1 - end));
        return provinces;
    }

    /**
     * 根据区Code获取街道
     * @param countyCode
     * @return
     */
    public List<Town> getTown(String countyCode) {
        QueryWrapper<Town> townQuery = new QueryWrapper<>();
        townQuery.select("TOWN_CODE","TOWN_NAME", "COUNTY_CODE", "COUNTY_NAME");
        townQuery.eq("COUNTY_CODE", countyCode);
        List<Town> towns = townMapper.selectList(townQuery);
        return towns;
    }

    /**
     * 根据编码查询网点
     * @param siteCode
     * @return
     */
    public Site getSite(String siteCode) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.select("SITE_CODE", "SITE_NAME", "HIDDEN_MANAGE", "BL_PRODUCT_TYPE", "BL_NOT_REC", "BL_NOT_DISP",
                        "BL_PAYMENT_TYPE", "BL_FRESHTANK");
        wrapper.eq("SITE_CODE", siteCode);
        wrapper.eq("BL_NOT_INPUT", 0);
        wrapper.eq("ROWNUM", 1);
        return siteMapper.selectOne(wrapper);
    }

    /**
     * 根据编码查询员工
     * @param empCode
     * @return
     */
    public Employee getEmployee(String empCode) {
        QueryWrapper<Employee> wrapper = new QueryWrapper<Employee>();
        wrapper.select("OWNER_SITE_CODE", "OWNER_SITE","EMPLOYEE_CODE", "EMPLOYEE_NAME");
        wrapper.eq("EMPLOYEE_CODE", empCode);
        wrapper.eq("BL_OPEN", 1);
        wrapper.eq("ROWNUM", 1);
        return employeeMapper.selectOne(wrapper);
    }

    /**
     * 校验省-市-区是否合法 并返回对应的Code
     * @return
     */
    public Town checkProvince(String province,
                                String city,
                                String county,
                                String town) {
        QueryWrapper wrapper = new QueryWrapper();
        //1.封装查询条件
        wrapper.select("PROVINCE_CODE", "PROVINCE", "COUNTY_CODE", "COUNTY_NAME", "CITY_CODE", "CITY_NAME", "COUNTY_NAME");
        wrapper.eq("PROVINCE", province);
        wrapper.eq("CITY_NAME", city);
        wrapper.eq("COUNTY_NAME", county);
        if(StrUtil.isNotBlank(town)){
            wrapper.eq("TOWN_NAME", town);
            wrapper.select("PROVINCE_CODE", "PROVINCE", "COUNTY_CODE", "COUNTY_NAME", "CITY_CODE", "CITY_NAME", "COUNTY_NAME","TOWN_CODE", "TOWN_NAME");
        }
        //2.取一条数据
        wrapper.eq("ROWNUM", 1);
        //3.执行
        return townMapper.selectOne(wrapper);
    }

    /**
     * 根据数据模糊匹配网点数据
     * @param site 条件
     * @return 网点
     */
    public List<Site> getSiteByAddress(Site site) {
        QueryWrapper wrapper = new QueryWrapper();
        //1.查询指定列
        wrapper.select("SITE_CODE", "SITE_NAME", "PROVINCE", "CITY", "COUNTY", "ADDRESS", "PHONE", "SALE_PHONE");
        //2.封装查询条件
        if(StrUtil.isNotBlank(site.getProvince())){
            wrapper.eq("PROVINCE", site.getProvince());
        }
        if(StrUtil.isNotBlank(site.getCity())){
            wrapper.eq("CITY", site.getCity());

        }
        if(StrUtil.isNotBlank(site.getCountry())){
            wrapper.eq("COUNTY", site.getCountry());
        }
        if(StrUtil.isNotBlank(site.getSiteName())){
            wrapper.like("SITE_NAME", site.getSiteName());
        }
        //3.返回查询结果
        return siteMapper.selectList(wrapper);
    }

    /**
     * 根据数据模糊匹配员工数据
     * @param emp
     * @return
     */
    public List<Employee> getEmpBylike(Employee emp){
        QueryWrapper<Employee> wrapper = new QueryWrapper<Employee>();
        //1.封装查询条件
        wrapper.select("EMPLOYEE_CODE","EMPLOYEE_NAME","OWNER_SITE","OWNER_SITE_CODE")
                .and(i -> i.eq("EMPLOYEE_CODE", emp.getMobilePhone()).or()
                        .eq("MOBILE_PHONE", emp.getMobilePhone()))
                .eq("BL_OPEN", 1)
                .eq("ROWNUM", 1);
        //2.返回查询结果
        return employeeMapper.selectList(wrapper);
    }

    /**
     * 查询当前用户是否有团队信息
     * @param token
     * @return
     */
    public WxGroup getWxGroupByOpenId(String token) {
        //1.先查询自己的用户数据
        WxGroup wxGroup = wxGroupMapper.selectById(token);
        if(wxGroup == null){
            return null;
        }
        //1.1赋值网点数据
        Site siteDb = getSite(wxGroup.getSiteCode());
        if(siteDb != null){
            wxGroup.setSiteName(siteDb.getSiteName());
            wxGroup.setBlPaymentType(siteDb.getBlPaymentType());
        }
        //2.查询团队设置（是否共享电子面单）,如果自己是超级管理员就不用去查询了
        if(wxGroup != null && 1 != wxGroup.getStatus()){
            QueryWrapper<WxGroup> wrapper = new QueryWrapper<>();
            wrapper.select("GROUP_ID","GROUP_NAME","BL_SHARE")
                    .eq("GROUP_ID", wxGroup.getGroupId())
                    .eq("STATUS", 1)
                    .eq("ROWNUM", 1);
            WxGroup groupAdmin = wxGroupMapper.selectOne(wrapper);
            wxGroup.setBlShare(groupAdmin.getBlShare());
        }
        //3查询一下网点电子面单的库存
        Map<String, Object> siteInventory = commonMapper.checkInventory(wxGroup.getSiteCode());
        BigDecimal quantity = (BigDecimal) siteInventory.get("QUANTITY");
        wxGroup.setQuantity(quantity != null?quantity.intValue():0);
        return wxGroup;
    }

    /**
     * 根据昵称查询团队成员信息
     * @param nickName
     * @return
     */
    public WxGroup getWxGroupByName(String groupId, String nickName) {
        QueryWrapper<WxGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("GROUP_ID",  groupId)
                .like("NICK_NAME", nickName)
                .eq("ROWNUM", 1);

        WxGroup wxGroup = wxGroupMapper.selectOne(wrapper);
        if(wxGroup == null){
            throw new GlobalException("未查询到团队成员信息，请核对昵称是否正确！");
        }
        //赋值网点数据
        Site siteDb = getSite(wxGroup.getSiteCode());
        if(siteDb != null){
            wxGroup.setSiteName(siteDb.getSiteName());
        }
        return wxGroup;
    }

    /**
     * 查询当前待审核的总数（只有超级管理员才有这个权限）
     * @param openId
     * @return
     */
    public Integer getAuditCount(String groupId, String openId) {
        Integer count = 0;
        //1.校验是否是超级管理员
        checkAuthOpenId(groupId, openId);
        //2.获取当前用户的团队信息
        WxGroup wxGroup = wxGroupMapper.selectById(openId);
        //3.查询待审核的人数(status=0)
        QueryWrapper<WxGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("GROUP_ID", wxGroup.getGroupId());
        wrapper.eq("STATUS", 0);
        count = wxGroupMapper.selectCount(wrapper);
        return count;
    }

    /**
     * 创建团队
     * @param wxGroup
     * @return
     */
    public boolean createWxGroup(WxGroup wxGroup) {
        //1.校验用户是否加入了其他团队
        checkExistOpenId(wxGroup.getOpenId());
        //2.校验网点编码及网点名称是否正确、且网点是启用状态
        QueryWrapper<Site> siteWrapper = new QueryWrapper<>();
        siteWrapper.eq("SITE_CODE", wxGroup.getSiteCode())
                .eq("SITE_NAME", wxGroup.getSiteName())
                .eq("BL_NOT_INPUT", "0")
                .eq("TEAM_STATUS", "1");
        Integer count = siteMapper.selectCount(siteWrapper);
        if(count < 1){
            throw new GlobalException("网点["+wxGroup.getSiteCode()+"]不存在、未启用或不允许创建团队！");
        }
        //4.0校验网点是否创建过团队
        WxGroup queryWxGroupBySite = new WxGroup();
        queryWxGroupBySite.setSiteCode(wxGroup.getSiteCode());
        List<WxGroup> listBySite = getWxGroup(queryWxGroupBySite);
        if(listBySite.size() > 0){
            throw new GlobalException("该网点["+wxGroup.getSiteName()+"]已创建过团队！");
        }
        //4.1校验团队名称是否存在、确保创建者是超级管理员、默认不共享电子面单、默认需要入团审批
        WxGroup queryWxGroup = new WxGroup();
        queryWxGroup.setGroupName(wxGroup.getGroupName());
        List<WxGroup> list = getWxGroup(queryWxGroup);
        if(list.size() > 0){
            throw new GlobalException("团队名称["+wxGroup.getGroupName()+"]已存在！");
        }
        //3.生成团队ID（"YD" + 8位数字）
        String groupId = "YD" +  commonMapper.getGroupId().trim();
        wxGroup.setGroupId(groupId);
        wxGroup.setStatus(1);
        wxGroup.setBlShare(0);
        wxGroup.setBlAudit(1);
        //5.保存团队数据
        return wxGroupMapper.insert(wxGroup) > 0;
    }

    /**
     * 加入团队
     * @param wxGroup
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addWxGroup(WxGroup wxGroup){
        //1.校验用户是否加入了其他团队
        checkExistOpenId(wxGroup.getOpenId());
        //2.团队ID不能为空
        if(StrUtil.isBlank(wxGroup.getGroupId())){
            throw new GlobalException("团队ID不能为空！");
        }
        //3校验网点编码及网点名称是否正确、且网点是启用状态
        QueryWrapper<Site> siteWrapper = new QueryWrapper<>();
        siteWrapper.eq("SITE_CODE", wxGroup.getSiteCode())
                .eq("BL_NOT_INPUT", "0");
        Integer count = siteMapper.selectCount(siteWrapper);
        if(count < 1){
            throw new GlobalException("网点["+wxGroup.getSiteCode()+"]不存在或未启用！");
        }
        //4.校验团队是否存在、校验是否和超级管理员的网点相等
        QueryWrapper<WxGroup> groupWrapper = new QueryWrapper<>();
        groupWrapper.eq("GROUP_ID", wxGroup.getGroupId());
        groupWrapper.eq("STATUS", 1);
        groupWrapper.eq("ROWNUM", 1);
        WxGroup queryWxGroup = wxGroupMapper.selectOne(groupWrapper);
        if(queryWxGroup == null){
            throw new GlobalException("团队["+wxGroup.getGroupId()+"]["+wxGroup.getGroupName()+"]不存在！");
        }
        if(!wxGroup.getSiteCode().equals(queryWxGroup.getSiteCode())){
            throw new GlobalException("团队所属网点需要和超级管理员一致 超级管理员网点：" + queryWxGroup.getSiteCode());
        }
        //5.如果开启了需要审批的开关（1需要审批 0不需要审批） 那么直接创建客户 状态是3
        if(null != queryWxGroup.getBlAudit() && 1 != queryWxGroup.getBlAudit()){
            //5.1创建所属网点下的客户
            String customerCode = commonMapper.getCustomerCode();
            Customer customer = new Customer();
            customer.setCustomerCode(customerCode);
            customer.setCustomerName(wxGroup.getNickName());
            customer.setCustomerFullName(wxGroup.getNickName());
            customer.setBlProjectCustomer(0);
            customer.setCustomerOwnerSiteCode(wxGroup.getSiteCode());
            customer.setCustomerOwnerSite(wxGroup.getSiteName());
            customer.setCustomerPhone1(ZtdAESUtils.aesEncryptString(wxGroup.getPhone()));
            customer.setBlOpen(1);
            customer.setCustomerBalanceMode("现金");
            customer.setCreateMan("小程序");
            customer.setCreateManCode("小程序");
            customer.setCreateSite("小程序");
            customer.setCreateSiteCode("小程序");
            //5.1 如果团长开启共享面单 就不需要检查面单额度,如果团长未开启共享面单  就需要开启检查面单额度
            if (queryWxGroup.getBlShare() == 1) {
                customer.setBlCheckEleCount(0);
            } else {
                customer.setBlCheckEleCount(1);
            }
            //5.2查询用户是否实名 如果已经实名需要将实名数据赋值到customer
            WxReal queryWxReal = new WxReal();
            queryWxReal.setOpenId(wxGroup.getOpenId());
            WxReal wxReal = userService.findRealId(queryWxReal);
            if(wxReal != null){
                customer.setRealIdType(wxReal.getRealIdType());
                customer.setRealIdCode(ZtdAESUtils.aesEncryptString(wxReal.getRealIdCode()));
                customer.setRealName(ZtdAESUtils.aesEncryptString(wxReal.getRealName()));
            }
            //5.3保存客户数据
            customerMapper.insert(customer);
            //5.4修改团队成员状态为普通管理员
            wxGroup.setStatus(3);
            wxGroup.setCustomerCode(customerCode);
            wxGroup.setCustomerName(wxGroup.getNickName());
        }else{
            //未开启不需要审核的话：直接就是待审核的状态即可
            wxGroup.setStatus(0);
        }
        //6.保存团队成员数据
        int insert = wxGroupMapper.insert(wxGroup);
        return insert > 0;
    }

    /**
     * 审核团队成员
     * @param openId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean auditWxGroup(String openId) {
        //1.查询团队成员数据
        WxGroup dbWxGroup = wxGroupMapper.selectById(openId);
        if(dbWxGroup == null){
            throw new GlobalException("未查询到团队成员数据！");
        }
        if(dbWxGroup.getStatus() != 0){
            throw new GlobalException("该成员的状态不是待审核！");
        }
        //2.创建所属网点下的客户
        String customerCode = commonMapper.getCustomerCode();
        Customer customer = new Customer();
        customer.setCustomerCode(customerCode);
        customer.setCustomerName(dbWxGroup.getNickName());
        customer.setCustomerFullName(dbWxGroup.getNickName());
        customer.setBlProjectCustomer(0);
        customer.setCustomerOwnerSiteCode(dbWxGroup.getSiteCode());
        customer.setCustomerOwnerSite(dbWxGroup.getSiteName());
        customer.setCustomerPhone1(ZtdAESUtils.aesEncryptString(dbWxGroup.getPhone()));
        customer.setBlOpen(1);
        customer.setCustomerBalanceMode("现金");
        customer.setCreateMan("小程序");
        customer.setCreateManCode("小程序");
        customer.setCreateSite("小程序");
        customer.setCreateSiteCode("小程序");
        //2.1 如果团长开启共享面单 就不需要检查面单额度,如果团长未开启共享面单  就需要开启检查面单额度
        QueryWrapper<WxGroup> groupWrapper = new QueryWrapper<>();
        groupWrapper.eq("GROUP_ID", dbWxGroup.getGroupId());
        groupWrapper.eq("STATUS", 1);
        groupWrapper.eq("ROWNUM", 1);
        //查询团长信息
        WxGroup queryWxGroup = wxGroupMapper.selectOne(groupWrapper);
        if (queryWxGroup.getBlShare() == 1) {
            customer.setBlCheckEleCount(0);
        } else {
            customer.setBlCheckEleCount(1);
        }
        //3.查询用户是否实名 如果已经实名需要将实名数据赋值到customer
        WxReal queryWxReal = new WxReal();
        queryWxReal.setOpenId(dbWxGroup.getOpenId());
        WxReal wxReal = userService.findRealId(queryWxReal);
        if(wxReal != null){
            customer.setRealIdType(wxReal.getRealIdType());
            customer.setRealIdCode(ZtdAESUtils.aesEncryptString(wxReal.getRealIdCode()));
            customer.setRealName(ZtdAESUtils.aesEncryptString(wxReal.getRealName()));
        }
        //4.保存客户数据
        customerMapper.insert(customer);
        //5.修改团队成员状态为普通管理员
        dbWxGroup.setStatus(3);
        dbWxGroup.setCustomerCode(customerCode);
        dbWxGroup.setCustomerName(dbWxGroup.getNickName());
        dbWxGroup.setUpdateDate(new Date());
        //6.保存团队数据
        return wxGroupMapper.updateById(dbWxGroup) > 0;
    }


    /**
     * 根据指定条件查询团队数据
     * @param queryWxGroup
     * @return
     */
    public List<WxGroup> getWxGroup(WxGroup queryWxGroup) {
        QueryWrapper<WxGroup> wrapper = new QueryWrapper<WxGroup>();
        //团队ID
        if(StrUtil.isNotBlank(queryWxGroup.getGroupId())){
            wrapper.eq("GROUP_ID", queryWxGroup.getGroupId());
        }
        //团队名称
        if(StrUtil.isNotBlank(queryWxGroup.getGroupName())){
            wrapper.eq("GROUP_NAME", queryWxGroup.getGroupName());
        }
        //openId
        if(StrUtil.isNotBlank(queryWxGroup.getOpenId())){
            wrapper.eq("OPEN_ID", queryWxGroup.getOpenId());
        }
        //用户手机号
        if(StrUtil.isNotBlank(queryWxGroup.getPhone())){
            wrapper.eq("PHONE", queryWxGroup.getPhone());
        }
        //用户昵称
        if(StrUtil.isNotBlank(queryWxGroup.getNickName())){
            wrapper.like("NICK_NAME", queryWxGroup.getNickName());
        }
        //状态
        if(queryWxGroup.getStatus() != null){
            wrapper.eq("STATUS", queryWxGroup.getStatus());
        }
        //网点
        if(StrUtil.isNotBlank(queryWxGroup.getSiteCode())){
            wrapper.eq("SITE_CODE", queryWxGroup.getSiteCode());
        }
        wrapper.orderByAsc("CREATE_DATE");
        return wxGroupMapper.selectList(wrapper);
    }

    /**
     * 更新团队成员数据
     * @param dbWxGroup
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWxGroupById(WxGroup dbWxGroup) {
        if(StrUtil.isNotBlank(dbWxGroup.getNickName())){
            //1.更新客户名称
            WxGroup wxGroup = wxGroupMapper.selectById(dbWxGroup.getOpenId());
            Customer customer = new Customer();
            customer.setCustomerCode(wxGroup.getCustomerCode());
            customer.setCustomerName(dbWxGroup.getNickName());
            customerMapper.updateById(customer);
        }
        dbWxGroup.setCustomerName(dbWxGroup.getNickName());
        return wxGroupMapper.updateById(dbWxGroup) > 0;
    }

    /**
     * 删除团队成员
     * 1.移除团队成员前需要将customer停用
     * 2.删除团队成员数据
     * @param openId
     * @return
     */
    public boolean deleteWxGroupById(String openId){
        //1.将customer停用
        WxGroup dbWxGroup = wxGroupMapper.selectById(openId);
        if(StrUtil.isNotBlank(dbWxGroup.getCustomerCode())){
            Customer customer = new Customer();
            customer.setCustomerCode(dbWxGroup.getCustomerCode());
            customer.setBlOpen(0);
            customerMapper.updateById(customer);
        }
        //3.删除团队成员数据
        return wxGroupMapper.deleteById(openId) > 0;
    }

    /**
     * 解散团队
     * 0.校验当前token是超级管理员
     * 1.查询出所有团队成员
     * 2.移除团队成员前需要将customer停用
     * 3.删除团队成员数据
     * @param groupId
     */
    public boolean emptyWxGroup(String token, String groupId) {
        //0.校验当前token是超级管理员
        checkAuthOpenId(groupId, token);
        QueryWrapper<WxGroup> wrapper = new QueryWrapper<WxGroup>();
        wrapper.eq("GROUP_ID", groupId);
        //1.查询出所有团队成员
        List<WxGroup> list = wxGroupMapper.selectList(wrapper);
        for(WxGroup wxGroup : list){
            deleteWxGroupById(wxGroup.getOpenId());
        }
        return true;
    }

    /**
     * 根据团队ID或名称查询团队信息（查询超级管理的的那条数据）
     * @param groupId
     * @return
     */
    public WxGroup getWxGroupByIdOrName(String groupId) {
        QueryWrapper<WxGroup> wrapper = new QueryWrapper<WxGroup>();
        wrapper.select("GROUP_ID","GROUP_NAME","BL_SHARE","BL_AUDIT", "SITE_CODE", "SITE_NAME");
        //团队ID或团队名称
        if(StrUtil.isNotBlank(groupId)){
            wrapper.and(i -> i.eq("GROUP_ID", groupId)
                    .or()
                    .eq("GROUP_NAME", groupId));
        }
        //状态
        wrapper.eq("STATUS", 1);
        //只取一条记录
        wrapper.eq("ROWNUM", 1);
        WxGroup wxGroup = wxGroupMapper.selectOne(wrapper);
        //赋值网点数据
        Site siteDb = getSite(wxGroup.getSiteCode());
        if(siteDb != null){
            wxGroup.setSiteName(siteDb.getSiteName());
        }
        return wxGroup;
    }

    /**
     * 校验指定用户是否是超级管理员
     * @param groupId
     * @param authOpenId
     * @return
     */
    public boolean checkAuthOpenId(String groupId, String authOpenId){
        WxGroup queryWxGroup = new WxGroup();
        queryWxGroup.setGroupId(groupId);
        queryWxGroup.setOpenId(authOpenId);
        queryWxGroup.setStatus(1);
        List<WxGroup> list = getWxGroup(queryWxGroup);
        if(list.size() < 1){
            throw new GlobalException("当前用户不是超级管理员！");
        }
        return true;
    }

    /**
     * 校验用户是否加入过其他团队
     * @param openId
     * @return
     */
    public boolean checkExistOpenId(String openId){
        WxGroup queryWxGroup = new WxGroup();
        queryWxGroup.setOpenId(openId);
        List<WxGroup> list = getWxGroup(queryWxGroup);
        if(list.size() > 0){
            throw new GlobalException("当前用户已加入团队["+list.get(0).getGroupName()+"]！");
        }
        return true;
    }

    /**
     * 修改团队名称
     * @param wxGroup
     * @return
     */
    public boolean editGroupName(WxGroup wxGroup) {
        //1.校验当前用户是否是管理员
        checkAuthOpenId(wxGroup.getGroupId(), wxGroup.getOpenId());
        //2.修改团队名称
        UpdateWrapper<WxGroup> wrapper = new UpdateWrapper<>();
        wrapper.eq("GROUP_ID", wxGroup.getGroupId());
        WxGroup dbWxGroup = new WxGroup();
        dbWxGroup.setGroupName(wxGroup.getGroupName());
        dbWxGroup.setUpdateDate(new Date());
        return wxGroupMapper.update(dbWxGroup, wrapper) > 0;
    }

    /**
     * 更改共享电子面单开关
     * @param wxGroup
     */
    public boolean editGroupBlShare(WxGroup wxGroup) {
        //1.校验当前用户是否是超级管理员
        checkAuthOpenId(wxGroup.getGroupId(), wxGroup.getOpenId());
        wxGroup.setUpdateDate(new Date());
        //2.修改团队共享电子面单开关
        return wxGroupMapper.updateById(wxGroup) > 0;
    }

    /**
     * 更改是否需要审批开关
     * @param wxGroup
     */
    public boolean editGroupBlAudit(WxGroup wxGroup) {
        //1.校验当前用户是否是超级管理员
        checkAuthOpenId(wxGroup.getGroupId(), wxGroup.getOpenId());
        wxGroup.setUpdateDate(new Date());
        //2.更改是否需要审批开关
        return wxGroupMapper.updateById(wxGroup) > 0;
    }

    /**
     * 清除团队成员数据 并推送模板消息
     * @param groupId
     * @param token
     * @param openId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean turnDownWxGroup(String groupId, String token, String openId) {
        //1.校验当前用户是否是超级管理员
        checkAuthOpenId(groupId, token);
        //2.清除团队数据
        wxGroupMapper.deleteById(openId);
        //3.推送模板消息
        String gzhOpenId = commonMapper.findGzhOpenId(openId);
        if(StrUtil.isNotBlank(gzhOpenId)){
            WxMessages msg = new WxMessages();
            msg.setOpenId(gzhOpenId);
            msg.setType("团队拒绝");
            msg.setContent("尊敬的用户，您申请加入的团队["+groupId+"]请求被拒绝了！");
            msg.setScanDate(new Date());
            wxMessagesMapper.insert(msg);
        }
        return true;
    }

    /**
     * 校验城市是否允许派件 不允许就不让下单
     * true：允许下单
     * false：不允许派件
     * @param city
     */
    public boolean checkDispByCity(String city) {
        QueryWrapper<City> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CITY_NAME", city)
                    .eq("BL_DISP",0);
        return cityMapper.selectCount(queryWrapper) < 1;
    }
}
