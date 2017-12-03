package com.jzy.game.cluster.http.server;

import com.jzy.game.cluster.manager.ServerManager;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.server.ServerInfo;

/**
 * 获取网关服务器
 *
 * <p>
 * http://127.0.0.1:8001/server/gate/ip
 * </p>
 * 
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 * 
 */
@HandlerEntity(path = "/server/gate/ip")
public class GetGateIpHandler extends HttpHandler {

	@Override
	public void run() {
		try {
			String version = getString("version");
			ServerInfo serverInfo = ServerManager.getInstance().getIdleGate(version);
			if (serverInfo == null) {
				getParameter().appendBody("无可用网关服");
			} else {
				getParameter().appendBody(serverInfo.getIp() + ":" + serverInfo.getPort());
			}
		} finally {
			response();
		}
	}

}
