package com.jzy.game.engine.mina.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.HttpServer;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.handler.HttpServerIoHandler;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;

/**
 * 游戏服http服务器
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月24日 上午11:28:28
 */
public class GameHttpSevice extends Service<MinaServerConfig>{

	private static final Logger log = LoggerFactory.getLogger(GameHttpSevice.class);

    private final HttpServer httpServer;
    private final MinaServerConfig minaServerConfig;

    public GameHttpSevice(ThreadPoolExecutorConfig threadExcutorConfig, MinaServerConfig minaServerConfig) {
        super(threadExcutorConfig);
        this.minaServerConfig = minaServerConfig;
        this.httpServer = new HttpServer(minaServerConfig, new GameHttpServerHandler(this));
    }
    
    public GameHttpSevice(MinaServerConfig minaServerConfig) {
        super(null);
        this.minaServerConfig = minaServerConfig;
        this.httpServer = new HttpServer(minaServerConfig, new GameHttpServerHandler(this));
    }

    @Override
    protected void running() {
        log.debug(" run ... ");
        httpServer.run();
    }

    @Override
    protected void onShutdown() {
    	super.onShutdown();
        log.debug(" stop ... ");
        httpServer.stop();
    }

    @Override
    public String toString() {
        return minaServerConfig.getName();
    }

	public MinaServerConfig getMinaServerConfig() {
		return minaServerConfig;
	}
    
}

/**
 * 消息处理器
 *
 * @author JiangZhiYong
 * @date 2017-03-31
 * QQ:359135103
 */
class GameHttpServerHandler extends HttpServerIoHandler {

    //private static final Logger log = LoggerFactory.getLogger(ClusterHttpServerHandler.class);

    private Service<MinaServerConfig> service;

    public GameHttpServerHandler(Service<MinaServerConfig> service) {
        this.service=service;
    }

	@Override
	protected Service<MinaServerConfig> getSevice() {
		return this.service;
	}
}
