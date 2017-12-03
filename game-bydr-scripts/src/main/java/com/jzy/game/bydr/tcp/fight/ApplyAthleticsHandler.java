package com.jzy.game.bydr.tcp.fight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.jedis.JedisPubSubMessage;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.bydr.BydrRoomMessage.ApplyAthleticsRequest;
import com.jzy.game.model.redis.channel.BydrWorldChannel;

/**
 * 报名竞技赛
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月3日 上午9:23:04
 */
@HandlerEntity(mid = MID.ApplyAthleticsReq_VALUE, msg = ApplyAthleticsRequest.class)
public class ApplyAthleticsHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplyAthleticsHandler.class);

	@Override
	public void run() {
		ApplyAthleticsRequest req = getMsg();
		LOGGER.info("{}参加竞技赛", rid);
		JedisPubSubMessage msg = new JedisPubSubMessage(rid, req.getType().getNumber(), req.getRank());
		JedisManager.getJedisCluster().publish(BydrWorldChannel.ApplyAthleticsReq.toString(), msg.toString());
	}

}
