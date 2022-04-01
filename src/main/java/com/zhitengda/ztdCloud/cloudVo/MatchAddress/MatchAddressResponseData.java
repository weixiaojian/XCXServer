package com.zhitengda.ztdCloud.cloudVo.MatchAddress;

import lombok.Data;

/**
 * 地址解析响应数据实体类
 * @author langao_q
 * @since 2021-05-07 14:20
 */
@Data
public class MatchAddressResponseData {

    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String cityName;
    /**
     * 区
     */
    private String countyName;
    /**
     * 街道
     */
    private String townName;
    /**
     * 详细地址
     */
    private String formatAddress;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 姓名
     */
    private String name;
    /**
     * 备注
     */
    private String remark;

    private String landlinePhone;
    private String dataFrom;
    private String reqAddress;
}
