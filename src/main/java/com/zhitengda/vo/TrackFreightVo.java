package com.zhitengda.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author langao_q
 * @since 2021-04-27 21:09
 */
@Data
public class TrackFreightVo {

    /**
     * 寄件城市
     */
    @NotBlank(message = "寄件城市不能为空")
    private String sendCity;
    /**
     * 收件省
     */
    @NotBlank(message = "收件省不能为空")
    private String acceptProvince;
    /**
     * 收件市
     */
    @NotBlank(message = "收件市不能为空")
    private String acceptCity;
    /**
     * 收件区
     */
    @NotBlank(message = "收件区不能为空")
    private String acceptCounty;
    /**
     * 重量
     */
    @NotBlank(message = "重量不能为空")
    private String goodsWeight;
}
