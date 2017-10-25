package com.jjy.game.gate.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.gate.server.handler.GateTcpGameServerHandler;
import com.jzy.game.engine.mina.TcpServer;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.server.Service;

/**
 * 子游戏连接 服务
 * <p>游戏服、大厅服等内部共用的服务器</p>
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月30日 下午2:20:01
 */
public class GateTcpGameServer extends Service<MinaServerConfig> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GateTcpGameServer.class);
	private TcpServer tcpServer;
	private MinaServerConfig minaServerConfig;

	public GateTcpGameServer(MinaServerConfig minaServerConfig) {
		super(null);
		this.minaServerConfig = minaServerConfig;
		tcpServer = new TcpServer(minaServerConfig, new GateTcpGameServerHandler(this));
	}

	@Override
	protected void running() {
		tcpServer.run();
	}

	@Override
	protected void onShutdown() {
		super.onShutdown();
		tcpServer.stop();

	}

	public MinaServerConfig getMinaServerConfig() {
		return minaServerConfig;
	}

}
