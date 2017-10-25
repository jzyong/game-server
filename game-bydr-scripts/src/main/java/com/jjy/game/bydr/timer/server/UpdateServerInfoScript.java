//package com.jjy.game.bydr.timer.server;
//
//import java.time.LocalTime;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.jjy.game.bydr.server.BydrServer;
//import com.jjy.game.engine.mina.config.MinaClientConfig;
//import com.jjy.game.engine.script.ITimerEventScript;
//import com.jjy.game.engine.server.ServerType;
//import com.jjy.game.message.ServerMessage.ServerListRequest;
//import com.jjy.game.message.ServerMessage.ServerRegisterRequest;
//
///**
// * 更新服务器信息脚本
// *
// * @author JiangZhiYong
// * @date 2017-04-09 QQ:359135103
// */
//public class UpdateServerInfoScript implements ITimerEventScript {
//	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServerInfoScript.class);
//
//	@Override
//	public void secondHandler(LocalTime localTime) {
//		// TODO 线程执行很忙，用另外线程调用
//		if (localTime.getSecond() % 10 == 0) { // 每5秒更新一次
//			// 向服务器集群更新服务器信息
//			MinaClientConfig minaClientConfig = BydrServer.getInstance().getBydr2ClusterClient().getMinaClientConfig();
//			ServerRegisterRequest request = BydrServer.getInstance().buildServerRegisterRequest(minaClientConfig);
//			BydrServer.getInstance().getBydr2ClusterClient().sendMsg(request);
//			BydrServer.getInstance().getBydr2HallClient().sendMsg(request);
//			// LOGGER.debug("更新服务器信息");
//			// 重连服务器监测
//			BydrServer.getInstance().getBydr2ClusterClient().getTcpClient().checkStatus();
//
//			// 获取大厅服务器列表
//			ServerListRequest.Builder builder = ServerListRequest.newBuilder();
//			builder.setServerType(ServerType.HALL.getType());
//			BydrServer.getInstance().getBydr2ClusterClient().sendMsg(builder.build());
//
//			BydrServer.getInstance().getBydr2HallClient().checkStatus();
//		}
//	}
//
//}
