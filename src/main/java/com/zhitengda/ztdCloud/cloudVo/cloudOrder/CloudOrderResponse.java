package com.zhitengda.ztdCloud.cloudVo.cloudOrder;

import lombok.Data;

import java.util.List;

/**
 * 云新增订单响应实体类
 *
 * @author langao_q
 * @since 2021-04-26 17:33
 */
@Data
public class CloudOrderResponse {

    private String request_id;
    private String code;
    private String msg;
    private boolean success;
    private String sub_code;
    private String sub_msg;
    private List<CloudOrderResponseData> resultList;
}
