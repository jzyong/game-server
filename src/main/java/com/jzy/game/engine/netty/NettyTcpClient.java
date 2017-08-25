package com.jzy.game.engine.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.TcpClient;
import com.jzy.game.engine.netty.code.DefaultClientChannelInitializer;
import com.jzy.game.engine.netty.config.NettyClientConfig;
import com.jzy.game.engine.netty.service.NettyClientService;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Netty Tcp 客户端
 * 
 * @see TcpClient
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月24日 下午8:13:14
 */
public class NettyTcpClient implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpClient.class);

	/** 配置 */
	private NettyClientConfig nettyClientConfig;
	/** 工作组 */
	private EventLoopGroup group;
	/** 初始化channel */
	private ChannelInitializer<SocketChannel> channelInitializer; 
	/**服务*/
	private NettyClientService nettyClientService;

	/**
	 * 
	 */
	public NettyTcpClient(NettyClientService nettyClientService) {
		this.nettyClientConfig =nettyClientService.getNettyClientConfig();
		this.channelInitializer=new DefaultClientChannelInitializer(nettyClientService);
	}


	public NettyTcpClient(NettyClientService nettyClientService,ChannelInitializer<SocketChannel> channelInitializer) {
		this.nettyClientConfig = nettyClientService.getNettyClientConfig();
		this.nettyClientService=nettyClientService;
		this.channelInitializer=channelInitializer;
	}

	@Override
	public void run() {
		connect();
	}

	/**
	 * 连接服务器
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月24日 下午8:40:13
	 */
	private void connect() {
		group = new NioEventLoopGroup(nettyClientConfig.getGroupThreadNuu());
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.TCP_NODELAY, nettyClientConfig.isTcpNoDealy());
			bootstrap.handler(channelInitializer);

			ChannelFuture channelFuture = bootstrap.connect(nettyClientConfig.getIp(), nettyClientConfig.getPort())
					.sync();
			channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					if (future.isSuccess()) {
						LOGGER.info("连接服务器{}:{}成功", nettyClientConfig.getIp(), nettyClientConfig.getPort());
						connectFinsh();
					} else {
						LOGGER.warn("连接服务器{}:{}失败", nettyClientConfig.getIp(), nettyClientConfig.getPort());
					}
				}
			});
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			LOGGER.error("连接客户端", e);
		} finally {
			reConnect();
			group.shutdownGracefully();
		}
	}

	/**
	 * 连接服务器完成
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月25日 上午9:14:31
	 */
	protected void connectFinsh() {

	}

	private void reConnect() {
		try {
			Thread.sleep(nettyClientConfig.getReConnectTime());
		} catch (InterruptedException e) {
			LOGGER.error("重连服务器", e);
		}
		connect();
	}

	public void stop() {
		group.shutdownGracefully();
	}


	public NettyClientService getNettyClientService() {
		return nettyClientService;
	}
	
	
}
