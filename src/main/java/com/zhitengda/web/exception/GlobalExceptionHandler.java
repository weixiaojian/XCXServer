package com.zhitengda.web.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhitengda.util.RetResult;
import com.zhitengda.util.ZtdDBUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

/**
 * 全局异常处理器
 *
 * @author langao_q
 * @since 2021-01-24 17:51
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public RetResult exceptionHandler(HttpServletRequest req, Exception e) {
        //请求参数
        String params = JSONUtil.toJsonStr(req.getParameterMap());
        //自定义异常处理
        if (ExceptionUtil.isCausedBy(e, GlobalException.class)) {
            GlobalException ex = (GlobalException) e;
            log.info("自定义异常：" + ex.getCm());
            return RetResult.warn(ex.getCm());
        }
        //Valid参数校验异常处理
        else if (ExceptionUtil.isCausedBy(e, BindException.class)) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            String msg = "参数校验异常：";
            if (errors != null) {
                for (ObjectError error : errors) {
                    FieldError fieldError = (FieldError) error;
                    msg += " [ " + fieldError.getDefaultMessage() + " ] ";
                }
            }
            ObjectError error = errors.get(0);
            log.info("【" + msg + "】" + params);
            return RetResult.warn(msg);
        }
        //入参异常
        else if (e instanceof MissingServletRequestParameterException) {
            String msg = "入参不通过：" + ((MissingServletRequestParameterException) e).getParameterName();
            log.info("【" + msg + "】" + params);
            return RetResult.warn(msg);
        }
        //数据库异常处理
        if (ExceptionUtil.isCausedBy(e, SQLException.class)) {
            String errorMsg = "未知SQL异常：" + e.getMessage();
            errorMsg = ZtdDBUtils.parseException(e);
            log.info("【sql异常：】" + errorMsg + "】" + params);
            return RetResult.warn(errorMsg);
        }
        //其他异常处理
        else {
            //json异常处理
            String msg = e.getMessage();
            if(StrUtil.contains(msg, "JSON parse error")){
                log.info("【json格式错误：" + e + "】" + params);
                return RetResult.warn("json数据格式错误");
            }
            log.info("【服务端异常】：" + params, e);
            return RetResult.fail("服务端异常：" + e.getMessage());
        }
    }

}
