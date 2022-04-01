package com.zhitengda.ztdCloud;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhitengda.util.ZtdHTTPUtils;
import com.zhitengda.web.exception.GlobalException;
import com.zhitengda.ztdCloud.cloudVo.MatchAddress.MatchAddressLisrResponse;
import com.zhitengda.ztdCloud.cloudVo.MatchAddress.MatchAddressResponse;
import com.zhitengda.ztdCloud.cloudVo.MatchAddress.MatchParsImgResponse;
import com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp.MatchSiteEmpRequest;
import com.zhitengda.ztdCloud.cloudVo.cloudCommon.AlipaySignature;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 智腾达云-地址解析工具类
 * @author langao_q
 * @since 2021-05-07 14:08
 */
@Slf4j
public class MatchAddressUtil {

    /**
     * 地址解析（韵达提供接口）
     * @param address
     * @return
     */
    public static MatchAddressResponse analysis(String address) {
        MatchAddressResponse response;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            //params.put("method", "matching.address");
            params.put("method", "matching.YdAddress");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            ArrayList<MatchSiteEmpRequest> dataList = new ArrayList<>();
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("address", address);
            params.put("biz_content", JSONUtil.toJsonStr(dataMap));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            log.info("【地址解析请求{}：】" + JSONUtil.toJsonStr(params), CloudParam.URL);
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            log.info("【地址解析响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("matching_YdAddress_response"), MatchAddressResponse.class);
            return response;
        } catch (Exception e) {
            log.error("【YD地址解析接口异常，耗时"+(System.currentTimeMillis() - start)+"毫秒：】", e);
            throw new GlobalException("【YD地址解析接口异常:" + e.getMessage() + "】");
        }
    }

    /**
     * 多个地址解析（韵达提供接口）
     * @param address
     * @return
     */
    public static MatchAddressLisrResponse analysisList(List<String> address) {
        MatchAddressLisrResponse response;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            //params.put("method", "matching.addressList");
            params.put("method", "matching.YdAddressList");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            ArrayList<MatchSiteEmpRequest> dataList = new ArrayList<>();
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("addressList", address);
            params.put("biz_content", JSONUtil.toJsonStr(dataMap));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            log.info("【批量地址解析请求：】" + JSONUtil.toJsonStr(params));
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            log.info("【批量地址解析响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("matching_YdAddressList_response"), MatchAddressLisrResponse.class);
            return response;
        } catch (Exception e) {
            log.error("【YD地址解析接口异常，耗时"+(System.currentTimeMillis() - start)+"毫秒：】", e);
            throw new GlobalException("【YD地址解析接口异常:" + e.getMessage() + "】");
        }
    }

    /**
     * 图片识别文字
     * @param img base64的图片
     * @return
     */
    public static MatchParsImgResponse analysisImg(String img) {
        MatchParsImgResponse response;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            params.put("method", "matching.YdPicAddress");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("address", img);
            params.put("biz_content", JSONUtil.toJsonStr(dataMap));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            //log.info("【图片识别文字请求：】" + JSONUtil.toJsonStr(params));
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            //log.info("【图片识别文字响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("matching_YdPicAddress_response"), MatchParsImgResponse.class);
            return response;
        } catch (Exception e) {
            log.error("【图片识别文字接口异常，耗时"+(System.currentTimeMillis() - start)+"毫秒：】", e);
            throw new GlobalException("【图片识别文字接口异常:" + e.getMessage() + "】");
        }
    }

    /**
     * 地址解析（智腾达提供接口）
     * @param address
     * @return
     */
    public static MatchAddressResponse ztdAnalysis(String address) {
        MatchAddressResponse response;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            params.put("method", "matching.address");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            ArrayList<MatchSiteEmpRequest> dataList = new ArrayList<>();
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("address", address);
            params.put("biz_content", JSONUtil.toJsonStr(dataMap));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            //log.info("【地址解析请求：】" + JSONUtil.toJsonStr(params));
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            //log.info("【地址解析响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("matching_address_response"), MatchAddressResponse.class);
            return response;
        } catch (Exception e) {
            log.error("【ZTD地址解析接口异常，耗时"+(System.currentTimeMillis() - start)+"毫秒：】", e);
            throw new GlobalException("【ZTD地址解析接口异常:" + e.getMessage() + "】");
        }
    }

    /**
     * 多个地址解析（智腾达提供接口）
     * @param address
     * @return
     */
    public static MatchAddressLisrResponse ztdAnalysisList(List<String> address) {
        MatchAddressLisrResponse response;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            params.put("method", "matching.addressList");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            ArrayList<MatchSiteEmpRequest> dataList = new ArrayList<>();
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("addressList", address);
            params.put("biz_content", JSONUtil.toJsonStr(dataMap));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            //log.info("【批量地址解析请求：】" + JSONUtil.toJsonStr(params));
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            //log.info("【批量地址解析响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("matching_addressList_response"), MatchAddressLisrResponse.class);
            return response;
        } catch (Exception e) {
            log.error("【ZTD地址解析接口异常，耗时"+(System.currentTimeMillis() - start)+"毫秒：】", e);
            throw new GlobalException("【ZTD地址解析接口异常:" + e.getMessage() + "】");
        }
    }

    public static void main(String[] args) {
        String address = "宝山区联谊路649弄3单元704室";
        MatchAddressResponse response1 = MatchAddressUtil.analysis(address);
        System.out.println(JSONUtil.toJsonStr(response1));

        String str = "上海市上海市松江区中山西路555弄16号";
        MatchAddressResponse response2 = MatchAddressUtil.ztdAnalysis(str);
        System.out.println(JSONUtil.toJsonStr(response2));


        /*String address1 = "1";
        String address2 = "广东省深圳市宝安区新安街道中粮商务公园1003号 张三15200991578";
        String address3 = "广东省深圳市宝安区新安街道中粮商务公园1003号 张三15200991577";
        ArrayList<String> list = new ArrayList<>();
        list.add(address1);
        list.add(address2);
        list.add(address3);
        MatchAddressLisrResponse response = MatchAddressUtil.analysisList(list);
        System.out.println(JSONUtil.toJsonStr(response));*/


       /* String img = "iVBORw0KGgoAAAANSUhEUgAAAFIAAAAZCAYAAACis3k0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAALjSURBVGhD7Vftsa0gDLQuC7Ieq6EZi+FtPkACQdDjnXk/3BmuBwhhswnoXeKHV/AJ+RI+IV/CJ+RL+IR8CZ+QL2Eo5LGvcVmWuAUduIuwx/3Q3wYhbssKv+7kPLr+Z0AclrhcBBe2DVZjDIQ84r4ucX3OFERAFIK1LkRIGv8tWb9wpLWWWwiWhPAvxAyby/NayGOPa+mkgQTRFeBy/SmkQKrjkZgIbll3sLmLVkj2VZAgIc8kSbxeYbCQqSK8tu67lL/bxCFt5glgxlnU0q4WEjiOGNBqtPywbkfAZkwbb6BHthmvUQp50PYMrkK1N0JWIpfoVyQtMtWk2ZjNPNaXx43J8VonyKaN7iVw2TsWDe8a1/tLHk4fp5BYdxF7R0inWlTITkIsqPpo03S0iVjZF6P2WN3AgZPiUeHqnSIJMB+Pg61OEnL00nGE7Ak2LyRXH2U4V6BHwvfHQlxkPgMitEV5I9lAvjLyfjOnRVq9RyWkEOFSPgLuKx1mtCSTYH3itMZmPIvcabI37uW2TCrA91YJPjzWJSQevm/xCeVXpgPawwm4EDI5TsKc/V5L/upPhgQSLe/Jx6hNRHmPEkmHYx+wP9cLX+PvCsQHiciJTtePzPZxKWS+K4SMH8zVXAVU88ZBhSIZTsZLUiq0a9cFcbophCJsKd60Hy6hmX8OukJSAJnAW0JOisJ2OIpqP+W7BvNftRB0bASIIXuVQgpGVw83V0iDl4SchvhsBIc4va+bFukFMXs3wj4H0Qo5RKfy/1hI5y2YHdi50u+Bj+2Z4OStqwJSgMb/DP5aSHyjydG80XIA4kPGnSrJx76Y0zF5UYyDS58t3oulPJpjTcvicJLea66QmDiBafQNATMPPOynAKlvSGR7CYr65qineRWb+kbAvF6h/SQ29Tn4FJSxfxavEVLn9a8a/0dPFov7iBJPIu/Z/fqkf/tm7EZP/QWkwYSvrz8Ug341++EpPiFfQYz/AFw/Dgw6dPxAAAAAAElFTkSuQmCC";
        MatchParsImgResponse response2 = MatchAddressUtil.analysisImg(img);
        System.out.println(JSONUtil.toJsonStr(response2));*/
    }

}
