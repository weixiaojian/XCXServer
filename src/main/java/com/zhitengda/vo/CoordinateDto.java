package com.zhitengda.vo;

import lombok.Data;

/**
 * 经纬度坐标
 * @author langao_q
 * @since 2021-03-01 11:07
 */
@Data
public class CoordinateDto {

    /**
     * 经度
     */
    private double longitude;
    /**
     * 维度
     */
    private double latitude;

    public CoordinateDto(){}

    public CoordinateDto(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
