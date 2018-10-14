package com.jzy.game.engine.util;

import java.security.MessageDigest;

/**
 * 密码工具
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月16日 下午3:41:31
 */
public final class CipherUtil {

	private CipherUtil() {
	}

	private static String md5(byte[] v) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdAlgorithm = MessageDigest.getInstance("MD5");
			mdAlgorithm.update(v);
			byte[] mdCode = mdAlgorithm.digest();

			int mdCodeLength = mdCode.length;
			char[] strMd5 = new char[mdCodeLength * 2];
			int k = 0;
			for (int i = 0; i < mdCodeLength; i++) {
				byte byte0 = mdCode[i];
				strMd5[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
				strMd5[(k++)] = hexDigits[(byte0 & 0xF)];
			}
			return new String(strMd5);
		} catch (Exception e) {
		}
		return "";
	}

	public static String MD5Encode(String s) {
		byte[] b = s.getBytes();
		return md5(b);
	}

	public static byte[] MD5Bytes(byte[] v) {
		return md5(v).getBytes();
	}
}
