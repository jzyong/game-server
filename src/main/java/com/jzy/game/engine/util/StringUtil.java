package com.jzy.game.engine.util;

/**
 * 字符串工具
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class StringUtil {

    /**
     *
     * @param str
     * @return true 字符串为空
     */
    public static boolean isNullOrEmpty(String str) {
        return null == str || str.trim().isEmpty();
    }
}
