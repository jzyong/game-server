/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jjy.game.dbtool.util;

import org.junit.Test;

import com.jjy.game.tool.util.ExcelUtil;

/**
 * excel 测试
 *
 * @author JiangZhiYong
 */
public class ExcelUtilTest {

    @Test
    public void testExcelRead() throws Exception {
        // ExcelUtil.getMetaData("E:\\arts\\策划文档\\翻牌机\\翻牌机数值\\ConfigRoom.xlsx");
        //ExcelUtil.readExcel("E:\\arts\\策划文档\\翻牌机\\翻牌机数值\\ConfigRoom.xlsx");
        ExcelUtil.getMetaData("E:\\arts\\策划文档\\翻牌机\\翻牌机数值\\ConfigRoom.xlsx", "room");
    }

    @Test
    public void testSheetName() throws Exception {
//        String[] sheetNames = ExcelUtil.getSheetNames("E:\\arts\\策划文档\\翻牌机\\翻牌机数值\\ConfigRoom.xlsx");
//        for (String sheetName : sheetNames) {
//            System.out.println(sheetName);
//        }
    }
}
