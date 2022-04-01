package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *  小程序团队表
 * </p>
 *
 * @author langao_q
 * @since 2021-06-18
 */
@Data
@TableName("TAB_WX_GROUP")
@EqualsAndHashCode(callSuper = false)
public class WxGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 团队id
     */
    private String groupId;

    /**
     * 团队名称
     */
    @NotBlank(message = "团队名称不能为空")
    private String groupName;

    /**
     * 微信openID
     */
    @TableId
    @NotBlank(message = "微信openID不能为空")
    private String openId;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 昵称（备注）
     */
    @NotBlank(message = "昵称（备注）不能为空")
    private String nickName;

    /**
     * 头像地址
     */
    @NotBlank(message = "头像地址不能为空")
    private String headimgurl;

    /**
     * 状态：0待审核 1超级管理员 2管理员 3普通成员
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 是否共享电子面单：0非共享  1共享（只有超级管理员才可以共享电子面单）
     */
    private Integer blShare;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateDate;

    /**
     * 所属网点编码
     */
    @NotNull(message = "所属网点编码不能为空")
    private String siteCode;

    /**
     * 所属网点名称
     */
    @NotNull(message = "所属网点名称不能为空")
    private String siteName;

    /**
     * 所属客户编码（管理员/普通用户）
     */
    private String customerCode;

    /**
     * 所属客户名称（管理员/普通用户）
     */
    private String customerName;

    /**
     * 是否不需要审批直接入团 1不需要审批
     */
    private Integer blAudit;

    /**
     * 电子面单库存
     */
    @TableField(exist = false)
    private Integer quantity;

    /**
     * 结算方式{0预付款，1月结)
     */
    @TableField(exist = false)
    private Integer blPaymentType;
}
