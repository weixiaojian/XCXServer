package com.zhitengda.ztdCloud.cloudVo.MatchAddress;

import lombok.Data;

/**
 * 地址解析响应实体类
 * @author langao_q
 * @since 2021-05-07 14:40
 */
@Data
public class MatchAddressResponse {

    private String request_id;
    private String code;
    private String msg;
    private boolean success;
    private MatchAddressResponseData data;

}
