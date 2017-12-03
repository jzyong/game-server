package com.jzy.game.gate.handler.http;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.jzy.game.engine.util.HttpUtil;

/**
 * 集群服务器请求测试
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月12日 下午3:09:49
 */
@Ignore
public class ClusterHttpTest {

	/**
	 * 获取大厅IP
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年7月12日 下午3:23:08
	 */
	@Test
	public void testGetHallIp() {
		Map<String, String> paramsMap = new HashMap<>();
		String httpPost = HttpUtil.httpPost("http://127.0.0.1:8001/server/hall/ip", paramsMap);
		System.err.println(httpPost);
	}
}
