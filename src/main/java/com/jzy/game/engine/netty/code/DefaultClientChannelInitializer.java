package com.jzy.game.engine.netty.code;

import com.jzy.game.engine.netty.handler.DefaultClientInBoundHandler;
import com.jzy.game.engine.netty.handler.DefaultInBoundHandler;
import com.jzy.game.engine.netty.handler.DefaultOutBoundHandler;
import com.jzy.game.engine.netty.service.NettyClientService;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 客户端默认初始化channel
 * ，服务器内部使用客户端
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月25日 上午9:28:47
 */
public class DefaultClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	private NettyClientService nettyClientService;
	
	public DefaultClientChannelInitializer(NettyClientService nettyClientService){
		this.nettyClientService=nettyClientService;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new DefaultOutBoundHandler());	
			ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(50*1024,0,4));	// 消息包格式:长度(4)+角色ID(8)+消息ID(4)+内容  
			ch.pipeline().addLast(new DefaultMessageCodec()); //消息加解密
			ch.pipeline().addLast(new DefaultClientInBoundHandler(nettyClientService)); //消息处理器
	}

}
