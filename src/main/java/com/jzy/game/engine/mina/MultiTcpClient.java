package com.jzy.game.engine.mina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.service.ClientService;

/**
 * 多客戶端管理
 *
 * @author JiangZhiYong
 * @date 2017-04-01 QQ:359135103
 */
public class MultiTcpClient {

	private static final Logger log = LoggerFactory.getLogger(MultiTcpClient.class);
	/**客户端列表 key：服务器ID*/
	private final Map<Integer, TcpClient> tcpClients = new ConcurrentHashMap<>();

	public MultiTcpClient() {
	}

	/**
	 * 添加客户端
	 * 
	 * @param service
	 * @param config
	 * @param clientProtocolHandler
	 */
	public void addTcpClient(ClientService service, MinaClientConfig config, IoHandler clientProtocolHandler) {
		TcpClient client = null;
		if (tcpClients.containsKey(config.getId())) {
			client = tcpClients.get(config.getId());
			client.setMinaClientConfig(config);
			return;
		}
		client = new TcpClient(service, config, clientProtocolHandler);
		tcpClients.put(config.getId(), client);
	}

	/**
	 * 添加客户端
	 * 
	 * @param service
	 * @param config
	 * @param clientProtocolHandler
	 */
	public void addTcpClient(ClientService service, MinaClientConfig config, IoHandler clientProtocolHandler,
			ProtocolCodecFactoryImpl factory) {
		TcpClient client = null;
		if (tcpClients.containsKey(config.getId())) {
			client = tcpClients.get(config.getId());
			client.setMinaClientConfig(config);
			return;
		}
		client = new TcpClient(service, config, clientProtocolHandler, factory);
		tcpClients.put(config.getId(), client);
	}

	public TcpClient getTcpClient(Integer id) {
		if (!tcpClients.containsKey(id)) {
			return null;
		}
		return tcpClients.get(id);
	}

	public void removeTcpClient(Integer id) {
		tcpClients.remove(id);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public boolean containsKey(Integer id) {
		return tcpClients.containsKey(id);
	}

	/**
	 * 向服务器发送数据
	 *
	 * @param sid
	 *            客户端ID
	 * @param obj
	 * @return
	 */
	public boolean sendMsg(Integer sid, Object obj) {
		if (!tcpClients.containsKey(sid)) {
			return false;
		}
		TcpClient client = tcpClients.get(sid);
		if (client == null) {
			return false;
		}
		return client.getService().sendMsg(obj);
	}

	/**
	 * 状态监测
	 */
	public void checkStatus() {
		tcpClients.values().forEach(c -> c.checkStatus());
	}

	public Map<Integer, TcpClient> getTcpClients() {
		return tcpClients;
	}

}
