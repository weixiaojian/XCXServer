package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *  模板消息实体类
 * </p>
 *
 * @author langao_q
 * @since 2021-03-10
 */
@Data
@TableName("TAB_WX_MESSAGES")
public class WxMessages implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * GUID
     */
    @TableId
    private String guid;

    /**
     * OPENID
     */
    private String openId;

    /**
     * 订单状态
     */
    private String type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 运单编号
     */
    private String billCode;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 操作网点
     */
    private String scanSite;

    /**
     * 操作网点手机号
     */
    private String scanSitePhone;

    /**
     * 操作人
     */
    private String scanMan;

    /**
     * 操作人手机号
     */
    private String scanManPhone;

    /**
     * 操作时间
     */
    private Date scanDate;

    /**
     * 备用字段1
     */
    private String field1;

    /**
     * 备用字段2
     */
    private String field2;

    /**
     * 状态：0表示待推送 1表示推送成功，2表示推送失败
     */
    private Integer blStatus;

    /**
     * 推送时间
     */
    private Date dealDate;

    /**
     * 错误原因
     */
    private String errorMsg;

    /**
     * 数据插入时间
     */
    private Date insertDate;


}
