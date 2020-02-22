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
 * @version $Id: $Id
 */
public class SingleMinaTcpClientService extends MinaClientService {
	private final MinaTcpClient tcpClient;
	
	
	/**
	 * <p>Constructor for SingleMinaTcpClientService.</p>
	 *
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 * @param factory a {@link com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl} object.
	 * @param ioHandler a {@link org.apache.mina.core.service.IoHandler} object.
	 * @param filters a {@link java.util.Map} object.
	 */
	public SingleMinaTcpClientService(MinaClientConfig minaClientConfig,ProtocolCodecFactoryImpl factory, IoHandler ioHandler,Map<String, IoFilter> filters) {
		super(minaClientConfig);
		tcpClient = new MinaTcpClient(this,  minaClientConfig, ioHandler,factory,filters);
	}

	
	/**
	 * <p>Constructor for SingleMinaTcpClientService.</p>
	 *
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 * @param ioHandler
	 *            消息处理器
	 * @param factory a {@link com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl} object.
	 */
	public SingleMinaTcpClientService(MinaClientConfig minaClientConfig,ProtocolCodecFactoryImpl factory, IoHandler ioHandler) {
		super(minaClientConfig);
		tcpClient = new MinaTcpClient(this,  minaClientConfig, ioHandler,factory);
	}

	/**
	 * <p>Constructor for SingleMinaTcpClientService.</p>
	 *
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
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
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 */
	public SingleMinaTcpClientService(MinaClientConfig minaClientConfig) {
		super(minaClientConfig);
		tcpClient = new MinaTcpClient(this, minaClientConfig, new DefaultClientProtocolHandler(this));
	}

	/** {@inheritDoc} */
	@Override
	protected void running() {
		tcpClient.run();

	}

	/**
	 * <p>Getter for the field <code>tcpClient</code>.</p>
	 *
	 * @return a {@link com.jzy.game.engine.mina.MinaTcpClient} object.
	 */
	public MinaTcpClient getTcpClient() {
		return tcpClient;
	}

	/** {@inheritDoc} */
	@Override
	public void checkStatus() {
		tcpClient.checkStatus();
	}
	
	

}
