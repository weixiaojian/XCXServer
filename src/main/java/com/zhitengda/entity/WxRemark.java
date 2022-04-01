package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信备注表
 * </p>
 *
 * @author langao_q
 * @since 2021-08-03
 */
@Data
@TableName("TAB_WX_REMARK")
@EqualsAndHashCode(callSuper = false)
public class WxRemark implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private String guid;

    /**
     * 微信用户标识
     */
    private String openId;

    /**
     * 备注内容
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;


}
