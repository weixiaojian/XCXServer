package com.zhitengda.ztdCloud.cloudVo.MatchSiteEmp;

import lombok.Data;

import java.util.List;

/**
 * 匹配揽件/派件-网点及员工响应实体
 * @author langao_q
 * @since 2021-04-27 17:35
 */
@Data
public class MatchSiteEmpResponse {

    private String request_id;
    private Integer code;
    private String msg;
    private boolean success;
    private List<MatchSiteEmpResponseData> data;
}
