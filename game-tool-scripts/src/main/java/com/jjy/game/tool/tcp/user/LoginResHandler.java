package com.jjy.game.tool.tcp.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallChatMessage.ChatRequest;
import com.jjy.game.message.hall.HallChatMessage.ChatType;
import com.jjy.game.message.hall.HallLoginMessage.LoginResponse;
import com.jjy.game.message.hall.HallLoginMessage.LoginSubGameRequest;
import com.jjy.game.message.system.SystemMessage.UdpConnectRequest;
import com.jjy.game.tool.client.Player;
import com.jjy.game.tool.client.PressureClientTool;
import com.jjy.game.tool.client.PressureServiceThread;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.server.ServerType;

/**
 * 登录返回
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午6:26:19
 */
@HandlerEntity(mid = MID.LoginRes_VALUE, msg = LoginResponse.class)
public class LoginResHandler extends TcpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginResHandler.class);

	public void run() {
		LoginResponse res = getMsg();
		long uid = res.getRid();

		long sendTime = (Long) session.getAttribute(PressureServiceThread.SEND_TIME, Long.MAX_VALUE);
		LOGGER.info("用户[{}] 登录成功，耗时：{}ms", uid, (System.currentTimeMillis() - sendTime));
		session.setAttribute("roleId", uid);
		Player player = (Player) session.getAttribute(Player.PLAYER);
		player.showLog(String.format("用户%s登录,耗时：%d", player.getUserName(), (System.currentTimeMillis() - sendTime)));

		// 登录子游戏
		LoginSubGameRequest.Builder builder3 = LoginSubGameRequest.newBuilder();
		builder3.setType(0);
		builder3.setRid(uid);
		builder3.setGameType(ServerType.GAME_BYDR.getType());
		session.write(builder3.build());

		// 登录udp
		UdpConnectRequest.Builder udpBuilder = UdpConnectRequest.newBuilder();
		udpBuilder.setSessionId(res.getSessionId());
		udpBuilder.setRid(res.getRid());
		player.getTcpSession().write(udpBuilder.build());

//		// 聊天测试
//		ChatRequest.Builder chatBuilder = ChatRequest.newBuilder();
//		chatBuilder.setChatType(ChatType.WORLD);
//		chatBuilder.setMsg("hello from " + player.getUserName());
//		player.sendUdpMsg(chatBuilder.build());
	}

}
