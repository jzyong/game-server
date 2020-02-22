package com.jzy.game.engine.mina.handler;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.message.MassMessage;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.util.MsgUtil;

import org.slf4j.Logger;

/**
 * 群发消息处理
 *
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 * @version $Id: $Id
 */
public abstract class MassProtocolHandler implements IoHandler {

	/** Constant <code>log</code> */
	protected static final Logger log = LoggerFactory.getLogger(MassProtocolHandler.class);
	protected final int messageHeaderLength;

	/**
	 * <p>Constructor for MassProtocolHandler.</p>
	 */
	public MassProtocolHandler() {
        messageHeaderLength = 4;
	}

	/** {@inheritDoc} */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// log.warn("已创建连接{}", session);
	}

	/** {@inheritDoc} */
	@Override
	public void sessionOpened(IoSession session) {
		log.warn("已打开连接{}", session);
//		getService().onIoSessionConnect(session);
	}

	/** {@inheritDoc} */
	@Override
	public void messageSent(IoSession ioSession, Object message) throws Exception {

	}

	/** {@inheritDoc} */
	@Override
	public void sessionClosed(IoSession session) {
		log.warn("连接{}已关闭sessionClosed", session);
//		getService().onIoSessionClosed(session);
	}

	/** {@inheritDoc} */
	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus) {
		log.warn("连接{}处于空闲{}", session, idleStatus);
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) {
		log.error("连接{}异常：{}", session, throwable);
		MsgUtil.close(session, "发生错误");
	}

	/** {@inheritDoc} */
	@Override
	public void inputClosed(IoSession session) throws Exception {
		log.warn("连接{}inputClosed已关闭", session);
		MsgUtil.close(session, "http inputClosed");
	}

	/** {@inheritDoc} */
	@Override
	public void messageReceived(IoSession session, Object obj) throws Exception {
		byte[] bytes = (byte[]) obj;
		try {
			if (bytes.length < messageHeaderLength) {
				log.error("messageReceived:消息长度{}小于等于消息头长度{}", bytes.length, messageHeaderLength);
				return;
			}
			IoBuffer buff = IoBuffer.wrap(bytes);
			int buffLength = buff.getInt();
			if (buffLength <= buff.remaining() - 8) {// 至少有一个id
				byte[] byteBuff = Arrays.copyOfRange(bytes, 4, buffLength + 4);
				IoBuffer msg = IoBuffer.wrap(byteBuff);
				ArrayList<Long> targets = new ArrayList<>();
				buff.position(buffLength + 4);
				while (buff.remaining() >= 8) {
					targets.add(buff.getLong());
				}
				MassMessage mass = new MassMessage(msg, targets);
				messageHandler(session, mass);
			}
		} catch (Exception e) {
			log.error("messageReceived", e);
		}
	}

	/**
	 * 消息处理
	 *
	 * @param session a {@link org.apache.mina.core.session.IoSession} object.
	 * @param msg a {@link com.jzy.game.engine.mina.message.MassMessage} object.
	 */
	protected abstract void messageHandler(IoSession session, MassMessage msg);

	/**
	 * <p>getService.</p>
	 *
	 * @return a {@link com.jzy.game.engine.server.Service} object.
	 */
	protected abstract Service<? extends BaseServerConfig> getService();
}
