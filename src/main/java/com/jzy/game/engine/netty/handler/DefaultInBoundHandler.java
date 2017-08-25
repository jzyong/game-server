package com.jzy.game.engine.netty.handler;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.IHandler;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.util.MsgUtil;
import com.jzy.game.engine.util.TimeUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 默认接收消息处理器 <br>
 * 消息直接用netty线程池处理，分发请重新实现messagehandler
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月25日 上午11:29:37
 */
public abstract class DefaultInBoundHandler extends SimpleChannelInboundHandler<IDMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInBoundHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IDMessage msg) throws Exception {
		if (!ScriptManager.getInstance().tcpMsgIsRegister(msg.getMsgId())) {
			forward(msg);
			return;
		}
		Class<? extends IHandler> handlerClass = ScriptManager.getInstance().getTcpHandler(msg.getMsgId());
		TcpHandler handler = (TcpHandler) handlerClass.newInstance();
		handler.setCreateTime(TimeUtil.currentTimeMillis());
		HandlerEntity handlerEntity = ScriptManager.getInstance().getTcpHandlerEntity(msg.getMsgId());
		Message message = MsgUtil.buildMessage(handlerEntity.msg(), (byte[]) msg.getMsg());
		handler.setMessage(message);
		handler.setRid(msg.getUserID());
		handler.setChannel(ctx.channel());
		messagehandler(handler, handlerEntity);
	}
	
	




	/**
	 * 消息处理
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月25日 下午12:01:04
	 * @param handler
	 */
	protected void messagehandler(TcpHandler handler, HandlerEntity handlerEntity) {
		if (getService() != null) {
			Executor executor = getService().getExecutor(handlerEntity.thread());
			if (executor != null) {
				executor.execute(handler);
				return;
			}
		}
		handler.run();
	}

	/**
	 * 消息跳转
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月25日 下午12:01:51
	 * @param idMessage
	 */
	protected void forward(IDMessage msg) {
		LOGGER.info("消息{} 未实现", msg.getMsgId());
	}

	public abstract Service<? extends BaseServerConfig> getService() ;
	
}
