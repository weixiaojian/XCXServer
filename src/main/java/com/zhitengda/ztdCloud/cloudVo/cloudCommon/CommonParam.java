package com.zhitengda.ztdCloud.cloudVo.cloudCommon;

import lombok.Data;

import java.util.List;

/**
 * 公共参数实体类
 * @author langao_q
 * @since 2021-04-27 9:38
 */
@Data
public class CommonParam<T> {

    private String app_id;
    private String method;
    private String format;
    private String charset;
    private String sign_type;
    private String timestamp;
    private String version;
    /**
     * 业务参数
     */
    private List<T> biz_content;
}
