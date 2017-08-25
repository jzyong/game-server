package com.jzy.game.engine.netty.service;

import com.jzy.game.engine.netty.NettyTcpClient;
import com.jzy.game.engine.netty.config.NettyClientConfig;

/**
 * Netty 单链接客户端
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月25日 下午3:59:07
 */
public class SingleNettyTcpClientService extends NettyClientService {
	private final NettyTcpClient nettyTcpClient;

	public SingleNettyTcpClientService(NettyClientConfig nettyClientConfig) {
		super(nettyClientConfig);
		nettyTcpClient = new NettyTcpClient(this);
	}

	@Override
	protected void running() {
		new Thread(nettyTcpClient).start();
	}

	public NettyTcpClient getNettyTcpClient() {
		return nettyTcpClient;
	}

	
}
