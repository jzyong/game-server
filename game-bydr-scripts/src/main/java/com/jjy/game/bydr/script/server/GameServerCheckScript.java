package com.jjy.game.bydr.script.server;

import com.jjy.game.bydr.manager.RoleManager;
import com.jjy.game.bydr.server.BydrServer;
import com.jjy.game.model.script.IGameServerCheckScript;
import com.jzy.game.engine.mina.config.MinaServerConfig;

/**
 * 服务器状态监测脚本
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午4:36:25
 */
public class GameServerCheckScript implements IGameServerCheckScript {

	@Override
	public void buildServerInfo(com.jjy.game.message.ServerMessage.ServerInfo.Builder builder) {
		IGameServerCheckScript.super.buildServerInfo(builder);
		MinaServerConfig minaServerConfig = BydrServer.getInstance().getGameHttpServer().getMinaServerConfig();
		builder.setHttpport(minaServerConfig.getHttpPort());
		builder.setIp(minaServerConfig.getIp());
		builder.setOnline(RoleManager.getInstance().getOnlineRoles().size());	//设置在线人数
	}

}
