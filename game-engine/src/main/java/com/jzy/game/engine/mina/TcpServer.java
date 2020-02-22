package com.jzy.game.engine.mina;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.code.DefaultProtocolCodecFactory;
import com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl;
import com.jzy.game.engine.mina.config.MinaServerConfig;

/**
 * TCP服务器
 *
 * @author JiangZhiYong
 * @date 2017-03-30 QQ:359135103
 * @version $Id: $Id
 */
public class TcpServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);
    private final MinaServerConfig minaServerConfig;
    private final NioSocketAcceptor acceptor;
    private final IoHandler ioHandler;
    private ProtocolCodecFactory factory;
    private OrderedThreadPoolExecutor threadpool; // 消息处理线程,使用有序线程池，保证所有session事件处理有序进行，比如先执行消息执行，再是消息发送，最后关闭事件
    private Map<String, IoFilter> filters; // 过滤器

    protected boolean isRunning; // 服务器是否运行

    /**
     * <p>Constructor for TcpServer.</p>
     *
     * @param minaServerConfig 配置
     * @param ioHandler 消息处理器
     */
    public TcpServer(MinaServerConfig minaServerConfig, IoHandler ioHandler) {
        this.minaServerConfig = minaServerConfig;
        this.ioHandler = ioHandler;
        acceptor = new NioSocketAcceptor();
    }

    /**
     * <p>Constructor for TcpServer.</p>
     *
     * @param minaServerConfig a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
     * @param ioHandler a {@link org.apache.mina.core.service.IoHandler} object.
     * @param factory a {@link org.apache.mina.filter.codec.ProtocolCodecFactory} object.
     */
    public TcpServer(MinaServerConfig minaServerConfig, IoHandler ioHandler, ProtocolCodecFactory factory) {
        this(minaServerConfig, ioHandler);
        this.factory = factory;
    }

    /**
     * <p>Constructor for TcpServer.</p>
     *
     * @param minaServerConfig a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
     * @param ioHandler a {@link org.apache.mina.core.service.IoHandler} object.
     * @param factory a {@link org.apache.mina.filter.codec.ProtocolCodecFactory} object.
     * @param filters 不要包含消息解码、线程池过滤器，已默认添加
     */
    public TcpServer(MinaServerConfig minaServerConfig, IoHandler ioHandler, ProtocolCodecFactory factory,
                     Map<String, IoFilter> filters) {
        this(minaServerConfig, ioHandler, factory);
        this.filters = filters;
    }

    /**
     * 连接会话数
     *
     * @return a int.
     */
    public int getManagedSessionCount() {
        return acceptor == null? 0 : acceptor.getManagedSessionCount();
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
                isRunning = true;
                DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
                if (factory == null) {
                    factory = new DefaultProtocolCodecFactory();
                }

                if (factory instanceof DefaultProtocolCodecFactory) {
                    ProtocolCodecFactoryImpl defaultFactory = (ProtocolCodecFactoryImpl) factory;
                    defaultFactory.getDecoder().setMaxReadSize(minaServerConfig.getMaxReadSize());
                    defaultFactory.getEncoder()
                                  .setMaxScheduledWriteMessages(minaServerConfig.getMaxScheduledWriteMessages());
                }

                chain.addLast("codec", new ProtocolCodecFilter(factory));
                threadpool = new OrderedThreadPoolExecutor(minaServerConfig.getOrderedThreadPoolExecutorSize());
                chain.addLast("threadPool", new ExecutorFilter(threadpool));
                if (filters != null) {
                    filters.forEach((key, filter) -> {
                        if ("ssl".equalsIgnoreCase(key) || "tls".equalsIgnoreCase(key)) { // ssl过滤器必须添加到首部
                            chain.addFirst(key, filter);
                        } else {
                            chain.addLast(key, filter);
                        }
                    });
                }

                acceptor.setReuseAddress(minaServerConfig.isReuseAddress()); // 允许地址重用

                SocketSessionConfig sc = acceptor.getSessionConfig();
                sc.setReuseAddress(minaServerConfig.isReuseAddress());
                sc.setReceiveBufferSize(minaServerConfig.getReceiveBufferSize());
                sc.setSendBufferSize(minaServerConfig.getSendBufferSize());
                sc.setTcpNoDelay(minaServerConfig.isTcpNoDelay());
                sc.setSoLinger(minaServerConfig.getSoLinger());
                sc.setIdleTime(IdleStatus.READER_IDLE, minaServerConfig.getReaderIdleTime());
                sc.setIdleTime(IdleStatus.WRITER_IDLE, minaServerConfig.getWriterIdleTime());

                acceptor.setHandler(ioHandler);

                try {
                    acceptor.bind(new InetSocketAddress(minaServerConfig.getPort()));
                    log.warn("已开始监听TCP端口：{}", minaServerConfig.getPort());
                } catch (IOException e) {
                    log.warn("监听TCP端口：{}已被占用", minaServerConfig.getPort());
                    log.error("TCP 服务异常", e);
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
                log.info("Server " + minaServerConfig.getName() + "is already stoped.");
                return;
            }
            isRunning = false;
            try {
                if (threadpool != null) {
                    threadpool.shutdown();
                }
                acceptor.unbind();
                acceptor.dispose();
                log.info("Server is stoped.");
            } catch (Exception ex) {
                log.error("", ex);
            }
        }
    }

}
