package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 基本资料-网点资料表
 * </p>
 *
 * @author langao_q
 * @since 2021-04-28
 */
@Data
@TableName("TAB_SITE")
@EqualsAndHashCode(callSuper = false)
public class Site implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 网点编号
     */
    @TableId
    private String siteCode;

    /**
     * 网点名称
     */
    private String siteName;

    /**
     * 派件所属网点CODE
     */
    private String superiorSiteCode;

    /**
     * 派件所属网点
     */
    private String superiorSite;

    /**
     * 货币类型
     */
    private String defaultCurrency;

    /**
     * 所属财务中心CODE
     */
    private String superiorFinanceCenterCode;

    /**
     * 所属财务中心
     */
    private String superiorFinanceCenter;

    /**
     * 网点类型
     */
    private String type;

    /**
     * 默认发件地名称CODE
     */
    private String defaultDestinationNameCode;

    /**
     * 默认发件地
     */
    private String defaultSendPlace;

    /**
     * 国家
     */
    private String country;

    /**
     * 所属省份 / 省份编号
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String county;

    /**
     * 乡/镇
     */
    private String town;

    /**
     * 所属片区名称
     */
    private String areaName;

    /**
     * 所属区域名称
     */
    private String rangeName;

    /**
     * 查询电话 --加密
     */
    private String phone;

    /**
     * 站点负责人 --加密
     */
    private String principal;

    /**
     * 站点经理  --加密
     */
    private String manager;

    /**
     * 站点经理电话  --加密
     */
    private String managerPhone;

    /**
     * 业务电话 --加密
     */
    private String salePhone;

    /**
     * 紧急联系电话  --加密
     */
    private String exigencePhone;

    /**
     * 传真电话 --加密
     */
    private String fax;

    /**
     * 网点简介
     */
    private String siteDesc;

    /**
     * 派送范围
     */
    private String dispatchRange;

    /**
     * 不派送范围
     */
    private String notDispatchRange;

    /**
     * 对外备注
     */
    private String publicRemark;

    /**
     * 内部备注
     */
    private String privateRemark;

    /**
     * 排序使用
     */
    private Integer orderBy;

    /**
     * 财务帐号
     */
    private String financialAccount;

    /**
     * 此网点淘宝服务负责人
     */
    private String tbSerName;

    /**
     * 此网点淘宝服务电话
     */
    private String tbSerTel;

    /**
     * 此网点淘宝服务旺旺帐号
     */
    private String tbSerWw;

    /**
     * 站点班车要求最早到达时间
     */
    private String carStartTime;

    /**
     * 站点班车要求最晚到达时间
     */
    private String carEndTime;

    /**
     * WEB显示
     */
    private Integer blWeb;

    /**
     * 加盟时间
     */
    private Date joinTime;

    /**
     * 地图坐标
     */
    private String mapsCoordinate;

    /**
     * 特殊服务区域
     */
    private String specserviceRange;

    /**
     * QQ号码
     */
    private String qq;

    /**
     * 联系邮箱  --加密
     */
    private String email;

    /**
     * 保证金
     */
    private BigDecimal serviceQuality;

    /**
     * 销售等级评定
     */
    private BigDecimal salesQuality;

    /**
     * 网点级别
     */
    private String levels;

    /**
     * 所属仓库
     */
    private String warehouseName;

    /**
     * 网点权限组别
     */
    private String siteLimite;

    /**
     * 转帐通知手机
     */
    private String jyMobile;

    /**
     * 转账密码
     */
    private String jyPwd;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建网点
     */
    private String createSite;

    /**
     * 创建网点编号
     */
    private String createSiteCode;

    /**
     * 创建人
     */
    private String createMan;

    /**
     * 创建人CODE
     */
    private String createManCode;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改人编号
     */
    private String modifierCode;

    /**
     * 修改站点
     */
    private String modifySite;

    /**
     * 修改站点CODE
     */
    private String modifySiteCode;

    /**
     * 修改时间
     */
    private Date modifyDate;

    /**
     * 1：网点停用，0：网点启用
     */
    private Integer blNotInput;

    /**
     * 停用时间
     */
    private Date stopDate;

    /**
     * 寄件代收货款 0-不允许 1-允许
     */
    private Integer blAllowAgentMoney;

    /**
     * 是否允许到付 0-不允许  1-允许
     */
    private Integer blAllowTopayment;

    /**
     * 寄件代收货款金额上限
     */
    private BigDecimal goodsPaymentLimited;

    /**
     * 到付款金额上限
     */
    private BigDecimal topaymentLimited;

    /**
     * 是否开通淘宝业务(1：表示开通)
     */
    private Integer blOpenTaobao;

    /**
     * 是否属于仲裁部门
     */
    private Integer blArbitrationDepartment;

    /**
     * 是否允许发送短信1：表示可以
     */
    private Integer blMessage;

    /**
     * 是否检查运单发放
     */
    private Integer blProvide;

    /**
     * 是否项目客户
     */
    private Integer blProjectclient;

    /**
     * 是否录单产生账单（1：表示产生）
     */
    private Integer blBilltoAccount;

    /**
     * *账套编号 (用于支持清账功能)
     */
    private String accountCode;

    /**
     * 网点祥细地址  --加密
     */
    private String address;

    /**
     * 数据来源
     */
    private String dataFrom;

    /**
     * 网点大小（平方米）
     */
    private BigDecimal siteArea;

    /**
     * 派件代收货款 0-不允许 1-允许
     */
    private Integer blAllowDispAgentMoney;

    /**
     * 派件代收货款金额上限
     */
    private BigDecimal dispGoodsPaymentLimited;

    /**
     * 企业全称
     */
    private String vendorName;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    private Integer sync;

    /**
     * 网点经纬度
     */
    private String sitePos;

    /**
     * 分拣代码
     */
    private String distributeCode;

    /**
     * 更改网点名称时间
     */
    private Date updateNameDate;

    /**
     * 是否不能揽收  1 不能寄件
     */
    private Integer blNotRec;

    /**
     * 是否不能派件  1 不能派件
     */
    private Integer blNotDisp;

    /**
     * 税号
     */
    private String taxId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 公司电话
     */
    private String companyPhone;

    /**
     * 开户银行名称
     */
    private String bankName;

    /**
     * 开户银行账号
     */
    private String bankAccount;

    /**
     * 注册地址
     */
    private String registerAddress;

    /**
     * 资质编号
     */
    private String qualificationsId;

    /**
     * 纳税人类型 : 一般纳税人  小规模纳税人
     */
    private String taxType;

    /**
     * 是否开发票  1是 0否
     */
    private Integer blInvoice;


    /**
     * 允许小程序创建团队(0不允许1允许)
     */
    private Integer teamStatus;

    /**
     * 是否隐藏打印信息（0不1隐藏）
     */
    private Integer hiddenManage;

    /**
     * 是否允许时效件（0-否，1-是）
     */
    private Integer blProductType;
    /**
     * 结算方式{0预付款，1月结)
     */
    private Integer blPaymentType;
    /**
     * 是否有冷柜(0-否,1-是)
     */
    private Integer blFreshtank;
}
