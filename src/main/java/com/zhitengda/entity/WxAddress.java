package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信用户地址表
 * </p>
 *
 * @author langao_q
 * @since 2021-04-27
 */
@Data
@TableName("TAB_WX_ADDRESS")
@EqualsAndHashCode(callSuper = false)
public class WxAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    @TableId
    private String guid;

    /**
     * 微信用户ID
     */
    private String openId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 省ID
     */
    private String proviceId;

    /**
     * 省
     */
    private String provice;

    /**
     * 市ID
     */
    private String cityId;

    /**
     * 市
     */
    private String city;

    /**
     * 县/区ID
     */
    private String countryId;

    /**
     * 区
     */
    private String country;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 公司
     */
    private String company;

    /**
     * 是否默认地址(0否 1是)
     */
    private Integer defaultValue;

    /**
     * 0,寄件，1，收件
     */
    private Integer blType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 街道ID
     */
    private String townId;

    /**
     * 街道
     */
    private String townName;


}
