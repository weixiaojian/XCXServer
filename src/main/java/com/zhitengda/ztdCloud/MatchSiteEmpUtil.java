package com.zhitengda.ztdCloud;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhitengda.util.ZtdHTTPUtils;
import com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp.MatchSiteEmpRequest;
import com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp.MatchSiteEmpResponse;
import com.zhitengda.ztdCloud.cloudVo.cloudCommon.AlipaySignature;
import com.zhitengda.web.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 智腾达云-网点揽派匹配工具类
 * @author langao_q
 * @since 2021-04-27 15:37
 */
@Slf4j
public class MatchSiteEmpUtil {


    /**
     * 网点揽派匹配
     * @param matchSiteEmpRequestList
     * @return
     */
    public static MatchSiteEmpResponse match(List<MatchSiteEmpRequest> matchSiteEmpRequestList) {
        MatchSiteEmpResponse response;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            params.put("method", "matching.matchingSiteEmployee");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("data", matchSiteEmpRequestList);
            params.put("biz_content", JSONUtil.toJsonStr(dataMap));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            log.info("【揽派匹配请求{}：】" + JSONUtil.toJsonStr(params), CloudParam.URL);
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            log.info("【揽派匹配响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("matching_matchingSiteEmployee_response"), MatchSiteEmpResponse.class);
            return response;
        } catch (Exception e) {
            log.error("【揽派匹配接口异常，耗时"+(System.currentTimeMillis() - start)+"毫秒：】", e);
            throw new GlobalException("【揽派匹配接口异常:" + e.getMessage() + "】");
        }
    }

    /**
     * 网点揽派匹配-韵达
     * @param matchSiteEmpRequestList
     * @return
     */
    public static MatchSiteEmpResponse YdMatch(List<MatchSiteEmpRequest> matchSiteEmpRequestList) {
        MatchSiteEmpResponse response;
        long start = System.currentTimeMillis();
        try {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", CloudParam.APP_ID);
            params.put("method", "matching.YDmatchingSiteEmployee");
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            params.put("version", "1.0");

            // 业务参数
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("data", matchSiteEmpRequestList);
            params.put("biz_content", JSONUtil.toJsonStr(dataMap));

            //生成签名
            String content = AlipaySignature.getSignContent(params);
            String sign = AlipaySignature.rsa256Sign(content, CloudParam.PRIVATE_KEY, "utf-8");
            params.put("sign", sign);

            //发起请求
            log.info("【揽派匹配请求{}：】" + JSONUtil.toJsonStr(params), CloudParam.URL);
            String responseData = ZtdHTTPUtils.sendPost(CloudParam.URL, params);
            long end = System.currentTimeMillis();
            log.info("【揽派匹配响应，耗时"+(end - start)+"毫秒】" + JSONUtil.toJsonStr(responseData));
            if(StrUtil.isBlank(responseData)){
                throw new GlobalException("返回结果为空！");
            }
            JSONObject obj = JSONUtil.parseObj(responseData);
            response = JSONUtil.toBean(obj.getJSONObject("matching_YDmatchingSiteEmployee_response"), MatchSiteEmpResponse.class);
            return response;
        } catch (Exception e) {
            log.error("【揽派匹配接口异常，耗时"+(System.currentTimeMillis() - start)+"毫秒：】", e);
            throw new GlobalException("【揽派匹配接口异常:" + e.getMessage() + "】");
        }
    }

    public static void main(String[] args) throws Exception {
        MatchSiteEmpRequest matchSiteEmpRequest = new MatchSiteEmpRequest();
        matchSiteEmpRequest.setId(String.valueOf(System.currentTimeMillis()));
        matchSiteEmpRequest.setBillCode(String.valueOf(System.currentTimeMillis()));
        matchSiteEmpRequest.setOrderBillCode(String.valueOf(System.currentTimeMillis()));
        matchSiteEmpRequest.setMatchingType(0);
        //上海市青浦区香花桥街道盈港东路6679号韵达总部
        matchSiteEmpRequest.setSendProvince("上海市");
        matchSiteEmpRequest.setSendCity("上海市");
        matchSiteEmpRequest.setSendCounty("青浦区");
        matchSiteEmpRequest.setSendTown("赵巷镇");
        matchSiteEmpRequest.setSendManAddress("盈港东路6679号");
        matchSiteEmpRequest.setAcceptProvince("上海市");
        matchSiteEmpRequest.setAcceptCity("上海市");
        matchSiteEmpRequest.setAcceptCounty("青浦区");
        matchSiteEmpRequest.setAcceptTown("白鹤镇");
        matchSiteEmpRequest.setAcceptManAddress("放鸟浜");

        ArrayList<MatchSiteEmpRequest> list = new ArrayList<>();
        list.add(matchSiteEmpRequest);
        MatchSiteEmpResponse response = MatchSiteEmpUtil.YdMatch(list);
        System.out.println(JSONUtil.toJsonStr(response));
    }

}
