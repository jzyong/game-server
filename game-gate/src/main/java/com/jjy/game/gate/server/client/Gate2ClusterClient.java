package com.jjy.game.gate.server.client;

import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.service.SingleMinaTcpClientService;

/**
 * 连接到集群管理
 *
 * @author JiangZhiYong
 * @date 2017-04-05 QQ:359135103
 */
public class Gate2ClusterClient extends SingleMinaTcpClientService {

	public Gate2ClusterClient(MinaClientConfig minaClientConfig) {
		super(minaClientConfig);
	}

}
