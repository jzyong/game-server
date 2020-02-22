package com.jzy.game.engine.mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.code.HttpServerCodecImpl;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.handler.HttpServerIoHandler;
import com.jzy.game.engine.util.SysUtil;

/**
 * http服务
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 * @version $Id: $Id
 */
public class HttpServer implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

	private final MinaServerConfig minaServerConfig;

	private final NioSocketAcceptor acceptor;

	private final HttpServerIoHandler ioHandler;

	protected boolean isRunning;	//通信是否在运行
	private OrderedThreadPoolExecutor threadpool;	//默认线程池

	/**
	 * <p>Constructor for HttpServer.</p>
	 *
	 * @param minaServerConfig a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
	 * @param ioHandler a {@link com.jzy.game.engine.mina.handler.HttpServerIoHandler} object.
	 */
	public HttpServer(MinaServerConfig minaServerConfig, HttpServerIoHandler ioHandler) {
		this.minaServerConfig = minaServerConfig;
		this.ioHandler = ioHandler;
		acceptor = new NioSocketAcceptor();
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		synchronized (this) {
			if (!isRunning) {
				isRunning = true;
				new Thread(new BindServer()).start();
			}
		}
	}

	/**
	 * <p>stop.</p>
	 */
	public void stop() {
		synchronized (this) {
			if (!isRunning) {
				LOG.info("HttpServer " + minaServerConfig.getName() + "is already stoped.");
				return;
			}
			isRunning = false;
			try {
				if (threadpool != null) {
					threadpool.shutdown();
				}
				acceptor.unbind();
				acceptor.dispose();
				LOG.info("Server is stoped.");
			} catch (Exception ex) {
				LOG.error("", ex);
			}
		}
	}

	/**
	 * 绑定端口
	 *
	 * @author JiangZhiYong
	 * @date 2017-03-31
	 * QQ:359135103
	 */
	private class BindServer implements Runnable {

		private final Logger LOG = LoggerFactory.getLogger(BindServer.class);

		@Override
		public void run() {
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			chain.addLast("codec", new HttpServerCodecImpl());

			// // 线程队列池
			OrderedThreadPoolExecutor threadpool = new OrderedThreadPoolExecutor(minaServerConfig.getOrderedThreadPoolExecutorSize());
			chain.addLast("threadPool", new ExecutorFilter(threadpool));

			acceptor.setReuseAddress(minaServerConfig.isReuseAddress()); // 允许地址重用

			SocketSessionConfig sc = acceptor.getSessionConfig();
			sc.setReuseAddress(minaServerConfig.isReuseAddress());
			sc.setReceiveBufferSize(minaServerConfig.getMaxReadSize());
			sc.setSendBufferSize(minaServerConfig.getSendBufferSize());
			sc.setTcpNoDelay(minaServerConfig.isTcpNoDelay());
			sc.setSoLinger(minaServerConfig.getSoLinger());
			sc.setIdleTime(IdleStatus.READER_IDLE, minaServerConfig.getReaderIdleTime());
			sc.setIdleTime(IdleStatus.WRITER_IDLE, minaServerConfig.getWriterIdleTime());

			acceptor.setHandler(ioHandler);

			try {
				acceptor.bind(new InetSocketAddress(minaServerConfig.getHttpPort()));
				LOG.warn("已开始监听HTTP端口：{}", minaServerConfig.getHttpPort());
			} catch (IOException e) {
				SysUtil.exit(getClass(), e, "监听HTTP端口：{}已被占用", minaServerConfig.getHttpPort());
			}
		}
	}
}
