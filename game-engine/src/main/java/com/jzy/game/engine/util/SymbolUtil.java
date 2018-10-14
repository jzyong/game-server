package com.jzy.game.engine.util;


/**
 * 符号工具
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月2日 上午9:53:55
 */
public final class SymbolUtil {

    public static final String Empty = "";
    public static final String ENTER = "\n";
    public static final String EmptyZero = "{0:0}";
    public static final String FENHAO = ";";//分号
    public static final String FENHAO_REG = ";|；";//分号
    public static final String MAOHAO_REG = ":|：";//冒号
    public static final String MAOHAO_1_REG = ":|：|=|_";//冒号
    public static final String DOUHAO = ",";
    public static final String DOUHAO_REG = ",|，";
    public static final String XIEGANG_REG = "/";
    public static final String SHUXIAN_REG = "\\|";
    public static final String XIAHUAXIAN_REG = "_";
    public static final String JINGHAO_REG = "\\#";
    public static final String DENGHAO = "=";
    public static final String AT_REG = "@";

    private SymbolUtil() {
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return null == str || str.trim().isEmpty();
    }
}
