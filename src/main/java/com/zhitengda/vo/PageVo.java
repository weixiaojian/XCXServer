package com.zhitengda.vo;

import lombok.Data;

/**
 * @author langao_q
 * @since 2021-02-04 15:47
 */
@Data
public class PageVo {

    /**
     * 起始页
     */
    protected Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    protected Integer pageSize = 10;

    /**
     * 排序列
     */
    protected String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    protected String isAsc = "asc";

    /**
     * openId
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
     * 0 未打印，1 已打印（去掉不用了）
     */
    private Integer blType;

    /**
     * 运单号
     */
    private String billCode;
    /**
     * 运单状态：未打印、已打印、待揽收、已揽收、在途、派件中、已签收、已取消
     */
    private String billStatus;
    /**
     * 起始时间
     */
    private String startDate;
    /**
     * 终止时间
     */
    private String endDate;
    /**
     * 团队Id
     */
    private String groupId;

    /**
     * 寄件网点编码
     */
    private String sendSiteCode;

    /**
     * 寄件客户编码
     */
    private String customerCode;
}
