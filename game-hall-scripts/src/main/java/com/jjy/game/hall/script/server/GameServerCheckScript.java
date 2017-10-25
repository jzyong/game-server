package com.jjy.game.hall.script.server;

import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jjy.game.hall.manager.RoleManager;
import com.jjy.game.hall.server.HallServer;
import com.jjy.game.model.script.IGameServerCheckScript;

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
		MinaServerConfig minaServerConfig = HallServer.getInstance().getHallHttpServer().getMinaServerConfig();
		builder.setHttpport(minaServerConfig.getHttpPort());
		builder.setIp(minaServerConfig.getIp());
		builder.setOnline(RoleManager.getInstance().getRoles().size());	//设置在线人数 TODO
	}

}
