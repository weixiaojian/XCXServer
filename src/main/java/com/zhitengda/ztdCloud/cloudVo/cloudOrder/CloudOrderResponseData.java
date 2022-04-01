package com.zhitengda.ztdCloud.cloudVo.cloudOrder;

import lombok.Data;

/**
 * 云新增订单响应实体类
 *
 * @author langao_q
 * @since 2021-04-26 17:33
 */
@Data
public class CloudOrderResponseData {

    private boolean success;
    private String saveId;
    private String msg;
}
