package com.jzy.game.gate.mina;

import org.apache.mina.core.session.IoSession;
import org.junit.Ignore;
import org.junit.Test;

import com.jzy.game.engine.mina.UdpServer;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.handler.DefaultProtocolHandler;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.Service;

/**
 * mina测试
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月1日 上午11:46:00
 */
@Ignore
public class MinaTest {

	@Test
	public void testUdpServer() throws Exception{
		MinaServerConfig minaServerConfig=new MinaServerConfig();
		minaServerConfig.setPort(8888);
		
		UdpServer udpServer=new UdpServer(minaServerConfig, new DefaultProtocolHandler(4) {
			
			@Override
			public Service<? extends BaseServerConfig> getService() {
				return null;
			}
			
			@Override
			protected void forward(IoSession session, int msgID, byte[] bytes) {
			}
		});
		new Thread(udpServer).start();
		
		Thread.sleep(50000);
	}
}
