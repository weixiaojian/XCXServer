package com.zhitengda.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.zhitengda.entity.*;
import com.zhitengda.service.IndexService;
import com.zhitengda.service.OrderService;
import com.zhitengda.service.UserService;
import com.zhitengda.util.CharacterUtils;
import com.zhitengda.util.RetResult;
import com.zhitengda.util.ZtdAESUtils;
import com.zhitengda.vo.*;
import com.zhitengda.web.exception.GlobalException;
import com.zhitengda.wx.WXConfig;
import com.zhitengda.ztdCloud.CloudOrderUtil;
import com.zhitengda.ztdCloud.MatchAddressUtil;
import com.zhitengda.ztdCloud.MatchSiteEmpUtil;
import com.zhitengda.ztdCloud.cloudVo.MatchAddress.MatchAddressLisrResponse;
import com.zhitengda.ztdCloud.cloudVo.MatchAddress.MatchAddressResponseData;
import com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp.MatchSiteEmpRequest;
import com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp.MatchSiteEmpResponse;
import com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp.MatchSiteEmpResponseData;
import com.zhitengda.ztdCloud.cloudVo.cloudOrder.CloudOrderRequest;
import com.zhitengda.ztdCloud.cloudVo.cloudOrder.CloudOrderResponse;
import com.zhitengda.ztdCloud.cloudVo.cloudOrder.CloudOrderResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;


/**
 * @author langao_q
 * @since 2021-01-31 17:29
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController extends BaseControoler{

    //下单常量字典编码
    private final String DIC_CODE = "Order_constant";

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private IndexService indexService;

    /**
     * 获取下单常量字典配置
     * @return
     */
    @GetMapping("/getOrderConstant")
    public RetResult getOrderConstant(){
        //1.获取下单常量列表
        List<OrderConstantDto> list = orderService.getOrderConstant(DIC_CODE);
        //2.封装成map返回
        Map<String, Object> map = new HashMap<>();
        for(OrderConstantDto oc : list){
            if("保价金额上限".equals(oc.getDicValueName())){
                map.put("insuredValue", oc.getDicValue());
            }else if("散客多件配置表".equals(oc.getDicValueName())){
                map.put("number", oc.getDicValue());
            }else if("下单默认重量".equals(oc.getDicValueName())){
                map.put("weight", oc.getDicValue());
            }else if("保价金额特殊品".equals(oc.getDicValueName())){
                map.put("specialValue", oc.getDicValue());
            }
        }
        return RetResult.success(map);
    }

    /**
     * 计算运费/到付款
     * @param trackFreight
     * @return
     */
    @PostMapping("/trackFreight")
    public RetResult trackFreight(@RequestBody @Validated TrackFreightVo trackFreight){
        BigDecimal  freight = orderService.trackFreight(trackFreight);
        return RetResult.success(freight);
    }


    /**
     * 保存订单数据
     * 1.校验运单号是否使用
     * 2.获取单号
     * 3.调用匹配揽派接口
     *  3.1揽件件网点/派件网点赋值，为空则赋值 “*”
     * 4.调用云下单接口
     * @param order 订单数据
     * @return 下单结果
     */
    @PostMapping("/saveOrder")
    public RetResult saveOrder(@RequestBody @Validated Order order){
        Site sendSite = null;
        MatchSiteEmpRequest matchSiteEmpRequest = new MatchSiteEmpRequest();
        ArrayList<MatchSiteEmpRequest> matchSiteEmpRequestList = new ArrayList<>();
        MatchSiteEmpResponse matchRes;
        MatchSiteEmpResponseData matchResData;
        CloudOrderResponse orderRes;
        CloudOrderResponseData orderResData;
        CloudOrderRequest request = new CloudOrderRequest();
        List<CloudOrderRequest> requestList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            log.info("【下单请求Info："+ Thread.currentThread().getId() +"】" + JSONUtil.toJsonStr(order));
            //1.校验单号是否使用
            if(StrUtil.isNotBlank(order.getBillCode())){
                orderService.checkBillCode(order.getBillCode());
            }
            //2.1获取单号
            String orderBill = orderService.getOrderCode("WX");
            //2.2判断一下预制二维码下单是否有匹配网点 如果有则匹配揽派接口增加sendSite、sendSiteCode参数
            if(StrUtil.isNotBlank(order.getBillCode())){
                sendSite = orderService.getSendSiteByBillCode(order.getBillCode());
                if(sendSite == null){
                    throw new GlobalException("运单号["+order.getBillCode()+"]未绑定二维码号段！");
                }else{
                    matchSiteEmpRequest.setSendSite(sendSite.getSiteName());
                    matchSiteEmpRequest.setSendSiteCode(sendSite.getSiteCode());
                }
            }
            //2.3获取当前操作用户的数据
            WxUser wxUser = userService.findByOpneId(getToken());
            //3.调用匹配揽派接口
            matchSiteEmpRequest.setId(String.valueOf(System.currentTimeMillis()));
            matchSiteEmpRequest.setOrderBillCode(orderBill);
            matchSiteEmpRequest.setMatchingType(0);
            matchSiteEmpRequest.setSendProvince(order.getSendProvince());
            matchSiteEmpRequest.setSendCity(order.getSendCity());
            matchSiteEmpRequest.setSendCounty(order.getSendCounty());
            matchSiteEmpRequest.setSendTown(order.getSendTown());
            matchSiteEmpRequest.setSendManAddress(order.getSendManAddress());
            matchSiteEmpRequest.setAcceptProvince(order.getAcceptProvince());
            matchSiteEmpRequest.setAcceptCity(order.getAcceptCity());
            matchSiteEmpRequest.setAcceptCounty(order.getAcceptCounty());
            matchSiteEmpRequest.setAcceptTown(order.getAcceptTown());
            matchSiteEmpRequest.setAcceptManAddress(order.getAcceptManAddress());
            //保价金额、代收货款、到付款、件数校验
            matchSiteEmpRequest.setInsuredValue(order.getInsuredValue());
            matchSiteEmpRequest.setGoodsPayment(order.getGoodsPayment());
            matchSiteEmpRequest.setTopayment(order.getTopayment());
            matchSiteEmpRequest.setPackingPiece(order.getPackingPiece());
            //如果是专属业务员下单，直接把网点和专属业务员传到匹配接口
            if(StrUtil.isNotBlank(order.getSendSite()) && StrUtil.isNotBlank(order.getSendSiteCode())
                && StrUtil.isNotBlank(order.getRecMan()) && StrUtil.isNotBlank(order.getRecManCode())){
                matchSiteEmpRequest.setSendSite(order.getSendSite());
                matchSiteEmpRequest.setSendSiteCode(order.getSendSiteCode());
                matchSiteEmpRequest.setTakePieceEmployee(order.getRecMan());
                matchSiteEmpRequest.setTakePieceEmployeeCode(order.getRecManCode());
            }
            //调用揽派匹配接口
            matchSiteEmpRequestList.add(matchSiteEmpRequest);
            matchRes = MatchSiteEmpUtil.YdMatch(matchSiteEmpRequestList);
            if (1 != matchRes.getCode() || CollUtil.isEmpty(matchRes.getData())) {
                throw new GlobalException("揽派匹配异常:" + matchRes.getMsg());
            }
            matchResData = matchRes.getData().get(0);
            if (1 != matchResData.getDispatchSuccess()) {
                throw new GlobalException("揽派服务收件地址匹配异常:" + matchResData.getDispatchMsg());
            }
            if (StrUtil.isBlank(matchResData.getDispatchSiteCode())) {
                throw new GlobalException("揽派服务收件地址匹配异常：该地址未开通服务(不支持派送)！" );
            }
            //此处优先级：预制、专属、团队、月结
            if(sendSite != null && StrUtil.isNotBlank(order.getBillCode())){
                //判断一下预制二维码下单是否有匹配网点 有的话直接复制给揽件网点
                matchResData.setSendSiteName(sendSite.getSiteName());
                matchResData.setSendSiteCode(sendSite.getSiteCode());
            }else if(StrUtil.isNotBlank(order.getSendSite()) && StrUtil.isNotBlank(order.getSendSiteCode())
                        && StrUtil.isNotBlank(order.getRecMan()) && StrUtil.isNotBlank(order.getRecManCode())){
                //判断一下是否是专属下单 有的话直接赋值给揽件网点和员工
                matchResData.setSendSiteName(order.getSendSite());
                matchResData.setSendSiteCode(order.getSendSiteCode());
                matchResData.setSendEmployeeName(order.getRecMan());
                matchResData.setSendEmployeeCode(order.getRecManCode());
            }else if(StrUtil.isNotBlank(order.getGroupId()) && StrUtil.isNotBlank(order.getGroupMember())){
                //判断一下是否是团队模式下单 且寄件网点不为空 直接赋值给寄件网点
                if(StrUtil.isBlank(order.getSendSiteCode()) || StrUtil.isBlank(order.getSendSite())){
                    throw new GlobalException("团队模式下寄件网点不能为空：" + order.getGroupId());
                }
                matchResData.setSendSiteName(order.getSendSite());
                matchResData.setSendSiteCode(order.getSendSiteCode());
            }else if(StrUtil.isNotBlank(wxUser.getCustomerCode()) && StrUtil.isNotBlank(wxUser.getCustomerName())){
                //判断一下是否月结客户下单
                if(!"月结".equals(order.getPaymentType())){
                    throw new GlobalException("用户绑定了月结客户 付款方式只能是月结！");
                }
                if(StrUtil.isEmpty(order.getCustomerCode())){
                    throw new GlobalException("用户绑定了月结客户 未收到客户数据！");
                }
                //查询客户所属网点 接赋值给揽件网点
                Customer cust = userService.findCustomerByCode(order.getCustomerCode());
                if(cust == null){
                    throw new GlobalException("未查询到客户数据或客户已停用！");
                }
                matchResData.setSendSiteName(cust.getCustomerOwnerSite());
                matchResData.setSendSiteCode(cust.getCustomerOwnerSiteCode());
            }else if(StrUtil.isNotEmpty(matchResData.getSendSiteCode())){
                //不是预制、不是专属、不是团队、不是月结、且匹配到寄件网点 则判断一下寄件地址匹配到的网点是否有面单库存 如果没有库存的话 寄件网点直接赋值为空
                Map<String,Object> siteInventory = orderService.checkInventory(matchResData.getSendSiteCode());
                if(siteInventory.isEmpty() || siteInventory.get("QUANTITY") == null || ((BigDecimal)siteInventory.get("QUANTITY")).intValue() < 1){
                    matchResData.setSendSiteName("");
                    matchResData.setSendSiteCode("");
                    matchResData.setSendEmployeeName("");
                    matchResData.setSendEmployeeCode("");
                    log.info("匹配到寄件网点没有库存："+matchResData.getSendSiteCode());
                }
            }

            //3.1揽件件网点/派件网点赋值，为空则赋值 “*”
            request.setSendSite(matchResData.getSendSiteName());
            request.setSendSiteCode(matchResData.getSendSiteCode());
            request.setRecMan(matchResData.getSendEmployeeName());
            request.setRecManCode(matchResData.getSendEmployeeCode());
            request.setRegisterSite(matchResData.getSendSiteName());
            request.setRegisterSiteCode(matchResData.getSendSiteCode());
            request.setRegisterMan(StrUtil.isNotBlank(matchResData.getSendEmployeeName())?matchResData.getSendEmployeeName():"微信");
            request.setRegisterManCode(StrUtil.isNotBlank(matchResData.getSendEmployeeCode())?matchResData.getSendEmployeeCode():"微信");
            //3.2四段码赋值
            request.setOneCode(matchResData.getBigPen());
            request.setTwoCode(matchResData.getChequer());
            request.setThreeCode(matchResData.getDeliveryCode());
            request.setFourCode(matchResData.getDispatchAreaName());

            request.setDispatchSite(matchResData.getDispatchSiteName());
            request.setDispatchSiteCode(matchResData.getDispatchSiteCode());
            request.setDispatchMan(matchResData.getDispatchEmployeeName());
            request.setDispatchManCode(matchResData.getDispatchAdminEmployeeCode());
            request.setSendSiteType(matchResData.getSendMatchingType());
            request.setDispatchSiteType(matchResData.getDispatchMatchingType());
            request.setRegisterDate(DateUtil.now());

            //4.调用云下单接口
            request.setOrderBill(orderBill);
            request.setOpenId(getToken());
            request.setBillCode(order.getBillCode());
            request.setSendProvince(order.getSendProvince());
            request.setSendCity(order.getSendCity());
            request.setSendCounty(order.getSendCounty());
            request.setSendTown(order.getSendTown());
            request.setSendManAddress(CharacterUtils.replacePrintStr(order.getSendManAddress()));
            request.setSendMan(order.getSendMan());
            request.setSendManMobile(order.getSendManMobile());
            request.setSendManCompany(order.getSendManCompany());
            request.setAcceptProvince(order.getAcceptProvince());
            request.setAcceptCity(order.getAcceptCity());
            request.setAcceptCounty(order.getAcceptCounty());
            request.setAcceptTown(order.getAcceptTown());
            request.setAcceptManAddress(CharacterUtils.replacePrintStr(order.getAcceptManAddress()));
            request.setAcceptMan(order.getAcceptMan());
            request.setAcceptManMobile(order.getAcceptManMobile());
            request.setAcceptManCompany(order.getAcceptManCompany());
            request.setPaymentType(order.getPaymentType());
            request.setPackingPiece(order.getPackingPiece());
            request.setGoodsWeight(order.getGoodsWeight());
            request.setGoodsType(order.getGoodsType());
            request.setGoodsName(order.getGoodsName());
            request.setDataFrom("微信");
            request.setCustomerDeliveryBeginTime(order.getCustomerDeliveryBeginTime());
            request.setCustomerDeliveryEndTime(order.getCustomerDeliveryEndTime());
            request.setRemark(CharacterUtils.replacePrintStr(order.getRemark()));
            request.setFreight(order.getFreight());
            request.setInsuredValue(order.getInsuredValue());
            request.setGroupId(order.getGroupId());
            request.setGroupMember(order.getGroupMember());
            request.setCustomerCode(order.getCustomerCode());
            request.setCustomerName(order.getCustomerName());
            request.setProductType(order.getProductType());
            //4.1校验寄件网点是否关闭揽收、是否欠费；派件网点是否关闭派件
            RetResult checkResult = orderService.checkSendSiteAndDispSite(request.getSendSiteCode(), request.getDispatchSiteCode());
            if(200 != checkResult.getCode()){
                throw new GlobalException(checkResult.getMessage());
            }
            //4.2获取用户实名数据
            WxReal wxReal = new WxReal();
            wxReal.setOpenId(getToken());
            WxReal realId = userService.findRealId(wxReal);
            if(realId != null){
                request.setRealIdCode(realId.getRealIdCode());
                request.setRealIdType(realId.getRealIdType());
                request.setRealName(realId.getRealName());
            }
            //5.1请求下单接口
            requestList.add(request);
            orderRes = CloudOrderUtil.addOrder(requestList);
            //5.2解析下单结果
            if (!"10000".equals(orderRes.getCode()) || CollUtil.isEmpty(orderRes.getResultList())) {
                throw new GlobalException("云下单失败:" + orderRes.getSub_msg());
            }
            orderResData = orderRes.getResultList().get(0);
            if (!orderResData.isSuccess()) {
                throw new GlobalException("云下单失败:" + orderResData.getMsg());
            }
            long end = System.currentTimeMillis();
            log.info("【下单响应Info："+ Thread.currentThread().getId() +"，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(orderResData));
            return RetResult.success(orderBill);
        }catch (Exception e){
            log.error("【下单异常，耗时"+(System.currentTimeMillis() - start)+"毫秒】" , e);
            return RetResult.warn("下单失败：" + e.getMessage());
        }
    }

    /**
     * 批量下单接口
     * 1.获取单号，揽派匹配参数封装，将订单数据转换为map方便后面查询失败的单（key为orderBill）
     * 2.调用匹配揽派接口（批量）
     *  2.1将揽派匹配的结果转换成map 订单号orderBill为key
     * 3.将揽派匹配结果回填到订单中、填充云下单接口所需参数
     *  3.1揽件件网点/派件网点赋值，为空则赋值 “*”
     * 4.云下单接口参数准备
     *  4.1获取用户实名数据
     *  4.2调用云下单接口
     *  4.3调用云下单返回解析（找出失败的订单）
     * @param orderList 订单集合数据
     * @return 下单结果
     */
    @PostMapping("/saveOrderList")
    public RetResult saveOrderList(@RequestBody @Validated ValidList<Order> orderList){
        ArrayList<MatchSiteEmpRequest> matchSiteEmpRequestList = new ArrayList<>();
        HashMap<String, MatchSiteEmpResponseData> matchResDataMap = new HashMap<>();
        MatchSiteEmpResponse matchRes;
        List<MatchSiteEmpResponseData> matchResDataList;
        CloudOrderResponse orderRes;
        List<CloudOrderResponseData> orderResDataList;
        List<CloudOrderRequest> requestlist = new ArrayList<>();
        HashMap<String, Order> orderMap = new HashMap<>();
        ArrayList<Order> successOrderList = new ArrayList<>();
        ArrayList<Order> errorOrderList = new ArrayList<>();
        HashMap<String, Object> resultMap = new HashMap<>();
        long start = System.currentTimeMillis();
        try {
            log.info("【批量下单请求Info："+ Thread.currentThread().getId() +"】" + JSONUtil.toJsonStr(orderList));
            //1.获取单号，揽派匹配参数封装，将订单数据转换为map方便后面查询失败的单（key为orderBill）
            orderList.forEach(order->{
                String orderBill = orderService.getOrderCode("WX");
                order.setOrderBill(orderBill);

                MatchSiteEmpRequest matchSiteEmpRequest = new MatchSiteEmpRequest();
                matchSiteEmpRequest.setId(orderBill);
                matchSiteEmpRequest.setMatchingType(0);
                matchSiteEmpRequest.setSendProvince(order.getSendProvince());
                matchSiteEmpRequest.setSendCity(order.getSendCity());
                matchSiteEmpRequest.setSendCounty(order.getSendCounty());
                matchSiteEmpRequest.setSendTown(order.getSendTown());
                matchSiteEmpRequest.setSendManAddress(order.getSendManAddress());
                matchSiteEmpRequest.setAcceptProvince(order.getAcceptProvince());
                matchSiteEmpRequest.setAcceptCity(order.getAcceptCity());
                matchSiteEmpRequest.setAcceptCounty(order.getAcceptCounty());
                matchSiteEmpRequest.setAcceptTown(order.getAcceptTown());
                matchSiteEmpRequest.setAcceptManAddress(order.getAcceptManAddress());
                matchSiteEmpRequestList.add(matchSiteEmpRequest);

                orderMap.put(orderBill, order);
            });
            //2.调用匹配揽派接口（批量）
            matchRes = MatchSiteEmpUtil.YdMatch(matchSiteEmpRequestList);
            if (1 != matchRes.getCode() || CollUtil.isEmpty(matchRes.getData())) {
                throw new GlobalException("揽派匹配异常:" + matchRes.getMsg());
            }
            matchResDataList = matchRes.getData();
            //2.1将揽派匹配的结果转换成map 数据里的id为key
            matchResDataList.forEach(matchResData->{
                matchResDataMap.put(matchResData.getId(), matchResData);
            });
            //2.2获取当前操作用户的数据
            WxUser wxUser = userService.findByOpneId(getToken());
            //3.将揽派匹配结果回填到订单中、填充云下单接口所需参数
            for (Order order : orderList) {
                MatchSiteEmpResponseData matchResData = matchResDataMap.get(order.getOrderBill());
                if(matchResData == null){
                    order.setErrMsg("没有揽派匹配结果！");
                    errorOrderList.add(order);
                    continue;
                }
                if (1 != matchResData.getDispatchSuccess()) {
                    order.setErrMsg("揽派服务收件地址匹配异常:" + matchResData.getDispatchMsg());
                    errorOrderList.add(order);
                    continue;
                }
                if (StrUtil.isBlank(matchResData.getDispatchSiteCode())) {
                    order.setErrMsg("揽派服务收件地址匹配异常：该地址未开通服务(不支持派送)！");
                    errorOrderList.add(order);
                    continue;
                }
                //判断一下是否是团队模式下单 且寄件网点不为空 直接赋值给寄件网点
                if(StrUtil.isNotBlank(order.getGroupId()) && StrUtil.isNotBlank(order.getGroupMember())){
                    if(StrUtil.isBlank(order.getSendSiteCode()) || StrUtil.isBlank(order.getSendSite())){
                        throw new GlobalException("团队模式下寄件网点不能为空：" + order.getGroupId());
                    }
                    matchResData.setSendSiteName(order.getSendSite());
                    matchResData.setSendSiteCode(order.getSendSiteCode());
                }else if(StrUtil.isNotBlank(wxUser.getCustomerCode()) && StrUtil.isNotBlank(wxUser.getCustomerName())){
                    //判断一下是否月结客户下单
                    if(!"月结".equals(order.getPaymentType())){
                        throw new GlobalException("用户绑定了月结客户 付款方式只能是月结！");
                    }
                    if(StrUtil.isEmpty(order.getCustomerCode())){
                        throw new GlobalException("用户绑定了月结客户 未收到客户数据！");
                    }
                    //查询客户所属网点 接赋值给揽件网点
                    Customer cust = userService.findCustomerByCode(order.getCustomerCode());
                    if(cust == null){
                        throw new GlobalException("未查询到客户数据或客户已停用！");
                    }
                    matchResData.setSendSiteName(cust.getCustomerOwnerSite());
                    matchResData.setSendSiteCode(cust.getCustomerOwnerSiteCode());
                }else if(StrUtil.isNotEmpty(matchResData.getSendSiteCode())){
                    //不是团队、不是月结、且匹配到寄件网点 则判断一下寄件地址匹配到的网点是否有面单库存 如果没有库存的话 寄件网点直接赋值为空
                    Map<String,Object> siteInventory = orderService.checkInventory(matchResData.getSendSiteCode());
                    if(siteInventory.isEmpty() || siteInventory.get("QUANTITY") == null || ((BigDecimal)siteInventory.get("QUANTITY")).intValue() < 1){
                        matchResData.setSendSiteName("");
                        matchResData.setSendSiteCode("");
                        matchResData.setSendEmployeeName("");
                        matchResData.setSendEmployeeCode("");
                    }
                }

                //3.1揽件件网点/派件网点赋值，为空则赋值 “*”
                CloudOrderRequest request = new CloudOrderRequest();
                request.setSendSite(matchResData.getSendSiteName());
                request.setSendSiteCode(matchResData.getSendSiteCode());
                request.setRecMan(matchResData.getSendEmployeeName());
                request.setRecManCode(matchResData.getSendEmployeeCode());
                request.setRegisterSite(matchResData.getSendSiteName());
                request.setRegisterSiteCode(matchResData.getSendSiteCode());
                request.setRegisterMan(StrUtil.isNotBlank(matchResData.getSendEmployeeName())?matchResData.getSendEmployeeName():"微信");
                request.setRegisterManCode(StrUtil.isNotBlank(matchResData.getSendEmployeeCode())?matchResData.getSendEmployeeCode():"微信");
                //3.2四段码赋值
                request.setOneCode(matchResData.getBigPen());
                request.setTwoCode(matchResData.getChequer());
                request.setThreeCode(matchResData.getDeliveryCode());
                request.setFourCode(matchResData.getDispatchAreaName());

                request.setDispatchSite(matchResData.getDispatchSiteName());
                request.setDispatchSiteCode(matchResData.getDispatchSiteCode());
                request.setDispatchMan(matchResData.getDispatchEmployeeName());
                request.setDispatchManCode(matchResData.getDispatchAdminEmployeeCode());
                request.setSendSiteType(matchResData.getSendMatchingType());
                request.setDispatchSiteType(matchResData.getDispatchMatchingType());
                request.setRegisterDate(DateUtil.now());

                //4.云下单接口参数准备
                request.setOrderBill(order.getOrderBill());
                request.setOpenId(getToken());
                request.setBillCode(order.getBillCode());
                request.setSaveId(order.getOrderBill());
                request.setSendProvince(order.getSendProvince());
                request.setSendCity(order.getSendCity());
                request.setSendCounty(order.getSendCounty());
                request.setSendTown(order.getSendTown());
                request.setSendManAddress(CharacterUtils.replacePrintStr(order.getSendManAddress()));
                request.setSendMan(order.getSendMan());
                request.setSendManMobile(order.getSendManMobile());
                request.setSendManCompany(order.getSendManCompany());
                request.setAcceptProvince(order.getAcceptProvince());
                request.setAcceptCity(order.getAcceptCity());
                request.setAcceptCounty(order.getAcceptCounty());
                request.setAcceptTown(order.getAcceptTown());
                request.setAcceptManAddress(CharacterUtils.replacePrintStr(order.getAcceptManAddress()));
                request.setAcceptMan(order.getAcceptMan());
                request.setAcceptManMobile(order.getAcceptManMobile());
                request.setAcceptManCompany(order.getAcceptManCompany());
                request.setPaymentType(order.getPaymentType());
                request.setPackingPiece(order.getPackingPiece());
                request.setGoodsWeight(order.getGoodsWeight());
                request.setGoodsType(order.getGoodsType());
                request.setGoodsName(order.getGoodsName());
                request.setDataFrom("微信");
                request.setCustomerDeliveryBeginTime(order.getCustomerDeliveryBeginTime());
                request.setCustomerDeliveryEndTime(order.getCustomerDeliveryEndTime());
                request.setRemark(CharacterUtils.replacePrintStr(order.getRemark()));
                request.setFreight(order.getFreight());
                request.setInsuredValue(order.getInsuredValue());
                request.setGroupId(order.getGroupId());
                request.setGroupMember(order.getGroupMember());
                request.setCustomerCode(order.getCustomerCode());
                request.setCustomerName(order.getCustomerName());
                request.setProductType(order.getProductType());
                request.setStorageType(order.getStorageType());

                //4.1校验寄件网点是否关闭揽收、是否欠费；派件网点是否关闭派件
                RetResult checkResult = orderService.checkSendSiteAndDispSite(request.getSendSiteCode(), request.getDispatchSiteCode());
                if(200 != checkResult.getCode()){
                    order.setErrMsg(checkResult.getMessage());
                    errorOrderList.add(order);
                    continue;
                }
                //4.1判断匹配的目的网点是否有冷柜
                if(StrUtil.isNotBlank(order.getStorageType())){
                    Site dispSiteDb = indexService.getSite(request.getDispatchSiteCode());
                    if(dispSiteDb != null && 0 == dispSiteDb.getBlFreshtank()){
                        order.setErrMsg("["+dispSiteDb.getSiteName()+"]该目的网点没有冷柜服务，下单失败!");
                        errorOrderList.add(order);
                        continue;
                    }
                }
                //4.2获取用户实名数据
                WxReal wxReal = new WxReal();
                wxReal.setOpenId(getToken());
                WxReal realId = userService.findRealId(wxReal);
                if(realId != null){
                    request.setRealIdCode(realId.getRealIdCode());
                    request.setRealIdType(realId.getRealIdType());
                    request.setRealName(realId.getRealName());
                }
                requestlist.add(request);
            }
            //4.2调用云下单接口(下单集合不为空才去下单接口)
            if(!requestlist.isEmpty()){
                orderRes = CloudOrderUtil.addOrder(requestlist);
                if (!"10000".equals(orderRes.getCode()) || CollUtil.isEmpty(orderRes.getResultList())) {
                    throw new GlobalException("云下单失败:" + orderRes.getSub_msg());
                }
                //4.3调用云下单返回解析（找出失败的订单）
                orderResDataList = orderRes.getResultList();
                orderResDataList.forEach(orderResData->{
                    if (!orderResData.isSuccess()) {
                        Order errorOrder = orderMap.get(orderResData.getSaveId());
                        errorOrder.setErrMsg("云下单失败:" + orderResData.getMsg());
                        errorOrderList.add(errorOrder);
                    }else{
                        //成功的订单
                        Order successOrder = orderMap.get(orderResData.getSaveId());
                        successOrderList.add(successOrder);
                    }
                });
                long end = System.currentTimeMillis();
                log.info("【批量下单响应Info："+ Thread.currentThread().getId() +"，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(orderResDataList));
            }
            resultMap.put("total", orderList.size());
            resultMap.put("successTotal", successOrderList.size());
            resultMap.put("errorTotal", errorOrderList.size());
            resultMap.put("errorList", errorOrderList);
            return RetResult.success(resultMap);
        }catch (Exception e){
            log.error("【批量下单异常，耗时"+(System.currentTimeMillis() - start)+"毫秒】" , e);
            return RetResult.warn("下单失败：" + e.getMessage());
        }
    }

    /**
     * 修改订单数据
     * 1.根据订单号查询订单数据
     * 2.比较寄/收件市、区、街道、详细地址是否改变
     *  2.1如果有修改就重新调用揽派匹配接口
     * 3.付款方式改变，费用和到付款其中一个需要置未0（根据付款方式决定）
     * 4.调用云修改订单接口
     * @param order 订单数据
     * @return 修改订单结果
     */
    @PostMapping("/updateOrder")
    public RetResult updateOrder(@RequestBody @Validated Order order){
        MatchSiteEmpRequest matchSiteEmpRequest = new MatchSiteEmpRequest();
        ArrayList<MatchSiteEmpRequest> matchSiteEmpRequestList = new ArrayList<>();
        MatchSiteEmpResponse matchRes;
        MatchSiteEmpResponseData matchResData;
        CloudOrderResponse orderRes;
        CloudOrderRequest request = new CloudOrderRequest();
        long start = System.currentTimeMillis();
        try {
            log.info("【修改订单请求Info："+ Thread.currentThread().getId() +"】" + JSONUtil.toJsonStr(order));
            if(StrUtil.isEmpty(order.getOrderBill())){
                throw new GlobalException("订单号不能为空！");
            }
            //1.根据订单号查询数据
            Order orderQry = new Order();
            orderQry.setOrderBill(order.getOrderBill());
            Order orderDb = orderService.getOrderByOrderBill(orderQry);
            //1.校验用户是否有取消订单的权限
            boolean cancelOrder = orderService.checkCancelOrder(getToken(), order.getOrderBill());
            if(!cancelOrder){
                throw new GlobalException("当前用户没有权限修改该订单["+order.getOrderBill()+"]！");
            }
            //2.0获取当前操作用户的数据
            WxUser wxUser = userService.findByOpneId(getToken());
            //2.1比较寄/收件市、区、街道、详细地址是否改变（注意：街道可能为空！）
            if(!order.getSendCity().equals(orderDb.getSendCity()) || !order.getSendCounty().equals(orderDb.getSendCounty())
                    || (StrUtil.isNotBlank(order.getSendTown()) && !order.getSendTown().equals(orderDb.getSendTown()))
                    || !order.getSendManAddress().equals(orderDb.getSendManAddress())
                    || !order.getAcceptCity().equals(orderDb.getAcceptCity()) || !order.getAcceptCounty().equals(orderDb.getAcceptCounty())
                    || (StrUtil.isNotBlank(order.getAcceptTown()) && !order.getAcceptTown().equals(orderDb.getAcceptTown()))
                    || !order.getAcceptManAddress().equals(orderDb.getAcceptManAddress())){
                //2.1如果有修改就重新调用揽派匹配接口
                matchSiteEmpRequest.setId(String.valueOf(System.currentTimeMillis()));
                matchSiteEmpRequest.setOrderBillCode(order.getOrderBill());
                matchSiteEmpRequest.setMatchingType(0);
                matchSiteEmpRequest.setSendProvince(order.getSendProvince());
                matchSiteEmpRequest.setSendCity(order.getSendCity());
                matchSiteEmpRequest.setSendCounty(order.getSendCounty());
                matchSiteEmpRequest.setSendTown(order.getSendTown());
                matchSiteEmpRequest.setSendManAddress(CharacterUtils.replacePrintStr(order.getSendManAddress()));
                matchSiteEmpRequest.setAcceptProvince(order.getAcceptProvince());
                matchSiteEmpRequest.setAcceptCity(order.getAcceptCity());
                matchSiteEmpRequest.setAcceptCounty(order.getAcceptCounty());
                matchSiteEmpRequest.setAcceptTown(order.getAcceptTown());
                matchSiteEmpRequest.setAcceptManAddress(CharacterUtils.replacePrintStr(order.getAcceptManAddress()));
                //保价金额、代收货款、到付款、件数校验
                matchSiteEmpRequest.setInsuredValue(order.getInsuredValue());
                matchSiteEmpRequest.setGoodsPayment(order.getGoodsPayment());
                matchSiteEmpRequest.setTopayment(order.getTopayment());
                matchSiteEmpRequest.setPackingPiece(order.getPackingPiece());
                //2.3如果是专属业务员下单，直接把网点和专属业务员传到匹配接口（*代表下单的时候为空）
                if(StrUtil.isNotBlank(order.getSendSite()) && StrUtil.isNotBlank(order.getSendSiteCode())
                        && !"*".equals(order.getSendSite()) && !"*".equals(order.getSendSiteCode())
                        && StrUtil.isNotBlank(order.getRecMan()) && StrUtil.isNotBlank(order.getRecManCode())
                        && !"*".equals(order.getRecMan()) && !"*".equals(order.getRecManCode())){
                    matchSiteEmpRequest.setSendSite(order.getSendSite());
                    matchSiteEmpRequest.setSendSiteCode(order.getSendSiteCode());
                    matchSiteEmpRequest.setTakePieceEmployee(order.getRecMan());
                    matchSiteEmpRequest.setTakePieceEmployeeCode(order.getRecManCode());
                }
                matchSiteEmpRequestList.add(matchSiteEmpRequest);
                matchRes = MatchSiteEmpUtil.YdMatch(matchSiteEmpRequestList);
                if (1 != matchRes.getCode() || CollUtil.isEmpty(matchRes.getData())) {
                    throw new GlobalException("揽派匹配异常:" + matchRes.getMsg());
                }
                matchResData = matchRes.getData().get(0);
                if (1 != matchResData.getDispatchSuccess()) {
                    throw new GlobalException("揽派服务收件地址匹配异常:" + matchResData.getDispatchMsg());
                }
                if (StrUtil.isBlank(matchResData.getDispatchSiteCode())) {
                    throw new GlobalException("揽派服务收件地址匹配异常：该地址未开通服务(不支持派送)！" );
                }
                //此处优先级：专属、团队、月结
                if(StrUtil.isNotBlank(order.getSendSite()) && StrUtil.isNotBlank(order.getSendSiteCode())
                        && StrUtil.isNotBlank(order.getRecMan()) && StrUtil.isNotBlank(order.getRecManCode())){
                    //判断一下是否是专属下单 有的话直接赋值给揽件网点和员工
                    matchResData.setSendSiteName(order.getSendSite());
                    matchResData.setSendSiteCode(order.getSendSiteCode());
                    matchResData.setSendEmployeeName(order.getRecMan());
                    matchResData.setSendEmployeeCode(order.getRecManCode());
                }else if(StrUtil.isNotBlank(order.getGroupId()) && StrUtil.isNotBlank(order.getGroupMember())){
                    //判断一下是否是团队模式下单 且寄件网点不为空 直接赋值给寄件网点
                    if(StrUtil.isBlank(order.getSendSiteCode()) || StrUtil.isBlank(order.getSendSite())){
                        throw new GlobalException("团队模式下寄件网点不能为空：" + order.getGroupId());
                    }
                    matchResData.setSendSiteName(order.getSendSite());
                    matchResData.setSendSiteCode(order.getSendSiteCode());
                }else if(StrUtil.isNotBlank(wxUser.getCustomerCode()) && StrUtil.isNotBlank(wxUser.getCustomerName())){
                    //判断一下是否月结客户下单
                    if("月结".equals(order.getPaymentType())){
                        throw new GlobalException("用户绑定了月结客户 付款方式只能是月结！");
                    }
                    if(StrUtil.isEmpty(order.getCustomerCode())){
                        throw new GlobalException("用户绑定了月结客户 未收到客户数据！");
                    }
                    //查询客户所属网点 接赋值给揽件网点
                    Customer cust = userService.findCustomerByCode(order.getCustomerCode());
                    if(cust == null){
                        throw new GlobalException("未查询到客户数据或客户已停用！");
                    }
                    matchResData.setSendSiteName(cust.getCustomerOwnerSite());
                    matchResData.setSendSiteCode(cust.getCustomerOwnerSiteCode());
                }else if(StrUtil.isNotEmpty(matchResData.getSendSiteCode())){
                    //不是专属、不是团队、不是月结、且匹配到寄件网点 则判断一下寄件地址匹配到的网点是否有面单库存 如果没有库存的话 寄件网点直接赋值为空
                    Map<String,Object> siteInventory = orderService.checkInventory(matchResData.getSendSiteCode());
                    if(siteInventory.isEmpty() || siteInventory.get("QUANTITY") == null || ((BigDecimal)siteInventory.get("QUANTITY")).intValue() < 1){
                        matchResData.setSendSiteName("");
                        matchResData.setSendSiteCode("");
                        matchResData.setSendEmployeeName("");
                        matchResData.setSendEmployeeCode("");
                        log.info("匹配到寄件网点没有库存："+matchResData.getSendSiteCode());
                    }
                }
                //2.3揽件件网点、订单录入网点，为空则赋值 “*”
                request.setSendSite(matchResData.getSendSiteName());
                request.setSendSiteCode(matchResData.getSendSiteCode());
                request.setRecMan(matchResData.getSendEmployeeName());
                request.setRecManCode(matchResData.getSendEmployeeCode());
                request.setRegisterSite(matchResData.getSendSiteName());
                request.setRegisterSiteCode(matchResData.getSendSiteCode());
                request.setRegisterMan(StrUtil.isNotBlank(matchResData.getSendEmployeeName())?matchResData.getSendEmployeeName():"微信");
                request.setRegisterManCode(StrUtil.isNotBlank(matchResData.getSendEmployeeCode())?matchResData.getSendEmployeeCode():"微信");
                //3.1四段码赋值
                request.setOneCode(matchResData.getBigPen());
                request.setTwoCode(matchResData.getChequer());
                request.setThreeCode(matchResData.getDeliveryCode());
                request.setFourCode(matchResData.getDispatchAreaName());

                request.setDispatchSite(matchResData.getDispatchSiteName());
                request.setDispatchSiteCode(matchResData.getDispatchSiteCode());
                request.setDispatchMan(matchResData.getDispatchEmployeeName());
                request.setDispatchManCode(matchResData.getDispatchAdminEmployeeCode());
                request.setSendSiteType(matchResData.getSendMatchingType());
                request.setDispatchSiteType(matchResData.getDispatchMatchingType());
            }

            //4.调用云下单接口
            request.setOrderBill(order.getOrderBill());
            //request.setOpenId(order.getOpenId());
            request.setBillCode(order.getBillCode());
            request.setSendCity(order.getSendCity());
            request.setSendCounty(order.getSendCounty());
            request.setSendTown(order.getSendTown());
            request.setSendManAddress(order.getSendManAddress());
            request.setSendMan(order.getSendMan());
            request.setSendManCompany(order.getSendManCompany());
            request.setSendManMobile(order.getSendManMobile());
            request.setAcceptCity(order.getAcceptCity());
            request.setAcceptCounty(order.getAcceptCounty());
            request.setAcceptTown(order.getAcceptTown());
            request.setAcceptManAddress(order.getAcceptManAddress());
            request.setAcceptMan(order.getAcceptMan());
            request.setAcceptManMobile(order.getAcceptManMobile());
            request.setAcceptManCompany(order.getAcceptManCompany());
            request.setPaymentType(order.getPaymentType());
            request.setPackingPiece(order.getPackingPiece());
            request.setGoodsWeight(order.getGoodsWeight());
            request.setGoodsType(order.getGoodsType());
            request.setGoodsName(order.getGoodsName());
            request.setDataFrom("微信");
            request.setCustomerDeliveryBeginTime(order.getCustomerDeliveryBeginTime());
            request.setCustomerDeliveryEndTime(order.getCustomerDeliveryEndTime());
            request.setRemark(CharacterUtils.replacePrintStr(order.getRemark()));
            request.setFreight(order.getFreight());
            request.setInsuredValue(order.getInsuredValue());
            request.setGroupId(order.getGroupId());
            request.setGroupMember(order.getGroupMember());
            request.setProductType(order.getProductType());
            //4.1校验寄件网点是否关闭揽收、是否欠费；派件网点是否关闭派件
            RetResult checkResult = orderService.checkSendSiteAndDispSite(request.getSendSiteCode(), request.getDispatchSiteCode());
            if(200 != checkResult.getCode()){
                throw new GlobalException(checkResult.getMessage());
            }
            //4.2获取用户实名数据
            WxReal wxReal = new WxReal();
            wxReal.setOpenId(getToken());
            WxReal realId = userService.findRealId(wxReal);
            if(realId != null){
                request.setRealIdCode(realId.getRealIdCode());
                request.setRealIdType(realId.getRealIdType());
                request.setRealName(realId.getRealName());
            }

            //修改所需参数
            request.setModifier("微信");
            request.setModifierCode("微信订单修改");
            request.setModifySite("微信");
            request.setModifySiteCode("微信");
            orderRes = CloudOrderUtil.editOrder(request);
            if (!"10000".equals(orderRes.getCode())) {
                throw new GlobalException("云修改订单失败:" + orderRes.getSub_msg());
            }
            long end = System.currentTimeMillis();
            log.info("【修改订单响应Info："+ Thread.currentThread().getId() +"，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(orderRes));
            return RetResult.success();
        }catch (Exception e){
            log.error("【修改订单异常，耗时"+(System.currentTimeMillis() - start)+"毫秒】" , e);
            return RetResult.warn("修改订单失败：" + e.getMessage());
        }
    }

    /**
     * 复制原有订单信息生成新的订单
     * @param orderBill
     * @return
     */
    @GetMapping("/copyOrder")
    public RetResult copyOrder(@RequestParam String orderBill){
        CloudOrderResponse orderRes;
        CloudOrderResponseData orderResData;
        CloudOrderRequest request = new CloudOrderRequest();
        List<CloudOrderRequest> requestList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            //1.获取原有的订单信息
            Order orderQry = new Order();
            orderQry.setOrderBill(orderBill);
            Order orderDb = orderService.getOrderByOrderBill(orderQry);
            //2.重新调用云下单接口
            if(orderDb == null){
                throw new GlobalException("未查询到订单数据：" + orderBill);
            }
            log.info("【复制订单开始["+orderBill+"]："+ Thread.currentThread().getId() +"】" + JSONUtil.toJsonStr(orderDb));
            //2.1获取单号
            String orderBillNew = orderService.getOrderCode("WX");
            //2.2获取当前操作用户的数据
            WxUser wxUser = userService.findByOpneId(getToken());
            //3.1揽件件网点/派件网点赋值，为空则赋值 “*”
            request.setSendSite(orderDb.getSendSite());
            request.setSendSiteCode(orderDb.getSendSiteCode());
            request.setRecMan(orderDb.getRecMan());
            request.setRecManCode(orderDb.getRecManCode());
            request.setRegisterSite(orderDb.getRegisterSite());
            request.setRegisterSiteCode(orderDb.getRegisterSiteCode());
            request.setRegisterMan(orderDb.getRegisterMan());
            request.setRegisterManCode(orderDb.getRegisterManCode());

            request.setDispatchSite(orderDb.getDispatchSite());
            request.setDispatchSiteCode(orderDb.getDispatchSiteCode());
            request.setDispatchMan(orderDb.getDispatchMan());
            request.setDispatchManCode(orderDb.getDispatchManCode());
            request.setSendSiteType(orderDb.getSendSiteType());
            request.setDispatchSiteType(orderDb.getDispatchSiteType());
            request.setRegisterDate(DateUtil.now());

            //4.调用云下单接口
            request.setOrderBill(orderBillNew);
            request.setOpenId(getToken());
            request.setSendProvince(orderDb.getSendProvince());
            request.setSendCity(orderDb.getSendCity());
            request.setSendCounty(orderDb.getSendCounty());
            request.setSendTown(orderDb.getSendTown());
            request.setSendManAddress(CharacterUtils.replacePrintStr(orderDb.getSendManAddress()));
            request.setSendMan(orderDb.getSendMan());
            request.setSendManMobile(orderDb.getSendManMobile());
            request.setSendManCompany(orderDb.getSendManCompany());
            request.setAcceptProvince(orderDb.getAcceptProvince());
            request.setAcceptCity(orderDb.getAcceptCity());
            request.setAcceptCounty(orderDb.getAcceptCounty());
            request.setAcceptTown(orderDb.getAcceptTown());
            request.setAcceptManAddress(CharacterUtils.replacePrintStr(orderDb.getAcceptManAddress()));
            request.setAcceptMan(orderDb.getAcceptMan());
            request.setAcceptManMobile(orderDb.getAcceptManMobile());
            request.setAcceptManCompany(orderDb.getAcceptManCompany());
            request.setPaymentType(orderDb.getPaymentType());
            request.setPackingPiece(orderDb.getPackingPiece());
            request.setGoodsWeight(orderDb.getGoodsWeight());
            request.setGoodsType(orderDb.getGoodsType());
            request.setGoodsName(orderDb.getGoodsName());
            request.setDataFrom("微信");
            request.setCustomerDeliveryBeginTime(orderDb.getCustomerDeliveryBeginTime());
            request.setCustomerDeliveryEndTime(orderDb.getCustomerDeliveryEndTime());
            request.setRemark(CharacterUtils.replacePrintStr(orderDb.getRemark()));
            request.setFreight(orderDb.getFreight());
            request.setInsuredValue(orderDb.getInsuredValue());
            request.setProductType(orderDb.getProductType());

            //4.1获取用户实名数据
            request.setRealIdCode(orderDb.getRealIdCode());
            request.setRealIdType(orderDb.getRealIdType());
            request.setRealName(orderDb.getRealName());

            //4.2查询当前操作人的团队数据 并赋值到订单中(要区分团队模式和个人模式)
            if(StrUtil.isNotBlank(orderDb.getGroupId())){
                //团队模式下的再来一单
                WxGroup wxGroup = indexService.getWxGroupByOpenId(getToken());
                request.setGroupId(wxGroup.getGroupId());
                request.setGroupMember(wxGroup.getNickName());
                if(StrUtil.isNotBlank(wxGroup.getCustomerCode())){
                    request.setCustomerCode(wxGroup.getCustomerCode());
                    request.setCustomerName(wxGroup.getCustomerName());
                }
            }else if(StrUtil.isNotBlank(wxUser.getCustomerCode()) && StrUtil.isNotBlank(wxUser.getCustomerName())){
                //判断一下是否月结客户下单：查询客户所属网点 接赋值给揽件网点、客户
                Customer cust = userService.findCustomerByCode(wxUser.getCustomerCode());
                if(cust == null){
                    throw new GlobalException("未查询到客户数据或客户已停用！");
                }
                request.setSendSite(cust.getCustomerOwnerSite());
                request.setSendSiteCode(cust.getCustomerOwnerSiteCode());
                request.setCustomerCode(cust.getCustomerCode());
                request.setCustomerName(cust.getCustomerName());
            }

            //5.调用网关下单接口
            requestList.add(request);
            orderRes = CloudOrderUtil.addOrder(requestList);
            //5.1解析下单结果
            if (!"10000".equals(orderRes.getCode()) || CollUtil.isEmpty(orderRes.getResultList())) {
                throw new GlobalException("云下单失败:" + orderRes.getSub_msg());
            }
            orderResData = orderRes.getResultList().get(0);
            if (!orderResData.isSuccess()) {
                throw new GlobalException("云下单失败:" + orderResData.getMsg());
            }
            long end = System.currentTimeMillis();
            log.info("【复制订单响应Info："+ Thread.currentThread().getId() +"，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(orderResData));
            return RetResult.success(orderBillNew);
        }catch (Exception e){
            log.error("【复制订单异常，耗时"+(System.currentTimeMillis() - start)+"毫秒】" , e);
            return RetResult.warn("下单失败：" + e.getMessage());
        }
    }

    /**
     * 取消订单
     * @param order
     * @return
     */
    @PostMapping("/cancelOrder")
    public RetResult cancelOrder(@RequestBody Order order){
        CloudOrderResponse orderRes;
        CloudOrderRequest request = new CloudOrderRequest();
        long start = System.currentTimeMillis();
        try {
            log.info("【取消订单请求Info："+ Thread.currentThread().getId() +"】" + JSONUtil.toJsonStr(order));
            if(StrUtil.isEmpty(order.getOrderBill())){
                throw new GlobalException("订单号不能为空！");
            }
            //1.校验用户是否有取消订单的权限
            boolean cancelOrder = orderService.checkCancelOrder(getToken(), order.getOrderBill());
            if(!cancelOrder){
                throw new GlobalException("当前用户没有权限取消该订单["+order.getOrderBill()+"]！");
            }
            //4.调用云下单接口
            request.setOrderBill(order.getOrderBill());
            request.setOpenId(getToken());
            request.setCancelReason(order.getCancelReason());
            request.setModifier("微信");
            request.setModifierCode("微信订单修改");
            request.setModifySite("微信");
            request.setModifySiteCode("微信");
            request.setDataFrom("微信");
            orderRes = CloudOrderUtil.cancelOrder(request);
            if (!"10000".equals(orderRes.getCode())) {
                throw new GlobalException("云取消订单失败:" + orderRes.getSub_msg());
            }
            long end = System.currentTimeMillis();
            log.info("【取消订单响应Info："+ Thread.currentThread().getId() +"，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(orderRes));
            return RetResult.success();
        }catch (Exception e){
            log.error("【取消订单异常，耗时"+(System.currentTimeMillis() - start)+"毫秒】" , e);
            return RetResult.warn("取消订单失败：" + e.getMessage());
        }
    }

    /**
     * excel下单模板下载
     * http://127.0.0.1:8081/YDTCPXCXServer/order/excelTemplate/快递寄件模板.xlsx
     * @param response
     * @param fileName
     * @return
     */
    @GetMapping("/excelTemplate/{fileName}")
    public void downOrderExcel(HttpServletResponse response, @PathVariable String fileName) throws IOException {
        // 设置信息给客户端不解析
        String type = new MimetypesFileTypeMap().getContentType(fileName);
        //设置为响应头
        response.setHeader("content-type", type);
        response.setContentType("application/octet-stream");
        byte[] buff = new byte[1024];
        //创建缓冲输入流
        BufferedInputStream bis = null;
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            //这个路径为待下载文件的路径
            bis = new BufferedInputStream(new FileInputStream(new File(wxConfig.getFilePath() + fileName)));
            int read = bis.read(buff);
            //通过while循环写入到指定了的文件夹中
            while (read != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                read = bis.read(buff);
            }
        } catch (IOException e) {
            log.error("下载失败：", e);
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        log.info("下载成功：{}", fileName);
    }

    /**
     * excel导入下单解析
     * @param request
     * @param file
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/importExcel")
    public RetResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        List<String> addrRequest = new ArrayList<>();
        MatchAddressLisrResponse addrResponse;
        List<MatchAddressResponseData> addrDataResponseList;
        HashMap<String, MatchAddressResponseData> addrDataResponseMap = new HashMap<>();
        ArrayList<ExcelOrderDto> resultList = new ArrayList<>();
        ArrayList<ExcelOrderDto> successList = new ArrayList<>();
        ArrayList<ExcelOrderDto> errList = new ArrayList<>();
        HashMap<String, Object> resultMap = new HashMap<>();
        try {
            //1.文件校验
            if(file.isEmpty()){
                throw new GlobalException("未接收到文件！");
            }
            if(!StrUtil.contains(file.getOriginalFilename(), "xls")){
                throw new GlobalException("不是xls、xlsx文件！");
            }
            if((file.getSize() / 1048576) > 10){
                throw new GlobalException("文件大小不能超过10M！");
            }
            //2.保存文件
            //新的文件名称：时间戳 + 上传文件
            String fileName = DateUtil.format(new Date(), "yyyyMMddHHmmss") + "_" + file.getOriginalFilename();
            //设置文件保存的路径
            String destFileName = wxConfig.getFilePath() + File.separator + fileName;
            //创建文件目录
            File destFile = new File(destFileName);
            boolean mkdirs = destFile.getParentFile().mkdirs();
            //复制文件到指定位置
            file.transferTo(destFile);

            //3.开始解析文件内容
            ExcelReader reader = ExcelUtil.getReader(FileUtil.file(destFileName));
            List<List<Object>> dataList = reader.read();
            if(dataList.size() < 2 || dataList.size() > 501){
                throw new GlobalException("excel不能小于2条或超过500条！");
            }
            //从第二行开始解析
            for(int i=1; i < dataList.size(); i++){
                List<Object> excel = dataList.get(i);
                ExcelOrderDto dto = new ExcelOrderDto();
                if(excel.size() != 0 && excel.size() >= 3){
                    //姓名、手机号、地址空校验
                    if(StrUtil.isBlankIfStr(excel.get(0)) || StrUtil.isBlankIfStr(excel.get(1)) || StrUtil.isBlankIfStr(excel.get(2))){
                        dto.setMsg("第"+(i + 1)+"条数据，姓名["+excel.get(0)+"]、联系方式["+excel.get(1)+"]、地址["+excel.get(2)+"]有空值！");
                        errList.add(dto);
                        continue;
                    }
                    //去掉姓名、手机号、地址中的空格
                    excel.set(0, String.valueOf(excel.get(0)).replaceAll(" ", ""));
                    excel.set(1, String.valueOf(excel.get(1)).replaceAll(" ", ""));
                    excel.set(2, String.valueOf(excel.get(2)).replaceAll(" ", ""));
                    //姓名长度限制10
                    if(excel.get(0).toString().length() > 10){
                        dto.setMsg("第"+(i + 1)+"条数据，姓名["+excel.get(0)+"]长度超出！");
                        errList.add(dto);
                        continue;
                    }
                    //手机号校验
                    if (!String.valueOf(excel.get(1)).matches("[0-9]+")) {
                        dto.setMsg("第"+(i + 1)+"条数据：手机号不合法！");
                        errList.add(dto);
                        continue;
                    }
                    //重量限制1~60kg
                    if(excel.size() >= 5 && !StrUtil.isBlankIfStr(excel.get(4))
                            && (new BigDecimal(String.valueOf(excel.get(4))).compareTo(BigDecimal.valueOf(1)) == -1
                            || new BigDecimal(String.valueOf(excel.get(4))).compareTo(BigDecimal.valueOf(60)) == 1)){
                        dto.setMsg("第"+(i + 1)+"条数据：重量不在1~60kg区间！");
                        errList.add(dto);
                        continue;
                    }
                    //时效件限制：只能为当日件、次日件，默认当日件
                    dto.setProductType("当日件");
                    if(excel.size() >= 7){
                        if(StrUtil.isBlankIfStr(excel.get(6))){
                            dto.setProductType("当日件");
                        }else if(!"当日件".equals(excel.get(6)) && !"次日件".equals(excel.get(6))){
                            dto.setProductType("当日件");
                        }else{
                            dto.setProductType(excel.get(6).toString());
                        }
                    }
                    //冷藏类型字段 只允许：0:常温,1:冷藏,2:冷冻
                    if(excel.size() >= 8 && !StrUtil.isBlankIfStr(excel.get(7))){
                        String  storageType = excel.get(7).toString();
                        if(!"常温".equals(storageType) && !"冷藏".equals(storageType) && !"冷冻".equals(storageType)){
                            dto.setMsg("第"+(i + 1)+"条数据：冷藏类型不是常温、冷藏、冷冻等示例值！");
                            errList.add(dto);
                            continue;
                        }else{
                            if("常温".equals(storageType)){
                                dto.setStorageType("0");
                            }else if("冷藏".equals(storageType)){
                                dto.setStorageType("1");
                            }else if("冷冻".equals(storageType)){
                                dto.setStorageType("2");
                            }
                        }
                    }
                    dto.setNumber(i + 1);
                    dto.setName(String.valueOf(excel.get(0)));
                    dto.setPhone(String.valueOf(excel.get(1)));
                    dto.setFromAddress(String.valueOf(excel.get(0)) + String.valueOf(excel.get(1)) + String.valueOf(excel.get(2)));
                    //物品名称为空的话 默认其他类型
                    dto.setGoodsName(excel.size()>=4&&!StrUtil.isBlankIfStr(excel.get(3))?String.valueOf(excel.get(3)):"其他类型");
                    //重量为空的话 默认1
                    dto.setWeight(excel.size()>=5&&!StrUtil.isBlankIfStr(excel.get(4))?String.valueOf(excel.get(4)):"1");
                    dto.setRemark(excel.size()>=6&&excel.get(5)!=null?String.valueOf(excel.get(5)):"");
                    resultList.add(dto);
                    addrRequest.add(String.valueOf(excel.get(0)) + String.valueOf(excel.get(1)) + String.valueOf(excel.get(2)));
                }else{
                    dto.setMsg("第"+(i + 1)+"条数据有空值(至少3列数据)！");
                    errList.add(dto);
                }
            }
            //3.1批量地址解析（正确数据不为空的情况下才去地址解析）
            if(addrRequest.size() > 0){
                addrResponse = MatchAddressUtil.analysisList(addrRequest);
                if(!"1".equals(addrResponse.getCode())){
                    throw new GlobalException("云地址解析失败:：" + addrResponse.getMsg());
                }
                //3.2解析结果回填(结果先转换成map 用原地址做key，回填时通过key取出)
                addrDataResponseList = addrResponse.getData();
                addrDataResponseList.forEach(d -> addrDataResponseMap.put(d.getReqAddress(), d));
                //3.3结果回填
                resultList.forEach(s->{
                    MatchAddressResponseData resMap = addrDataResponseMap.get(s.getFromAddress());
                    if(resMap != null){
                        s.setProvince(resMap.getProvince());
                        s.setCity(resMap.getCityName());
                        s.setCounty(resMap.getCountyName());
                        s.setTown(resMap.getTownName());
                        s.setAddress(resMap.getFormatAddress());
                    }
                });
            }
            //4.对返回结果做校验：
            for (ExcelOrderDto result : resultList) {
                System.out.println(JSONUtil.toJsonStr(result));
                //去掉字符串中的"\n"、"\t"、"\r"
                result.setName(result.getName().replaceAll("\n|\t|\r", ""));
                result.setGoodsName(result.getGoodsName().replaceAll("\n|\t|\r", ""));
                result.setRemark(result.getRemark().replaceAll("\n|\t|\r", ""));
                //省-市-区-地址不能为空
                if (StrUtil.isBlank(result.getProvince()) || StrUtil.isBlank(result.getCity())
                        || StrUtil.isBlank(result.getCounty())|| StrUtil.isBlank(result.getAddress())) {
                    result.setMsg("第"+(result.getNumber())+"条数据：地址不合法(未解析出省/市/区)！");
                    errList.add(result);
                    continue;
                }
                //校验城市是否允许派件  不允许就不让下单
                if(!indexService.checkDispByCity(result.getCity())){
                    result.setMsg("第"+(result.getNumber())+"条数据：["+result.getCity()+"]未开启派件服务不允许下单！");
                    errList.add(result);
                    continue;
                }
                successList.add(result);
            }
            //5.返回结果封装
            resultMap.put("total", dataList.size() - 1);
            resultMap.put("successTotal", successList.size());
            resultMap.put("errorTotal", errList.size());
            resultMap.put("successList", successList);
            resultMap.put("errorList", errList);
            return RetResult.success(resultMap);
        } catch (GlobalException e) {
            return RetResult.warn("文件解析失败：" + e.getCm());
        } catch (Exception e) {
            log.error("文件解析失败：", e);
            return RetResult.warn("文件解析失败：文件格式不正确 请参考模板内容！");
        }
    }

    /**
     * 查询未打印/已打印 分页数据
     * 1.个人模式下：直接传当前用户的openId过来查询未打印/已打印的订单
     * 2.团队模式下：
     * 	a.管理员查所有：传团队Id即可 openId不赋值
     * 	b.管理员查指定成员：传团队Id + openId
     * 	c.普通成员：传团队Id + openId
     * @param pageVo
     * @return
     */
    @GetMapping("/getMyOrder")
    public RetResult getMyOrder(PageVo pageVo){
        if(StrUtil.isBlank(pageVo.getOpenId()) && StrUtil.isBlank(pageVo.getGroupId())){
            throw new GlobalException("openId和团队Id至少一个不为空！");
        }
        //个人模式：以用户绑定月结的客户编码去查询数据
        if(StrUtil.isEmpty(pageVo.getGroupId())){
            WxUser user = userService.findByOpneId(getToken());
            if(StrUtil.isNotBlank(user.getCustomerCode())){
                pageVo.setCustomerCode(user.getCustomerCode());
                pageVo.setOpenId("");
            }
        }else{
            //团队模式下需要校验当前token是否是管理员或者超级管理员
            WxGroup wxGroup = indexService.getWxGroupByOpenId(getToken());
            if(wxGroup == null){
                throw new GlobalException("未查询到当前用户有团队信息！");
            }
            //查自己权限范围内的单子
            if((wxGroup.getStatus() == 1 || wxGroup.getStatus() == 2) && StrUtil.isEmpty(pageVo.getOpenId())){
                //当前用户是管理员：查询寄件网点为团队所属网点的订单
                pageVo.setSendSiteCode(wxGroup.getSiteCode());
            }else {
                //查指定人的单子的单子
                WxGroup findWxGroup = indexService.getWxGroupByOpenId(pageVo.getOpenId());
                pageVo.setCustomerCode(findWxGroup.getCustomerCode());
                pageVo.setOpenId("");
            }
        }
        //判断用户输入的是运单号、手机号、收件人
        if(StrUtil.isNotBlank(pageVo.getBillCode())
                && (pageVo.getBillCode().startsWith("D") || pageVo.getBillCode().length() == 15)){
            pageVo.setBillCode(pageVo.getBillCode());
        }else if(StrUtil.isNotBlank(pageVo.getBillCode()) && pageVo.getBillCode().length() == 11){
            pageVo.setPhone(ZtdAESUtils.aesEncryptString(pageVo.getBillCode()));
            pageVo.setBillCode("");
        }else if(StrUtil.isNotBlank(pageVo.getBillCode())){
            pageVo.setName(ZtdAESUtils.aesEncryptString(pageVo.getBillCode()));
            pageVo.setBillCode("");
        }
        RetPage<Order> page = orderService.findMyOrderPage(pageVo);
        return RetResult.success(page);
    }

    /**
     * 根据订单号查询订单详情
     * @param orderBill
     * @return
     */
    @GetMapping("/getOrderByOrderBill")
    public RetResult getOrderByOrderBill(@RequestParam String orderBill){
        Order order = new Order();
        //order.setOpenId(getToken());
        order.setOrderBill(orderBill);
        Order orderDb = orderService.getOrderByOrderBill(order);
        return RetResult.success(orderDb);
    }

    /**
     * 根据订单号集合获取运单打印List数据
     * @param orderBill
     * @return
     */
    @GetMapping("/getOrderPrint")
    public RetResult getOrderPrint(@RequestParam String orderBill){
        //1.获取list集合数据
        List<Bill> orderDb = orderService.getOrderPrint(orderBill);
        if(orderDb == null || orderDb.size() < 1){
            throw new GlobalException("该订单["+orderBill+"]没有产生运单！");
        }
        return RetResult.success(orderDb);
    }

    /**
     * 校验运单号是否被使用
     * @param billCode
     * @return
     */
    @GetMapping("/checkBillCode")
    public RetResult checkBillCode(@RequestParam String billCode){
        //1.校验单号是否被使用
        orderService.checkBillCode(billCode);
        //2.校验是否绑定二维码号段(是否有绑定网点)
        Site sendSite = orderService.getSendSiteByBillCode(billCode);
        if(sendSite == null){
            throw new GlobalException("运单号["+billCode+"]未绑定二维码号段！");
        }
        return RetResult.success(billCode);
    }

    /**
     * 根据运单号获取物流轨迹
     * 1.先根据用户输入的Code查出订单号和运单号
     * 2.根据订单号查询出订单数据、寄件地址、收件地址、状态、
     * 3.根据运单号查询轨迹
     * @param billCode
     * @return
     */
    @GetMapping("/getScanBillCode")
    public RetResult getScanBillCode(@RequestParam String billCode){
        Order orderDb;
        Order orderQry = new Order();
        HashMap<String, Object> map;
        List<RetScanPathDto> list = new ArrayList<>();
        //校验用户是否有查看订单的权限
        boolean cancelOrder = orderService.checkLookOrder(getToken(), billCode);
        if(!cancelOrder){
            throw new GlobalException("当前用户没有权限查看该订单["+billCode+"]！");
        }
        //0.根据长度判断是订单号还是运单号 D开头的是订单号  否则就是运单号
        if(billCode.startsWith("D")){
            //orderQry.setOpenId(getToken());
            orderQry.setOrderBill(billCode);
        }else{
            //orderQry.setOpenId(getToken());
            orderQry.setBillCode(billCode);
        }
        //1.先根据用户输入的Code查出订单数据
        orderDb = orderService.getOrderByOrderBill(orderQry);
        //2.根据订单数据做操作：寄件地址/收件地址(转换为经纬度坐标)
        map = orderService.getCoordinate(orderDb);
        //3.根据运单号查询轨迹（有单号时才去查询）
        if(StrUtil.isNotBlank(orderDb.getBillCode())){
            list = orderService.getScanBillCode(billCode);
        }
        //4.封装返回结果
        HashMap<String, Object> res = new HashMap<>();
        res.put("order", orderDb);
        res.put("send", map.get("send"));
        res.put("rec", map.get("rec"));
        res.put("sendCity", orderDb.getSendCity());
        res.put("recCity", orderDb.getAcceptCity());
        res.put("billStatus", orderDb.getBillStatus());
        res.put("list", list);

        return RetResult.success(res);
    }

    /**
     * 统计我寄的和我收的总数
     * @param phone
     * @return
     */
    @GetMapping("/getMyOrderCount")
    public RetResult getMyOrderCount(@RequestParam String phone) {
        String token = getToken();
        Map<String, Integer> count = orderService.getMyOrderCount(token, phone);
        return RetResult.success(count);
    }

    /**
     * 分页查询我的运单
     * 运单状态：全部、已揽收、在途、已派件、已签收
     * 1.个人模式下：直接传当前用户的openId过来查询运单
     * 2.团队模式下：
     * 	a.管理员查所有：传团队Id即可 openId不赋值
     * 	b.管理员查指定成员：传团队Id + openId
     * 	c.普通成员：传团队Id + openId
     * @param pageVo
     * @return
     */
    @GetMapping("/getMyWaybill")
    public RetResult getMyWaybill(PageVo pageVo) {
        //校验参数不能为空
        if(StrUtil.isBlank(pageVo.getOpenId()) && StrUtil.isBlank(pageVo.getGroupId())){
            throw new GlobalException("openId和团队Id至少一个不为空！");
        }

        //个人模式：以用户绑定月结的客户编码去查询数据
        if(StrUtil.isEmpty(pageVo.getGroupId())){
            WxUser user = userService.findByOpneId(getToken());
            if(StrUtil.isNotBlank(user.getCustomerCode())){
                pageVo.setCustomerCode(user.getCustomerCode());
                pageVo.setOpenId("");
            }
        }else{
            //团队模式下需要校验当前token是否是管理员或者超级管理员
            WxGroup wxGroup = indexService.getWxGroupByOpenId(getToken());
            if(wxGroup == null){
                throw new GlobalException("未查询到当前用户有团队信息！");
            }
            //查自己权限范围内的单子
            if((wxGroup.getStatus() == 1 || wxGroup.getStatus() == 2) && StrUtil.isEmpty(pageVo.getOpenId())){
                //当前用户是管理员：查询寄件网点为团队所属网点的订单
                pageVo.setSendSiteCode(wxGroup.getSiteCode());
            }else {
                //查指定人的单子的单子
                WxGroup findWxGroup = getToken().equals(pageVo.getOpenId())?wxGroup:indexService.getWxGroupByOpenId(pageVo.getOpenId());
                if(StrUtil.isEmpty(findWxGroup.getCustomerCode())) {
                    throw new GlobalException("当前用户没有客户编码！");
                }
                pageVo.setCustomerCode(findWxGroup.getCustomerCode());
                pageVo.setOpenId("");
            }
        }
        //判断用户输入的是运单号、收件人、姓名
        if(StrUtil.isNotBlank(pageVo.getBillCode())
                && (pageVo.getBillCode().startsWith("D") || pageVo.getBillCode().length() == 15)){
            pageVo.setBillCode(pageVo.getBillCode());
        }else if(StrUtil.isNotBlank(pageVo.getBillCode()) && pageVo.getBillCode().length() == 11){
            pageVo.setPhone(ZtdAESUtils.aesEncryptString(pageVo.getBillCode()));
            pageVo.setBillCode("");
        }else if(StrUtil.isNotBlank(pageVo.getBillCode())){
            pageVo.setName(ZtdAESUtils.aesEncryptString(pageVo.getBillCode()));
            pageVo.setBillCode("");
        }
        RetPage<Bill> page = orderService.findMyWaybillPage(pageVo);
        return RetResult.success(page);
    }

    /**
     * 更改订单的打印状态
     * @param orderBill
     * @return
     */
    @GetMapping("/updateBlPrint")
    public RetResult updateBlPrint(@RequestParam String orderBill){
        if(StrUtil.isEmpty(orderBill)){
            throw new GlobalException("打印的订单号不能为空！");
        }
        orderService.updateBlPrint(orderBill, getToken());
        return RetResult.success();
    }

    /**
     * 对外单独的轨迹查询接口（可查所有订单）
     * @param billCode 运单号
     * @return
     */
    @GetMapping("/getTrajectory")
    public RetResult getTrajectory(@RequestParam String billCode){
        List<RetScanPathDto> resultList = orderService.getScanBillCode(billCode);
        return RetResult.success(resultList);
    }

    /**
     * 添加微信快捷备注
     * @param remark
     * @return
     */
    @GetMapping("/addWxRemark")
    public RetResult addWxRemark(@RequestParam String remark){
        if(StrUtil.isBlank(remark)){
            throw new GlobalException("备注不能为空！");
        }
        WxRemark wxRemark = new WxRemark();
        wxRemark.setOpenId(getToken());
        wxRemark.setRemark(remark);
        orderService.addWxRemark(wxRemark);
        return RetResult.success();
    }

    /**
     * 获取微信快捷备注列表
     * @return
     */
    @GetMapping("/getWxRemarks")
    public RetResult getWxRemarks(){
        List<WxRemark> list = orderService.getWxRemarks(getToken());
        return RetResult.success(list);
    }

    /**
     * 删除快捷备注
     * @param id
     * @return
     */
    @GetMapping("/delWxRemark")
    public RetResult delWxRemark(String id){
        if(StrUtil.isEmpty(id)){
            throw new GlobalException("快捷备注id不能为空！");
        }
        orderService.delWxRemark(id);
        return RetResult.success();
    }

    /**
     * 判断用户是否允许时效件
     * @param type 0是月结 1是团队
     * @param code  客户编码 网点编码
     * @return
     */
    @GetMapping("/getBlProductType")
    public RetResult delWxRemark(@RequestParam Integer type, @RequestParam String code){
        Site siteDb;
        //1.月结情况 先取月结客户所属网点 再看网点是否允许时效件
        if(0 == type){
            Customer customerDb = userService.findCustomerByCode(code);
            siteDb = indexService.getSite(customerDb.getCustomerOwnerSiteCode());
        }else{
            //2.团队情况 直接看网点是否允许时效件
            siteDb = indexService.getSite(code);
        }
        if(siteDb == null){
            throw new GlobalException("未查询到["+code+"]网点数据！");
        }
        if(0==siteDb.getBlProductType()){
            throw new GlobalException("网点["+siteDb.getSiteName()+"]不允许时效件！");
        }
        return RetResult.success("允许时效件下单！");
    }

    /**
     * 校验城市是否允许派件 不允许就不让下单
     * @param city
     * @return
     */
    @RequestMapping("/checkDispByCity")
    public RetResult checkDispByCity(@RequestParam String city){
        if(!indexService.checkDispByCity(city)){
            return RetResult.warn("["+city+"]未开启派件服务不允许下单！");
        }else {
            return RetResult.success();
        }
    }

}
