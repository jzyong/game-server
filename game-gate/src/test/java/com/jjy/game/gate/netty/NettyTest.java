package com.jjy.game.gate.netty;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Ignore;
import org.junit.Test;

import com.jjy.game.message.ServerMessage.ServerInfo;
import com.jjy.game.message.ServerMessage.ServerRegisterRequest;
import com.jjy.game.message.hall.HallLoginMessage;
import com.jjy.game.message.hall.HallLoginMessage.LoginRequest;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.netty.config.NettyClientConfig;
import com.jzy.game.engine.netty.service.MutilNettyTcpClientService;
import com.jzy.game.engine.netty.service.SingleNettyTcpClientService;
import com.jzy.game.engine.server.ServerState;
import com.jzy.game.engine.server.ServerType;


/**
 * Netty 测试
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月25日 下午4:23:48
 */
//@Ignore
public class NettyTest {

	/**
	 * 测试单客户端
	 * <br>连接集群服务器
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月25日 下午4:26:30
	 */
	@Test
	public void testSingleClient() {
		try {
			NettyClientConfig config=new NettyClientConfig();
			config.setMaxConnectCount(3);
			config.setPort(8000);
			SingleNettyTcpClientService service=new SingleNettyTcpClientService(config);
			new Thread(service).start();
			Thread.sleep(5000);
			
			ServerRegisterRequest.Builder builder = ServerRegisterRequest.newBuilder();
			ServerInfo.Builder info = ServerInfo.newBuilder();
			info.setId(1);
			info.setIp(config.getIp());
			info.setMaxUserCount(1000);
			info.setOnline(1);
			info.setName("测试服务器");
			info.setState(ServerState.NORMAL.getState()); 
			info.setType(ServerType.GAME_3.getType());
			info.setWwwip("");
			info.setPort(11);
			info.setHttpport(1);
			builder.setServerInfo(info);
			for(int i=0;i<10;i++){
				service.sendMsg(builder.build());
				Thread.sleep(3000);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 测试连接多服务器客户端
	 * <br>
	 * 连接网关服务器
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月25日 下午4:26:30
	 */
	@Test
	public void testMutilClient() {
		try {
			NettyClientConfig config=new NettyClientConfig();
			config.setPort(8003);
			config.setMaxConnectCount(10);
			MutilNettyTcpClientService service=new MutilNettyTcpClientService(config);
			new Thread(service).start();
			Thread.sleep(1000);
			
			com.jzy.game.engine.server.ServerInfo serverInfo=new com.jzy.game.engine.server.ServerInfo();
			serverInfo.setId(1);
			serverInfo.setIp("127.0.0.1");
			service.addTcpClient(serverInfo, config.getPort());
			
			Thread.sleep(5000);
			
			ServerRegisterRequest.Builder builder = ServerRegisterRequest.newBuilder();
			ServerInfo.Builder info = ServerInfo.newBuilder();
			info.setId(1);
			info.setIp(config.getIp());
			info.setMaxUserCount(1000);
			info.setOnline(1);
			info.setName("测试服务器");
			info.setState(ServerState.NORMAL.getState()); 
			info.setType(ServerType.GAME_3.getType());
			info.setWwwip("");
			info.setPort(11);
			info.setHttpport(1);
			builder.setServerInfo(info);
			service.sendMsg(new IDMessage(null, builder.build(), 0, builder.getMid().getNumber()));
			
			Thread.sleep(3000);
			
//			LoginRequest.Builder builder2 = LoginRequest.newBuilder();
//			builder2.setAccount("jzy1");
//			builder2.setPassword("123");
//			builder2.setLoginType(HallLoginMessage.LoginType.ACCOUNT);
//			for(int i=0;i<10;i++){
//				service.broadcastMsg();
//				Thread.sleep(3000);
//			}
			Thread.sleep(500000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testInetAddress() throws Exception{
		InetAddress inetAddress = InetAddress.getByName("192.168.0.17");
		System.err.println(inetAddress.toString());
	}
	
}
