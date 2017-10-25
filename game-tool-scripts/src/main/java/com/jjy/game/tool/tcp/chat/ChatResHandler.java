package com.jjy.game.tool.tcp.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallChatMessage.ChatResponse;
import com.jjy.game.tool.client.Player;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 聊天返回
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月13日 下午4:26:37
 */
@HandlerEntity(mid = MID.ChatRes_VALUE, msg = ChatResponse.class)
public class ChatResHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatResHandler.class);

	@Override
	public void run() {
		ChatResponse res = getMsg();
		LOGGER.info("聊天：{}", res.toString());
		Player player = (Player) session.getAttribute(Player.PLAYER);
		if(player!=null&&res!=null) {
			player.showLog(String.format("聊天--> %s:%s",player.getUserName(),res.toString() ));
		}
	}

}
