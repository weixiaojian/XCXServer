package com.zhitengda.web.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhitengda.service.UserService;
import com.zhitengda.util.RetResult;
import com.zhitengda.util.ServletUtil;
import com.zhitengda.web.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 登陆拦截器
 * @author langao_q
 * @since 2021-01-31 17:57
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {

        try {
            String tokenParam = req.getParameter(ServletUtil.COOKIE_NAME_TOKEN);
            String tokenCookie = ServletUtil.getTokenByCookie();

            String token = StrUtil.isNotEmpty(tokenParam)?tokenParam:tokenCookie;

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            String methodName = method.getName();

            //为空时跳转到登陆页面
            if(StrUtil.isEmpty(tokenParam) && StrUtil.isEmpty(tokenCookie)) {
                log.error("【"+methodName+"】用户未登陆：未获取到token！");
                ServletUtil.renderString(res, JSONUtil.toJsonStr(RetResult.warn("用户未登陆！")));
                return false;
            }

            //数据库中的用户数据为空时跳转到登陆页面
            Integer dbUser = userService.checkUserByOpneId(token);
            if(dbUser < 1) {
                log.error("【"+methodName+"】没有登陆，未查询到该用户："  + token);
                ServletUtil.renderString(res, JSONUtil.toJsonStr(RetResult.warn("没有登陆，未查询到该用户！")));
                return false;
            }
            //放行
            return true;
        } catch (Exception e) {
            log.error("登陆拦截器错误：" + e.getMessage());
            throw new GlobalException("服务端异常：登陆拦截器错误！");
        }
    }
}
