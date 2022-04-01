package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信AccessToken表
 * </p>
 *
 * @author langao_q
 * @since 2021-05-26
 */
@Data
@TableName("TAB_WX_TOKEN")
@EqualsAndHashCode(callSuper = false)
public class WxToken implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * appId
     */
    @TableId
    private String appId;

    /**
     * token
     */
    private String token;

    /**
     * 有效时间 秒（7200秒过期）
     */
    private Long expiresIn;

    /**
     * 更新时间
     */
    private Date updateDate;

}
