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
 * 基本资料-省份表
 * </p>
 *
 * @author langao_q
 * @since 2021-02-23
 */
@Data
@TableName("TAB_PROVINCE")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 省份编号
     */
    private String provinceCode;

    /**
     * GUID唯一值
     */
    @TableId
    private String guid;

    /**
     * 所属国家
     */
    private String countryName;

    /**
     * 所属国家代码/区号
     */
    private String countryCode;

    /**
     * 所属区域编号
     */
    private String areaCode;

    /**
     * 所属区域名称
     */
    private String areaName;

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
     * 省份别名
     */
    private String provinceNickname;

    private Integer sync;

    /**
     * 市集合
     */
    @TableField(exist = false)
    private List<City> cityList;
}
