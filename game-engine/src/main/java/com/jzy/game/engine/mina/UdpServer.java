package com.jzy.game.engine.mina;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.code.DefaultProtocolCodecFactory;
import com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl;
import com.jzy.game.engine.mina.config.MinaServerConfig;

/**
 * UDP服务器
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月1日 上午10:50:45
 * @version $Id: $Id
 */
public class UdpServer implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(UdpServer.class);
	private final MinaServerConfig minaServerConfig;
	private final NioDatagramAcceptor acceptor;
	private final IoHandler ioHandler;
	private ProtocolCodecFactoryImpl factory;
	private OrderedThreadPoolExecutor threadpool; // 消息处理线程,使用有序线程池，保证所有session事件处理有序进行，比如先执行消息执行，再是消息发送，最后关闭事件
	protected boolean isRunning; // 服务器是否运行
	private Map<String, IoFilter> filters; //过滤器

	/**
	 * <p>Constructor for UdpServer.</p>
	 *
	 * @param minaServerConfig a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
	 * @param ioHandler a {@link org.apache.mina.core.service.IoHandler} object.
	 */
	public UdpServer(MinaServerConfig minaServerConfig, IoHandler ioHandler) {
        this.minaServerConfig = minaServerConfig;
		this.ioHandler = ioHandler;
		acceptor = new NioDatagramAcceptor();
	}

	/**
	 * <p>Constructor for UdpServer.</p>
	 *
	 * @param minaServerConfig a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
	 * @param ioHandler a {@link org.apache.mina.core.service.IoHandler} object.
	 * @param factory a {@link com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl} object.
	 */
	public UdpServer(MinaServerConfig minaServerConfig, IoHandler ioHandler, ProtocolCodecFactoryImpl factory) {
		this(minaServerConfig, ioHandler);
		this.factory = factory;
	}
	
	/**
	 * <p>Constructor for UdpServer.</p>
	 *
	 * @param minaServerConfig a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
	 * @param ioHandler a {@link org.apache.mina.core.service.IoHandler} object.
	 * @param factory a {@link com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl} object.
	 * @param filters a {@link java.util.Map} object.
	 */
	public UdpServer(MinaServerConfig minaServerConfig, IoHandler ioHandler, ProtocolCodecFactoryImpl factory,Map<String, IoFilter> filters) {
		this(minaServerConfig, ioHandler, factory);
		this.filters=filters;
	}

	/**
	 * 连接会话数
	 *
	 * @return a int.
	 */
	public int getManagedSessionCount() {
		return acceptor == null ? 0 : acceptor.getManagedSessionCount();
	}

	/**
	 * 广播所有连接的消息
	 *
	 * @param obj a {@link java.lang.Object} object.
	 */
	public void broadcastMsg(Object obj) {
        acceptor.broadcast(obj);
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		synchronized (this) {
			if (!isRunning) {
				DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
				if (factory == null) {
					factory = new DefaultProtocolCodecFactory();
				}
				factory.getDecoder().setMaxReadSize(minaServerConfig.getMaxReadSize());
				factory.getEncoder().setMaxScheduledWriteMessages(minaServerConfig.getMaxScheduledWriteMessages());
				chain.addLast("codec", new ProtocolCodecFilter(factory));
				threadpool = new OrderedThreadPoolExecutor(minaServerConfig.getOrderedThreadPoolExecutorSize());
				chain.addLast("threadPool", new ExecutorFilter(threadpool));
				if(filters != null){
                    filters.forEach((key, filter)->chain.addLast(key, filter));
				}

				DatagramSessionConfig dc = acceptor.getSessionConfig();
				dc.setReuseAddress(minaServerConfig.isReuseAddress());
				dc.setReceiveBufferSize(minaServerConfig.getReceiveBufferSize());
				dc.setSendBufferSize(minaServerConfig.getSendBufferSize());
				dc.setIdleTime(IdleStatus.READER_IDLE, minaServerConfig.getReaderIdleTime());
				dc.setIdleTime(IdleStatus.WRITER_IDLE, minaServerConfig.getWriterIdleTime());
				dc.setBroadcast(true);
				dc.setCloseOnPortUnreachable(true);

				acceptor.setHandler(ioHandler);
				try {
					acceptor.bind(new InetSocketAddress(minaServerConfig.getPort()));
					LOGGER.warn("已开始监听UDP端口：{}", minaServerConfig.getPort());
				} catch (IOException e) {
					LOGGER.warn("监听UDP端口：{}已被占用", minaServerConfig.getPort());
					LOGGER.error("UDP, 服务异常", e);
				}
			}
		}
	}

	/**
	 * <p>stop.</p>
	 */
	public void stop() {
		synchronized (this) {
			if (!isRunning) {
				LOGGER.info("Server " + minaServerConfig.getName() + "is already stoped.");
				return;
			}
			isRunning = false;
			try {
				if (threadpool != null) {
					threadpool.shutdown();
				}
				acceptor.unbind();
				acceptor.dispose();
				LOGGER.info("Server is stoped.");
			} catch (Exception ex) {
				LOGGER.error("", ex);
			}
		}
	}

}
