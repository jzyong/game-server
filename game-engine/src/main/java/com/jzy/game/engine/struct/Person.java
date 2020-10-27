package com.jzy.game.engine.struct;

import com.alibaba.fastjson.annotation.JSONField;
import com.jzy.game.engine.mina.message.IDMessage;
import io.netty.channel.Channel;
import org.apache.mina.core.session.IoSession;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 人物
 * <br>
 * TODO 组合替换继承
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月26日 下午1:40:11
 */
public abstract class Person {
	private static final Logger LOGGER = LoggerFactory.getLogger(Person.class);

	@JSONField
	@Id
	protected long id;

	/** 昵称 */
	@JSONField
	@Indexed
	protected String nick;

	/** 用户ID */
	@JSONField
	@Indexed
	protected long userId;

	/** 金币 */
	@JSONField
	protected long gold;

	/** 钻石 */
	@JSONField
	protected long gem;

	/** 所在大厅ID */
	@JSONField
	protected int hallId;

	/** 所在游戏服ID */
	@JSONField
	protected int gameId;

	/** 头像 */
	@JSONField
	protected String head;

	/** 创建时间 */
	@JSONField
	protected Date createTime;

	/** 登录时间 */
	@JSONField
	protected Date loginTime;

	/** 等级 */
	@JSONField
	protected int level;

	/** 冷却缓存 */

	/** 连接会话 */
	protected transient IoSession ioSession;
	
	protected transient Channel channel;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public long getGem() {
		return gem;
	}

	public void setGem(long gem) {
		this.gem = gem;
	}

	public int getHallId() {
		return hallId;
	}

	public void setHallId(int hallId) {
		this.hallId = hallId;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}



	public IoSession getIoSession() {
		return ioSession;
	}

	public void setIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public void saveToRedis(String propertiesName) {
		
	}

	/**
	 * 发送消息，带ID头
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月3日 下午2:53:28
	 */
	public boolean sendMsg(Object message) {
		if (getIoSession() != null) {
			IDMessage idm = new IDMessage(getIoSession(), message, getId());
			getIoSession().write(idm);
			return true;
		} else if (getChannel() != null) {
			getChannel().writeAndFlush(new IDMessage(channel, message, getId(), null));
		} else {
			LOGGER.warn("连接session==null | channel==null {}", message);
		}
		return false;
	}
}
