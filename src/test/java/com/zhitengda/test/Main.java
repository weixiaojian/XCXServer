package com.zhitengda.test;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author langao_q
 * @since 2021-04-28 11:24
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        LinkedList<String> list = new LinkedList<>();
        list.add("0");
        list.add(1, "1");
    }

}
