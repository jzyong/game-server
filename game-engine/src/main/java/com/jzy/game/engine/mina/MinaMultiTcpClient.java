package com.jzy.game.engine.mina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.service.MinaClientService;

/**
 * 多客戶端管理,连接多个服务器
 *
 * @author JiangZhiYong
 * @date 2017-04-01 QQ:359135103
 * @version $Id: $Id
 */
public class MinaMultiTcpClient {

	private static final Logger log = LoggerFactory.getLogger(MinaMultiTcpClient.class);
	/**客户端列表 key：服务器ID*/
	private final Map<Integer, MinaTcpClient> tcpClients = new ConcurrentHashMap<>();

	/**
	 * <p>Constructor for MinaMultiTcpClient.</p>
	 */
	public MinaMultiTcpClient() {
	}

	/**
	 * 添加客户端
	 *
	 * @param service a {@link com.jzy.game.engine.mina.service.MinaClientService} object.
	 * @param config a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 * @param clientProtocolHandler a {@link org.apache.mina.core.service.IoHandler} object.
	 */
	public void addTcpClient(MinaClientService service, MinaClientConfig config, IoHandler clientProtocolHandler) {
		MinaTcpClient client = null;
		if (tcpClients.containsKey(config.getId())) {
			client = tcpClients.get(config.getId());
			client.setMinaClientConfig(config);
			return;
		}
		client = new MinaTcpClient(service, config, clientProtocolHandler);
		tcpClients.put(config.getId(), client);
	}

	/**
	 * 添加客户端
	 *
	 * @param service a {@link com.jzy.game.engine.mina.service.MinaClientService} object.
	 * @param config a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 * @param clientProtocolHandler a {@link org.apache.mina.core.service.IoHandler} object.
	 * @param factory a {@link com.jzy.game.engine.mina.code.ProtocolCodecFactoryImpl} object.
	 */
	public void addTcpClient(MinaClientService service, MinaClientConfig config, IoHandler clientProtocolHandler,
			ProtocolCodecFactoryImpl factory) {
		MinaTcpClient client = null;
		if (tcpClients.containsKey(config.getId())) {
			client = tcpClients.get(config.getId());
			client.setMinaClientConfig(config);
			return;
		}
		client = new MinaTcpClient(service, config, clientProtocolHandler, factory);
		tcpClients.put(config.getId(), client);
	}

	/**
	 * <p>getTcpClient.</p>
	 *
	 * @param id a {@link java.lang.Integer} object.
	 * @return a {@link com.jzy.game.engine.mina.MinaTcpClient} object.
	 */
	public MinaTcpClient getTcpClient(Integer id) {
		if (!tcpClients.containsKey(id)) {
			return null;
		}
		return tcpClients.get(id);
	}

	/**
	 * <p>removeTcpClient.</p>
	 *
	 * @param id a {@link java.lang.Integer} object.
	 */
	public void removeTcpClient(Integer id) {
		tcpClients.remove(id);
	}

	/**
	 * <p>containsKey.</p>
	 *
	 * @param id a {@link java.lang.Integer} object.
	 * @return a boolean.
	 */
	public boolean containsKey(Integer id) {
		return tcpClients.containsKey(id);
	}

	/**
	 * 向服务器发送数据
	 *
	 * @param sid
	 *            客户端ID
	 * @param obj a {@link java.lang.Object} object.
	 * @return a boolean.
	 */
	public boolean sendMsg(Integer sid, Object obj) {
		if (!tcpClients.containsKey(sid)) {
			return false;
		}
		MinaTcpClient client = tcpClients.get(sid);
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

	/**
	 * <p>Getter for the field <code>tcpClients</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<Integer, MinaTcpClient> getTcpClients() {
		return tcpClients;
	}

}
