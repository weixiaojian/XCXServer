package com.zhitengda.vo;

import lombok.Data;

/**
 * excel下单实体类
 *
 * @author langao_q
 * @since 2021-05-17 17:46
 */
@Data
public class ExcelOrderDto {
    /**
     * 收件人名称
     */
    private String name;
    /**
     * 收件人手机号
     */
    private String phone;
    /**
     * 完整的收件地址
     */
    private String fromAddress;
    /**
     * 物品名称
     */
    private String goodsName;
    /**
     * 重量
     */
    private String weight;
    /**
     * 备注
     */
    private String remark;

    /**
     * 省、市、区、街道、详细地址
     */
    private String province;
    private String city;
    private String county;
    private String town;
    private String address;

    /**
     * 时效件：当日件、次日件
     */
    private String productType;
    /**
     * 冷藏类型：0:常温,1:冷藏,2:冷冻
     */
    private String storageType;
    /**
     * 当前数据行号
     */
    private Integer number;

    /**
     * 错误原因
     */
    private String msg;


}
