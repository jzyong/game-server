/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jjy.game.dbtool.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import com.jjy.game.tool.util.FileUtil;

/**
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class FileUtilTest {

    @Test
    public void testGetFile() {
        List<File> sourceFileList = new ArrayList<>();
        FileUtil.getFiles("E:\\arts\\策划文档\\翻牌机\\翻牌机数值", sourceFileList, "xlsx", null);
    }
}
