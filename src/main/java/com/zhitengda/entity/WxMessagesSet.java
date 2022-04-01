package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信消息推送设置表
 * </p>
 *
 * @author langao_q
 * @since 2021-04-30
 */
@Data
@TableName("TAB_WX_MESSAGES_SET")
@EqualsAndHashCode(callSuper = false)
public class WxMessagesSet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * openid
     */
    @TableId
    private String openId;

    /**
     * 寄件人-揽收成功（1开启 0关闭）
     */
    private Integer blSendOrder;

    /**
     * 寄件人-签收成功（1开启 0关闭）
     */
    private Integer blSendSign;

    /**
     * 寄件人-快递派送（1开启 0关闭）
     */
    private Integer blSendDisp;

    /**
     * 收件人-快递发出（1开启 0关闭）
     */
    private Integer blRecOut;

    /**
     * 收件人-快递派送（1开启 0关闭）
     */
    private Integer blRecDisp;

    /**
     * 收件人-签收成功（1开启 0关闭）
     */
    private Integer blRecSign;

    /**
     * 创建日 期
     */
    private Date createDate;

    /**
     * 修改日期
     */
    private Date updateDate;
}
