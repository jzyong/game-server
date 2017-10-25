package com.jjy.game.hall.tcp.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.manager.RoleManager;
import com.jjy.game.hall.script.IGmScript;
import com.jjy.game.hall.server.HallServer;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallChatMessage.ChatRequest;
import com.jjy.game.message.hall.HallChatMessage.ChatResponse;
import com.jjy.game.model.struct.Role;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.script.ScriptPool;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 聊天处理 <br>
 * 消息发往网关进行转发
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月13日 下午3:15:49
 */
@HandlerEntity(mid = MID.ChatReq_VALUE, msg = ChatRequest.class)
public class ChatHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatHandler.class);

	@Override
	public void run() {
		ChatRequest req = getMsg();

		Role role = RoleManager.getInstance().getRole(this.rid);

		if (role == null) {
			LOGGER.warn("{}-{}未登陆", rid, MsgUtil.getIp(session));
			return;
		}
		// TODO 消息内容过滤

		// gm命令执行
		ScriptPool scriptPool = ScriptManager.getInstance().getBaseScriptEntry();
		if (scriptPool.predicateScripts(IGmScript.class, (IGmScript script) -> script.isGMCmd(req.getMsg()))) {
			scriptPool.executeScripts(IGmScript.class, script -> script.executeGm(this.rid, req.getMsg()));
		}

		switch (req.getChatType()) {
		case PRIVATE:
			chatPrivate(req, role);
			break;
		case WORLD:
			chatWorld(req, role);
			break;

		default:
			break;
		}
	}

	/**
	 * 私聊
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月13日 下午3:23:41
	 */
	private void chatPrivate(ChatRequest req, Role role) {
		// TODO 接收是否在线等验证

		if (req.getReceiverId() < 1) {
			LOGGER.warn("消息错误，接受者ID为：{}", req.getReceiverId());
			return;
		}

		ChatResponse.Builder builder = ChatResponse.newBuilder();
		builder.setChatType(req.getChatType());
		builder.setSenderHead(role.getHead() == null ? "" : role.getHead());
		builder.setSenderId(role.getId());
		builder.setSenderNick(role.getNick());
		builder.setMsg(req.getMsg());

		HallServer.getInstance().getHall2GateClient().broadcastMsg(builder.build(), req.getReceiverId());
	}

	/**
	 * 世界聊天
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月13日 下午3:46:32
	 * @param req
	 * @param role
	 */
	private void chatWorld(ChatRequest req, Role role) {
		ChatResponse.Builder builder = ChatResponse.newBuilder();
		builder.setChatType(req.getChatType());
		builder.setSenderHead(role.getHead() == null ? "" : role.getHead());
		builder.setSenderId(role.getId());
		builder.setSenderNick(role.getNick());
		builder.setMsg(req.getMsg());

		HallServer.getInstance().getHall2GateClient().broadcastMsg(builder.build());
	}

}
