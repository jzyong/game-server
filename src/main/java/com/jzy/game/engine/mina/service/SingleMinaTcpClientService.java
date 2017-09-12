package com.jzy.game.engine.mina.service;

import java.util.Map;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoHandler;

import com.jzy.game.engine.mina.MinaTcpClient;
import com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.handler.DefaultClientProtocolHandler;

/**
 * 单个tcp连接客户端
 *
 * @author JiangZhiYong
 * @date 2017-04-09 QQ:359135103
 */
public class SingleMinaTcpClientService extends MinaClientService {
	private final MinaTcpClient tcpClient;
	
	
	public SingleMinaTcpClientService(MinaClientConfig minaClientConfig,ProtocolCodecFactoryImpl factory, IoHandler ioHandler,Map<String, IoFilter> filters) {
		super(minaClientConfig);
		tcpClient = new MinaTcpClient(this,  minaClientConfig, ioHandler,factory,filters);
	}

	
	/**
	 * 
	 * @param minaClientConfig
	 * @param ioHandler
	 *            消息处理器
	 */
	public SingleMinaTcpClientService(MinaClientConfig minaClientConfig,ProtocolCodecFactoryImpl factory, IoHandler ioHandler) {
		super(minaClientConfig);
		tcpClient = new MinaTcpClient(this,  minaClientConfig, ioHandler,factory);
	}

	/**
	 * 
	 * @param minaClientConfig
	 * @param ioHandler
	 *            消息处理器
	 */
	public SingleMinaTcpClientService(MinaClientConfig minaClientConfig, IoHandler ioHandler) {
		super(minaClientConfig);
		tcpClient = new MinaTcpClient(this, minaClientConfig, ioHandler);
	}

	/**
	 * 默认消息处理器
	 * 
	 * @param minaClientConfig
	 */
	public SingleMinaTcpClientService(MinaClientConfig minaClientConfig) {
		super(minaClientConfig);
		tcpClient = new MinaTcpClient(this, minaClientConfig, new DefaultClientProtocolHandler(this));
	}

	@Override
	protected void running() {
		tcpClient.run();

	}

	public MinaTcpClient getTcpClient() {
		return tcpClient;
	}

	@Override
	public void checkStatus() {
		tcpClient.checkStatus();
	}
	
	

}
