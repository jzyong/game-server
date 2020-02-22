package com.jzy.game.engine.mina.service;

import java.util.Map;

import org.apache.mina.core.filterchain.IoFilter;

import com.jzy.game.engine.mina.TcpServer;
import com.jzy.game.engine.mina.code.ClientProtocolCodecFactory;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.handler.ClientProtocolHandler;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.thread.ServerThread;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.engine.thread.timer.event.ServerHeartTimer;

/**
 * 游戏前端消息接收 服务
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月29日 下午2:15:38
 * @version $Id: $Id
 */
public class ClientServerService extends Service<MinaServerConfig> {
    protected final TcpServer tcpServer;
    protected final MinaServerConfig minaServerConfig;
    protected final ClientProtocolHandler clientProtocolHandler;


    /**
     * 不创建默认IO线程池
     *
     * @param minaServerConfig a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
     */
    public ClientServerService(MinaServerConfig minaServerConfig) {
        this(null, minaServerConfig);
    }

    /**
     * 使用默认消息处理器
     *
     * @param threadExcutorConfig a {@link com.jzy.game.engine.thread.ThreadPoolExecutorConfig} object.
     * @param minaServerConfig a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
     */
    public ClientServerService(ThreadPoolExecutorConfig threadExcutorConfig, MinaServerConfig minaServerConfig) {
        this(threadExcutorConfig, minaServerConfig, new ClientProtocolHandler(8));
    }

    /**
     * <p>Constructor for ClientServerService.</p>
     *
     * @param threadExcutorConfig 线程池配置
     * @param minaServerConfig 服务器配置
     * @param clientProtocolHandler 消息处理器
     */
    public ClientServerService(ThreadPoolExecutorConfig threadExcutorConfig, MinaServerConfig minaServerConfig,
                               ClientProtocolHandler clientProtocolHandler) {
        super(threadExcutorConfig);
        this.minaServerConfig = minaServerConfig;
        this.clientProtocolHandler = clientProtocolHandler;
        tcpServer = new TcpServer(minaServerConfig, clientProtocolHandler, new ClientProtocolCodecFactory());
    }

    /**
     * <p>Constructor for ClientServerService.</p>
     *
     * @param threadExcutorConfig 线程池配置
     * @param minaServerConfig 服务器配置
     * @param clientProtocolHandler 消息处理器
     * @param filters a {@link java.util.Map} object.
     */
    public ClientServerService(ThreadPoolExecutorConfig threadExcutorConfig, MinaServerConfig minaServerConfig,
                               ClientProtocolHandler clientProtocolHandler, Map<String, IoFilter> filters) {
        super(threadExcutorConfig);
        this.minaServerConfig = minaServerConfig;
        this.clientProtocolHandler = clientProtocolHandler;
        tcpServer = new TcpServer(minaServerConfig, clientProtocolHandler, new ClientProtocolCodecFactory(), filters);
    }


    /** {@inheritDoc} */
    @Override
    protected void running() {
        clientProtocolHandler.setService(this);
        tcpServer.run();
        // 添加定时器 ,如果心跳配置为0，则没有定时器
        ServerThread syncThread = getExecutor(ThreadType.SYNC);
        if (syncThread != null) {
            syncThread.addTimerEvent(new ServerHeartTimer());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onShutdown() {
        super.onShutdown();
        tcpServer.stop();

    }

    /**
     * <p>Getter for the field <code>minaServerConfig</code>.</p>
     *
     * @return a {@link com.jzy.game.engine.mina.config.MinaServerConfig} object.
     */
    public MinaServerConfig getMinaServerConfig() {
        return minaServerConfig;
    }

    /**
     * <p>Getter for the field <code>tcpServer</code>.</p>
     *
     * @return a {@link com.jzy.game.engine.mina.TcpServer} object.
     */
    public TcpServer getTcpServer() {
        return tcpServer;
    }


}
