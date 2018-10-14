package com.jzy.game.engine.redis;

import com.jzy.game.engine.redis.jedis.JedisPubSubMessage;
import com.jzy.game.engine.script.IScript;

/**
 * 订阅消息处理器
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月10日 上午10:29:29
 */
public interface IPubSubScript extends IScript {

    /**
     * 消息处理
     * @param channel
     * @param message
     */
    void onMessage(String channel, JedisPubSubMessage message);
}
