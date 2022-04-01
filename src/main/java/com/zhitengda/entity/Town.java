package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * *基本资料-乡镇表
 * </p>
 *
 * @author langao_q
 * @since 2021-04-27
 */
@Data
@TableName("TAB_TOWN")
@EqualsAndHashCode(callSuper = false)
public class Town implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 乡镇编号
     */
    private String townCode;

    /**
     * 乡镇名称
     */
    private String townName;

    /**
     * 区/县编号/代码
     */
    private String countyCode;

    /**
     * *所属区县
     */
    private String countyName;

    /**
     * 城市编号/城市代码
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 省份编号
     */
    private String provinceCode;

    /**
     * GUID
     */
    @TableId
    private String guid;

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
    private LocalDateTime modifyDate;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 数据来源
     */
    private String dataFrom;

    private Integer sync;


}
