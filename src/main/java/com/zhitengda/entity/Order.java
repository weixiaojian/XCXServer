package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * *订单信息表
 * </p>
 *
 * @author langao_q
 * @since 2021-04-23
 */
@Data
@TableName("TAB_ORDER")
@EqualsAndHashCode(callSuper = false)
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号（主键）
     */
    @TableId
    private String orderBill;

    /**
     * 客户原始单号：运单号
     */
    private String originalBillCode;

    /**
     * 订单类型(预报订单/约车订单)
     */
    private String orderType;

    /**
     * 约车编号
     */
    private String aboutCarCode;

    /**
     * 订单录入网点
     */
    private String registerSite;

    /**
     * 订单录入网点CODE
     */
    private String registerSiteCode;

    /**
     * 订单录入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date registerDate;

    /**
     * 订单录入人
     */
    private String registerMan;

    /**
     * 订单录入人CODE
     */
    private String registerManCode;

    /**
     * 预约接货时间
     */
    private Date deliveryTime;

    /**
     * 接货状态（未安排、已安排、取消、接货成功、接货失败）
     */
    private String deliveryStatus;

    /**
     * 接货方式（客户自送/上门取货）
     */
    private String deliveryMode;

    /**
     * 接货网点
     */
    private String deliverySite;

    /**
     * 接货网点编号
     */
    private String deliverySiteCode;

    /**
     * 接货完成时间
     */
    private Date pickFinishDate;

    /**
     * 接货失败原因
     */
    private String pickFailReason;

    /**
     * 寄件客户编号---------------------------------------------
     */
    private String customerCode;

    /**
     * 寄件客户名称
     */
    private String customerName;

    /**
     * 寄件国家
     */
    private String sendCountry;

    /**
     * 寄件-省/州
     */
    @NotBlank(message = "寄件-省不能为空")
    private String sendProvince;

    /**
     * 寄件-市
     */
    @NotBlank(message = "寄件-市不能为空")
    private String sendCity;

    /**
     * 寄件-区/县
     */
    @NotBlank(message = "寄件-区/县不能为空")
    private String sendCounty;

    /**
     * 寄件-乡镇/街道
     */
    private String sendTown;

    /**
     * 寄件人公司
     */
    private String sendManCompany;

    /**
     * 寄件人详细地址
     */
    @NotBlank(message = "寄件人详细地址不能为空")
    private String sendManAddress;

    /**
     * 寄件地址邮编
     */
    private String sendZipCode;

    /**
     * 寄件人
     */
    @Size(min = 1, max = 10, message = "寄件人长度要在10以内")
    @NotBlank(message = "寄件人不能为空")
    private String sendMan;

    /**
     * 寄件人电话
     */
    private String sendManPhone;

    /**
     * 寄件人手机
     */
    @NotBlank(message = "寄件人手机号不能为空")
    private String sendManMobile;

    /**
     * 收件人国家---------------------------------------------
     */
    private String acceptCountry;

    /**
     * 收件-省份
     */
    @NotBlank(message = "收件-省不能为空")
    private String acceptProvince;

    /**
     * 收件-市
     */
    @NotBlank(message = "收件-市不能为空")
    private String acceptCity;

    /**
     * 收件-区/县
     */
    @NotBlank(message = "收件-区/县不能为空")
    private String acceptCounty;

    /**
     * 收件-乡镇
     */
    private String acceptTown;

    /**
     * 收件人公司
     */
    private String acceptManCompany;

    /**
     * 收件人地址
     */
    @NotBlank(message = "收件人详细地址不能为空")
    private String acceptManAddress;

    /**
     * 收件地址邮编
     */
    private String acceptZipCode;

    /**
     * 收件人
     */
    @Size(min = 1, max = 10, message = "收件人长度要在10以内")
    @NotBlank(message = "收件人不能为空")
    private String acceptMan;

    /**
     * 收件人电话
     */
    private String acceptManPhone;

    /**
     * 收件人手机---------------------------------------------
     */
    @NotBlank(message = "收件人手机号不能为空")
    private String acceptManMobile;

    /**
     * 业务类型（小包、专线、国际快递、国内干线、物流）
     */
    private String businessType;

    /**
     * 产品类型（无/当日件/次日件）
     */
    private String productType;

    /**
     * 货物总名称
     */
    @NotBlank(message = "货物名称不能为空")
    private String goodsName;

    /**
     * 货物总体积
     */
    private BigDecimal goodsVolume;

    /**
     * 货物总重量
     */
    @Max(value = 60, message = "货物重量不能超过60KG")
    @NotNull(message = "货物重量不能为空")
    private BigDecimal goodsWeight;

    /**
     * 货物总包装件数
     */
    @Max(value = 1, message = "下单件数只能为1件")
    @NotNull(message = "货物件数不能为空")
    private Long packingPiece;

    /**
     * 代收货款
     */
    private BigDecimal goodsPayment;

    /**
     * 代收货款币别
     */
    private String goodsPaymentCurrency;

    /**
     * 保价金额
     */
    private BigDecimal insuredValue;

    /**
     * 保价金额币别---------------------------------------------
     */
    private String insuredValueCurrency;

    /**
     * 派车时间
     */
    private Date dispCarDate;

    /**
     * 派车人
     */
    private String dispCarMan;

    /**
     * 派车人编号
     */
    private String dispCarManCode;

    /**
     * 司机接货出发时间（APP回写）
     */
    private Date sendCarDate;

    /**
     * 到达取货地点时间（APP回写）
     */
    private Date comeCarDate;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * *数据来源（APP/微信/K9/OMS/API接口等）
     */
    private String dataFrom;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改人CODE/回写业务来源
     */
    private String modifierCode;

    /**
     * 修改时间
     */
    private Date modifyDate;

    /**
     * 修改站点
     */
    private String modifySite;

    /**
     * 修改站点CODE
     */
    private String modifySiteCode;

    /**
     * 订单状态（关联字典表t_sys_dictionary_detail.dic_code='ORDER_STATUS'）
     */
    private String orderStatus;

    /**
     * 接单员工
     */
    private String recMan;

    /**
     * 接单员工CODE
     */
    private String recManCode;

    /**
     * 打印状态（=1表示已打印，=0未打印）
     */
    private Integer blPrint;

    /**
     * 打印人
     */
    private String printMan;

    /**
     * 打印网点
     */
    private String printSite;

    /**
     * 打印时间
     */
    private Date printDate;

    /**
     * 转入单号
     */
    private String transferCode;

    /**
     * 到付款
     */
    private BigDecimal topayment;

    /**
     * 寄件网点
     */
    private String sendSite;

    /**
     * 寄件网点CODE
     */
    private String sendSiteCode;

    /**
     * 派件网点
     */
    private String dispatchSite;

    /**
     * 派件网点CODE
     */
    private String dispatchSiteCode;

    /**
     * 微信下单标识
     */
    private String openId;

    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;

    /**
     * 运单号
     */
    private String billCode;

    /**
     * 支付方式  到付 现金 月结
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentType;

    /**
     * 已分配网点时间
     */
    private Date assignSiteDate;

    /**
     * 网点接单时间
     */
    private Date acceptSiteDate;

    /**
     * 揽件时间
     */
    private Date pickupDate;

    /**
     * 寄件人GUID
     */
    private String sendManGuid;

    /**
     * 收件人GUID
     */
    private String acceptManGuid;

    /**
     * 运费
     */
    private BigDecimal freight;

    private Integer sync;

    /**
     * 收件原始地址
     */
    private String originalAcceptAddress;

    /**
     * 寄件网点匹配类型（1-地址库，2-电子围栏，3-暂无，4-客户所属网点，5-众邮人工分拣）
     */
    private Integer sendSiteType;

    /**
     * 派件网点匹配类型（1-地址库，2-电子围栏，3-暂无，4-大网派，5-众邮派，6-大网人工分拣）
     */
    private Integer dispatchSiteType;

    /**
     * 客户预约开始时间(数据库是Date 此处修改为String)
     */
    private String customerDeliveryBeginTime;

    /**
     * 客户预约结束时间(数据库是Date 此处修改为String)
     */
    private String customerDeliveryEndTime;

    /**
     * 客户保价费
     */
    private BigDecimal customerInsuredFee;

    /**
     * 实名发件人
     */
    private String realName;

    /**
     * 实名证件号
     */
    private String realIdCode;

    /**
     * 派送方式（默认派送，派送或者自提）
     */
    private String dispatchMode;

    /**
     * 订单打印次数
     */
    private BigDecimal printCount;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 取消时间
     */
    private Date cancelDate;

    /**
     * 打印批次号
     */
    private String printBatchNo;

    /**
     * 打印序号
     */
    private BigDecimal printOrderNo;

    /**
     * 实名证件类型，关联数据字典t_sys_dictionary_detail.dic_code="ID_TYPE"
     */
    private String realIdType;

    /**
     * EXCL导入批次号
     */
    private String exclImportNum;

    /**
     * EXCL导入批次(行数index)
     */
    private BigDecimal exclImportNumRow;

    /**
     * 寄件人邮箱
     */
    private String sendEmail;

    /**
     * 收件人邮箱
     */
    private String acceptEmail;

    /**
     * 运输方式(汽运/航空)
     */
    private String classType;

    /**
     * 是否项目客户件标识[1.是,0.否]
     */
    private Integer blProjectCustomer;

    /**
     * 是否特殊品[1.是,0.否]
     */
    private Integer blSpecial;

    /**
     * 货物类型
     */
    private String goodsType;

    /**
     * 派件员
     */
    private String dispatchMan;

    /**
     * 派件员编号
     */
    private String dispatchManCode;

    /**
     * 是否有派件调度  1 是
     */
    private Integer blDispatchSiteCode;

    /**
     * 派件调度时间
     */
    private Date dispatchSiteDate;

    /**
     * 三段码
     */
    private String threeCode;

    /**
     * 二段码
     */
    private String twoCode;

    /**
     * 目地分拨中心
     */
    private String destinationCenter;

    /**
     * 目地分拨中心编号
     */
    private String destinationCenterCode;

    /**
     * 团队ID
     */
    private String groupId;

    /**
     * 团队成员名称
     */
    private String groupMember;
    /**
     * 冷藏类型(0:常温,1:冷藏,,2:冷冻)
     */
    private String storageType;

    /**
     * 运单状态
     */
    @TableField(exist = false)
    private String billStatus;

    /**
     * 下单失败原因
     */
    @TableField(exist = false)
    private String errMsg;

}
