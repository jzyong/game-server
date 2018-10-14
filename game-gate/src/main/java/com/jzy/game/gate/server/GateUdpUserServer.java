package com.jzy.game.gate.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.firewall.BlacklistFilter;
import com.jzy.game.engine.mina.UdpServer;
import com.jzy.game.engine.mina.code.ClientProtocolCodecFactory;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.Service;
import com.jzy.game.gate.script.IGateServerScript;
import com.jzy.game.gate.server.handler.GateUdpUserServerHandler;

/**
 * 网关 用户udp 服务器
 * <p>
 * 1.在弱网条件下，udp效率更高，tcp存在阻塞重发，三次握手，消息重组等条件，速度很慢；如果消息丢包影响不大，实时性要求高，可以使用udp替换tcp
 * <br>
 * 2.网关服务器收到前端udp消息通过内部的tcp消息进行转发，当收到内部服务器的tcp消息为udp消息时，用udp返回给用户
 * <br>
 * 3.udp消息是不可靠的，只能辅助进行消息处理（可考虑实现可靠udp）
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月1日 下午1:45:20
 */
public class GateUdpUserServer extends Service<MinaServerConfig>{
	private final UdpServer udpServer;
	private final MinaServerConfig minaServerConfig;
	final Map<String, IoFilter> filters=new HashMap<>();
	private final BlacklistFilter blacklistFilter;	//IP黑名单过滤器

	public GateUdpUserServer(MinaServerConfig minaServerConfig) {
		super(null);
		this.minaServerConfig=minaServerConfig;
		blacklistFilter=new BlacklistFilter();
		filters.put("Blacklist", blacklistFilter);
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IGateServerScript.class, script->script.setIpBlackList(blacklistFilter));
		udpServer=new UdpServer(minaServerConfig, new GateUdpUserServerHandler(this),new ClientProtocolCodecFactory(),filters);
	}

	@Override
	protected void running() {
		udpServer.run();
		
	}

	@Override
	protected void onShutdown() {
		super.onShutdown();
		udpServer.stop();

	}

	public MinaServerConfig getMinaServerConfig() {
		return minaServerConfig;
	}
	
}
