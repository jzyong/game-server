package com.jzy.game.cluster.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.engine.mina.HttpServer;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.handler.HttpServerIoHandler;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.model.handler.http.favicon.GetFaviconHandler;
import com.jzy.game.model.handler.http.server.ExitServerHandler;
import com.jzy.game.model.handler.http.server.JvmInfoHandler;
import com.jzy.game.model.handler.http.server.ReloadConfigHandler;
import com.jzy.game.model.handler.http.server.ReloadScriptHandler;
import com.jzy.game.model.handler.http.server.ThreadInfoHandler;

/**
 * http连接
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 */
public class ClusterHttpServer extends Service<MinaServerConfig> {

	private static final Logger log = LoggerFactory.getLogger(ClusterHttpServer.class);

	private final HttpServer httpServer;
	private final MinaServerConfig minaServerConfig;

	public ClusterHttpServer(ThreadPoolExecutorConfig threadExcutorConfig, MinaServerConfig minaServerConfig) {
		super(threadExcutorConfig);
		this.minaServerConfig = minaServerConfig;
		this.httpServer = new HttpServer(minaServerConfig, new ClusterHttpServerHandler(this));
	}

	@Override
	protected void running() {
		log.debug(" run ... ");
		httpServer.run();
		ScriptManager.getInstance().addIHandler(ReloadScriptHandler.class);
		ScriptManager.getInstance().addIHandler(ExitServerHandler.class);
		ScriptManager.getInstance().addIHandler(ReloadConfigHandler.class);
		ScriptManager.getInstance().addIHandler(JvmInfoHandler.class);
		ScriptManager.getInstance().addIHandler(GetFaviconHandler.class);
		ScriptManager.getInstance().addIHandler(ThreadInfoHandler.class);
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
}

/**
 * 消息处理器
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 */
class ClusterHttpServerHandler extends HttpServerIoHandler {

	// private static final Logger log =
	// LoggerFactory.getLogger(ClusterHttpServerHandler.class);

	private final Service<MinaServerConfig> service;

	public ClusterHttpServerHandler(Service<MinaServerConfig> service) {
		this.service = service;
	}

	@Override
	protected Service<MinaServerConfig> getSevice() {
		return this.service;
	}

}
