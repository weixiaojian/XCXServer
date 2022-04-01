package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 基本资料-寄件客户资料表
 * </p>
 *
 * @author langao_q
 * @since 2021-06-21
 */
@Data
@TableName("TAB_CUSTOMER")
@EqualsAndHashCode(callSuper = false)
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户编号
     */
    @TableId
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户全称
     */
    private String customerFullName;

    /**
     * 客户所属网点
     */
    private String customerOwnerSite;

    /**
     * 客户所属网点ID
     */
    private String customerOwnerSiteCode;

    /**
     * 客户结算类型 现金/支票/月结
     */
    private String customerBalanceMode;

    /**
     * 客户联系人
     */
    private String customerLinkman;

    /**
     * 客户电话1
     */
    private String customerPhone1;

    /**
     * 客户电话2
     */
    private String customerPhone2;

    /**
     * 客户传真
     */
    private String customerFax;

    /**
     * 客户地址
     */
    private String customerAddress;

    /**
     * 客户寄件地
     */
    private String custSendAddress;

    /**
     * 客户类型(VIP客户/代理等)
     */
    private String customerType;

    /**
     * 所属业务员
     */
    private String operationEmployee;

    /**
     * 业务员编号
     */
    private String employeeCode;

    /**
     * 所属外务员
     */
    private String fieldMan;

    /**
     * 客户折扣
     */
    private BigDecimal customerRebate;

    /**
     * 客户密码
     */
    private String balancePassword;

    /**
     * 邮政编号
     */
    private String postCode;

    /**
     * 网址
     */
    private String url;

    /**
     * E-Mail
     */
    private String email;

    /**
     * 开户银行卡号
     */
    private String bankCode;

    /**
     * 开户银行名称
     */
    private String bankName;

    /**
     * 开户行地址
     */
    private String bankAddress;

    /**
     * 开户人
     */
    private String holder;

    /**
     * 开户人身份证号码
     */
    private String idCode;

    /**
     * 开户卡号账户类型（对公/对私）
     */
    private String haccountType;

    /**
     * 是否启用(0.否 1.是)
     */
    private Integer blOpen;

    /**
     * 客户分类(用于界面树形显示)
     */
    private String customerSiteType;

    /**
     * 备注
     */
    private String remark;

    /**
     * GUID唯一值
     */
    private String guid;

    /**
     * 客户等级(钻石/铂金/黄金/白银/普通)
     */
    private String customerLevel;

    /**
     * 联系QQ
     */
    private String linkQq;

    /**
     * 营业执照号码
     */
    private String businessNumber;

    /**
     * 注册地址
     */
    private String businessAddress;

    /**
     * 创建人员姓名
     */
    private String createMan;

    /**
     * 创建人编号
     */
    private String createManCode;

    /**
     * 创建站点
     */
    private String createSite;

    /**
     * 创建站点编号
     */
    private String createSiteCode;

    /**
     * 创建时间
     */
    private Date createDate;

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
     * 客户所属国家
     */
    private String country;

    /**
     * 客户所属省份
     */
    private String province;

    /**
     * 客户所属城市
     */
    private String city;

    /**
     * 客户所属区县
     */
    private String county;

    /**
     * 是否允许登录[0:不允许,1:允许]
     */
    private String loginFlag;

    /**
     * 开户银行联行号
     */
    private String bankKey;

    /**
     * 代收货款服务费
     */
    private BigDecimal goodsPayment;

    /**
     * 服务费百分比
     */
    private BigDecimal feePercentage;

    /**
     * 返款周期（默认值T+3【T+0、T+1、T+2、T+3】
     */
    private String rebateCycle;

    /**
     * 是否允许代收货款 0-不允许 1-允许
     */
    private Integer blAllowAgentMoney;

    /**
     * 是否允许到付 0-不允许  1-允许
     */
    private Integer blAllowTopayment;

    private Integer sync;

    /**
     * 实名发件人
     */
    private String realName;

    /**
     * 实名发件人证件号码
     */
    private String realIdCode;

    /**
     * 实名发件人证件类型，关联数据字典t_sys_dictionary_detail.dic_code="ID_TYPE"
     */
    private String realIdType;

    /**
     * 是否实名认证标识，空或者0标识未实名，1标识已实名
     */
    private Integer blCert;

    /**
     * 实名认证时间
     */
    private Date certDate;

    /**
     * 是否为项目客户  1 是
     */
    private Integer blProjectCustomer;

    /**
     * 是否支持一票多件 1 是
     */
    private Integer blMultiple;

    /**
     * 商家编码：关联TAB_PLATFORM_POP_APPLY中的  VENDOR_CODE
     */
    private String vendorCode;

    /**
     * 用户接口秘钥
     */
    private String interfaceKey;

    /**
     * 是否检查面单额度[1.是,0.否]
     */
    private Integer blCheckEleCount;

    /**
     * 是否检查预付款额度[1.是,0.否]
     */
    private Integer blCheckAccount;

    /**
     * 算费是否检查分拨到件时间
     */
    private Integer blCheckCenterComeDate;


}
