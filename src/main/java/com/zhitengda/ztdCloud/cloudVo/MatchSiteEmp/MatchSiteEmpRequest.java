package com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 匹配揽件/派件-网点及员工请求实体类
 *
 * @author langao_q
 * @since 2021-04-26 17:23
 */
@Data
public class MatchSiteEmpRequest {

    /**
     * 请求id
     */
    private String id;
    private String orderBillCode;
    private String billCode;
    /**
     * 匹配类型：0(默认)：揽派，1：揽收(寄件人信息不能为空)，2：派件(收件人信息不能为空)
     */
    private Integer matchingType;
    /**
     * 寄件人相关数据
     */
    private String sendProvince;
    private String sendCity;
    private String sendCounty;
    private String sendTown;
    private String sendMan;
    private String sendManAddress;
    /**
     * 收件人相关数据
     */
    private String acceptProvince;
    private String acceptCity;
    private String acceptCounty;
    private String acceptTown;
    private String acceptMan;
    private String acceptManAddress;
    /**
     * 寄件网点及员工
     */
    private String sendSite;
    private String sendSiteCode;
    private String sendEmployeeCode;
    private String sendEmployeeName;
    private String takePieceEmployee;
    private String takePieceEmployeeCode;
    /**
     * 派件网点及员工
     */
    private String dispatchSite;
    private String dispatchSiteCode;
    private String dispatchMan;
    private String dispatchManCode;
    private String goodsWeight;

    /**
     * 保价金额
     * 代收货款
     * 到付款
     * 件数
     */
    private BigDecimal insuredValue;
    private BigDecimal goodsPayment;
    private BigDecimal topayment;
    private Long packingPiece;
}
