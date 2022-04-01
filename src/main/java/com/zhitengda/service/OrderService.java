package com.zhitengda.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhitengda.entity.*;
import com.zhitengda.mapper.CommonMapper;
import com.zhitengda.mapper.OrderMapper;
import com.zhitengda.mapper.WxGroupMapper;
import com.zhitengda.mapper.WxRemarkMapper;
import com.zhitengda.util.RetResult;
import com.zhitengda.util.TXMapUtil;
import com.zhitengda.util.ZtdAESUtils;
import com.zhitengda.vo.*;
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
public class OrderService {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WxRemarkMapper wxRemarkMapper;

    @Autowired
    private WxGroupMapper wxGroupMapper;

    @Autowired
    private IndexService indexService;

    public List<OrderConstantDto> getOrderConstant(String dic_code) {
        return commonMapper.getOrderConstant(dic_code);
    }


    /**
     * 校验运单号是否被使用
     * @param billCode
     * @return
     */
    public boolean checkBillCode(String billCode) {
        Integer cut = commonMapper.checkBillCode(billCode);
        if(cut > 0){
            throw new GlobalException("运单号已被使用：" + billCode);
        }
        return true;
    }

    /**
     * 获取单号
     * @param dataFrom
     * @return
     */
    public String getOrderCode(String dataFrom) {
        String orderBill = commonMapper.getOrderCode(dataFrom);
        return orderBill;
    }

    /**
     * 根据运单号获取绑定的所属网点
     * @param billCode
     */
    public Site getSendSiteByBillCode(String billCode) {
        return commonMapper.getSendSiteByBillCode(billCode);
    }

    /**
     * 获取寄件网点面单库存
     * @param sendSiteCode
     * @return
     */
    public Map<String, Object> checkInventory(String sendSiteCode) {
        return commonMapper.checkInventory(sendSiteCode);
    }

    /**
     * 计算运费/到付款
     * @param trackFreight
     * @return
     */
    public BigDecimal trackFreight(TrackFreightVo trackFreight) {
        return commonMapper.trackFreight(trackFreight);
    }


    /**
     * 查询我寄的/我收的 分页数据
     * @param pageVo
     * @return
     */
    public RetPage<Order> findMyOrderPage(PageVo pageVo) {
        //2.执行查询
        IPage<Order> page = new Page<>(pageVo.getPageNum(), pageVo.getPageSize());
        page = commonMapper.findMyOrderPage(page, pageVo);
        //3.解密集合中的数据（寄/收：姓名、手机号、详细地址）
        for(Order o : page.getRecords()){
            o.setSendMan(ZtdAESUtils.aesDecryptString(o.getSendMan()));
            o.setSendManMobile(ZtdAESUtils.aesDecryptString(o.getSendManMobile()));
            o.setSendManAddress(ZtdAESUtils.aesDecryptString(o.getSendManAddress()));
            o.setAcceptMan(ZtdAESUtils.aesDecryptString(o.getAcceptMan()));
            o.setAcceptManMobile(ZtdAESUtils.aesDecryptString(o.getAcceptManMobile()));
            o.setAcceptManAddress(ZtdAESUtils.aesDecryptString(o.getAcceptManAddress()));
        }
        //4.封装返回结果RetPage
        RetPage retPage = new RetPage(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), page.getRecords());
        return retPage;
    }

    /**
     * 根据运单号获取扫描轨迹
     * @param billCode
     * @return
     */
    public List<RetScanPathDto> getScanBillCode(String billCode) {
        //1.查询轨迹
        List<RetScanPathDto> list = commonMapper.getScanBillCode(billCode);
        //2.解密上/下一站、手机号
        for(RetScanPathDto dto : list){
            dto.setPreOrNextStation(ZtdAESUtils.aesDecryptString(dto.getPreOrNextStation()));
            dto.setPhone(ZtdAESUtils.aesDecryptString(dto.getPhone()));
        }
        return list;
    }

    /**
     * 查询订单的寄件地址和收件地址转换为经纬度坐标
     * @param order
     * @return
     */
    public HashMap<String , Object> getCoordinate(Order order) {
        HashMap<String , Object> map = new HashMap<>();
        CoordinateDto send = null;
        CoordinateDto rec = null;
        if(order == null){
            String code = StrUtil.isNotBlank(order.getBillCode())?order.getBillCode():order.getOrderBill();
            throw new GlobalException("订单不存在或者没有权限操作该订单(" + code + ")") ;
        }
        //1.拼接寄件地址和收件地址
        String sendAddress = order.getSendProvince() + order.getSendCity() + order.getSendCounty() + order.getSendManAddress();
        String recAddress = order.getAcceptProvince() + order.getAcceptCity() + order.getAcceptCounty() + order.getAcceptManAddress();
        //2.把寄件地址和收件地址转换为经纬度坐标
        send = TXMapUtil.addressToCoordinateSystem(sendAddress);
        rec = TXMapUtil.addressToCoordinateSystem(recAddress);
        //3.返回结果
        map.put("send", send);
        map.put("rec", rec);
        return map;
    }

    /**
     * 根据订单号/运单号查询订单详情
     * [只有对应的openId才能查看]
     * @param order
     * @return
     */
    public Order getOrderByOrderBill(Order order) {
        //1.查询
        Order orderDb = commonMapper.getOrderByOrderBill(order);
        //2.解密集合中的数据（寄/收：姓名、手机号、详细地址）
        if(orderDb == null){
            String code = StrUtil.isNotBlank(order.getBillCode())?order.getBillCode():order.getOrderBill();
            throw new GlobalException("订单不存在或者没有权限操作该订单(" + code + ")") ;
        }
        orderDb.setSendMan(ZtdAESUtils.aesDecryptString(orderDb.getSendMan()));
        orderDb.setSendManMobile(ZtdAESUtils.aesDecryptString(orderDb.getSendManMobile()));
        orderDb.setSendManAddress(ZtdAESUtils.aesDecryptString(orderDb.getSendManAddress()));
        orderDb.setAcceptMan(ZtdAESUtils.aesDecryptString(orderDb.getAcceptMan()));
        orderDb.setAcceptManMobile(ZtdAESUtils.aesDecryptString(orderDb.getAcceptManMobile()));
        orderDb.setAcceptManAddress(ZtdAESUtils.aesDecryptString(orderDb.getAcceptManAddress()));
        orderDb.setCustomerName(ZtdAESUtils.aesDecryptString(orderDb.getCustomerName()));
        return orderDb;
    }

    /**
     * 统计我寄的和我收的总数
     * @param token
     * @param phone
     * @return
     */
    public Map<String, Integer> getMyOrderCount(String token, String phone) {
        Map<String, Integer> map = new HashMap(2);
        //1.统计我寄的
        if(StrUtil.isNotBlank(token)){
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("OPEN_ID", token);
            Integer sendCount = orderMapper.selectCount(wrapper);
            map.put("sendCount", sendCount);
        }
        //2.统计我收的
        if(StrUtil.isNotBlank(phone)){
            Integer recCount = commonMapper.getRecOrderCount(ZtdAESUtils.aesEncryptString(phone));
            map.put("recCount", recCount);
        }
        return map;
    }

    /**
     * 分页查询我的运单
     * @param pageVo
     * @return
     */
    public RetPage<Bill> findMyWaybillPage(PageVo pageVo) {

        //1.执行查询
        IPage<Bill> page = new Page<>(pageVo.getPageNum(), pageVo.getPageSize());
        page = commonMapper.findMyWaybillPage(page, pageVo);
        //2.解密集合中的数据（寄/收：姓名、手机号、详细地址）
        for(Bill b : page.getRecords()){
            b.setSendMan(ZtdAESUtils.aesDecryptString(b.getSendMan()));
            b.setSendManPhone(ZtdAESUtils.aesDecryptString(b.getSendManPhone()));
            b.setSendManAddress(ZtdAESUtils.aesDecryptString(b.getSendManAddress()));
            b.setAcceptMan(ZtdAESUtils.aesDecryptString(b.getAcceptMan()));
            b.setAcceptManPhone(ZtdAESUtils.aesDecryptString(b.getAcceptManPhone()));
            b.setAcceptManAddress(ZtdAESUtils.aesDecryptString(b.getAcceptManAddress()));
        }
        //3.封装返回结果RetPage
        RetPage retPage = new RetPage(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), page.getRecords());
        return retPage;
    }

    /**
     * 根据订单号集合获取订单List数据
     * @param orderBill
     * @return
     */
    public List<Bill> getOrderPrint(String orderBill) {
        List<String> orderBills = Arrays.asList(orderBill.split(","));
        HashMap<String, Object> param = new HashMap<>();
        param.put("orderBill", orderBill);
        param.put("orderBills", orderBills);
        List<Bill> billsPrint = commonMapper.getOrderPrint(param);
        //是否启用韵达网点三段码匹配
        billsPrint.forEach(bill -> {
            //1.解密集合中的数据（寄/收：姓名、手机号、详细地址）
            bill.setSendMan(ZtdAESUtils.aesDecryptString(bill.getSendMan()));
            bill.setSendManPhone(ZtdAESUtils.aesDecryptString(bill.getSendManPhone()));
            bill.setSendManAddress(ZtdAESUtils.aesDecryptString(bill.getSendManAddress()));
            bill.setAcceptMan(ZtdAESUtils.aesDecryptString(bill.getAcceptMan()));
            bill.setAcceptManPhone(ZtdAESUtils.aesDecryptString(bill.getAcceptManPhone()));
            bill.setAcceptManAddress(ZtdAESUtils.aesDecryptString(bill.getAcceptManAddress()));

            //2.取当前订单的寄件网点来判断是否开启电话隐藏开关
            Site site = indexService.getSite(bill.getSendSiteCode());
            if(1 == site.getHiddenManage()
                    && StrUtil.isNotBlank(bill.getSendManPhone()) && StrUtil.isNotBlank(bill.getAcceptManPhone())){
                bill.setSendManPhone(bill.getSendManPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
                bill.setAcceptManPhone(bill.getAcceptManPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
            }
        });
        return billsPrint;
    }

    /**
     * 更改订单的打印状态
     * @param orderBill
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBlPrint(String orderBill, String openId) {
        List<String> orderBills = Arrays.asList(orderBill.split(","));
        List<Order> orderList = commonMapper.getOrderByOrderCode(orderBills);
        for(Order order : orderList){
            Map<String, Object> param = new HashMap<>();
            param.put("ORDER_CODE", order.getOrderBill());
            param.put("BILL_CODE", order.getBillCode());
            param.put("PRINT_MAN", "微信");
            param.put("PRINT_MAN_CODE","微信");
            param.put("PRINT_SITE", "微信");
            param.put("PRINT_SITE_CODE", "微信");
            param.put("PRINT_DATE", new Date());
            param.put("PRINT_TYPE", "订单打印");
            param.put("PRINT_DATA_FROM", "微信");
            param.put("PRINT_DATA_TYPE", "订单");
            commonMapper.insertPrintLog(param);
        }
        return true;
    }

    /**
     * 下单成功如果未匹配到寄件网点的需要给总部下的客服部门员工发送消息（异常不影响业务 只打印不抛出）
     * 1.插入到消息表TAB_NOTICE即可
     * @param orderBills
     */
    public boolean sendRecMsg(List<String> orderBills) {
        try {
            for(String orderBill : orderBills){
                Map<String, Object> param = new HashMap<>();
                param.put("BILL_CODE", orderBill);
                param.put("TITLE", "小程序下单待揽件：" + orderBill);
                param.put("NOTICE_NAME", "部门通知");
                param.put("SPECIFY_PERSON", "ALL");
                param.put("SEND_SITE", "总部");
                param.put("SEND_SITE_CODE", "8888888");
                param.put("PERSON", "智腾达管理员");
                param.put("ALLOW_RESTORE", 0);
                param.put("CONTENT",  "小程序下单待揽件：" + orderBill);
                param.put("BL_MUST_SEE", 0);
                param.put("BL_MUST_REPLY", 0);
                param.put("PERSON_CODE", "admin");
                param.put("BL_FROM", 1);
                param.put("REC_SITE", ";总部;");
                param.put("REC_SITE_CODE", ";8888888;");
                param.put("BL_DEPT", 1);
                param.put("REC_DEPT", ";客服部;");
                commonMapper.sendRecMsg(param);
            }
        }catch (Exception e){
            log.error("给客服部门员工发送消息异常：", e);
        }
        return true;
    }

    /**
     * 添加微信快捷备注
     * @param wxRemark
     * @return
     */
    public boolean addWxRemark(WxRemark wxRemark) {
        return wxRemarkMapper.insert(wxRemark) > 0;
    }

    /**
     * 获取微信快捷备注列表
     * @param openId
     * @return
     */
    public List<WxRemark> getWxRemarks(String openId) {
        QueryWrapper<WxRemark> wrapper = new QueryWrapper<>();
        wrapper.eq("OPEN_ID", openId)
        .orderByDesc("CREATE_DATE");
        return wxRemarkMapper.selectList(wrapper);
    }

    /**
     * 校验用户是否有修改/取消订单的权限
     * @param token
     * @param orderBill
     * @return
     */
    public boolean checkCancelOrder(String token, String orderBill) {
        //1.查询订单详情
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("ORDER_BILL, BILL_CODE, OPEN_ID, CUSTOMER_CODE")
                .eq("ORDER_BILL", orderBill);
        Order orderDb = orderMapper.selectOne(queryWrapper);
        if(orderDb == null){
            throw new GlobalException("未查询到该订单["+orderBill+"]");
        }
        //2.散客：判断是否是用户自己的订单，订单的openId相等
        if(token.equals(orderDb.getOpenId())){
            return true;
        }
        //3.团队：判断寄件客户是否是自己，订单的customerCode相等
        WxGroup wxGroup = wxGroupMapper.selectById(token);
        if(wxGroup != null && StrUtil.isNotEmpty(orderDb.getCustomerCode())
                && StrUtil.isNotEmpty(wxGroup.getCustomerCode()) && orderDb.getCustomerCode().equals(wxGroup.getCustomerCode())){
            return true;
        }
        return false;
    }

    /**
     * 校验用户是否有查看订单详情/轨迹的权限
     * @param token
     * @param orderBill
     * @return
     */
    public boolean checkLookOrder(String token, String orderBill) {
        //1.查询订单详情
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("ORDER_BILL, BILL_CODE, OPEN_ID, CUSTOMER_CODE, SEND_SITE_CODE")
                .eq("ORDER_BILL", orderBill)
                .or()
                .eq("BILL_CODE", orderBill);
        Order orderDb = orderMapper.selectOne(queryWrapper);
        if(orderDb == null){
            throw new GlobalException("未查询到该订单["+orderBill+"]");
        }
        //2.散客：判断是否是用户自己的订单，订单的openId相等
        if(token.equals(orderDb.getOpenId())){
            return true;
        }
        //3.团队：判断寄件客户是否是自己，订单的customerCode相等
        WxGroup wxGroup = wxGroupMapper.selectById(token);
        if(wxGroup != null && StrUtil.isNotEmpty(orderDb.getCustomerCode())
                && StrUtil.isNotEmpty(wxGroup.getCustomerCode()) && orderDb.getCustomerCode().equals(wxGroup.getCustomerCode())){
            return true;
        }
        //3.1判断用户是否是超级管理员/管理员，如果是 并且寄件网点和订单的相等也能查看
        if(wxGroup != null  && (1 == wxGroup.getStatus() || 2 == wxGroup.getStatus())
                && wxGroup.getSiteCode().equals(orderDb.getSendSiteCode())){
            return true;
        }
        return false;
    }

    /**
     * 删除快捷备注id
     * @param id
     * @return
     */
    public boolean delWxRemark(String id) {
        return wxRemarkMapper.deleteById(id) > 0;
    }

    /**
     * 校验寄件网点是否关闭揽收、是否欠费；派件网点是否关闭派件
     * @param sendSiteCode 寄件网点
     * @param dispatchSiteCode 派件网点
     * @return
     */
    public RetResult checkSendSiteAndDispSite(String sendSiteCode, String dispatchSiteCode) {
        //1.判断寄件网点是否关闭揽收、是否欠费
        if(StrUtil.isNotBlank(sendSiteCode)){
            if(commonMapper.checkSiteCodOwe(sendSiteCode) > 0){
                return RetResult.warn("网点["+sendSiteCode+"]COD账户已欠费！");
            }
            //判断是否关闭揽收
            Site sendSite = indexService.getSite(sendSiteCode);
            if(sendSite == null || (sendSite.getBlNotRec() != null && 1 == sendSite.getBlNotRec())){
                return RetResult.warn("网点["+sendSite.getSiteName()+"]不存在或已关闭揽收！");
            }
            //判断是否欠费：目前结算余额 <= 警戒结算金额
            if(commonMapper.checkSiteAccOwe(sendSiteCode) > 0){
                return RetResult.warn("网点["+sendSite.getSiteName()+"]预付款账户已欠费！");
            }
        }
        //2.判断派件网点是否关闭派件
        if(StrUtil.isNotBlank(dispatchSiteCode)){
            Site dispSite = indexService.getSite(dispatchSiteCode);
            if(dispSite == null || (dispSite.getBlNotDisp() != null && 1 == dispSite.getBlNotDisp())){
                return RetResult.warn("网点["+dispSite.getSiteName()+"]不存在或已关闭派件！");
            }
        }
        return RetResult.success();
    }
}
