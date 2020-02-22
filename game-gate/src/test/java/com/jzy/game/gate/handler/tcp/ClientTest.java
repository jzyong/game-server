package com.jzy.game.gate.handler.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;

import org.apache.mina.core.buffer.IoBuffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.protobuf.Message;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.bydr.BydrRoomMessage.EnterRoomRequest;
import com.jzy.game.message.hall.HallLoginMessage;
import com.jzy.game.message.hall.HallLoginMessage.LoginRequest;
import com.jzy.game.message.hall.HallLoginMessage.LoginSubGameRequest;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.engine.util.IntUtil;



/**
 * 消息测试
 *
 * @author JiangZhiYong
 * @date 2017-02-28 QQ:359135103
 */
@Ignore
public class ClientTest {

	private static final int port = 8002;
	private static final String ip = "127.0.0.1";
	private TcpClient2 tcpClient;

	public static long START_TIME;

	@Before
	public void init() {
		getClient();
	}

	@After
	public void after() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 登录
	 *
	 * @throws Exception
	 */
	@Test
	public void testLogin() throws Exception {
		START_TIME = System.currentTimeMillis();
		LoginRequest.Builder builder = LoginRequest.newBuilder();
		builder.setAccount("jzy");
		builder.setPassword("123");
		builder.setLoginType(HallLoginMessage.LoginType.ACCOUNT);
		sendMsg(builder.build(), builder.getMid());
		
		LoginSubGameRequest.Builder builder3 = LoginSubGameRequest.newBuilder();
//		builder3.setSid("11111");
		builder3.setType(1);
		builder3.setRid(101);
		builder3.setGameType(ServerType.GAME_BYDR.getType());
		sendMsg(builder3.build(), builder3.getMid());
		
		EnterRoomRequest.Builder builder2 = EnterRoomRequest.newBuilder();
//		builder2.setSid("111");
//		builder2.setType(2);
		sendMsg(builder2.build(), builder2.getMid());
	}

//	/**
//	 * 登录子游戏
//	 */
//	@Test
//	public void testLoginSubGame() {
////		START_TIME = System.currentTimeMillis();
////		LoginSubGameRequest.Builder builder = LoginSubGameRequest.newBuilder();
////		builder.setSid("11111");
////		builder.setType(1);
////		builder.setUid(1);
////		builder.setGameType(ServerType.GAME_BYDR.getType());
////		sendMsg(builder.build(), builder.getMid());
////		
////		
////		EnterRoomRequest.Builder builder2 = EnterRoomRequest.newBuilder();
////		builder2.setSid("111");
////		builder2.setType(2);
////		sendMsg(builder2.build(), builder2.getMid());
//
//	}
//
//	/**
//	 * 进入捕鱼房间
//	 */
//	@Test
//	public void ZBydrEnterRoom() {
////		EnterRoomRequest.Builder builder = EnterRoomRequest.newBuilder();
////		builder.setSid("111");
////		builder.setType(2);
////		sendMsg(builder.build(), builder.getMid());
//	}

	/**
	 * 发送消息
	 *
	 * @param msg
	 * @param mid
	 */
	private void sendMsg(Message msg, MID mid) {
		byte[] b = msg.toByteArray();
		int len = 8 + b.length;// （len = mid_int_len + protobuf_int_len +
		// protobuf_body_bytes_len）

		IoBuffer buf = IoBuffer.allocate(len + 2);// （total_len = len + short_len）
		byte[] shorBytes = IntUtil.short2Bytes((short) len, ByteOrder.LITTLE_ENDIAN);
		// 写入字节长度
		buf.put(shorBytes);
		// 写入mid
		buf.put(IntUtil.writeIntToBytesLittleEnding(mid.getNumber()));
		// 写入protobuf长度
		buf.put(IntUtil.writeIntToBytesLittleEnding(b.length));

		buf.put(b);
		ByteBuffer writeBuffer = ByteBuffer.wrap(buf.array());

		try {
			 Thread.sleep(1000);
			getClient().socketChannel.write(writeBuffer);
			getClient().socketChannel.register(getClient().getSelector(), SelectionKey.OP_READ);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private TcpClient2 getClient() {
		if (tcpClient == null) {
			try {
				tcpClient = new TcpClient2(ip, port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return tcpClient;
	}
}
