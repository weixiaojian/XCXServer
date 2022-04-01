package com.zhitengda.ztdCloud.cloudVo.MatchAddress;

import lombok.Data;

import java.util.List;

/**
 * 地址解析响应实体类(多个)
 * @author langao_q
 * @since 2021-05-07 14:40
 */
@Data
public class MatchAddressLisrResponse {

    private String request_id;
    private String code;
    private String msg;
    private boolean success;
    private List<MatchAddressResponseData> data;

}
