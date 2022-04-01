package com.zhitengda.util;


import com.zhitengda.web.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 利用HttpClient对象发送http请求的处理工具类
 * @author wst
 * @since 2016-12-18
 *
 */
@Slf4j
public class ZtdHTTPUtils {

    public static org.apache.commons.httpclient.HttpClient httpClient = null;
    //utf-8编码格式
    public static final String URL_UTF8 = "UTF-8";
    //gbk编码格式
    public static final String URL_GBK = "GBK";
    //ISO8859编码格式
    public static final String URL_ISO88591 = "ISO8859-1";
    //参数分隔符
    private static final String SEPARATER_FLAG = "&";
    //默认返回空字符串
    private static final String EMPTY = "";

    //复用连接的连接管理类
    private static MultiThreadedHttpConnectionManager connectionManager = null;
    //ConnectionManager管理的连接池中取出连接的超时时间（10秒）
    private static int connectionTimeOut = 60 * 1000;
    //建立连接超时时间
    private static int socketTimeOut = 30 * 1000;
    //每台主机的最大连接数
    private static int maxConnectionPerHost = 500;
    //最大连接数
    private static int maxTotalConnections = 500;

    private static org.apache.commons.httpclient.HttpClient client;

    static{
        //实例化复用连接的HttpClient对象
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
        connectionManager.getParams().setSoTimeout(socketTimeOut);
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
        connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
        client = new HttpClient(connectionManager);
    }
    public static String sendPost(String url, Map<String, String> params){
        return sendPost(url,params,URL_UTF8);
    }
    /**
     * httpclient发送post请求
     * @param url 请求url路径
     * @param params 请求参数
     * @param enc 请求编码
     * @return 响应结果
     */
    public static String sendPost(String url, Map<String, String> params, String enc){
        String response = EMPTY;
        PostMethod postMethod = null;
        try {
            postMethod = new PostMethod(url);
            postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);
            postMethod.addRequestHeader("connection", "Keep-Alive");
            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,enc);
            //将表单的值放入postMethod中  
            NameValuePair[] names  = new NameValuePair[params.size()];
            Set<String> keySet = params.keySet();
            int index = 0;
            for(String key : keySet){
                names[index++] = new NameValuePair(key, params.get(key));
            }
            postMethod.setRequestBody(names);
            //执行postMethod  
            int statusCode = client.executeMethod(postMethod);
            if(statusCode == HttpStatus.SC_OK) {
                //response = postMethod.getResponseBodyAsString();
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer .append(str );
                }
                response = stringBuffer.toString();
            }else{
                log.error("响应状态码 = " + postMethod.getStatusCode());
            }
        }catch(HttpException e){
            log.error("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
        }catch(SocketTimeoutException e){
            log.error("接口超时异常", e);
            throw new GlobalException("接口超时");
        }catch(IOException e){
            log.error("发生网络异常", e);
        }finally{
            if(postMethod != null){
                postMethod.releaseConnection();
                postMethod = null;
            }
        }
        return response;
    }

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        ZtdHTTPUtils.sendPost("http://127.0.0.1:8081/NZXCXServer/order/updateOrder", map);
    }

}
