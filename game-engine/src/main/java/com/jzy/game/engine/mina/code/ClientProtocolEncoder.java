/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.engine.mina.code;

import com.google.protobuf.Message;
import com.jzy.game.engine.util.IntUtil;
import com.jzy.game.engine.util.MsgUtil;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 游戏客户端返回消息编码
 *
 * @author JiangZhiYong
 * @QQ 359135103
 * @version $Id: $Id
 */
public class ClientProtocolEncoder extends ProtocolEncoderImpl {
	private static final Logger LOGGER =LoggerFactory.getLogger(ClientProtocolEncoder.class);

	/** {@inheritDoc} */
	@Override
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput out) throws Exception {
		if (getOverScheduledWriteBytesHandler() != null
				&& session.getScheduledWriteMessages() > getMaxScheduledWriteMessages()
				&& getOverScheduledWriteBytesHandler().test(session)) {
			LOGGER.warn("{}消息{}大于最大累积{}",MsgUtil.getIp(session), session.getScheduledWriteMessages(),getMaxScheduledWriteMessages());
			return;
		}

		IoBuffer buf = null;
		if (obj instanceof Message) {
			buf = MsgUtil.toGameClientIobuffer((Message) obj);
		} else if (obj instanceof byte[]) {
			byte[] data = (byte[]) obj; // 消息ID（4字节）+protobuf
			buf = IoBuffer.allocate(data.length + 6);
			// 消息长度
			byte[] lengthBytes = IntUtil.short2Bytes((short) (data.length + 4), ByteOrder.LITTLE_ENDIAN);
			buf.put(lengthBytes);

			// 消息ID ,将顺序改变为前端客户端顺序
			byte[] idBytes = new byte[4];
			idBytes[0] = data[3];
			idBytes[1] = data[2];
			idBytes[2] = data[1];
			idBytes[3] = data[0];
			buf.put(idBytes);
			// protobuf长度
			int protobufLength = data.length - 4; // 移除消息ID长度
			buf.put(IntUtil.writeIntToBytesLittleEnding(protobufLength));
			// 数据
			buf.put(data, 4, protobufLength);

		}

		if (buf != null && session.isConnected()) {
			buf.rewind();
			out.write(buf);
			out.flush();
		}
	}

}
