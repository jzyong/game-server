package com.jzy.game.engine.netty.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.ServerType;

/**
 * netty 客户端配置
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月24日 下午8:18:20 TODO 需要修改配置
 */
@Root
public class NettyClientConfig extends BaseServerConfig {

	// 工作组线程数
	@Element(required = false)
	private int groupThreadNum = 1;

	// 当前服务器的类型,如当前服务器是gameserver.那么对应ServerType.GameServer = 10
	@Element(required = false)
	private ServerType type = ServerType.GATE;

	// 其他配置,如配置服务器允许开启的地图
	@Element(required = false)
	private String info;

	//
	@Element(required = false)
	private boolean tcpNoDealy = true;

	// IP
	@Element(required = false)
	private String ip = "127.0.0.1";

	// 端口
	@Element(required = false)
	private int port = 8080;

	//客户端创建的最大连接数
    @Element(required = false)
    private int maxConnectCount = 1;


	public int getGroupThreadNum() {
		return groupThreadNum;
	}

	public void setGroupThreadNum(int groupThreadNum) {
		this.groupThreadNum = groupThreadNum;
	}

	public ServerType getType() {
		return type;
	}

	public void setType(ServerType type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isTcpNoDealy() {
		return tcpNoDealy;
	}

	public void setTcpNoDealy(boolean tcpNoDealy) {
		this.tcpNoDealy = tcpNoDealy;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxConnectCount() {
		return maxConnectCount;
	}

	public void setMaxConnectCount(int maxConnectCount) {
		this.maxConnectCount = maxConnectCount;
	}

	
}
