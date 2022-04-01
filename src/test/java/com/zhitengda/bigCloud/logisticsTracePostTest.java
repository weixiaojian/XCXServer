package com.zhitengda.bigCloud;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.zhitengda.util.ZtdHTTPUtils;
import com.zhitengda.ztdCloud.cloudVo.cloudCommon.AlipayApiException;
import com.zhitengda.ztdCloud.cloudVo.cloudCommon.AlipaySignature;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Demo请求接口
 */
public class logisticsTracePostTest {


    private static String url = "http://t-cityexp-tms.yundasys.com:8320";
    private static String appId = "20210419833671097911083008";
    private static String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALyJDyiUFv7nXHbd CjjEy8bUUXnT5LqqowzgCvzMvNGmMbSVtizyNU3QgO9l+Vr5b5Oii+BvAx6OjtyO fs5wBNy96pnUVEViIurmoinLTBiEOmyNj9dZSAtcVNM3u536QrbJUeIJLp3CfuJA 6+LXzcOxA5eXwLsLqHnVW7ld7kV9AgMBAAECgYEAqINU4i8jMqj2k2NULJNxlADX 5i1HhrTX0b069OPGbRi/2qnpz9iZXjjb1Z5utyq12N5zaaGvxxMii+5tVvT20g/6 OTg5WgUcNXDYTO9l/6yTYquA7X72anqTi48o1j4pqSIzuCJ9FwPoJ9P5oN11CzAt RJiaWompazVUeatNaV0CQQDx9hVNoYEz6mveUHluL4/LDyl8IlbUsEtzVn9B026x nFq586EnfOR4Ujc1oq+I1OpOgFg6VjrEQA43GZG/i1XnAkEAx3ltR0uuwDkj/6Lr wxI7oODmZWD/oQofjfWts7HocgGuMSWhUgp8sOKJHwHWlmWDpaFIBMlGcA+OEO2b TC0U+wJBAKpWPG8XaLbcrC2o56ObVhZ0yRa7sfcvWR6MYTA9Iatl0DUif2wmL01J S9a1fe/NPFohXntLIznvglJl7MY02DcCQQCOtzDZRqvDud4ooz789UmWQ/JzbB/Y 7y4+Wh0qKMp+7vbbEOpHkVs/DwU+GwmV1xGGakrlqb/soyRotidbLBILAkBbmcW5 hvSSGqXmerS94fQ4lFaHS5V2T2YFwTbMsE2ItLmlzrkkjrlC4ZoIEWLer+QJzebE eIzsPHwNXq0Oy5eT";

/*
    public static String url = "http://cityexp-gateway.yundasys.com/";
    public static String appId = "20210809874293175395549184";
    public static String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCOYJ0I2i5gNKYCY0KIRRgufDT6b5fj4Hj1VciIBRE+2dcXBN5M4lEwzR3l54osZRXoFjpZIJtBNbHMb0Q2GRQjGs7uDaWFkxw0pE3Ieg0AKk+lSytDCTULYc2rEPQWL6bf1jpxvN+O2kfgJ/9q/tHL3CHS9sUmc2s2WKryiaGbEtuhmVG8VBxvli4ej+ksIsIUrQupGFglZNip7PWS3jIQ4azinCA5kj0rCkzDkAceVYzKOZsIxgUOuJ9m7Bd4feJajXkxF7kDkW9gekUF4vFe1u2Tbg0bgnoUl3rMQTNqpTKsUVvSljv7JD74QqqGVgPhyBkUtmF6jMPgb1Gw71PdAgMBAAECggEABFajUlq7oQjZqib2Jwrb8sasIpl16mazqN4lTyfAX8bpi/UFBRi8Wp1VdOT6S+IEqd7Lhl28/V6yfUVCsddPysYNWimmSoyyBlSfXiWB8ehYJvREbDWCnUcsqjLJDUgEcLupaj/UHBlAqW7ulIcRQ4IAsdn9Je/Ma4qGIv/Yoc4En3nxvLPgl3cMY2JsEQjOnc8RX9gxp+PFtVXXAWCBuCZktYIarJlaAG8oxSanGboJ2M7aa14+b44Nh967ZRqRc33aTs7vRzwMJRxyZk7tRgbbevZ7zqxqlCMLTBV5EZIrxzNqYV9GA1uURbVS6ABOgEuNhIqjr1cw+B0noNOLAQKBgQDQOa7wjX01u5fVh4G9GJbrazX6mwJczMR12IyOxUqUrO/nBcUdmUHN0ZMeTVhzmi+NRMbGx0vn7Bfi4tGrseyjjGoQHLDn0KYjgp5PfbuZlXgkjrLTJXv6Adx1fxi15FMhCnlGdRo3k2Y6wBDN4gonbWit2Q0k6Y4+5QaVEwGgnQKBgQCvC0m6MZS3WzPSMGXMQhijby2WxVP2dtMzoRsiUg/SHxc5HsExtfmzMM7WvQ2gB8i6ktGpihO2m3NYaGFHAWmQEkwziexBFs2ioerkYq3/mKPxrbGDZSOyIBxkJbRUHOpx22CpdFjX13MzGd1uvUQ7wKPlxdUMoAIyg+sDYaf8QQKBgQC/fNypcaS8/lhCUQx5xpw7wWtxnI2Oo+zj5xJT1ZGhqmwwy3iboK4IRfwCeOSn4xpp0t7uPKdMkCzjRO3G9KgHO4Dqckgp1xKCQ4Os3JbiRben/ubrEGSz5HRGRWyHEYN1NjmTFuvWQiJYUvrhK0dlPNNwVNQmpvZeVqY3/7OWbQKBgD6RqF73zhLkdNeTGTA0CB6TZRZZnrOgscnosoxIDQOsvV5bke80/vaAYc78cNxdK/EU/gC+LGG/mzFkzjDVXmhzQe3CRULW5dZWTmIoq82LQD7pOWSgSGwpcjySBa1zUJI2Oo0X25H7n2AKxM4KtVMufudpGciTgXWRFfZ1d9iBAoGBAL73c3PdXpgtHgugOmAcSpflLhrTEEGn0I722zJVmM7H9TkHE2ARrKid7AAwh43m5zrNuUwq5Smf+JaVWsNk7DXOu+sxploDwgusJ0Yy5mzktnn5KhWAhsdrS+ZQM2m7ALbPvw5Ch5U1wiSmbziXHcfkbAG9n+wAs4lcM7JJuKeG";
*/

    /**
    参数	            类型	    是否必填	    最大长度	    描述	            示例值
    app_id	        String	是	        32	    分配给开发者的应用ID	2014072300007148
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
    public static void main(String[] args) throws AlipayApiException {

        // 公共请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("app_id", appId);
        params.put("method", "tms.waybill.logisticsTrace");
        params.put("format", "json");
        params.put("charset", "utf-8");
        params.put("sign_type", "RSA2");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("version", "1.0");

        // 业务参数
        Map<String, Object> bizContent = new HashMap<>();
        ArrayList<String> billCodes = new ArrayList<>();
        billCodes.add("527670484765563");
        //billCodes.add("522639053234590");
        bizContent.put("mailNos", billCodes);

        params.put("biz_content", "{\"mailNos\":[\"524644315073255\"]}");
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
