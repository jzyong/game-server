package com.jzy.game.engine.netty.handler;

import com.jzy.game.engine.script.IScript;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.Service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

/**
 * netty handler脚本
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月30日 上午10:33:10
 */
public interface IChannelHandlerScript extends IScript{
	
	/**
	 * channel 激活
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月30日 上午10:36:11
	 * @param handlerClass 
	 * @param channel
	 */
	default void channelActive(Class<? extends ChannelHandler> handlerClass,Channel channel){
		
	}
	
	/**
	 * channel 激活
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月30日 上午10:36:11
	 * @param handlerClass 
	 * @param channel
	 */
	default void channelActive(Class<? extends ChannelHandler> handlerClass,Service<? extends BaseServerConfig> service,Channel channel){
		
	}
	
	/**
	 * channel 空闲
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月30日 上午10:36:11
	 * @param handlerClass
	 * @param channel
	 */
	default void channelInActive(Class<? extends ChannelHandler> handlerClass,Channel channel){
		
	}

}
