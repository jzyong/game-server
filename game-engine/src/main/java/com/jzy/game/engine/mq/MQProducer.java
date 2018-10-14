package com.jzy.game.engine.mq;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQ 消息生产者，消息发送
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月28日 上午10:38:30
 */
public class MQProducer extends MQService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MQProducer.class);

	public MQProducer(MQConfig mqConfig) {
		super(mqConfig);
	}

	/**
	 * 发送消息
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年7月28日 下午2:43:29
	 * @param destName
	 * @param msg
	 */
	public void sendMsg(String destName, String msg) {
		sendMsg(destName, msg, DeliveryMode.NON_PERSISTENT);
	}

	/**
	 * 消息进行持久化
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年7月28日 下午2:43:39
	 * @param destName
	 * @param msg
	 */
	public void sendPresistentMsg(String destName, String msg) {
		sendMsg(destName, msg, DeliveryMode.PERSISTENT);
	}

	private boolean sendMsg(String destName, String msg, int deliverMode) {
		Connection conn = getConnection();
		if (conn == null) {
			LOGGER.warn("MQ 创建连接失败 消息：{}", msg);
			return false;
		}
		Session session = null;
		try {
			conn.start();
			session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Destination destination = session.createQueue(destName);
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(deliverMode);
			TextMessage message = session.createTextMessage(msg);
			producer.send(message);
			return true;
		} catch (Exception e) {
			LOGGER.error("sendMsg", e);
            closeConnection();
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception e) {
					LOGGER.error("sendMsg", e);
				}
			}
		}
		return false;
	}
}
