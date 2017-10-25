package com.jjy.game.hall.server;

import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.service.SingleMinaTcpClientService;

/**
 * 连接集群 tcp客户端
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年6月28日 下午4:16:19
 */
public class Hall2ClusterClient extends SingleMinaTcpClientService{

	public Hall2ClusterClient(MinaClientConfig minaClientConfig) {
		super(minaClientConfig);
	}

	@Override
	protected void running() {
		super.running();
		// ServerThread executor = getExecutor(ThreadType.SYNC);
		// executor.addTimerEvent(new ServerHeartTimer()); //TODO 临时添加
	}
	
	

}
