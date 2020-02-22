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
 * @version $Id: $Id
 */
public abstract class TcpHandler extends AbsHandler {
	private Message message;
	protected long rid; // 角色ID
	protected Person person; // 角色

	/** {@inheritDoc} */
	@Override
	public Message getMessage() {
		return message;
	}

	/**
	 * 获取消息
	 *
	 * @param <T> a T object.
	 * @return a T object.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> T getMsg() {
		return (T) message;
	}

	/**
	 * <p>Getter for the field <code>person</code>.</p>
	 *
	 * @param <T> a T object.
	 * @return a T object.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Person> T getPerson() {
		return (T) person;
	}

	/**
	 * <p>Setter for the field <code>person</code>.</p>
	 *
	 * @param person a {@link com.jzy.game.engine.struct.Person} object.
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/** {@inheritDoc} */
	@Override
	public void setMessage(Object message) {
		this.message = (Message) message;
	}

	/**
	 * <p>Getter for the field <code>rid</code>.</p>
	 *
	 * @return a long.
	 */
	public long getRid() {
		return rid;
	}

	/**
	 * <p>Setter for the field <code>rid</code>.</p>
	 *
	 * @param rid a long.
	 */
	public void setRid(long rid) {
		this.rid = rid;
	}

	/**
	 * 发送带ID的消息
	 *
	 * @param object a {@link java.lang.Object} object.
	 */
	public void sendIdMsg(Object object) {
		if (getSession() != null && getSession().isConnected()) {
			getSession().write(new IDMessage(session, object, rid));
		} else if (getChannel() != null && getChannel().isActive()) {
			getChannel().writeAndFlush(new IDMessage(channel, object, rid, null));
		}
	}

}
