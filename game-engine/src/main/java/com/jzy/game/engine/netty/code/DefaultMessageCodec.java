package com.jzy.game.engine.netty.code;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.util.MsgUtil;
import com.jzy.game.engine.util.TimeUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

/**
 * 内部默认消息解码器
 * <br>消息有带角色ID的，有不带角色ID的
 * @note IDMessage mina、netty通用
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月25日 上午10:10:54
 */
public class DefaultMessageCodec extends ByteToMessageCodec<Object> {
	private static final Logger LOGGER=LoggerFactory.getLogger(DefaultMessageCodec.class);
	private int headerLength=12;	//消息头长度
	
	/**
	 * 消息头长度默认12 角色ID+消息ID
	 */
	public DefaultMessageCodec() {
    }
	
	/**
	 * 
	 * @param headerLength 4消息ID  12角色ID+消息ID
	 */
	public DefaultMessageCodec(int headerLength){
		this.headerLength=headerLength;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf out) throws Exception {
		//使用了mina进行转换
		long start=TimeUtil.currentTimeMillis();
		byte[] bytes = null; //消息体
		if(obj instanceof IDMessage){	//消息头12 消息ID+角色ID
			bytes=MsgUtil.toIobuffer((IDMessage)obj).array();
		}else if(obj instanceof Message){	//消息头4 消息ID 
			bytes=MsgUtil.toIobuffer((Message)obj).array();
		}
		
		if (bytes != null) {
//			LOGGER.info(bytes.toString());
			out.writeBytes(bytes);	//消息体
//			ctx.flush();	//TODO 不flush 消息体未发送？
		}
//		LOGGER.debug("加密：{}",(TimeUtil.currentTimeMillis()-start));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 1) {// 空包不处理
			return;
		}
		long start=TimeUtil.currentTimeMillis();
		int length = in.readInt();
		long userId=0;
		if(headerLength==12){
			 userId= in.readLong();
		}
		int msgId = in.readInt();

		int bLen = length - headerLength; 
		byte[] data = new byte[bLen];
		in.readBytes(data);
		IDMessage msg = new IDMessage(ctx.channel(), data, userId, msgId);
		out.add(msg);
//		LOGGER.debug("解密：{}",(TimeUtil.currentTimeMillis()-start));
	}

}
