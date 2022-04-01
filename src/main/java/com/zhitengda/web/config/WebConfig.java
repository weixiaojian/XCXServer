package com.zhitengda.web.config;

import com.zhitengda.web.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author langao_q
 * @since 2020-11-25 18:22
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Autowired
    private AuthInterceptor authInterceptor;


    /**
     * 校验Cookie或Request中token，为空或者token没有对应用户信息的就去登陆页面
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登陆拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/searchUser"
                        , "/user/getOpenIdByCode"
                        , "/user/decryptPhone"
                        , "/user/updateUser"
                        , "/order/excelTemplate/*"
                        , "/order/getTrajectory");
    }

    /**
     * 开启跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路由
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 是否允许证书（cookies）
                .allowCredentials(true)
                // 设置允许的方法
                .allowedMethods("*")
                // 跨域允许时间
                .maxAge(3600);
    }
}