package com.jjy.game.tool.tcp.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.system.SystemMessage.UdpConnectResponse;
import com.jjy.game.tool.client.Player;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * udp连接返回
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月1日 下午5:11:56
 */
@HandlerEntity(mid = MID.UdpConnectRes_VALUE, msg = UdpConnectResponse.class)
public class UdpConnectResHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(UdpConnectResHandler.class);

	public void run() {
		UdpConnectResponse res = getMsg();
		Player player = (Player) session.getAttribute(Player.PLAYER);
		LOGGER.info("udp连接：{}", res.toString());
		if (res.getResult() == 0) {
			player.showLog(String.format("%s udp连接成功", player.getUserName()));
			player.setUdpLogin(true);
		}
	}

}
