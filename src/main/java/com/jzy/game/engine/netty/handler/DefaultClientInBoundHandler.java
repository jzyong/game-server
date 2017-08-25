package com.jzy.game.engine.netty.handler;

import com.jzy.game.engine.netty.service.NettyClientService;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.Service;

import io.netty.channel.ChannelHandlerContext;

/**
 * 内部客户端 默认消息
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月25日 下午3:24:23
 */
public class DefaultClientInBoundHandler extends DefaultInBoundHandler {
	
	private NettyClientService netttyClientService;
	
	

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
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		netttyClientService.channelInactive(ctx.channel());
	}

	
	
}
