package com.zhitengda.wx;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置映射类
 *
 * @author langao_q
 * @since 2021-01-31 16:18
 */
@Data
@Component
@ConfigurationProperties(prefix = "weixin")
public class WXConfig {

    /**
     * 微信appid
     */
    private String appId;
    /**
     * 微信appSecret
     */
    private String appSecret;
    /**
     * 微信token
     */
    private String token;
    /**
     * 调试模式-控制台打印请求日志
     */
    private boolean devMode;
    /**
     * 是否显示sql
     */
    private boolean showSql;
    /**
     * 授权地址
     */
    private String urlPath;
    /**
     * 订阅消息模板id
     */
    private String templateId;
    /**
     * 文件保存/下载路径
     */
    private String filePath;
    /**
     * 韵达实名认证请求地址
     */
    private String realNamePath;
}
