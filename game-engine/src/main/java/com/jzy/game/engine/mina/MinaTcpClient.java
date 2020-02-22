package com.jzy.game.engine.mina;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.code.DefaultProtocolCodecFactory;
import com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.service.MinaClientService;

/**
 * Mina客户端
 *
 * @author JiangZhiYong
 * @date 2017-04-01
 * QQ:359135103
 * @version $Id: $Id
 */
public final class MinaTcpClient implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MinaTcpClient.class);

    private NioSocketConnector connector;		//TCP连接
    private MinaClientConfig minaClientConfig;			//客户端配置
    private final IoHandler clientProtocolHandler;		//消息处理器
    private final ProtocolCodecFilter codecFilter;		//消息过滤器
    private int maxConnectCount;						//最大连接数
    private Consumer<MinaClientConfig> sessionCreateCallBack;
    private final MinaClientService service; 	//附属的客户端服务
    private final ProtocolCodecFactoryImpl factory;		//消息工厂
    private Map<String, IoFilter> filters; //过滤器

    
    /**
     * <p>Constructor for MinaTcpClient.</p>
     *
     * @param service a {@link com.jzy.game.engine.mina.service.MinaClientService} object.
     * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
     * @param clientProtocolHandler a {@link org.apache.mina.core.service.IoHandler} object.
     * @param factory a {@link com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl} object.
     * @param filters a {@link java.util.Map} object.
     */
    public MinaTcpClient(MinaClientService service, MinaClientConfig minaClientConfig, IoHandler clientProtocolHandler, ProtocolCodecFactoryImpl factory,Map<String, IoFilter> filters) {
        this.factory=factory;
        codecFilter = new ProtocolCodecFilter(factory);
        this.service = service;
        this.clientProtocolHandler = clientProtocolHandler;
        this.filters=filters;
        init(clientProtocolHandler);
        setMinaClientConfig(minaClientConfig);
    }
    
    /**
     * <p>Constructor for MinaTcpClient.</p>
     *
     * @param service a {@link com.jzy.game.engine.mina.service.MinaClientService} object.
     * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
     * @param clientProtocolHandler a {@link org.apache.mina.core.service.IoHandler} object.
     * @param factory a {@link com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl} object.
     */
    public MinaTcpClient(MinaClientService service, MinaClientConfig minaClientConfig, IoHandler clientProtocolHandler, ProtocolCodecFactoryImpl factory) {
        this.factory=factory;
        codecFilter = new ProtocolCodecFilter(factory);
        this.service = service;
        this.clientProtocolHandler = clientProtocolHandler;
        init(clientProtocolHandler);
        setMinaClientConfig(minaClientConfig);
    }

    /**
     * 使用默认消息解码工厂
     *
     * @param service a {@link com.jzy.game.engine.mina.service.MinaClientService} object.
     * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
     * @param clientProtocolHandler a {@link org.apache.mina.core.service.IoHandler} object.
     */
    public MinaTcpClient(MinaClientService service, MinaClientConfig minaClientConfig, IoHandler clientProtocolHandler) {
        factory =new DefaultProtocolCodecFactory();
        codecFilter = new ProtocolCodecFilter(factory);
        this.service = service;
        this.clientProtocolHandler = clientProtocolHandler;
        init(clientProtocolHandler);
        setMinaClientConfig(minaClientConfig);
    }

    /**
     * 初始化tcp连接
     * @param clientProtocolHandler
     */
    private void init(IoHandler clientProtocolHandler) {
        connector = new NioSocketConnector();
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        chain.addLast("codec", codecFilter);
        if(filters != null){
            filters.forEach((key, filter)->{
				if("ssl".equalsIgnoreCase(key) || "tls".equalsIgnoreCase(key)){	//ssl过滤器必须添加到首部
					chain.addFirst(key, filter);
				}else{
					chain.addLast(key, filter);
				}
			});
		}
        connector.setHandler(clientProtocolHandler);
        connector.setConnectTimeoutMillis(60000L);
        connector.setConnectTimeoutCheckInterval(10000);
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
            log.warn("开始链接其他服务器,共 [" + getMinaClientConfig().getMaxConnectCount() + "] 个");
            MinaClientConfig.MinaClienConnToConfig connTo = getMinaClientConfig().getConnTo();
            if (connTo == null) {
                log.warn("没有连接配置");
                return;
            }
            for (int i = 0; i < getMinaClientConfig().getMaxConnectCount(); i++) {
                ConnectFuture connect = connector.connect(new InetSocketAddress(connTo.getHost(), connTo.getPort()));
                connect.awaitUninterruptibly(10000L);
                if (!connect.isConnected()) {
                    log.warn("失败！连接到服务器：" + connTo);
                    break;
                } else {
                    log.warn("成功！连接到服务器：" + connTo);
                    if (sessionCreateCallBack != null) {
                        sessionCreateCallBack.accept(getMinaClientConfig());
                    }
                }
            }
        } else {
            log.warn("连接配置为null");
        }
    }

    /**
     * <p>stop.</p>
     */
    public void stop() {
        synchronized (this) {
            try {
                connector.dispose();
                log.info("Client is stoped.");
            } catch (Exception ex) {
                log.error("", ex);
            }
        }
    }

    /**
     * 状态监测
     */
    public void checkStatus() {
        if (connector.getManagedSessionCount() < maxConnectCount ||
            connector.getManagedSessions().size() < maxConnectCount) {
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
     * 设置连接配置
     *
     * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
     */
    public void setMinaClientConfig(MinaClientConfig minaClientConfig) {
        if (minaClientConfig == null) {
            return;
        }
        this.minaClientConfig = minaClientConfig;
        SocketSessionConfig sc = connector.getSessionConfig();
        maxConnectCount = minaClientConfig.getMaxConnectCount();
        sc.setReceiveBufferSize(minaClientConfig.getReceiveBufferSize()); // 524288
        sc.setSendBufferSize(minaClientConfig.getSendBufferSize()); // 1048576
        sc.setMaxReadBufferSize(minaClientConfig.getMaxReadSize()); // 1048576
        factory.getDecoder().setMaxReadSize(minaClientConfig.getMaxReadSize());
        sc.setSoLinger(minaClientConfig.getSoLinger()); // 0
    }

    /**
     * <p>Getter for the field <code>clientProtocolHandler</code>.</p>
     *
     * @return a {@link org.apache.mina.core.service.IoHandler} object.
     */
    public IoHandler getClientProtocolHandler() {
        return clientProtocolHandler;
    }

    /**
     * <p>Getter for the field <code>sessionCreateCallBack</code>.</p>
     *
     * @return a {@link java.util.function.Consumer} object.
     */
    public Consumer<MinaClientConfig> getSessionCreateCallBack() {
        return sessionCreateCallBack;
    }

    /**
     * <p>Setter for the field <code>sessionCreateCallBack</code>.</p>
     *
     * @param sessionCreateCallBack a {@link java.util.function.Consumer} object.
     */
    public void setSessionCreateCallBack(Consumer<MinaClientConfig> sessionCreateCallBack) {
        this.sessionCreateCallBack = sessionCreateCallBack;
    }

    /**
     * <p>Getter for the field <code>service</code>.</p>
     *
     * @return a {@link com.jzy.game.engine.mina.service.MinaClientService} object.
     */
    public MinaClientService getService() {
        return service;
    }
    
}
