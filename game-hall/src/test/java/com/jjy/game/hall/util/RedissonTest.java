package com.jjy.game.hall.util;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.redisson.api.RAtomicLong;

import com.jzy.game.engine.redis.redisson.RedissonManager;

/**
 * redisson功能测试
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月30日 上午10:46:17
 */
public class RedissonTest {
	/**配置路径*/
	private static final String configPath="E:\\Java\\bydr2\\game-hall\\target\\config";
	
	@Before
	public void init() {
		RedissonManager.connectRedis(configPath);
	}

	/**
	 * 测试过期
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月30日 上午10:46:43
	 */
	@Test
	public void testExipreAsync() {
		//long 
		RAtomicLong atomicLong = RedissonManager.getRedissonClient().getAtomicLong("longtest");
		atomicLong.addAndGet(100);
		atomicLong.expireAt(new Date().getTime()+100000);
	}
}
