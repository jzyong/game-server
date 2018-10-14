package com.jzy.game.engine.util;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.mina.message.MassMessage;

import java.nio.ByteOrder;

/**
 * 消息工具
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 */
public final class MsgUtil {

	protected static final Logger log = LoggerFactory.getLogger(MsgUtil.class);

	private MsgUtil() {
	}

	/**
	 * 转换为发送的iobuff
	 *
	 * @param message
	 * @return 【length|msgid|data】
	 */
    public static IoBuffer toIobuffer(final Message message) {
		int msgID = getMessageID(message);
		byte[] msgData = message.toByteArray();
		int msgDataLength = msgData.length;
		IoBuffer buf = IoBuffer.allocate(8 + msgDataLength);
		buf.putInt(msgDataLength + 4);
		buf.putInt(msgID);
		buf.put(msgData);
		buf.rewind();
		return buf;
	}

	/**
	 * 转换为游戏客户端发送的iobuff
	 * 
	 * @note portobuf长度对于服务器多余了，服务器进行了大小端转换
	 * @param message
	 * @return 【length|msgid|protobuf_length|data】
	 */
    public static IoBuffer toGameClientIobuffer(final Message message) {
		int msgID = getMessageID(message);
		byte[] msgData = message.toByteArray();
		int protobufLength = msgData.length;
		IoBuffer buf = IoBuffer.allocate(10 + protobufLength); // 消息长度2字节 mid
																// 4字节
																// protobuf长度4字节

		// 消息长度
		byte[] lengthBytes = IntUtil.short2Bytes((short) (protobufLength + 8), ByteOrder.LITTLE_ENDIAN);
		buf.put(lengthBytes);
		// buf.putInt(protobufLength + 8);
		// 消息ID
		buf.put(IntUtil.writeIntToBytesLittleEnding(msgID));
		// buf.putInt(msgID);
		// protobuf长度
		buf.put(IntUtil.writeIntToBytesLittleEnding(protobufLength));
		// buf.putInt(protobufLength);
		buf.put(msgData); // 真实数据
		buf.rewind();
		return buf;
	}

	public static IoBuffer toIobuffer(final MassMessage message) {
		IoBuffer buf = IoBuffer.allocate(8 + message.getLength());
		buf.putInt(message.getLength() + 4); // 总长度
		buf.putInt(message.getBuffLength()); // 内容长度
		buf.put(message.getBuffer());
		for (Long target : message.getTargets()) {
			buf.putLong(target);
		}
		buf.rewind();
		return buf;
	}

	/**
	 * 转换为发送的iobuff
	 *
	 * @param message
	 * @param id
	 * @return 【length|msgid|data】
	 */
    public static IoBuffer toIobufferWithID(final Message message, long id) {
		int msgID = getMessageID(message);
		byte[] msgData = message.toByteArray();
		int msgDataLength = msgData.length;
		IoBuffer buf = IoBuffer.allocate(16 + msgDataLength);
		buf.putInt(msgDataLength + 12);
		buf.putLong(id);
		buf.putInt(msgID);
		buf.put(msgData);
		buf.rewind();
		return buf;
	}

	/**
	 * 转换为未携带消息长度的iobuff
	 *
	 * @param message
	 * @return 【msgid|data】
	 */
    public static IoBuffer toIobufferWithoutLength(final Message message) {
		int msgID = getMessageID(message);
		byte[] msgData = message.toByteArray();
		if (msgData.length < 1) {
			return null;
		}
		IoBuffer buf = IoBuffer.allocate(4 + msgData.length);
		buf.putInt(msgID);
		buf.put(msgData);
		buf.rewind();
		return buf;
	}

	/**
	 * 去掉消息【id|msgid|data】的id部分并转换为【length|msgid|data】
	 *
	 * @param bytes
	 * @param idlength
	 * @return 【length|msgid|data】
	 */
    public static IoBuffer toIobufferWithoutID(final byte[] bytes, final int idlength) {
		if (bytes.length < idlength || bytes.length < 1) {
			return null;
		}
		byte[] target = Arrays.copyOfRange(bytes, idlength, bytes.length);
		IoBuffer buf = IoBuffer.allocate(target.length + 4);
		buf.putInt(target.length);
		buf.put(target);
		buf.rewind();
		return buf;
	}

	public static MassMessage toMassMessage(final Message message, Collection<Long> targets) {
		IoBuffer msg = toIobuffer(message);
		MassMessage mass = new MassMessage(msg, targets);
		return mass;
	}

	/**
	 * 转换为发送的iobuff
	 *
	 * @param message
	 * @return
	 */
    public static IoBuffer toIobuffer(final IDMessage message) {
		if (message.getMsg() == null) {
			return null;
		}
		byte[] msgData = null;
		IoBuffer buf = null;
		if (message.getMsg() instanceof byte[]) { // 包含消息id和内容【msgid|data】
			msgData = (byte[]) message.getMsg();
			if (msgData.length < 1) {
				return null;
			}
			buf = IoBuffer.allocate(12 + msgData.length);
			buf.putInt(msgData.length + 8);// 4
			buf.putLong(message.getUserID());// 8
			buf.put(msgData);
		} else if (message.getMsg() instanceof IoBuffer) { // 包含消息id和内容【msgid|data】
			IoBuffer msg = (IoBuffer) message.getMsg();
			msg.rewind();
			if (msg.limit() < 1) {
				return null;
			}
			buf = IoBuffer.allocate(12 + msg.limit());
			buf.putInt(msg.limit() + 8);// 4
			buf.putLong(message.getUserID());// 8
			buf.put(msg);
		} else if (message.getMsg() instanceof Message) {// 仅包含消息内容【data】
			Message msg = (Message) message.getMsg();
			int msgID = getMessageID(msg);
			msgData = msg.toByteArray();
			if (msgData.length < 1) {
				return null;
			}
			buf = IoBuffer.allocate(16 + msgData.length);
			buf.putInt(msgData.length + 12);// 4
			buf.putLong(message.getUserID());// 8
			buf.putInt(msgID);// 4
			buf.put(msgData);
		} else {
			log.warn("无法解析消息类型：" + message.getMsg().getClass().getName());
			return null;
		}
		buf.rewind();
		return buf;
	}

	/**
	 * 游戏前端收到消息改为网关内部消息进行转发
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年7月21日 上午11:01:10
	 * @param bytes
	 * @return
	 */
	public static byte[] clientToGame(int msgID, byte[] bytes) {
		IoBuffer ioBuffer = IoBuffer.allocate(bytes.length - 4);
		ioBuffer.putInt(msgID); // 消息ID
		ioBuffer.put(bytes, 8, bytes.length - 8);
		return ioBuffer.array();
	}

	/**
	 * 获取IP地址
	 *
	 * @param session
	 * @return
	 */
    public static String getIp(IoSession session) {
		try {
			if (session != null && session.isConnected()) {
				String clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
				return clientIP;
			}
		} catch (Exception e) {
		}
		return "0.0.0.0";
	}

	/**
	 * 消息ID
	 *
	 * @param message
	 * @return
	 */
    public static int getMessageID(final Message message) {
		Descriptors.EnumValueDescriptor field = (Descriptors.EnumValueDescriptor) message
				.getField(message.getDescriptorForType().findFieldByNumber(1));
		int msgID = field.getNumber();
		// if (msgID < 99999) {
		// log.warn("消息类型异常{},id{}", message.getClass().getName(), msgID);
		// }
		return msgID;
	}

	public static int getMessageID(final byte[] bytes, final int offset) throws Exception {
		int msgID = bytes[offset + 3] & 0xFF | (bytes[offset + 2] & 0xFF) << 8 | (bytes[offset + 1] & 0xFF) << 16
				| (bytes[offset] & 0xFF) << 24;
		// if (msgID < 99999) {
		// int head = bytes[3] & 0xFF | (bytes[2] & 0xFF) << 8 | (bytes[1] &
		// 0xFF) << 16 | (bytes[0] & 0xFF) << 24;
		// log.warn("消息类型异常offset{},id{},head{},bytes{}", offset, msgID, head,
		// IntUtil.BytesToStr(bytes));
		// }
		return msgID;
	}

	public static long getMessageRID(final byte[] bytes, final int offset) {
		long uID = ((((long) bytes[0 + offset] & 0xff) << 56) | (((long) bytes[1 + offset] & 0xff) << 48)
				| (((long) bytes[2 + offset] & 0xff) << 40) | (((long) bytes[3 + offset] & 0xff) << 32)
				| (((long) bytes[4 + offset] & 0xff) << 24) | (((long) bytes[5 + offset] & 0xff) << 16)
				| (((long) bytes[6 + offset] & 0xff) << 8) | (((long) bytes[7 + offset] & 0xff)));
		return uID;
	}

	/**
	 * 关闭连接
	 *
	 * @param session
	 * @param reason
	 */
	public static void close(IoSession session, String reason) {
		log.error(String.format("%s -->连接关闭原因 %s", session.toString(), reason));
		session.closeNow();
	}

	public static void close(IoSession session, String fmt, Object... args) {
		String reason = String.format(fmt, args);
		log.error(String.format("%s -->连接关闭原因 %s", session.toString(), reason));
		session.closeNow();
	}

	/**
	 * 构建消息
	 *
	 * @param clazz
	 * @param bytes
	 * @return
	 */
	public static Message buildMessage(Class<? extends Message> clazz, byte[] bytes) throws Exception {
		Method parseFromMethod = clazz.getDeclaredMethod("parseFrom", new Class<?>[] { byte[].class });
		Object object = parseFromMethod.invoke(null, bytes);
		return (Message) object;
	}

	public static Message buildMessage(Class<? extends Message> clazz, final byte[] bytes, int off, int len)
			throws Exception {
		IoBuffer ib = IoBuffer.wrap(bytes, off, len);
		return buildMessage(clazz, ib);
	}

	public static Message buildMessage(Class<? extends Message> clazz, IoBuffer ioBuffer) throws Exception {
		if (ioBuffer.remaining() < 1) {
			return null;
		}
		byte[] bytes = new byte[ioBuffer.remaining()];
		ioBuffer.get(bytes);
		return buildMessage(clazz, bytes);
	}

	/**
	 * session空闲程度排序
	 */
	public static final Comparator<IoSession> sessionIdleComparator = (IoSession o1, IoSession o2) -> {
		int res = o1.getScheduledWriteMessages() - o2.getScheduledWriteMessages();
		if (res == 0) {
			res = (int) (o1.getWrittenBytes() - o2.getWrittenBytes());
		}
		return res;
	};

	// /**
	// * 构建广播消息，默认发送给本服务器addTargetServer 为0标识全服；setSourceServer为0不显示服务器名
	// *
	// * @param callback
	// * @param content
	// * @param language
	// * @param author
	// * @param layer 0默认按先进先出策略（时间）；>1越高排序越前；（如果累计，排序优先按layer排序，其次按time排序）
	// * @param params
	// * @return
	// */
	// public static NoticeMessage.ResBroadcastMessage
	// buildResBroadcastMessage(Consumer<NoticeMessage.ResBroadcastMessage.Builder>
	// callback,
	// String content, int language, String author, int layer, String... params)
	// {
	// NoticeMessage.ResBroadcastMessage.Builder broadcastBuider =
	// NoticeMessage.ResBroadcastMessage.newBuilder();
	// NoticeMessage.Notice.Builder noticeBuider =
	// NoticeMessage.Notice.newBuilder();
	// broadcastBuider.setSourceServer(Config.SERVER_ID);
	// broadcastBuider.addTargetServer(Config.SERVER_ID);
	// callback.accept(broadcastBuider);
	// noticeBuider.setTime(TimeUtil.currentTimeMillis());
	// if (author != null) {
	// noticeBuider.setAuthor(author);
	// }
	// if (content != null) {
	// noticeBuider.setContent(content);
	// }
	// noticeBuider.setContentID(language);
	// noticeBuider.setLayer(layer);
	// if (params != null && params.length > 0) {
	// for (String param : params) {
	// noticeBuider.addParams(param);
	// }
	// }
	// broadcastBuider.setNotice(noticeBuider.build());
	// return broadcastBuider.build();
	// }
}
