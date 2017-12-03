package com.jzy.game.gate.script.server;

import java.util.HashSet;
import java.util.Set;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.engine.script.IInitScript;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.gate.script.IGateServerScript;

/**
 * 注册udp消息和udp服务器
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月1日 下午2:43:00
 */
public class UdpMsgRegisterScript implements IGateServerScript, IInitScript {
	private Set<Integer> udpMsgIds = new HashSet<>(); // udp支持的消息
	private Set<ServerType> udpServers = new HashSet<>(); // udp支持的服务器

	@Override
	public void init() {
		// 注册udp游戏
		udpServers.add(ServerType.GAME_BYDR);

		// 注册udp消息,只需要注册返回消息
		udpMsgIds.add(MID.HeartRes_VALUE);
		udpMsgIds.add(MID.EnterRoomRes_VALUE);	
		udpMsgIds.add(MID.ChatRes_VALUE);
	}

	@Override
	public boolean isUdpMsg(ServerType serverType, int msgId) {
		if (serverType == null) {
			return false;
		}
		return udpServers.contains(serverType) && udpMsgIds.contains(msgId);
	}

}
