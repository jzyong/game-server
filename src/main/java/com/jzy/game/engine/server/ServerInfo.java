package com.jzy.game.engine.server;

import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 服务器信息
 *
 * @author JiangZhiYong
 * @date 2017-04-01 QQ:359135103
 */
public class ServerInfo {
	private final static Logger log = LoggerFactory.getLogger(ServerInfo.class);

	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	// 服务器ID
	private int id;

	// 服务器名称
	protected String name;
	// 地址
	private String ip;
	// 外网地址
	private String wwwip;
	// 端口
	private int port;
	// 当前状态 1表示维护；0表示正常
	private int state = 0;
	// http端口
	private int httpPort;
	// 最大用户人数
	private int maxUserCount;
	// 在线人数
	@JSONField(serialize=true)
	private  int online;
	// 服务器类型
	private int type;
	
	@JSONField(serialize=false)
	private transient IoSession session;

	/** 客户端多连接管理 */
	@JSONField(serialize=false)
	protected transient final Queue<IoSession> sessions = new ConcurrentLinkedQueue<>();

	public ServerInfo() {
	}

	@JSONField(serialize=false)
	public void onIoSessionConnect(IoSession session) {
		if (!sessions.contains(session)) {
			sessions.add(session);
		}
	}

	/**
	 * 获取连接列表中最空闲的有效的连接
	 *
	 * @return
	 */
	@JSONField(serialize=false)
	public IoSession getMostIdleIoSession() {
		IoSession session = null;
		sessions.stream().sorted(MsgUtil.sessionIdleComparator);
		while (session == null && !sessions.isEmpty()) {
			session =sessions.poll();
			log.debug("空闲session {}", session.getId());
			if (session != null && session.isConnected()) {
				sessions.offer(session);
				break;
			} 
		}
		return session;
	}

	public void sendMsg(Object message) {
		IoSession se = getSession();
		if (se != null) {
			se.write(message);
		} else {
			log.warn("服务器:" + name + "连接会话为空");
		}
	}

	@JSONField(serialize=false)
	public IoSession getSession() {
		if (session == null || !session.isActive()) {
			this.session = getMostIdleIoSession();
		}
		return session;
	}

	@JSONField(serialize=false)
	public void setSession(IoSession session) {
		this.session = session;
	}
	
	@JSONField(serialize=false)
	public String getHttpUrl(String content) {
		StringBuilder sb = new StringBuilder("http://").append(getIp()).append(":").append(getHttpPort()).append("/")
				.append(content);
		return sb.toString().trim();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getWwwip() {
		return wwwip;
	}

	public void setWwwip(String wwwip) {
		this.wwwip = wwwip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public int getMaxUserCount() {
		return maxUserCount;
	}

	public void setMaxUserCount(int maxUserCount) {
		this.maxUserCount = maxUserCount;
	}

	@JSONField(serialize=true)
	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ServerInfo [id=" + id + ", name=" + name + ", ip=" + ip + ", wwwip=" + wwwip + ", port=" + port
				+ ", state=" + state + ", httpPort=" + httpPort + ", maxUserCount=" + maxUserCount + ", type=" + type
				+ "]";
	}

}
