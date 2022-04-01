package com.zhitengda.web.exception;


/**
 * 自定义异常
 * @author langao_q
 * @since 2020-11-24 17:55
 */
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private String cm;

    public GlobalException(String cm) {
        super(cm);
        this.cm = cm;
    }

    public String getCm() {
        return cm;
    }
}
