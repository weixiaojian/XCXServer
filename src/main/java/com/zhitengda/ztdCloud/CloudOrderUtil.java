package com.zhitengda.ztdCloud;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhitengda.util.ZtdHTTPUtils;
import com.zhitengda.web.exception.GlobalException;
import com.zhitengda.ztdCloud.cloudVo.cloudCommon.AlipaySignature;
import com.zhitengda.ztdCloud.cloudVo.cloudOrder.CloudOrderResponse;
import com.zhitengda.ztdCloud.cloudVo.cloudOrder.CloudOrderRequest;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 智腾达云-订单工具类
 * @author langao_q
 * @since 2021-04-27 17:06
 */
@Slf4j
public class CloudOrderUtil {

    /**
     * 系统内部订单下单接口
     * @param cloudOrderRquestList
     * @return
     */
    public static CloudOrderResponse addOrder(List<CloudOrderRequest> cloudOrderRquestList) {
        CloudOrderResponse response;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            params.put("method", "system.order.add");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("data", cloudOrderRquestList);
            params.put("biz_content", JSONUtil.toJsonStr(dataMap));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            log.info("【云下单请求{}】" + JSONUtil.toJsonStr(params), CloudParam.URL);
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            log.info("【云下单响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("system_order_add_response"), CloudOrderResponse.class);
            return response;
        }catch (Exception e){
            log.error("【云下单接口异常】", e);
            throw new GlobalException("【云下单接口异常:" + e.getMessage() + "】");
        }
    }

    /**
     * 系统内部订单修改接口
     * @param cloudOrderRquest
     * @return
     */
    public static CloudOrderResponse editOrder(CloudOrderRequest cloudOrderRquest) {
        CloudOrderResponse response ;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            params.put("method", "system.order.edit");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            params.put("biz_content", JSONUtil.toJsonStr(cloudOrderRquest));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            log.info("【云修改订单请求{}】" + JSONUtil.toJsonStr(params), CloudParam.URL);
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            log.info("【云修改订单响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("system_order_edit_response"), CloudOrderResponse.class);
            return response;
        }catch (Exception e){
            log.error("【云修改订单异常】", e);
            throw new GlobalException("【云修改订单接口异常:" + e.getMessage() + "】");
        }
    }

    /**
     * 系统内部订单取消接口
     * @param cloudOrderRquest
     * @return
     */
    public static CloudOrderResponse cancelOrder(CloudOrderRequest cloudOrderRquest) {
        CloudOrderResponse response ;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            params.put("method", "system.order.del");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            params.put("biz_content", JSONUtil.toJsonStr(cloudOrderRquest));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            log.info("【云取消订单请求{}】" + JSONUtil.toJsonStr(params), CloudParam.URL);
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            log.info("【云取消订单响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("system_order_del_response"), CloudOrderResponse.class);
            return response;
        }catch (Exception e){
            log.error("【云取消订单异常】", e);
            throw new GlobalException("【云取消订单接口异常:" + e.getMessage() + "】");
        }
    }

    public static void testAdd(){
        List<CloudOrderRequest> requestList = new ArrayList<>();
        CloudOrderRequest request = new CloudOrderRequest();
        request.setOrderBill("D0000000018304");
        //request.setBillCode("524352289317141");
        request.setOpenId("oIPte5ZIgam4vZGTIwrBtd7uKmVo");
        request.setSendProvince("内蒙古自治区");
        request.setSendCity("长沙市");
        request.setSendCounty("天心区");
        //request.setSendTown("沙井街道");
        request.setSendManAddress("一号");
        request.setSendMan("张三");
        request.setSendManMobile("15200991579");

        request.setAcceptProvince("广东省");
        request.setAcceptCity("深圳市");
        request.setAcceptCounty("宝安区");
        request.setAcceptTown("新安街道");
        request.setAcceptManAddress("甲岸村");
        request.setAcceptMan("李四");
        request.setAcceptManMobile("15200991579");

        request.setSendSite("上海青浦盈港网点");
        request.setSendSiteCode("YD003");
        request.setRecMan("ddd");
        request.setRecManCode("NZW001255005");
        request.setDispatchSite("派件一级网点");
        request.setDispatchSiteCode("NZW001257");
        request.setDispatchMan("派件一级网点(管理员)");
        request.setDispatchManCode("NZW001257888");
        request.setRegisterSite("寄件一级网点");
        request.setRegisterSiteCode("NZW001255");
        request.setRegisterMan("ddd");
        request.setRegisterManCode("NZW001255005");
        request.setRegisterDate(DateUtil.now());

        request.setPaymentType("现金");
        request.setOrderStatus("待处理");
        request.setPackingPiece(1L);
        request.setGoodsWeight(new BigDecimal(10));
        request.setGoodsType("文件");
        request.setGoodsName("文件");
        request.setDataFrom("微信");
        request.setCustomerDeliveryBeginTime("2021-04-27 10:00");
        request.setCustomerDeliveryEndTime("2021-04-27 11:00");
        request.setRealIdCode("471182177683172883");
        request.setRealIdType("ID00001");
        request.setRealName("张三");
        request.setRemark("请携带纸箱！");

        request.setFreight(new BigDecimal(20));
        request.setInsuredValue(new BigDecimal(35));

        requestList.add(request);
        CloudOrderResponse response = CloudOrderUtil.addOrder(requestList);
        System.out.println(JSONUtil.toJsonStr(response));
    }

    public static void testEdit(){
        CloudOrderRequest request = new CloudOrderRequest();
        request.setOrderBill("D0000072979");
        request.setOpenId("oIPte5ZIgam4vZGTIwrBtd7uKmVo1");
        request.setSendProvince("广东省");
        request.setSendCity("深圳市");
        request.setSendCounty("宝安区");
        request.setSendTown("新安街道");
        request.setSendManAddress("中粮商务公园");
        request.setSendMan("张三");
        request.setSendManMobile("15200991579");

        request.setAcceptProvince("广东省");
        request.setAcceptCity("深圳市");
        request.setAcceptCounty("宝安区");
        request.setAcceptTown("新安街道");
        request.setAcceptManAddress("甲岸村");
        request.setAcceptMan("李四");
        request.setAcceptManMobile("15200991579");

        request.setSendSite("寄件一级网点");
        request.setSendSiteCode("NZW001255");
        request.setRecMan("ddd");
        request.setRecManCode("NZW001255005");
        request.setDispatchSite("派件一级网点");
        request.setDispatchSiteCode("NZW001257");
        request.setDispatchMan("派件一级网点(管理员)");
        request.setDispatchManCode("NZW001257888");
        request.setRegisterSite("寄件一级网点");
        request.setRegisterSiteCode("NZW001255");
        request.setRegisterMan("ddd");
        request.setRegisterManCode("NZW001255005");
        request.setRegisterDate(DateUtil.now());

        request.setPaymentType("现金");
        request.setOrderStatus("待处理");
        request.setPackingPiece(1L);
        request.setGoodsWeight(new BigDecimal(10));
        request.setGoodsType("文件");
        request.setGoodsName("文件");
        request.setDataFrom("微信");
        request.setCustomerDeliveryBeginTime("2021-04-27 10:00");
        request.setCustomerDeliveryEndTime("2021-04-27 11:00");
        request.setRealIdCode("471182177683172883");
        request.setRealIdType("ID00001");
        request.setRealName("张三");
        request.setRemark("请携带纸箱！");

        request.setFreight(new BigDecimal(20));
        request.setInsuredValue(new BigDecimal(35));

        //修改所需
        request.setModifier("微信");
        request.setModifierCode("微信订单修改");
        request.setModifySite("微信");
        request.setModifySiteCode("微信");
        CloudOrderResponse response = CloudOrderUtil.editOrder(request);
        System.out.println(JSONUtil.toJsonStr(response));
    }

    public static void testCancel(){
        CloudOrderRequest request = new CloudOrderRequest();
        request.setOrderBill("D0000073182");
        request.setOpenId("oj9L35GWVjHt3Gh1F2AECZj3prvc");
        request.setModifier("微信");
        request.setModifierCode("微信订单修改");
        request.setModifySite("微信");
        request.setModifySiteCode("微信");
        request.setDataFrom("微信");
        CloudOrderResponse response = CloudOrderUtil.editOrder(request);
        System.out.println(JSONUtil.toJsonStr(response));
    }

    public static void main(String[] args) throws Exception {
        testAdd();

        //testEdit();

        //testCancel();
    }

}
