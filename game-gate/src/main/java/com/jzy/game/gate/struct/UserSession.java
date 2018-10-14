package com.jzy.game.gate.struct;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.gate.manager.ServerManager;
import com.jzy.game.gate.manager.UserSessionManager;

/**
 * 用户连接会话
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月30日 下午2:13:54
 */
public class UserSession {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserSession.class);

	/** 用户ID */
	private long userId;

	/** 角色ID */
	private long roleId;

	/** 游戏前端用户session */
	private IoSession clientSession;
	
	/**游戏前端udp会话*/
	private IoSession clientUdpSession;

	/** 当前连接的子游戏 session */
	private IoSession gameSession;
	
	/** 当前连接大厅session */
	private IoSession hallSession;

	/** 登录的游戏类型 */
	private ServerType serverType;

	/** 登录的游戏ID */
	private int serverId;
	
	/** 登录的大厅ID */
	private int hallServerId;
	
	/**客户端使用服务器的版本号*/
	private String version;

	public UserSession(IoSession clientSession) {
        this.clientSession = clientSession;
		UserSessionManager.getInstance().onUserConnected(this);
	}

	/**
	 * 发送给游戏客户端
	 *
	 * @param msg
	 * @return
	 */
	public boolean sendToClient(Object msg) {
		try {
			if (clientSession != null && clientSession.isConnected()) {
				// LOGGER.debug(" bytes:{}", msg);
				clientSession.write(msg);
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("sendToClient:", e);
		}
		return false;
	}
	
	/**
	 * 发送给游戏客户端,udp
	 *
	 * @param msg
	 * @return
	 */
	public boolean sendToClientUdp(Object msg) {
		try {
			if (getClientUdpSession() != null && getClientUdpSession().isConnected()) {
				// LOGGER.debug(" bytes:{}", msg);
				getClientUdpSession().write(msg);
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("sendToClientUdp:", e);
		}
		return false;
	}

	/**
	 * 发送给游戏客户端
	 *
	 * @param msg
	 * @return
	 */
	public boolean sendToGame(Object msg) {
		try {
			if (getUserId() < 1) {
				return false;
			}

			if (getGameSession() != null && getGameSession().isConnected()) {
				IDMessage idMessage = new IDMessage(getGameSession(), msg, roleId < 1 ? userId : roleId);
				idMessage.run();
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("sendToGame:", e);
		}
		return false;
	}
	
	/**
	 * 发送给游戏客户端
	 *
	 * @param msg
	 * @return
	 */
	public boolean sendToHall(Object msg) {
		try {
			if (getUserId() < 1) {
				return false;
			}

			if (getHallSession() != null && getHallSession().isConnected()) {
				IDMessage idMessage = new IDMessage(getHallSession(), msg, roleId < 1 ? userId : roleId);
				idMessage.run();
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("sendToGame:", e);
		}
		return false;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public IoSession getClientSession() {
		return clientSession;
	}

	public void setClientSession(IoSession clientSession) {
		this.clientSession = clientSession;
	}

	public IoSession getGameSession() {
		if ((gameSession == null || !gameSession.isConnected()) && getServerId() > 0) {
			ServerInfo serverInfo = ServerManager.getInstance().getGameServerInfo(serverType, serverId);
			if (serverInfo != null) {
				gameSession = serverInfo.getMostIdleIoSession();
//				LOGGER.debug("用户{} 游戏session{}", userId, gameSession.getId());
			}else{
				LOGGER.warn("{}-{}没有服务器连接", serverType, serverId);
			}
		}
		return gameSession;
	}

	public void setGameSession(IoSession gameSession) {
		this.gameSession = gameSession;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public IoSession getHallSession() {
		if (hallSession == null || !hallSession.isConnected()) {
			ServerInfo serverInfo = ServerManager.getInstance().getGameServerInfo(ServerType.HALL, hallServerId);	
			if (serverInfo != null) {
				hallSession = serverInfo.getMostIdleIoSession();
				LOGGER.debug("用户{} 大厅session{}", userId, hallSession.getId());
			}
		}
		
		return hallSession;
	}

	public void setHallSession(IoSession hallSession) {
		this.hallSession = hallSession;
	}
	
	public IoSession getClientUdpSession() {
		return clientUdpSession;
	}

	public void setClientUdpSession(IoSession clientUdpSession) {
		this.clientUdpSession = clientUdpSession;
	}

	public int getHallServerId() {
		return hallServerId;
	}

	public void setHallServerId(int hallServerId) {
		this.hallServerId = hallServerId;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 移除游戏连接状态
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年7月27日 上午9:41:13
	 */
	public void removeGame(){
        setGameSession(null);
        setServerId(0);
        setServerType(null);
	}
	
	public void removeHall(){
        hallSession =null;
        hallServerId =0;
	}
	
}
