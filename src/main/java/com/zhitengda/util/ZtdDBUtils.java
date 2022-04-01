package com.zhitengda.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * 
 * <p>Description: 异常信息错误消息解析工具类<／p>
 *
 * @author wst
 *
 * @date 2019年7月19日
 */
public class ZtdDBUtils {
	
	/**
	 * 解析异常错误消息
	 * @param exception 异常对象
	 * @param isOriginal true标识显示原始错误，false标识否
	 * @return
	 */
	public  static String parseException(Throwable exception,Boolean isOriginal) {
        Throwable e = null;
        if(exception!=null && exception.getCause()!=null){
            exception=exception.getCause();
        }
        if (exception instanceof InvocationTargetException) {
            e = exception.getCause();
        } else {
            e = exception;
        }
        String errorMsg = null;
        if (e instanceof SQLException) {
            errorMsg = psrseDataAccessException((SQLException) e);
        }
        else if(e instanceof JSONException)
        {
        	errorMsg = "json数据格式错误";
        }
        else 
        {
        	if(isOriginal)
        	{
        		errorMsg = StrUtil.toString(e.getMessage() == null?(e.getCause()==null?"":e.getCause().getMessage()):e.getMessage());
        		if(StrUtil.isBlank(errorMsg))
        		{
        			errorMsg = e.getStackTrace().toString();
        		}
        		if(errorMsg.length() > 2000)
        		{
        			errorMsg = errorMsg.substring(0,2000);
        		}
        	}
        	else
        	{
        		errorMsg="服务器内部错误";
        	}
        }
        return errorMsg;
    }
	
	public  static String parseException(Throwable exception) {
        Throwable e = null;
        if(exception!=null && exception.getCause()!=null){
            exception=exception.getCause();
        }
        if (exception instanceof InvocationTargetException) {
            e = exception.getCause();
        } else {
            e = exception;
        }
        String errorMsg = null;
        if (e instanceof SQLException) {
            errorMsg = psrseDataAccessException((SQLException) e);
        }
        else if(e instanceof JSONException)
        {
        	errorMsg = "json数据格式错误";
        }
        else 
        {
            errorMsg="服务器内部错误";
        }
        errorMsg = errorMsg.replace("\n", "");
        return errorMsg;
    }

    private  static String psrseDataAccessException(SQLException exception) {
        String errMsg = exception.getMessage();
        String errorMsg = null;
        if (errMsg != null && errMsg.indexOf("ORA-") != -1) {
            // 数据库错误
            String dbErrorCode = errMsg.substring(errMsg.indexOf("ORA"), errMsg.indexOf("ORA") + 9);
            int index=errMsg.indexOf(dbErrorCode)+10;
            if ("ORA-02292".equals(dbErrorCode)) {
                errorMsg = "违反外键约束";
            } else if ("ORA-01400".equals(dbErrorCode)) {
                errorMsg = errMsg.substring(index,errMsg.indexOf("\n", index))+"(非空字段为空)";
            } else if ("ORA-04098".equals(dbErrorCode)) {
                errorMsg = "触发器未编译";
            } else if ("ORA-12519".equals(dbErrorCode)) {
                errorMsg = "数据库连接不够";
            } else if ("ORA-00001".equals(dbErrorCode)) {
                errorMsg = errMsg.substring(index,errMsg.indexOf("\n", index))+"(数据已存在,请勿重复上传)";
            } else if ("ORA-00904".equals(dbErrorCode)) {
                errorMsg = "标识符无效";
            } else if ("ORA-14400".equals(dbErrorCode)) {
                errorMsg = "插入的分区关键字未映射到任何分区";
            } else if ("ORA-01438".equals(dbErrorCode)) {
                errorMsg = "值大于为此列指定的允许精度 ";
            } else if ("ORA-01407".equals(dbErrorCode)) {
                errorMsg = errMsg.substring(index,errMsg.indexOf("\n", index))+"(无法更新 ,非空字段为 NULL)";
            } else if ("ORA-12899".equals(dbErrorCode)) {
                errorMsg = errMsg.substring(index,errMsg.indexOf("\n", index));
            } else if ("ORA-01843".equals(dbErrorCode)) {
                errorMsg = "无效的月份 ";
            } else if ("ORA-01830".equals(dbErrorCode)) {
                errorMsg = "日期格式图片在转换整个输入字符串之前结束";
            } else if ("ORA-00932".equals(dbErrorCode)) {
                errorMsg = "数据类型不一致: 应为 -, 但却获得 CLOB";
            } else if ("ORA-00918".equals(dbErrorCode)) {
                errorMsg = "列名重复:";
            } else if ("ORA-20002".equals(dbErrorCode)) {
                int len = errMsg.lastIndexOf("ORA-20002");
                errorMsg = errMsg.substring(len, errMsg.indexOf("ORA", len + 1));
            } else if ("ORA-04088".equals(dbErrorCode)) {
                errorMsg = "触发器错误";
            } else if ("ORA-20006".equals(dbErrorCode)) {
                int len = errMsg.indexOf("ORA-20006");
                errorMsg = errMsg.substring(index,errMsg.indexOf("\n", index));
            } else if ("ORA-01013".equals(dbErrorCode)) {
                errorMsg = "超时,请缩小要操作的数据范围";
            } else {
                if (errMsg != null && errMsg.indexOf("ORA") > 0) {
                    errMsg = errMsg.substring(errMsg.lastIndexOf("ORA"), errMsg.length());
                }
                // 先用ORA-分割，截取它后面的 括号部分中的 约束名称
                errMsg = errMsg.split("ORA-")[1];
                if (errMsg.contains("(") && errMsg.contains(")")) {
                    errorMsg = errMsg.substring(errMsg.indexOf("(") + 1, errMsg.indexOf(")"));
                } else {
                    errorMsg = errMsg;
                }
            }
        }
        else
        {
        	errorMsg = errMsg;
        }
        return errorMsg;
    }
}
