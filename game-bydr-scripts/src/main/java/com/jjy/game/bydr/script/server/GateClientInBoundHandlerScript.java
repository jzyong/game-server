package com.jjy.game.bydr.script.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.server.BydrServer;
import com.jjy.game.message.ServerMessage;
import com.jjy.game.message.ServerMessage.ServerRegisterRequest;
import com.jjy.game.model.script.IGameServerCheckScript;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.netty.config.NettyClientConfig;
import com.jzy.game.engine.netty.handler.DefaultClientInBoundHandler;
import com.jzy.game.engine.netty.handler.IChannelHandlerScript;
import com.jzy.game.engine.netty.service.MutilNettyTcpClientService;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.IMutilTcpClientService;
import com.jzy.game.engine.server.ServerState;
import com.jzy.game.engine.server.Service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

/**
 * netty连接网关客户端消息处理器脚本
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月30日 上午10:52:05
 */
public class GateClientInBoundHandlerScript implements IChannelHandlerScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(GateClientInBoundHandlerScript.class);

	/**
	 * 向网关服注册channel 连接
	 */
	@Override
	public void channelActive(Class<? extends ChannelHandler> handlerClass, Service<? extends BaseServerConfig> service,
			Channel channel) {
		if (!handlerClass.isAssignableFrom(DefaultClientInBoundHandler.class)
				|| !(service instanceof MutilNettyTcpClientService)) {
			return;
		}
		// 向网关服注册session
		IMutilTcpClientService<? extends BaseServerConfig> client = BydrServer.getInstance().getBydr2GateClient();
		if (!(client instanceof MutilNettyTcpClientService)) {
			LOGGER.warn("未开启netty服务");
			return;
		}
		NettyClientConfig config = ((MutilNettyTcpClientService) client).getNettyClientConfig();
		ServerRegisterRequest.Builder builder = ServerRegisterRequest.newBuilder();
		ServerMessage.ServerInfo.Builder info = ServerMessage.ServerInfo.newBuilder();
		info.setId(config.getId());
		info.setIp("");
		info.setMaxUserCount(1000);
		info.setOnline(1);
		info.setName(config.getName());
		info.setState(ServerState.NORMAL.getState());
		info.setType(config.getType().getType());
		info.setWwwip("");
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IGameServerCheckScript.class,
				script -> script.buildServerInfo(info));
		builder.setServerInfo(info);
		channel.writeAndFlush(new IDMessage(channel, builder.build(), 0, 0));
	}

}
