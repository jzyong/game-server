package com.jzy.game.bydr.script.server;

import com.jzy.game.bydr.manager.RoleManager;
import com.jzy.game.bydr.server.BydrServer;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.model.script.IGameServerCheckScript;

/**
 * 服务器状态监测脚本
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午4:36:25
 */
public class GameServerCheckScript implements IGameServerCheckScript {

	@Override
	public void buildServerInfo(com.jzy.game.message.ServerMessage.ServerInfo.Builder builder) {
		IGameServerCheckScript.super.buildServerInfo(builder);
		MinaServerConfig minaServerConfig = BydrServer.getInstance().getGameHttpServer().getMinaServerConfig();
		builder.setHttpport(minaServerConfig.getHttpPort());
		builder.setIp(minaServerConfig.getIp());
		builder.setOnline(RoleManager.getInstance().getOnlineRoles().size());	//设置在线人数
	}

}
