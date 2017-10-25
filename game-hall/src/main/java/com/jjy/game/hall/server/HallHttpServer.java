package com.jjy.game.hall.server;

import com.jjy.game.model.handler.http.favicon.GetFaviconHandler;
import com.jjy.game.model.handler.http.server.ExitServerHandler;
import com.jjy.game.model.handler.http.server.JvmInfoHandler;
import com.jjy.game.model.handler.http.server.ReloadConfigHandler;
import com.jjy.game.model.handler.http.server.ReloadScriptHandler;
import com.jjy.game.model.handler.http.server.ThreadInfoHandler;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.service.GameHttpSevice;
import com.jzy.game.engine.script.ScriptManager;

/**
 * 大厅HTTP服务
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月25日 上午11:46:07
 */
public class HallHttpServer extends GameHttpSevice {

	public HallHttpServer(MinaServerConfig minaServerConfig) {
		super(minaServerConfig);
	}

	@Override
	protected void running() {
		super.running();
		ScriptManager.getInstance().addIHandler(ReloadScriptHandler.class);
		ScriptManager.getInstance().addIHandler(ExitServerHandler.class);
		ScriptManager.getInstance().addIHandler(ReloadConfigHandler.class);
		ScriptManager.getInstance().addIHandler(JvmInfoHandler.class);
		ScriptManager.getInstance().addIHandler(GetFaviconHandler.class);
		ScriptManager.getInstance().addIHandler(ThreadInfoHandler.class);
	}

}
