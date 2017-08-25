package com.jzy.game.engine.mina.service;

import org.apache.mina.core.service.IoHandler;

import com.jzy.game.engine.mina.TcpClient;
import com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.handler.DefaultClientProtocolHandler;

/**
 * 单个tcp连接客户端
 *
 * @author JiangZhiYong
 * @date 2017-04-09 QQ:359135103
 */
public class SingleTcpClientService extends ClientService {
	private final TcpClient tcpClient;
	
	
	/**
	 * 
	 * @param minaClientConfig
	 * @param ioHandler
	 *            消息处理器
	 */
	public SingleTcpClientService(MinaClientConfig minaClientConfig,ProtocolCodecFactoryImpl factory, IoHandler ioHandler) {
		super(minaClientConfig);
		tcpClient = new TcpClient(this,  minaClientConfig, ioHandler,factory);
	}

	/**
	 * 
	 * @param minaClientConfig
	 * @param ioHandler
	 *            消息处理器
	 */
	public SingleTcpClientService(MinaClientConfig minaClientConfig, IoHandler ioHandler) {
		super(minaClientConfig);
		tcpClient = new TcpClient(this, minaClientConfig, ioHandler);
	}

	/**
	 * 默认消息处理器
	 * 
	 * @param minaClientConfig
	 */
	public SingleTcpClientService(MinaClientConfig minaClientConfig) {
		super(minaClientConfig);
		tcpClient = new TcpClient(this, minaClientConfig, new DefaultClientProtocolHandler(this));
	}

	@Override
	protected void running() {
		tcpClient.run();

	}

	public TcpClient getTcpClient() {
		return tcpClient;
	}

}
