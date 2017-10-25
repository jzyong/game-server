package com.jjy.game.hall.server;

import org.junit.Test;

import com.jzy.game.engine.mq.MQConfig;
import com.jzy.game.engine.mq.MQProducer;

/**
 * 测试MQ
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月28日 下午3:07:49
 */
public class MQProducerTest {

	@Test
	public void testSendMsg(){
		MQConfig mqConfig=new MQConfig();
		MQProducer mqProducer=new MQProducer(mqConfig);
		for(int i=0;i<10000;i++){
			mqProducer.sendMsg("hall", "hello");
		}
		
	}
}
