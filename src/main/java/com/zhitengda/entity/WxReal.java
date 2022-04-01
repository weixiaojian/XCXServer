package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信实名认证表
 * </p>
 *
 * @author langao_q
 * @since 2021-02-03
 */
@Data
@TableName("TAB_WX_REAL")
public class WxReal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * guid
     */
    @TableId
    private String guid;

    /**
     * 微信openid
     */
    private String openId;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String realName;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 证件类型
     */
    private String realIdType;

    /**
     * 证件号码
     */
    @NotBlank(message = "证件号码不能为空")
    private String realIdCode;

    /**
     * 1男 0女
     */
    private Integer sex;

    /**
     * 民族
     */
    private String national;

    /**
     * 证件地址
     */
    private String realIdAddress;

    private String remark;

    private Date createDate;

    private Date updateDate;
}
