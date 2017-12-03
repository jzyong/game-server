package com.jzy.game.bydr.server;

import com.jzy.game.bydr.thread.RoomExecutor;
import com.jzy.game.engine.netty.config.NettyClientConfig;
import com.jzy.game.engine.netty.service.MutilNettyTcpClientService;
import com.jzy.game.engine.thread.ServerThread;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.engine.thread.timer.event.ServerHeartTimer;

/**
 * netty 连接网关服
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月14日 下午4:52:20
 */
public class Bydr2GateClientNetty extends MutilNettyTcpClientService {

	public Bydr2GateClientNetty(NettyClientConfig nettyClientConfig) {
		super(nettyClientConfig);
	}

	public Bydr2GateClientNetty(ThreadPoolExecutorConfig threadPoolExecutorConfig,
			NettyClientConfig nettyClientConfig) {
		super(threadPoolExecutorConfig, nettyClientConfig);
	}

	@Override
	protected void initThread() {
		super.initThread();
		// 全局同步线程
		ServerThread syncThread = getExecutor(ThreadType.SYNC);
		syncThread.addTimerEvent(new ServerHeartTimer());

		// 添加房间线程池
		RoomExecutor roomExecutor = new RoomExecutor();
		getServerThreads().put(ThreadType.ROOM, roomExecutor);
	}

}
