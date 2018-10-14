package com.jzy.game.gate.handler.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.bydr.BydrRoomMessage.EnterRoomResponse;
import com.jzy.game.message.hall.HallLoginMessage.LoginResponse;
import com.jzy.game.message.hall.HallLoginMessage.LoginSubGameResponse;
import com.jzy.game.engine.util.IntUtil;

public class TCPClientReadThread implements Runnable {

	private final Selector selector;
	final String imei;
	final TcpClient2 client;

	public TCPClientReadThread(TcpClient2 client, Selector selector, String imei) {
		this.selector = selector;
		this.imei = imei;
		this.client = client;
		new Thread(this).start();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() {
		try {
			while (true) {
				selector.select();
				Iterator ite = selector.selectedKeys().iterator();
				while (ite.hasNext()) {

					SelectionKey key = (SelectionKey) ite.next();
					ite.remove();
					if (key.isConnectable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						if (channel.isConnectionPending()) {
							channel.finishConnect();

						}
						channel.configureBlocking(false);
						channel.register(selector, SelectionKey.OP_READ);

					} else {
						if (key.isReadable()) {
							read(key);
						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param key
	 * @throws IOException
	 */
	public void read(SelectionKey key) throws IOException {

		SocketChannel channel = (SocketChannel) key.channel();
		try {
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			int count = channel.read(buffer);
			if (count < 4) {
				return;
			}
			buffer.flip();
			buffer.mark();

			// 读取short len
			byte[] dst = new byte[2];
			buffer.get(dst, 0, 2);
			int len = IntUtil.bytes2Short(dst, ByteOrder.LITTLE_ENDIAN);
			int lastLen = buffer.remaining();
			if (lastLen < len) {
				buffer.reset();
				return;
			}

			// 读取int mid
			dst = new byte[4];
			buffer.get(dst, 0, 4);
			int mid = IntUtil.bytes2Int(dst, ByteOrder.LITTLE_ENDIAN);
			System.out.println("mid: " + mid);

			// 读取冗余的protobuf len
			dst = new byte[4];
			buffer.get(dst, 0, 4);
			int protobufLen = IntUtil.bytes2Int(dst, ByteOrder.LITTLE_ENDIAN);
			// System.out.println("protobufLen: " + protobufLen);

			byte[] bytes = new byte[protobufLen];
			buffer.get(bytes, 0, protobufLen);
			buffer.clear();

			for (byte c : bytes) {
				System.out.print(c + ",");
			}
			System.out.println();

			parseResponse(mid, bytes);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

	}

	void parseResponse(int mid, byte[] bytes) throws InvalidProtocolBufferException {
		Message msg = null;
		switch (mid) {
		// case ConstantsCommon.MID_SERVER_MSG://返回信息
		// msg = serverMsgResponse(bytes);
		// break;
		case MID.LoginRes_VALUE:// 登录返回
			msg = loginResponse(bytes);
			LoginResponse r = (LoginResponse) msg;
			break;
		case MID.LoginSubGameRes_VALUE:
			msg = loginSubGameResponse(bytes);
			break;
		case MID.EnterRoomRes_VALUE:
			msg = enterRoomResponse(bytes);

		default:
			break;
		}

		if (msg != null) {
			// String jsonFormat = JsonFormat.printToString(msg);
			System.out.println(msg);
		}
	}

	/**
	 * 登录子游戏返回
	 * 
	 * @param bytes
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	Message enterRoomResponse(byte[] bytes) throws InvalidProtocolBufferException {
		EnterRoomResponse.Builder builder = EnterRoomResponse.newBuilder().mergeFrom(bytes);
		System.err.println((System.currentTimeMillis() - ClientTest.START_TIME) + "ms");
		return builder.build();
	}

	/**
	 * 登录子游戏返回
	 * 
	 * @param bytes
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	Message loginSubGameResponse(byte[] bytes) throws InvalidProtocolBufferException {
		LoginSubGameResponse.Builder builder = LoginSubGameResponse.newBuilder().mergeFrom(bytes);
		System.err.println((System.currentTimeMillis() - ClientTest.START_TIME) + "ms");
		return builder.build();
	}

	/**
	 * 解析登录返回
	 *
	 * @param bytes
	 * @throws InvalidProtocolBufferException
	 * @author Administrator
	 * @data 2016年9月9日 上午11:59:28
	 */
	Message loginResponse(byte[] bytes) throws InvalidProtocolBufferException {
		LoginResponse.Builder builder = LoginResponse.newBuilder().mergeFrom(bytes);
		System.err.println((System.currentTimeMillis() - ClientTest.START_TIME) + "ms");
		return builder.build();
	}
}
