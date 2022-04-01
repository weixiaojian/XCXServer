package com.zhitengda.web.api;

import io.github.yedaxia.apidocs.Docs;
import io.github.yedaxia.apidocs.DocsConfig;

/**
 * api文档生成
 * @author langao_q
 * @since 2020-07-29 10:55
 */
public class ApiUtil {

    public static void main(String[] args) {
        DocsConfig config = new DocsConfig();
        // 项目根目录
        config.setProjectPath("E:/idea-work/ztd/yunda/XCXServer");
        // 项目名称
        config.setProjectName("XCXServer");
        // 声明该API的版本
        config.setApiVersion("V1.0");
        // 生成API 文档所在目录
        config.setDocsPath("E:/idea-work/ztd/yunda/XCXServer/api");
        // 配置自动生成
        config.setAutoGenerate(Boolean.TRUE);
        // 执行生成文档
        Docs.buildHtmlDocs(config);
    }
}
