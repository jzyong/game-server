package com.jzy.game.engine.util;

import java.util.regex.Pattern;

/**
 * 字符串工具
 *
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 * @version $Id: $Id
 */
public final class StringUtil {

    private StringUtil() {
    }

    /**
     * <p>isNullOrEmpty.</p>
     *
     * @param str a {@link java.lang.String} object.
     * @return true 字符串为空
     */
    public static boolean isNullOrEmpty(String str) {
        return null == str || str.trim().isEmpty();
    }
    
	/**
	 * 判断整数（int）
	 *
	 * @note + -判断也为真
	 * @param str a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isInteger(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	// 判断浮点数（double和float ）
	/**
	 * <p>isDouble.</p>
	 *
	 * @param str a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isDouble(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 首字母大写
	 *
	 * @param Str a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String upFirstChar(String Str) {
		char[] cs = Str.toCharArray();
		if (Character.isLowerCase(cs[0])) {
			cs[0] -= 32;
			return String.valueOf(cs);
		}
		return Str;
	}

	/**
	 * 首字母转小写
	 *
	 * @param s a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String lowerFirstChar(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		}

		return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}
}
