package com.jzy.game.engine.redis;

import com.alibaba.fastjson.JSON;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  redis的订阅发布消息
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月10日 上午10:27:29
 */
public class JedisPubSubMessage {

    //消息的目标
    private long id;
    //消息的目标集
    private Set<Long> ids;
    //消息来源的服务器
    private int server;

    //消息目标的服务器
    private int target;
    //消息的值，json格式
    private String json;
    //属性 key
    private String key;
    //属性 int key
    private int intValue;
    //属性 long key
    private long longValue;

    public JedisPubSubMessage() {
    }

    public JedisPubSubMessage(long id) {
        this.id = id;
    }

    public JedisPubSubMessage(long id, int server) {
        this.id = id;
        this.server = server;
    }

    public JedisPubSubMessage(long id, int server, int target) {
        this.id = id;
        this.server = server;
        this.target = target;
    }

    public JedisPubSubMessage(long id, int server, int target, String key, int intValue) {
        this.id = id;
        this.server = server;
        this.target = target;
        this.key = key;
        this.intValue = intValue;
    }

    public JedisPubSubMessage(long id, int server, int target, String key, long longValue) {
        this.id = id;
        this.server = server;
        this.target = target;
        this.key = key;
        this.longValue = longValue;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the server
     */
    public int getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(int server) {
        this.server = server;
    }

    /**
     * @return the target
     */
    public int getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(int target) {
        this.target = target;
    }

    /**
     * @return the json
     */
    public String getJson() {
        return json;
    }

    /**
     * @param json the json to set
     */
    public void setJson(String json) {
        this.json = json;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the intValue
     */
    public int getIntValue() {
        return intValue;
    }

    /**
     * @param intValue the intValue to set
     */
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    /**
     * @return the longValue
     */
    public long getLongValue() {
        return longValue;
    }

    /**
     * @param longValue the longValue to set
     */
    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    /**
     * @return the ids
     */
    public Set<Long> getIds() {
        return ids;
    }

    /**
     * @param ids the ids to set
     */
    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    public void addIds(Long... ids) {
        if (this.ids == null) {
            this.ids = new HashSet<>();
        }
        this.ids.addAll(Arrays.asList(ids));
    }
}
