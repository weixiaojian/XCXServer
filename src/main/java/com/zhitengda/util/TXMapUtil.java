package com.zhitengda.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhitengda.vo.CoordinateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 对接腾讯地图接口
 *
 * @Author: yanjieSir
 * @Date: 2020-12-31 09:21:31
 */
@Service
@Slf4j
public class TXMapUtil {

    private static String key = "OJABZ-6L2WJ-U7LFS-F4TQO-FLPQJ-TGBPP";

    /**
     * 地址转坐标
     */
    private static final String ADDRESS_TO_COORDINATE_URL = "https://apis.map.qq.com/ws/geocoder/v1/";

    /**
     * 地址转坐标
     *
     * @param address 详细地址
     * @return 腾讯json
     */
    public static String addressToCoordinate(String address) {
        //去除一下空格
        String replace = address.replace(" ", "");
        Map<String, Object> reqMap = new HashMap<>(3);
        reqMap.put("key", key);
        reqMap.put("address", address);
        reqMap.put("output", "json");
        return HttpUtil.get(ADDRESS_TO_COORDINATE_URL, reqMap);
    }

    /**
     * 地址转坐标(封装经纬度结果)
     * 按照详细地址存缓存
     *
     * @param address 详细地址
     * @return 腾讯json
     */
    public static CoordinateDto addressToCoordinateSystem(String address) {
        String redisKey = "addressToCoordinateSystem:" + address;
        CoordinateDto system = null;
        String res = addressToCoordinate(address);
        JSONObject object = JSON.parseObject(res);
        //每秒达到限制
        if (object.getInteger("status") == 120) {
            log.info("此key每秒请求量已达到上限，休眠一秒");
            ThreadUtil.sleep(1000);
            //从新请求
            res = addressToCoordinate(address);
            object = JSON.parseObject(res);
        }
        //换取坐标错误
        if (object.getInteger("status") == 0) {
            JSONObject result = object.getJSONObject("result");
            JSONObject location = result.getJSONObject("location");
            system = new CoordinateDto(location.getDouble("lng"), location.getDouble("lat"));
            return system;
        }
        log.error("请求腾讯地址换取坐标错误：" + object.getString("message"));
        system = new CoordinateDto(121.458183, 31.092365);
        return system;
    }


    public static void main(String[] args) {
        CoordinateDto res = TXMapUtil.addressToCoordinateSystem("上海市上海市闵行区永南路16");
        CoordinateDto res1 = TXMapUtil.addressToCoordinateSystem("上海市上海市徐汇区汾阳路110号The Bull Claw鳌足汇汾阳路110号鳌足汇餐厅");
        System.out.println(res);
        System.out.println(res1);
    }
}
