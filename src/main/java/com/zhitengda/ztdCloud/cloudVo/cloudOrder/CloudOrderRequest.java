package com.zhitengda.ztdCloud.cloudVo.cloudOrder;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 云新增订单请求实体类
 *
 * @author langao_q
 * @since 2021-04-26 17:33
 */
@Data
public class CloudOrderRequest {

    private String orderBill;
    private String openId;
    private String billCode;
    private String saveId;
    /**
     * 寄件
     */
    private String sendProvince;
    private String sendCity;
    private String sendCounty;
    private String sendTown;
    private String sendManCompany;
    private String sendManAddress;
    private String sendMan;
    private String sendManPhone;
    private String sendManMobile;
    /**
     * 收件
     */
    private String acceptProvince;
    private String acceptCity;
    private String acceptCounty;
    private String acceptTown;
    private String acceptManCompany;
    private String acceptManAddress;
    private String acceptMan;
    private String acceptManPhone;
    private String acceptManMobile;
    /**
     * 寄件客户
     */
    private String customerCode;
    private String customerName;
    /**
     * 揽/派站点及员工
     */
    private String sendSiteCode;
    private String sendSite;
    private String recManCode;
    private String recMan;
    private String dispatchSiteCode;
    private String dispatchSite;
    private String dispatchManCode;
    private String dispatchMan;
    private String registerSiteCode;
    private String registerSite;
    private String registerManCode;
    private String registerMan;
    private String registerDate;
    private Integer sendSiteType;
    private Integer dispatchSiteType;
    /**
     * 支付方式  到付 现金 月结
     */
    private String paymentType;
    /**
     * 订单状态
     */
    private String orderStatus;
    /**
     * 运费
     */
    private BigDecimal freight;
    /**
     * 保价金额
     */
    private BigDecimal insuredValue;
    /**
     * 货物总重量
     */
    private BigDecimal goodsWeight;
    /**
     * 货物总包装件数
     */
    private Long packingPiece;
    /**
     * 货物类型
     */
    private String goodsType;
    /**
     * 货物总名称
     */
    private String goodsName;
    /**
     * 产品类型（无/当日件/次日件）
     */
    private String productType;
    /**
     * 预约时间
     */
    private String customerDeliveryBeginTime;
    private String customerDeliveryEndTime;
    /**
     * 实名信息
     */
    private String realIdCode;
    private String realIdType;
    private String realName;
    /**
     * 数据来源（APP/微信/K9/OMS/API接口等）
     */
    private String dataFrom;
    /**
     * 订单备注
     */
    private String remark;
    /**
     * 修改人信息（微信）
     */
    private String modifier;
    private String modifierCode;
    private String modifySite;
    private String modifySiteCode;

    /**
     * 取消原因
     */
    private String cancelReason;
    /**
     * 团队ID
     */
    private String groupId;

    /**
     * 团队成员名称
     */
    private String groupMember;
    /**
     * 一段码、二段码、三段码、四段码
     */
    private String oneCode;
    private String twoCode;
    private String threeCode;
    private String fourCode;
    /**
     * 冷藏类型(0:常温,1:冷藏,2:冷冻)
     */
    private String storageType;
}
