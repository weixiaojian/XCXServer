package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 基本资料-区县表
 * </p>
 *
 * @author langao_q
 * @since 2021-02-23
 */
@Data
@TableName("TAB_COUNTY")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class County implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 区/县名称
     */
    private String countyName;

    /**
     * 区/县编号/代码
     */
    private String countyCode;

    /**
     * 城市编号/城市代码
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 邮政编码
     */
    private String mailCode;

    /**
     * GUID唯一值
     */
    @TableId
    private String guid;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 省份编号
     */
    private String provinceCode;

    /**
     * 修改网点
     */
    private String modifySite;

    /**
     * 修改网点编号
     */
    private String modifySiteCode;

    /**
     * 修改人
     */
    private String modifyMan;

    /**
     * 修改人编号
     */
    private String modifyManCode;

    /**
     * 修改时间
     */
    private Date modifyDate;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 数据来源
     */
    private String dataFrom;

    /**
     * 区县别名
     */
    private String countyNickname;

    private Integer sync;

    /**
     * 区集合
     */
    @TableField(exist = false)
    private List<Town> townList;

}
