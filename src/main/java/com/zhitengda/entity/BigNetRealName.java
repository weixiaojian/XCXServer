package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 韵达大网实名认证记录表
 * @author langao_q
 * @since 2021-08-25 15:39
 */
@Data
@TableName("TAB_REAL_NAME")
@EqualsAndHashCode(callSuper = false)
public class BigNetRealName {

    private static final long serialVersionUID = 1L;

    @TableId
    private String guid;

    /**
     * 散客/协议客户/业务员
     */
    private String type;

    /**
     * 协议客户:组织机构代码证件/税务登记证号证件/统一社会信用代码证件
     */
    private String cardType;

    /**
     * 散客:身份证号  & 协议客户:协议客户证件号  & 业务员:快递员身份证号码
     */
    private String sid;

    /**
     * 性别
     */
    private String sex;

    /**
     * 民族
     */
    private String nationality;

    /**
     * 证件地址
     */
    private String cardAddress;

    /**
     * 散客/协议客户:寄件人姓名 业务员:快递员名字
     */
    private String customerName;

    /**
     * 散客/协议客户:寄件人电话 业务员:快递员手机号
     */
    private String customerPhone;

    /**
     * 通过标识
     */
    private Integer blAudit;

    /**
     * 通过时间
     */
    private Date auditDate;

    /**
     * 创建人
     */
    private String createMan;

    /**
     * 创建人编号
     */
    private String createManCode;

    /**
     * 创建网点
     */
    private String createSite;

    /**
     * 创建网点编号
     */
    private String createSiteCode;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改人
     */
    private String modifyMan;

    /**
     * 修改人编号
     */
    private String modifyManCode;

    /**
     * 修改网点
     */
    private String modifySite;

    /**
     * 修改网点编号
     */
    private String modifySiteCode;

    /**
     * 修改时间
     */
    private Date modifyDate;

    /**
     * 数据来源
     */
    private String vSource;

    /**
     * 微信用户openId
     */
    private String openId;


}
