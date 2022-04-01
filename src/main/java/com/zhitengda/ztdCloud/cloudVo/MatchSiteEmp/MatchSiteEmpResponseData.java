package com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp;

import lombok.Data;

/**
 * 匹配揽件/派件-网点及员工响应实体类
 * @author langao_q
 * @since 2021-04-27 17:35
 */
@Data
public class MatchSiteEmpResponseData {

    private String id;
    private String orderBillCode;
    private String billCode;
    /**
     * 揽件网点及员工
     */
    private int sendSuccess;
    private String sendMsg;
    private String sendGuid;
    private String sendSiteName;
    private String sendSiteCode;
    private String sendEmployeeName;
    private String sendEmployeeCode;
    private int sendMatchingType;
    /**
     * 派件网点、员工、网点管理员
     */
    private int dispatchSuccess;
    private String dispatchMsg;
    private String dispatchGuid;
    private String dispatchSiteName;
    private String dispatchSiteCode;
    private String dispatchEmployeeName;
    private String dispatchEmployeeCode;
    private String dispatchAdminEmployeeName;
    private String dispatchAdminEmployeeCode;
    private int dispatchMatchingType;
    /**
     * 小件员区域编码
     */
    private String dispatchAreaName;

    /**
     * 韵达：大笔（一段）、格口号（二段）、派送码（三段）、四段码打印值（四段
     */
    private String bigPen;
    private String chequer;
    private String deliveryCode;
    private String fourCode;
    /**
     * 是否开启韵达接口  开启  关闭
     */
    private String isYD;
}
