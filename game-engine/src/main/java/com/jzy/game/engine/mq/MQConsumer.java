package com.jzy.game.engine.mq;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.script.ScriptManager;

/**
 * MQ 消费者，监听器
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月28日 上午10:37:44
 */
public class MQConsumer extends MQService implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(MQConsumer.class);

	private final String queueName; // 队列名称
	private boolean connected; // 是否连接

	public MQConsumer(MQConfig mqConfig) {
		super(mqConfig);
        queueName = mqConfig.getQueueName();
	}

	public MQConsumer(String configPath, String queueName) {
		super(configPath);
		
		this.queueName = queueName;
	}

	@Override
	public void run() {
		MessageConsumer consumer = null;
		while (true) {
			try {
				if (!connected) { // 连接
					Connection conn = getConnection();
					if (conn == null) {
						LOGGER.error("启动MQ失败，获取连接失败");
                        connected = false;
						Thread.sleep(3000);
						break;
					}
					conn.start();
					Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
					consumer = session.createConsumer(new ActiveMQQueue(queueName));
                    connected = true;
				} else if (consumer != null) { // 接收消息
					Message msg = consumer.receive();
					if (msg == null) {
						continue;
					}
					if (msg instanceof TextMessage) {
						String body = ((TextMessage) msg).getText();
						if (body == null) {
							continue;
						}
						ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IMQScript.class,
								script -> script.onMessage(body));
					} else {
						LOGGER.warn("不支持的消息：{}", msg.getClass().getName());
					}
				}
			} catch (Exception e) {
				LOGGER.error("消息接收", e);
                closeConnection();
                connected = false;
			}
		}
	}
	
	public void stop(){
        closeConnection();
	}

}
