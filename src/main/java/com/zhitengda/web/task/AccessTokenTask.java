package com.zhitengda.web.task;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhitengda.entity.WxToken;
import com.zhitengda.mapper.WxTokenMapper;
import com.zhitengda.wx.WXConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 获取AccessToken任务  保存到数据库
 * @author langao_q
 * @since 2020-08-20 15:20
 */
@Slf4j
@Component
public class AccessTokenTask {

    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private WxTokenMapper wxTokenMapper;

    /**
     * 定时任务获取AccessToken/JsapiTicket
     * 每7000秒调用一次，并设置延迟一秒执行
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 7100 * 1000)
    public void refreshToken() {
        try {
            //刷新AccessToken
            WxToken wxToken = new WxToken();
            //1.组装url
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                    + "&appid="+ wxConfig.getAppId()
                    + "&secret=" + wxConfig.getAppSecret();
            //2.发起请求
            String result = HttpUtil.get(url);
            log.info("【AccessTokenTask更新】: " + result);
            //3.解析结果
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if (jsonObject != null) {
                try {
                    wxToken.setAppId(wxConfig.getAppId());
                    wxToken.setToken(jsonObject.getStr("access_token"));
                    wxToken.setExpiresIn(jsonObject.getLong("expires_in"));
                    wxToken.setUpdateDate(new Date());
                    WxToken dbToken = wxTokenMapper.selectById(wxConfig.getAppId());
                    if(dbToken != null){
                        wxTokenMapper.updateById(wxToken);
                    }else{
                        wxTokenMapper.insert(wxToken);
                    }
                } catch (Exception e) {
                    log.error("保存微信adcessToken出错：", e);
                }
            } else {
                log.info("获取token失败，返回結果：" + result);
            }
        } catch (Exception e) {
            log.error("获取微信adcessToken出错：", e);
        }
    }
}
