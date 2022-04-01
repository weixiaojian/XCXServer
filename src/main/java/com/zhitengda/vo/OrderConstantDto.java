package com.zhitengda.vo;

import lombok.Data;

/**
 * 下单常量配置实体类
 * @author langao_q
 * @since 2021-04-27 15:12
 */
@Data
public class OrderConstantDto {

    /**
     * 字典编码
     */
    private String dicCode;
    /**
     * 字典值
     */
    private String dicValue;
    /**
     * 字典值名称
     */
    private String dicValueName;
    /**
     * 字典显示值
     */
    private String dicDisValue;

}
