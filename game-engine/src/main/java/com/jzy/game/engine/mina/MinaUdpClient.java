package com.jzy.game.engine.mina;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl;
import com.jzy.game.engine.mina.config.MinaClientConfig;

/**
 * mina udp 客户端
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月1日 上午11:31:39
 * @version $Id: $Id
 */
public final class MinaUdpClient implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(MinaUdpClient.class);

	private NioDatagramConnector connector;
	private MinaClientConfig minaClientConfig; // 客户端配置
	private final IoHandler clientProtocolHandler; // 消息处理器
	private final ProtocolCodecFilter codecFilter; // 消息过滤器
	private int maxConnectCount; // 最大连接数
	private Consumer<MinaClientConfig> sessionCreateCallBack;
	private final ProtocolCodecFactoryImpl factory; // 消息工厂
	private IoSession session;	//连接会话

	/**
	 * <p>Constructor for MinaUdpClient.</p>
	 *
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 * @param clientProtocolHandler a {@link org.apache.mina.core.service.IoHandler} object.
	 * @param factory a {@link com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl} object.
	 */
	public MinaUdpClient(MinaClientConfig minaClientConfig, IoHandler clientProtocolHandler,
			ProtocolCodecFactoryImpl factory) {
        this.minaClientConfig = minaClientConfig;
		this.clientProtocolHandler = clientProtocolHandler;
		this.factory = factory;
        codecFilter =new ProtocolCodecFilter(this.factory);
		init(this.clientProtocolHandler);
		setMinaClientConfig(minaClientConfig);
	}

	/**
	 * 初始化tcp连接
	 * 
	 * @param clientProtocolHandler
	 */
	private void init(IoHandler clientProtocolHandler) {
        connector = new NioDatagramConnector();
        connector.getFilterChain().addLast("codec", codecFilter);
        connector.setHandler(clientProtocolHandler);
		connector.setConnectTimeoutMillis(60000L);
		connector.setConnectTimeoutCheckInterval(10000);
	}

	/**
	 * 设置连接配置
	 *
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 */
	public void setMinaClientConfig(MinaClientConfig minaClientConfig) {
		if (minaClientConfig == null) {
			return;
		}
		this.minaClientConfig = minaClientConfig;
		DatagramSessionConfig dc = connector.getSessionConfig();
		maxConnectCount = minaClientConfig.getMaxConnectCount();
		dc.setReceiveBufferSize(minaClientConfig.getReceiveBufferSize()); // 524288
		dc.setSendBufferSize(minaClientConfig.getSendBufferSize()); // 1048576
		dc.setMaxReadBufferSize(minaClientConfig.getMaxReadSize()); // 1048576
        factory.getDecoder().setMaxReadSize(minaClientConfig.getMaxReadSize());
	}

	/**
	 * 广播所有连接的消息
	 *
	 * @param obj a {@link java.lang.Object} object.
	 */
	public void broadcastMsg(Object obj) {
        connector.broadcast(obj);
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		synchronized (this) {
			connect();
		}
	}

	/**
	 * 连接服务器
	 */
	public void connect() {
		if (getMinaClientConfig() != null) {
			LOGGER.warn("开始链接其他服务器,共 [" + getMinaClientConfig().getMaxConnectCount() + "] 个");
			MinaClientConfig.MinaClienConnToConfig connTo = getMinaClientConfig().getConnTo();
			if (connTo == null) {
				LOGGER.warn("没有连接配置");
				return;
			}
			for (int i = 0; i < getMinaClientConfig().getMaxConnectCount(); i++) {
				ConnectFuture connect = connector
						.connect(new InetSocketAddress(connTo.getHost(), connTo.getPort()));
				connect.awaitUninterruptibly(10000L);
				if (!connect.isConnected()) {
					LOGGER.warn("失败！连接到服务器：" + connTo);
					break;
				} else {
					LOGGER.warn("成功！连接到服务器：" + connTo);
                    session =connect.getSession();
					if (sessionCreateCallBack != null) {
						sessionCreateCallBack.accept(getMinaClientConfig());
					}
				}
			}
		} else {
			LOGGER.warn("连接配置为null");
		}
	}

	/**
	 * <p>stop.</p>
	 */
	public void stop() {
		synchronized (this) {
			try {
				connector.dispose();
				LOGGER.info("Client is stoped.");
			} catch (Exception ex) {
				LOGGER.error("", ex);
			}
		}
	}

	/**
	 * 状态监测
	 */
	public void checkStatus() {
		if (connector.getManagedSessionCount() < maxConnectCount
            || connector.getManagedSessions().size() < maxConnectCount) {
			connect();
		}
	}

	/**
	 * <p>Getter for the field <code>minaClientConfig</code>.</p>
	 *
	 * @return a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 */
	public MinaClientConfig getMinaClientConfig() {
		return minaClientConfig;
	}

	/**
	 * <p>Getter for the field <code>session</code>.</p>
	 *
	 * @return a {@link org.apache.mina.core.session.IoSession} object.
	 */
	public IoSession getSession() {
		return session;
	}

	
}
