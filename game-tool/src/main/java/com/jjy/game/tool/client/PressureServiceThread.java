package com.jjy.game.tool.client;

import java.time.LocalTime;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.bydr.BydrRoomMessage.EnterRoomRequest;
import com.jjy.game.message.bydr.BydrRoomMessage.RoomType;
import com.jjy.game.message.hall.HallChatMessage.ChatRequest;
import com.jjy.game.message.hall.HallChatMessage.ChatType;
import com.jjy.game.message.hall.HallLoginMessage;
import com.jjy.game.message.hall.HallLoginMessage.LoginRequest;
import com.jjy.game.message.hall.HallLoginMessage.QuitSubGameRequest;
import com.jjy.game.message.system.SystemMessage.HeartRequest;
import com.jzy.game.engine.mina.service.SingleMinaTcpClientService;
import com.jzy.game.engine.script.ITimerEventScript;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.thread.ServerThread;
import com.jzy.game.engine.thread.timer.TimerEvent;
import com.jzy.game.engine.thread.timer.event.ServerHeartTimer;

/**
 * 压力测试线程
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午5:20:22
 */
public class PressureServiceThread extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(PressureServiceThread.class);
	public static final String SEND_TIME = "sendTime";
	public static final String USER_NAME = "userName";

	private Player player;

	public PressureServiceThread(Player player) {
		super();
		this.player = player;
	}

	@Override
	public void run() {
		try {

			// 登录游戏消息
			LoginRequest.Builder builder = LoginRequest.newBuilder();
			builder.setAccount(player.getUserName());
			builder.setPassword("123");
			builder.setLoginType(HallLoginMessage.LoginType.ACCOUNT);
			player.getTcpSession().setAttribute(SEND_TIME, System.currentTimeMillis());
			player.getTcpSession().setAttribute(USER_NAME, player.getUserName());
			player.sendTcpMsg(builder.build());

			ThreadGroup group = new ThreadGroup(player.getUserName());
			ServerThread serverThread = new ServerThread(group, USER_NAME, 500, 1000);
			serverThread.addTimerEvent(new PlayerTimerEvent(player));
			serverThread.start();

			/*
			 * // 退出子游戏 QuitSubGameRequest.Builder quitBuilder =
			 * QuitSubGameRequest.newBuilder(); quitBuilder.setRid(1);
			 * service.sendMsg(quitBuilder.build());
			 */

		} catch (Exception e) {
			LOGGER.warn("PressureServiceThread", e);
		}

	}

	/**
	 * 玩家定时器
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年10月20日 上午11:39:45
	 */
	public class PlayerTimerEvent extends ServerHeartTimer {

		private Player player;

		public PlayerTimerEvent(Player player) {
			super();
			this.player = player;
		}

		@Override
		public void run() {
			LocalTime localTime = LocalTime.now();
			int _sec = localTime.getSecond();
			if (sec != _sec) { // 每秒钟执行
				sec = _sec;
				HeartRequest.Builder heartBuilder = HeartRequest.newBuilder();
				player.sendUdpMsg(heartBuilder.build());
				player.getUdpSession().setAttribute(SEND_TIME, System.currentTimeMillis());
			}
			int _min = localTime.getMinute();
			if (min != _min) { // 每分钟执行
				min = _min;
				
			}
		}

	}

}
