package com.zhitengda.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 物流轨迹返回实体类
 * @author langao_q
 * @since 2021-02-26 11:30
 */
@Data
public class RetScanPathDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     */
    private String billCode;

    /**
     * 扫描日期
     */
    private String scanDate;
    /**
     * 扫描日期2
     */
    private String scanDate2;
    /**
     * 扫描日期3
     */
    private String scanDate3;
    /**
     * 扫描类型
     */
    private String scanType;
    /**
     * 扫描网点
     */
    private String scanSite;
    /**
     * 上一站/下一站
     */
    private String preOrNextStation;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 扫描人
     */
    private String scanMan;
}
