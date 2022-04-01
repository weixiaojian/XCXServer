package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信投诉建议表
 * </p>
 *
 * @author langao_q
 * @since 2021-08-18
 */
@Data
@TableName("TAB_WX_ADVICE")
@EqualsAndHashCode(callSuper = false)
public class WxAdvice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private String guid;

    /**
     * 微信用户标识
     */
    @NotBlank(message = "用户标识不能为空")
    private String openId;

    /**
     * 问题类型
     */
    @NotNull(message = "问题类型不能为空")
    private Integer type;

    /**
     * 建议内容
     */
    @NotBlank(message = "建议内容不能为空")
    private String content;

    /**
     * 创建时间
     */
    private Date createDate;


}
