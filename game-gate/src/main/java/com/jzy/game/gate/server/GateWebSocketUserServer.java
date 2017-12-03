package com.jzy.game.gate.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.firewall.BlacklistFilter;
import com.jzy.game.engine.mina.TcpServer;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.websocket.WebSocketCodecFactory;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.Service;
import com.jzy.game.gate.script.IGateServerScript;
import com.jzy.game.gate.server.handler.GateWebSocketUserServerHandler;

/**
 * 网关 用户WebSocket 服务器（网页链接）
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月19日 下午1:37:02
 */
public class GateWebSocketUserServer extends Service<MinaServerConfig> {
	private final TcpServer tcpServer;
	private final MinaServerConfig minaServerConfig;
	Map<String, IoFilter> filters=new HashMap<>();
	private  BlacklistFilter blacklistFilter;	//IP黑名单过滤器
	private GateWebSocketUserServerHandler gateWebSocketUserServerHandler;

	public GateWebSocketUserServer(MinaServerConfig minaServerConfig) {
		super(null);
		this.minaServerConfig=minaServerConfig;
		blacklistFilter=new BlacklistFilter();
		filters.put("Blacklist", blacklistFilter);
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IGateServerScript.class, script->script.setIpBlackList(blacklistFilter));
		gateWebSocketUserServerHandler=new GateWebSocketUserServerHandler();
		tcpServer=new TcpServer(minaServerConfig, gateWebSocketUserServerHandler, new WebSocketCodecFactory(), filters);
	}

	@Override
	protected void running() {
		gateWebSocketUserServerHandler.setService(this);
		tcpServer.run();
		
	}

	@Override
	protected void onShutdown() {
		super.onShutdown();
		tcpServer.stop();

	}

	public MinaServerConfig getMinaServerConfig() {
		return minaServerConfig;
	}
	
	
}
