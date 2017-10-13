package com.jjy.game.engine.util;

import org.junit.Test;

import com.jzy.game.engine.util.SysUtil;

/**
 * 系统工具测试
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年10月12日 下午2:28:07
 */
public class SysUtilTest {

	@Test
	public void testJvmInfo() {
		System.err.println(SysUtil.jvmInfo("\r\n"));
//		System.err.println(SysUtil.jvmInfo("<br>"));
	}
}
