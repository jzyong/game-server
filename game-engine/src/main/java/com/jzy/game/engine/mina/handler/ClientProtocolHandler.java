package com.jzy.game.engine.mina.handler;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.IHandler;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.util.IntUtil;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 游戏前端消息处理器
 * <p>
 * 包长度（2）+消息ID（4）+消息长度（4）+消息内容
 * 
 * <br>
 * decoder 已去掉包长度
 * </p>
 *
 * @author JiangZhiYong
 * @date 2017-04-01 QQ:359135103
 */
public class ClientProtocolHandler extends DefaultProtocolHandler {

	private static final Logger log = LoggerFactory.getLogger(ClientProtocolHandler.class);
	protected Service<MinaServerConfig> service;

	public ClientProtocolHandler(int messageHeaderLength) {
		super(messageHeaderLength);
	}

	@Override
	public void messageReceived(IoSession session, Object obj) throws Exception {
		byte[] bytes = (byte[]) obj;
		try {
			if (bytes.length < messageHeaderLength) {
				log.error("messageReceived:消息长度{}小于等于消息头长度{}", bytes.length, messageHeaderLength);
				return;
			}
			int mid = IntUtil.bigEndianByteToInt(bytes, 0, 4); // 消息ID
			// int protoLength=IntUtil.bigEndianByteToInt(bytes, 6, 4); //TODO
			// 消息长度,不需要？

			if (ScriptManager.getInstance().tcpMsgIsRegister(mid)) {
				Class<? extends IHandler> handlerClass = ScriptManager.getInstance().getTcpHandler(mid);
				HandlerEntity handlerEntity = ScriptManager.getInstance().getTcpHandlerEntity(mid);
				if (handlerClass != null) {
//					log.info("{} {} bytes:{}",messageHeaderLenght, bytes.length, bytes );
					Message message = MsgUtil.buildMessage(handlerEntity.msg(), bytes, messageHeaderLength,
							bytes.length - messageHeaderLength);
					TcpHandler handler = (TcpHandler) handlerClass.newInstance();
					if (handler != null) {
						messageHandler(session, handlerEntity, message, handler);
						return;
					}
				}
			}
			forward(session, mid, bytes);

		} catch (Exception e) {
			log.error("messageReceived", e);
		}
	}

	@Override
	protected void forward(IoSession session, int msgID, byte[] bytes) {
		log.warn("消息[{}]未实现", msgID);

	}

	@Override
	public Service<? extends BaseServerConfig> getService() {
		return service;
	}

	public void setService(Service<MinaServerConfig> service) {
		this.service = service;
	}

}
