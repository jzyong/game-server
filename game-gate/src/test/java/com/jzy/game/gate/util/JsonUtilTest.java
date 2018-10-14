package com.jzy.game.gate.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import com.jzy.game.engine.util.JsonUtil;
import com.jzy.game.model.struct.User;

/**
 * json 工具测试
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月7日 下午2:56:27
 */
@Ignore
public class JsonUtilTest {

	@Test
	public void testObjectToMap() {
		User user = new User();
		user.setId(1);
		user.setAccunt("jzy");
		user.setPassword("1111111");
		user.setIp("127.0.0.1");
		Map<String, String> map = JsonUtil.object2Map(user);
		System.err.println(map);
	}

	@Test
	public void testMapToObject() {
		Map<String, String> map = new HashMap<>();
		map.put("id", String.valueOf(1));
		map.put("accunt", "jzy");
		map.put("password", "1111111");
		User user = new User();
		JsonUtil.map2Object(map, user);
		System.err.println(user);
	}

	
}
