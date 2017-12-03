package com.jzy.game.gate.tcp.server;

import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.ServerMessage.ServerListResponse;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.gate.manager.ServerManager;

/**
 * 返回游戏服务器列表
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月30日 下午5:47:25
 */
@HandlerEntity(mid = MID.ServerListRes_VALUE, msg = ServerListResponse.class)
public class ServerListHandler extends TcpHandler {

	@Override
	public void run() {
		ServerListResponse response = getMsg();
		if (response != null) {
			response.getServerInfoList().forEach(info -> {
				ServerManager.getInstance().updateServerInfo(info);
			});
		}

	}

}
