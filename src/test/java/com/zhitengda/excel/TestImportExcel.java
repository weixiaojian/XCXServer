package com.zhitengda.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import java.util.List;

/**
 * 测试excel导入下单
 * @author langao_q
 * @since 2021-05-17 15:34
 */
public class TestImportExcel {

    public static void main(String[] args) {
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file("F:\\uploaded\\20210518121204_test.xlsx"));
        List<List<Object>> read = reader.read();
    }

}
