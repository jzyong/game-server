package com.jzy.game.engine.handler;

import com.google.protobuf.Message;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.struct.Person;

/**
 * Tcp 处理器
 * <br>也可能处理udp请求
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public abstract class TcpHandler extends AbsHandler {
	private Message message;
	protected long rid; // 角色ID
	protected Person person; // 角色

	@Override
	public Message getMessage() {
		return message;
	}

	/**
	 * 获取消息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> T getMsg() {
		return (T) message;
	}

	@SuppressWarnings("unchecked")
	public <T extends Person> T getPerson() {
		return (T) person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	public void setMessage(Object message) {
		this.message = (Message) message;
	}

	public long getRid() {
		return rid;
	}

	public void setRid(long rid) {
		this.rid = rid;
	}

	/**
	 * 发送带ID的消息
	 * 
	 * @param object
	 */
	public void sendIdMsg(Object object) {
		if (getSession() != null && getSession().isConnected()) {
			getSession().write(new IDMessage(session, object, rid));
		} else if (getChannel() != null && getChannel().isActive()) {
			getChannel().writeAndFlush(new IDMessage(channel, object, rid, null));
		}
	}

}
