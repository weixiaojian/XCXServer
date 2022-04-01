package com.zhitengda.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * FrameTab-系统全局设置表
 * </p>
 *
 * @author langao_q
 * @since 2022-01-05
 */
@Data
@TableName("T_SYS_SHARE_SET")
@EqualsAndHashCode(callSuper = false)
public class SysShareSet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 共享设置-参数编号
     */
    private String shareCode;

    /**
     * 共享设置-参数名称
     */
    private String shareName;

    /**
     * 共享设置-参数值
     */
    private String shareValue;

    /**
     * 共享设置-参数描述
     */
    private String shareDesc;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 更新人
     */
    private String updateMan;

    /**
     * 设置规则
     */
    private String shareRule;

    /**
     * 设置规则2
     */
    private String shareRule2;

    /**
     * 所属配置
     */
    private String ownerConfig;

    /**
     * 平台代码(0.TMS,1.OMS,2.APP,微信)
     */
    private Integer platformCode;

    private Integer sync;


}
