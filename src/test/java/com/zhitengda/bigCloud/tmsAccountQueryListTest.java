package com.zhitengda.bigCloud;

import com.zhitengda.util.ZtdHTTPUtils;
import com.zhitengda.ztdCloud.cloudVo.cloudCommon.AlipayApiException;
import com.zhitengda.ztdCloud.cloudVo.cloudCommon.AlipaySignature;

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
public class tmsAccountQueryListTest {


    private static String url = "http://121.37.175.249:8320";
    private static String appId = "20210519844622421133623296";
    private static String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCb8P+tftkf+4B11vo/JGZXVLHrfFhv4RXtSzLP/8NomYVPyk5C1W5xBspINi+hLJTaRuGvRzjmkZzTDEwDSFMnoS9/FfPaCrtRI0lNEI+hBLUnSj8DcheDHDkmg9McDot5T58LavVNoCGneH+z61RMwMVb73MEusGy2zOLSV4HPRI5WHKy5gbtsAiM2nKRZ8Lv/tGU5sFxz3C8mVtyJ2toJjRKOr44cqs+Rzp3MD58Jma1aYiQYV8JIMLjGPUlxsn5VyVTzVzpYWo0em5OkSXWwYxOsH/3H6gQvudJlZOiTb4DCTsU3LElrVVz1b+FtjEcGhX3JMg7Wph5GWi6Ofb3AgMBAAECggEAQk7QuRo2AwZzUiguUokMx1epK+O8yx3fr/4Ixi8QDTsyV4JDkuGkS4VCoEACMrt9M+9t6ZUUWd5S297ft4maakB8rMJN5Uoo6lBhy9Yd7K7OxL0qXDG4U7S9jGYDb2uz3MqBa154niBYDxkK3BtVxik2Amin9Lvgv0e7/B8fT1362MYRvFEjiBAgVVW2Mnmo4OkEU28ycysvaoTiCbwb9qpdJYRcCqYMFKaW3yWHISLmbVXfRfQrlPILqfhHVK7ysX1Wx/EovzDUHO09cyBEjQG+hYwdotJxt5jnE2zXwylymz18wV8zFB8Fe24YLxS3D2+pFU7wJjaZts84lan00QKBgQDcKE0X+ye/3oLW4VvjBboVo6FGc6bMQgkjrSxaC4YlU3+Ea9PcLKmlUofYEGvMEVQw/smjUT8RJcQTFeMIj1VOXpOVgyWesWDJQIQOokwQjQCOzAa8Ofqh3M6Jm8qq6XjcdH/utsVX7wBwoWirReDZNdJdyNPqP9YGp9pqRkBZSQKBgQC1VE8CGe3+XV2tO7HAjciQBAXRycokXXZfhx+AyyNWwZfQ1xW0vPk3zBEAZwCnFiYh57bm7emCKIDGXQ2jfbhJ8pFwErmdFswURfUxw8DM9dd7ryga0XSJnYN7P0ekPJUS5npOVbH2U8SgThIsUPxImkYPpVOjIea8tJEB4XIOPwKBgDk2Q+qSex7ybR/4So+xok0TS8rqLRNxqbxauWbhDchPbZj1CIZ0GNX8n463nbvXWQCuhanqaDBFlzG6g1hrHykRjNrSzQjzYYqLOIYxCQFYEygIv0tU6dv8bLP4B2/o4l6v67sWRix20K90UqYHr8niobt7gA6k7jKmliz9+qT5AoGBAIV6JBaV47zkGXrVgEkPlV19uJLvCV+vdN1o5+W0orTQGOLgENhUateJ3dIAAFj+Ut+OY/Klmsf27KQYB1wbxTaZ2jk3b26VQ3CA9rbFM/0r0oz3KHxlRnWufwSQVjotZkbdZZaYOkHq88hU78ezbaI7/eqydliVMfYirMNHot+dAoGBAMXQ/gq3SXRF3/y5aflRX+9RvUo6lLifg83mOngP6vF9yWfYl+ZRdo1UidZFmsIPAHZ+fIZNnARV/gIbCTLW+fJ5LwN+U47qs+s63VCMkHOcL3zpGMazqZJRrvYw2VchjSuPH9AuVrq00dQeyjIdYldfO35HWZoNuORb8TEZ5cGy";

/*
    public static String url = "http://cityexp-gateway.yundasys.com/";
    public static String appId = "20210918888805914952335360";
    public static String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCu12OZS3A/diOK1M+WK5SrAfoXq3cre2vJAu2zfaYUQXBdRmSvwzPsxI6Kk2DOHIBS+fpG+IPb/EXQbCdZYscFEZKFZzEuYnzclZ7ScRR0MH6opC5BUVVmvNZcEv3fWHob6YW+j7C2zND0NJvjDVSW0PV+6wqktREl59BzjRWClqv6oZxXoytyL0AOXgoEzf/VQWzDnRf5tF2xmov62dhpRxvbXiwU6U+webwiFDG6e1JyFQ/lb8aYcpI9x8jhLnZs+C2TuV5MIRV2XqNEh11uun1hFjAFWDaBAn/k7W2+vL3kNKtJLgfpQ32HvuBmVfcvvjI+yQuzBzzy3OaILlnlAgMBAAECggEABPAqbPr3eaNhj5tBHbGvakENCjwaioza13uQsSAcX7gVb1z8nQY7YjWO/RT6h/fAHBfZXMXKG519b0yOs/SZQGhlpgTrG69GbYiASttMw5dXDHZOpCOi8NrmhwU2TGeYU09+KoquaAtS34LY4EnRGM1gEaybyVkf2d05Qvo6IE0LIoGYg82HzmYbv9mn8p+fWaGrQtQBeMmHlZrPcT8Dz9rJq8WsSpMEFtf1V8NdT2aNIyV9YMNZCR7WkyXQRupQvUQdFBgNJv2KAetvZ9LmvuC66pp7QfpSidLIIy8ZIVQkoqrthRM345abFRfjWdUMLXloM1s/EWSqes0Y0y+sgQKBgQDl2ro7ZpsqDHAOy5t5MjFhXa7tY55ceYSae9+xNidvK1tIY6fSIcDOn47oBnUwe8bixAHB3/By1WPrfIkTqME6t/9H4jII3L8jDWQ5k5PlivD9ty6PUv/vAhCkYSN83GBr1XX8lrESulTknHTZ4HL2FQaDlOB7r7H/lmF5C35FBQKBgQDCurPtQ7sGP3LUDMP6CHip6UFUBGax/IV1mlw/lXjeZzDnuId1KVSraArAOw53qlL/ARvyA7zDCBaTq8ERyHUGp/33gYTy0Z7waQ4lMfIlE5MJF2LgHU/FeGZz7hyaYqsol7nQ25m62I/np80TbIWl495QX5Pcrt3DUBfmmhPXYQKBgHJkcNqvLfm3Ru2WF0Ix4LF8Vlp+gYm0UHIxxBoFwYpqIXvdXOAPPE2Yyx5eWGRQtaKVueoq8Re1a4z+5ZmLGt+GQt4bjJssgVOIjqC4+Zn7lAQw7lMWE5l3haDJV0gExVflXFrhBC813JoJ432rXotp21wQ88eaoRN6W/hJ6AWhAoGBAK75JIOlwH+0s3FmpxIcWpAST+k2qzBzP1iVAx7/KERvZLpSTSUxpir+qephJnbQTRNl475rLUnyEpdOVYWtIbqBaOrlLpkhUjejrlotru8xz4TzxpyquesubKDu7DStRC6NZyIMsO37ehMl7Ffvw4d0YWbG+w3o8st1RISpHfsBAoGASKq+LMqjXn1yV3eDOz6MoO5lAijEcZjHLXkTa7sfIGn7jKfIX1y+QH2ogYiPeh0hOHMeBpJFqPC6UrsQjcl2MS1W0vek9NQoCeG/qufHqiVFcnxs5T8KyKeY3KmrIIATP5GYABhY0pwE1nTGUQPQ1iLsGd2XUbrXqitJW6d4AIg=";
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
        params.put("method", "tms.account.queryList");
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

        params.put("biz_content", "{\"sellerIdList\":[123,123,123],\"branchCode\":\"SH00021\"}");
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
