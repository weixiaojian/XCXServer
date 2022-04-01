package com.zhitengda.order;

import com.zhitengda.ztdCloud.cloudVo.cloudCommon.AlipaySignature;
import com.zhitengda.util.ZtdHTTPUtils;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 模仿支付宝客户端请求接口
 */
public class AlipayClientPostTest  {

    String url = "http://123.60.36.88:8088";
    String appId = "20210421834516817341841408";
    // 平台提供的私钥
    String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCOrj19x1H9P+JRDSp4yHPwRQlNY0I8RYI8k+9zlhaZouBdHlPB73IZqXBrPF6dgM6V77nrfDm2cd1fpZGtSDBsYZJhy3uLK3nwPznAhQyTWylTHUfzlSW27F2Ti58N3FeInAR03GPXne6lgLmL0J+o8RdFG9L27GeHeHyQZs8JZ4xiBXOMInltPWCr3KUQCsHYJN3ogJ/DVclF1Rr4yRzqK7f0iR8dbBW41zMZA7r1Qgw/0XQQtjL8bTu+gty+iBCAH77L/Vy4fxv/6q4csaVdvMtcoNcj1itJptM9btdgwjlX1TQ1njRqbWrFpNJ7Xj5562gQ4vMZ1jGtCnClYz4nAgMBAAECggEAOGyFWsFBFjJ2UHExc5Rp7QGQHS98mRMv3r7t+brH8zfrHEgK/W2wpJ9SGsZD8GtoxviHgK2esRy0W8fBJVKf+6xWVCBBGhc2YKDQOOckwv1RCVJSCuzXflBY7SlQ3A1f/GEejgw9253UK1UNi1IUYZ3+AtFvC0EP7A3kzMIra98Uf+++n5YAU/Y2XLZuT6V+hr0ecKbbWBdYusKfRh+REn0VzS75g355P8DK1ou1K8gy6s6yPK/qdc0n/zc6TcUAUCG97FnoyFpD4P8XNvrSAkAhu1Cm/xrGD8m7nkcDmMlqzl1oemx7fiCeCzOEQl3/mldQYVjBf5a2Nbt/UjyoUQKBgQDPxTU6JgxCrGCKDHSVsZno+lcvrkMDbQXLe5Qn8Zef/uJU+Y47T3mQhr5aJCHB5AGmJ62CYEuHZF7b4B/VSK95Hyy5OfcU9sDSYtGdVGHfr1hVknPpFgeNaAQNtXvE/kSXqLBwBpto3hW0XhIwmfgOFMiwa0AnL0elOuTiNskcXwKBgQCvzRA+79g9+/ScWt4E5dYP5IidMNWMn00/RKeO8yw9sQRCfXJdDGdx7A3Rcjmj4yOMjtWiI+zCCfMdM1Us7SpeLPt07jbyQ0noB4fAuR6PybFlyJJPt46yUpldMWUcZxVvljDCaEWyj7/U3oEk8FJic6qgkPNhV6xuPEvkIiszOQKBgGYosWNGEgE2gMFgHnL8QJEthnELXifxE0haGx8Iy2UADol5q1W+XieSGUZ5PZ/4XFc2azoP3xNTZtD2ArD8bEtB6NuqhLLqISVRMYAKXZV+whBRUmzpSA1fd57F/XV/EHsBlr9+gCuwBzwIhSkgi7dgwMz97y1VMgUsUB5qkbhBAoGBAKkmBFiaCxTwpNX/9BkAT2uxNuUPxJjt+bRDSXkYblvZQ0D6VJxEwA9z0YPRhLY0aNLn55N/P2Efk6Rn7K8ybo54d2V1grasmaLABBUhyM8OgDpvtp8u/QSExv1M9n1SjeR0vOuAynbZKvkVMWsqNAp4QhcGALaLVeQm7dpOKgS5AoGBAJPVp6RXS2JE6qIloHNdBHXXqbwDd5My0oadkNKioFEUGy+OVotvf1vAhLVQaJ+MkYKIJkfv/W/GP7BYVbw3eCIW7Gh+KLx24PbJllILfCxVUcsJl9hJWZDnwuEpBfGb+sNf2VUzMzjon32axv9LaibUB5lqAco1u7YVOL9SLesQ";

    /**
    参数	            类型	    是否必填	    最大长度	    描述	            示例值
    app_id	        String	是	        32	    支付宝分配给开发者的应用ID	2014072300007148
    method	        String	是	        128	    接口名称	alipay.trade.fastpay.refund.query
    format	        String	否	        40	    仅支持JSON	JSON
    charset	        String	是	        10	    请求使用的编码格式，如utf-8,gbk,gb2312等	utf-8
    sign_type	    String	是	        10	    商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2	RSA2
    sign	        String	是	        344	    商户请求参数的签名串，详见签名	详见示例
    timestamp	    String	是	        19	    发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"	2014-07-24 03:07:50
    version	        String	是	        3	    调用的接口版本，固定为：1.0	1.0
    app_auth_token	String	否	        40	    详见应用授权概述
    biz_content	    String	是		请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
     */
    // 这个请求会路由到story服务
    @Test
    public void testGet() throws Exception {

        // 公共请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("app_id", appId);
        params.put("method", "matching.matchingSiteEmployee");
        params.put("format", "json");
        params.put("charset", "utf-8");
        params.put("sign_type", "RSA2");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("version", "1.0");

        // 业务参数
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("id", "1");
        bizContent.put("name", "葫芦娃");

        params.put("biz_content", "{\"data\":[{\"acceptCity\":\"深圳市\",\"acceptCounty\":\"宝安区\",\"acceptMan\":\"\",\"acceptManAddress\":\"甲岸村\",\"acceptProvince\":\"广东省\",\"acceptTown\":\"新安街道\",\"billCode\":\"\",\"customerCode\":\"\",\"customerName\":\"\",\"dispatchMan\":\"\",\"dispatchManCode\":\"\",\"dispatchSite\":\"\",\"dispatchSiteCode\":\"\",\"goodsPayment\":\"\",\"goodsWeight\":\"\",\"id\":\"12312\",\"insuredValue\":\"\",\"orderBillCode\":\"\",\"packingPiece\":\"\",\"paymentType\":\"\",\"sendCity\":\"深圳市\",\"sendCounty\":\"宝安区\",\"sendMan\":\"\",\"sendManAddress\":\"中粮商务公园\",\"sendProvince\":\"广东省\",\"sendSite\":\"\",\"sendSiteCode\":\"\",\"sendTown\":\"新安街道\",\"takePieceEmployee\":\"\",\"takePieceEmployeeCode\":\"\",\"topayment\":\"\"}],\"name\":\"\"}");
        String content = AlipaySignature.getSignContent(params);
        String sign = AlipaySignature.rsa256Sign(content, privateKey, "utf-8");
        params.put("sign", sign);

        System.out.println("----------- 请求信息 -----------");
        System.out.println("请求参数：" + buildParamQuery(params));
        System.out.println("商户秘钥：" + privateKey);
        System.out.println("待签名内容：" + content);
        System.out.println("签名(sign)：" + sign);
        System.out.println("URL参数：" + buildUrlQuery(params));

        System.out.println("----------- 返回结果 -----------");
        String responseData = ZtdHTTPUtils.sendPost(url,params);

        System.out.println(responseData);
    }

    protected static String buildParamQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString().substring(1);
    }

    protected static String buildUrlQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                sb.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().substring(1);
    }


}
