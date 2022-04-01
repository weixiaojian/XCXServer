package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 运单信息表
 * </p>
 *
 * @author langao_q
 * @since 2021-05-21
 */
@Data
@TableName("TAB_BILL")
@EqualsAndHashCode(callSuper = false)
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单编号
     */
    @TableId
    private String billCode;

    /**
     * 转入单号
     */
    private String transferCode;

    /**
     * 订单编号
     */
    private String orderBillCode;

    /**
     * 寄件日期
     */
    private Date sendDate;

    /**
     * 件数
     */
    private BigDecimal pieceNumber;

    /**
     * 录单重量
     */
    private BigDecimal billWeight;

    /**
     * 计费重量
     */
    private BigDecimal feeWeight;

    /**
     * 体积重量
     */
    private BigDecimal volumeWeight;

    /**
     * 结算重量
     */
    private BigDecimal settlementWeight;

    /**
     * 寄件网点
     */
    private String sendSite;

    /**
     * 寄件网点CODE
     */
    private String sendSiteCode;

    /**
     * 录入网点
     */
    private String registerSite;

    /**
     * 录入网点CODE到付
     */
    private String registerSiteCode;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 目的地CODE
     */
    private String destinationCode;

    /**
     * 派件网点
     */
    private String dispatchSite;

    /**
     * 派件网点CODE
     */
    private String dispatchSiteCode;

    /**
     * 派件下属网点
     */
    private String dispatchUnderlingSite;

    /**
     * 派件下属网点CODE
     */
    private String dispatchUnderlingSiteCode;

    /**
     * 结算目的地
     */
    private String feeDestination;

    /**
     * 结算目的地CODE
     */
    private String feeDestinationCode;

    /**
     * 录入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date registerDate;

    /**
     * 录入人
     */
    private String registerMan;

    /**
     * 录入人编号
     */
    private String registerManCode;

    /**
     * 寄件财务中心
     */
    private String sendFinanceCenter;

    /**
     * 寄件财务中心CODE
     */
    private String sendFinanceCenterCode;

    /**
     * 派件财务中心
     */
    private String dispatchFinanceCenter;

    /**
     * 派件财务中心CODE
     */
    private String dispatchFinanceCenterCode;

    /**
     * 寄件网点所属省份
     */
    private String sendOwnerPovince;

    /**
     * 派件网点所属省份
     */
    private String dispOwnerPovince;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 到付款
     */
    private BigDecimal topayment;

    /**
     * 代收货款
     */
    private BigDecimal goodsPayment;

    /**
     * 货款状态
     */
    private String goodsState;

    /**
     * 寄件币别
     */
    private String currency;

    /**
     * 支付方式
     */
    private String paymentType;

    /**
     * 付款方
     */
    private String paymentSide;

    /**
     * 寄件取件员---------------------------------------
     */
    private String takePieceEmployee;

    /**
     * 寄件取件员CODE
     */
    private String takePieceEmployeeCode;

    /**
     * 寄件承包区
     */
    private String sendOwnerRange;

    /**
     * 寄件承包区CODE
     */
    private String sendOwnerRangeCode;

    /**
     * 派件人
     */
    private String dispatchMan;

    /**
     * 派件人编号
     */
    private String dispatchManCode;

    /**
     * 派件承包区
     */
    private String dispOwnerRange;

    /**
     * 派件承包区CODE
     */
    private String dispOwnerRangeCode;

    /**
     * 物品名称---------------------------------------
     */
    private String goodsName;

    /**
     * 物品类型
     */
    private String goodsType;

    /**
     * 货物体积
     */
    private BigDecimal volume;

    /**
     * 货物实际价值
     */
    private BigDecimal realValue;

    /**
     * 包装及数量
     */
    private BigDecimal goodsCount;

    /**
     * 包装类型
     */
    private String packType;

    /**
     * 快件类型
     */
    private String soonPieceType;

    /**
     * 派送方式 即 送货方式（字典编码：DISPATCH_MODE：送货/自提）
     */
    private String dispatchMode;

    /**
     * 运输方式(汽运/航空)
     */
    private String classType;

    /**
     * 班次(早中晚班)
     */
    private String className;

    /**
     * 客户编号-------------------------------------------
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 市场专员
     */
    private String marketMan;

    /**
     * 市场专员CODE
     */
    private String marketManCode;

    /**
     * 开户行
     */
    private String bank;

    /**
     * 开户行地址
     */
    private String bankAddress;

    /**
     * 开户帐号
     */
    private String account;

    /**
     * 开户人
     */
    private String holder;

    /**
     * 开户人身份证号码
     */
    private String idCode;

    /**
     * 寄件人
     */
    private String sendMan;

    /**
     * 寄件人公司
     */
    private String sendManCompany;

    /**
     * 寄件人电话
     */
    private String sendManPhone;

    /**
     * 寄件人地址
     */
    private String sendManAddress;

    /**
     * 寄件省
     */
    private String sendProvince;

    /**
     * 寄件市
     */
    private String sendCity;

    /**
     * 寄件区/县
     */
    private String sendCounty;

    /**
     * 是否发货短信通知
     */
    private Integer blMessage;

    /**
     * 收件人
     */
    private String acceptMan;

    /**
     * 收件人公司
     */
    private String acceptManCompany;

    /**
     * 收件人电话
     */
    private String acceptManPhone;

    /**
     * 收件人地址
     */
    private String acceptManAddress;

    /**
     * 收件-省份
     */
    private String acceptProvince;

    /**
     * 收件-市
     */
    private String acceptCity;

    /**
     * 收件-区/县
     */
    private String acceptCounty;

    /**
     * 是否收货短信通知
     */
    private Integer blAcceptMessage;

    /**
     * 签收标识（1：已签收，0：未签收）-------------------------------------------
     */
    private Integer blSignsMarking;

    /**
     * 签收人
     */
    private String signMan;

    /**
     * 签收人CODE
     */
    private String signManCode;

    /**
     * 签收站点
     */
    private String signSite;

    /**
     * 签收站点CODE
     */
    private String signSiteCode;

    /**
     * 签收时间
     */
    private Date signDate;

    /**
     * 派送时间
     */
    private Date dispatchDate;

    /**
     * 中心审核标识----------------------------------------
     */
    private Integer blCenterAudit;

    /**
     * 中心审核人
     */
    private String centerAuditMan;

    /**
     * 中心审核人CODE
     */
    private String centerAuditManCode;

    /**
     * 中心审核时间
     */
    private Date centerAuditDate;

    /**
     * 网点审核标识
     */
    private Integer blSiteAuditor;

    /**
     * 网点审核人
     */
    private String siteAuditMan;

    /**
     * 网点审核人CODE
     */
    private String siteAuditManCode;

    /**
     * 网点审核时间
     */
    private Date siteAuditDate;

    /**
     * 回单标识-------------------------------------------
     */
    private Integer blIndenture;

    /**
     * 回单编号
     */
    private String rBillcode;

    /**
     * 问题件标识
     */
    private Integer blQuestionId;

    /**
     * 问题件原因
     */
    private String problemCause;

    /**
     * 是否退件标识
     */
    private Integer blReturn;

    /**
     * 是否转件标识
     */
    private Integer blTransfer;

    /**
     * 是否贵重物品标识
     */
    private Integer blValuables;

    /**
     * 是否特殊物品标识
     */
    private Integer blSpecial;

    /**
     * 是否淘宝件标识
     */
    private Integer blTaobao;

    /**
     * 是否重算费用标识
     */
    private Integer blCalc;

    /**
     * 保险费/保价金额--------------------------------------
     */
    private BigDecimal insurance;

    /**
     * 保价手续费(针对保价金额进行抽成的费用)
     */
    private BigDecimal insuranceFee;

    /**
     * 包装费
     */
    private BigDecimal packFee;

    /**
     * 回单费
     */
    private BigDecimal returnbillFee;

    /**
     * 客户报价运费
     */
    private BigDecimal guestFreight;

    /**
     * 其他费用
     */
    private BigDecimal otherFee;

    /**
     * 备用费用1 (使用后更改备注)
     */
    private BigDecimal otherFee1;

    /**
     * 备用费用2 (使用后更改备注)
     */
    private BigDecimal otherFee2;

    /**
     * 备用费用3 (使用后更改备注)
     */
    private BigDecimal otherFee3;

    /**
     * 备用费用4 (使用后更改备注)
     */
    private BigDecimal otherFee4;

    /**
     * 子单号------------------------------
     */
    private String billCodeSub;

    /**
     * 子单号(4000)
     */
    private String billCodeSub1;

    /**
     * 是否生成过子单标识(用于标记大字段的子单号字段是否已拆分)
     */
    private Integer blInsertSubbill;

    /**
     * 是否购买保险标识----------------------------------
     */
    private Integer blInsure;

    /**
     * 投保币别
     */
    private String insureCurrency;

    /**
     * 投保金额
     */
    private BigDecimal insureMoney;

    /**
     * 投保备注
     */
    private String insureRemark;

    /**
     * 投保状态
     */
    private Integer blInsurestatus;

    /**
     * 投保网点
     */
    private String insureSite;

    /**
     * 投保网点ID
     */
    private String insureSiteCode;

    /**
     * 投保时间
     */
    private Date insureDate;

    /**
     * 被保险人
     */
    private String customsMan;

    /**
     * 保单唯一码
     */
    private String commodityCode;

    /**
     * 保单生成人
     */
    private String insuranceMan;

    /**
     * 保单生成人CODE
     */
    private String insuranceManCode;

    /**
     * 保单生成网点
     */
    private String insuranceSite;

    /**
     * 保单生成网点CODE
     */
    private String insuranceSiteCode;

    /**
     * 保单生成时间
     */
    private Date insuranceDate;

    /**
     * 寄件图片备注--------------------------------------
     */
    private String billPicSendRmk;

    /**
     * 签收图片备注
     */
    private String billPicDispatchRmk;

    /**
     * 重量信息
     */
    private String weightInfo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改人CODE
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
     * 数据插入时间 [可用于多库同步]
     */
    private Date insertDate;

    /**
     * 数据产生来源*
     */
    private String dataFrom;

    /**
     * 打印次数
     */
    private String printNumber;

    /**
     * 目地分拨中心
     */
    private String destinationCenter;

    /**
     * 目地分拨中心编号
     */
    private String destinationCenterCode;

    /**
     * *目的省份*
     */
    private String destinationProvince;

    /**
     * *目的城市*
     */
    private String destinationCity;

    /**
     * *目的区/县*
     */
    private String destinationCounty;

    /**
     * 寄件人乡镇
     */
    private String sendTown;

    /**
     * 收件人乡镇
     */
    private String acceptTown;

    /**
     * 是否有手动修改过到付款 1 有
     */
    private Integer isEditTopayment;

    /**
     * 运单取消标识
     */
    private Integer blCancel;

    /**
     * 是否直发
     */
    private String blDirect;

    private Integer sync;

    /**
     * 寄件分拨中心
     */
    private String sendCenter;

    /**
     * 寄件分拨中心编号
     */
    private String sendCenterCode;

    /**
     * 寄件网点匹配类型（1-地址库，2-电子围栏，3-暂无，4-客户所属网点，5-众邮人工分拣）
     */
    private Integer sendSiteType;

    /**
     * 派件网点匹配类型（1-地址库，2-电子围栏，3-暂无，4-大网派，5-众邮派，6-大网人工分拣，7-大网返调度）
     */
    private Integer dispatchSiteType;

    /**
     * 实名发件人
     */
    private String realName;

    /**
     * 实名发件人身份证
     */
    private String realIdCode;

    /**
     * 客户保价费
     */
    private BigDecimal customerInsuredFee;

    /**
     * 运单拦截标识 1 是
     */
    private Integer blIntercept;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 拦截原因
     */
    private String interceptReason;

    /**
     * 取消时间
     */
    private Date cancelDate;

    /**
     * 拦截时间
     */
    private Date interceptDate;

    /**
     * 预计送达时效
     */
    private Date arriveTime;

    /**
     * 预计送达时效更新时间
     */
    private Date arriveRecordDate;

    /**
     * 实名发件人证件类型，关联数据字典t_sys_dictionary_detail.dic_code="ID_TYPE"
     */
    private String realIdType;

    /**
     * 是否项目客户件标识[1.是,0.否]
     */
    private Integer blProjectCustomer;

    /**
     * 是否修改过收件地址 1 是
     */
    private Integer blEditAcceptAddr;

    /**
     * 三段码
     */
    private String threeCode;

    /**
     * 二段码
     */
    private String twoCode;

    /**
     * 目的分拨三段码
     */
    private String destinationThreeCode;

    /**
     * 四段码
     */
    private String fourCode;

    /**
     * 一段码
     */
    private String oneCode;

    /**
     * 是否韵达匹配三段码[1.是,0.否]
     */
    private Integer blSiteMatchYd;

    /**
     * 运单状态
     */
    @TableField(exist = false)
    private String billStatus;

}
