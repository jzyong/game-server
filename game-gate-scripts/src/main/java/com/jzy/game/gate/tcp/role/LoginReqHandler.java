package com.jzy.game.gate.tcp.role;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallLoginMessage.LoginRequest;
import com.jzy.game.message.hall.HallLoginMessage.LoginRequest.Builder;
import com.jzy.game.message.system.SystemMessage.SystemErroCode;
import com.jzy.game.message.system.SystemMessage.SystemErrorResponse;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.engine.util.MsgUtil;
import com.jzy.game.gate.manager.ServerManager;
import com.jzy.game.gate.manager.UserSessionManager;
import com.jzy.game.gate.server.GateServer;
import com.jzy.game.gate.struct.UserSession;

/**
 * 登录请求
 * <p>
 * 保存用户session 设置UserSession 大厅ID，大厅session<br>
 * TODO 重连处理？？？？
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月21日 下午1:31:55
 */
@HandlerEntity(mid = MID.LoginReq_VALUE, desc = "登陆", msg = LoginRequest.class)
public class LoginReqHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginReqHandler.class);

	@Override
	public void run() {
		LoginRequest request = getMsg();
		UserSession userSession = UserSessionManager.getInstance().getUserSessionBySessionId(session.getId());
		if (userSession == null) {
			session.write(
					ServerManager.getInstance().buildSystemErrorResponse(SystemErroCode.ConectReset, "连接会话已失效，请重连"));
			LOGGER.warn("连接会话已失效，请重连");
			return;
		}
		
		ServerInfo serverInfo = ServerManager.getInstance().getIdleGameServer(ServerType.HALL,userSession);
		if (serverInfo == null) {
			SystemErrorResponse.Builder sysBuilder = SystemErrorResponse.newBuilder();
			sysBuilder.setErrorCode(SystemErroCode.HallNotFind);
			sysBuilder.setMsg("未开启大厅服");
			getSession().write(sysBuilder.build());
			LOGGER.warn("大厅服不可用");
			return;
		}
		IoSession hallSession = serverInfo.getMostIdleIoSession();
		Builder builder = request.toBuilder();
		builder.setSessionId(session.getId());
		builder.setIp(MsgUtil.getIp(session));
		builder.setGateId(GateServer.getInstance().getGateTcpUserServer().getMinaServerConfig().getId());
		if (serverInfo == null || hallSession == null) {
			LOGGER.warn("大厅服务器未准备就绪");
			session.write(ServerManager.getInstance().buildSystemErrorResponse(SystemErroCode.HallNotFind, "没可用大厅服"));
			return;
		}
		
		userSession.setHallServerId(serverInfo.getId());
		userSession.setHallSession(hallSession);
		userSession.setVersion(request.getVersion());

		IDMessage idMessage = new IDMessage(hallSession, builder.build(), 0);
		idMessage.run();

	}

}
