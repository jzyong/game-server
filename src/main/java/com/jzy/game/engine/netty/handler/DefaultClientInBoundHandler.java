package com.jzy.game.engine.netty.handler;

import com.jzy.game.engine.netty.service.NettyClientService;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.Service;

import io.netty.channel.ChannelHandlerContext;

/**
 * 内部客户端 默认消息
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月25日 下午3:24:23
 */
public class DefaultClientInBoundHandler extends DefaultInBoundHandler {

	private NettyClientService netttyClientService;
	private ServerInfo serverInfo;

	public DefaultClientInBoundHandler(NettyClientService netttyClientService, ServerInfo serverInfo) {
		super();
		this.netttyClientService = netttyClientService;
		this.serverInfo = serverInfo;
	}

	public DefaultClientInBoundHandler(NettyClientService netttyClientService) {
		super();
		this.netttyClientService = netttyClientService;
	}

	@Override
	public Service<? extends BaseServerConfig> getService() {
		return netttyClientService;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		netttyClientService.channelActive(ctx.channel());
		if (this.serverInfo != null) {
			serverInfo.onChannelActive(ctx.channel());
		}
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IChannelHandlerScript.class,
				script -> script.channelActive(DefaultClientInBoundHandler.class, netttyClientService,ctx.channel()));
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		netttyClientService.channelInactive(ctx.channel());
	}

}
