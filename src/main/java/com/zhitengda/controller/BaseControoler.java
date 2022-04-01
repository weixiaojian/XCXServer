package com.zhitengda.controller;

import cn.hutool.core.util.StrUtil;
import com.zhitengda.util.ServletUtil;
import com.zhitengda.web.exception.GlobalException;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础controller类
 * @author langao_q
 * @since 2021-01-31 17:29
 */
public class BaseControoler {

    /**
     * 获取req中 或 cookie中的token
     * @return
     */
    protected String getToken(){
        try {
            HttpServletRequest req = ServletUtil.getRequest();
            String tokenParam = req.getParameter(ServletUtil.COOKIE_NAME_TOKEN);
            String tokenCookie = ServletUtil.getTokenByCookie();
            String openId = StrUtil.isNotEmpty(tokenParam)?tokenParam:tokenCookie;
            return  openId;
        }catch (Exception e){
            throw new GlobalException("获取token失败，请重试！");
        }
    }

}
