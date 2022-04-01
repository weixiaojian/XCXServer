package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信用户表(小程序)
 * </p>
 *
 * @author langao_q
 * @since 2021-02-01
 */
@Data
@TableName("TAB_WX_USER")
@EqualsAndHashCode(callSuper = false)
public class WxUser implements Serializable {

    private static final long serialVersionUID = 1L;

    public WxUser(){
    }

    public WxUser(String openId, String unionid, String phone, String sessionKey, Integer isLogin){
        this.openId = openId;
        this.unionid = unionid;
        this.phone = phone;
        this.sessionKey = sessionKey;
        this.isLogin = isLogin;
    }
    /**
     * 微信openID
     */
    @TableId
    private String openId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 网点编码
     */
    private String siteCode;

    /**
     * 员工编码
     */
    private String employeeCode;

    /**
     * 绑定的网点名称
     */
    private String siteName;

    /**
     * 绑定的员工名称
     */
    private String employeeName;

    /**
     * 是否登陆（１，是，０，否）
     */
    private Integer isLogin;

    /**
     * 微信返回的session_key
     */
    private String sessionKey;

    /**
     * 数据来源
     */
    private String dataFrom;

    /**
     * unionid
     */
    private String unionid;

    private Integer sync;

    private Date createDate;

    private Date updateDate;

    private String headimgurl;

    private String nickName;

    /**
     * 月结客户编码、名称
     */
    private String customerCode;
    private String customerName;
}
