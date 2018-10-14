package com.jzy.game.engine.redis.jedis;

import java.util.HashSet;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 *redis集群配置文件
 */
public class JedisClusterConfig {

    @ElementList(required = false)
    private HashSet<JedisClusterNodesConfig> nodes = new HashSet<>();
    @Element(required = true)
    private int poolMaxTotal=500;
    @Element(required = true)
    private int poolMaxIdle=5;
    @Element
    private int connectionTimeout=2000;
    @Element
    private int soTimeout=2000;
    @Element
    private int maxRedirections=6;
    @Element
    private int timeBetweenEvictionRunsMillis=30000;
    @Element
    private int minEvictableIdleTimeMillis=1800000;
    @Element
    private int softMinEvictableIdleTimeMillis=1800000;
    @Element
    private int maxWaitMillis=60000;
    @Element
    private boolean testOnBorrow=true;
    @Element
    private boolean testWhileIdle;
    @Element
    private boolean testOnReturn;
    
    public HashSet<JedisClusterNodesConfig> getNodes() {
        return nodes;
    }

    public void setNodes(HashSet<JedisClusterNodesConfig> nodes) {
        this.nodes = nodes;
    }

    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public void setPoolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    public int getPoolMaxIdle() {
        return poolMaxIdle;
    }

    public void setPoolMaxIdle(int poolMaxIdle) {
        this.poolMaxIdle = poolMaxIdle;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getMaxRedirections() {
        return maxRedirections;
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = maxRedirections;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    public void setSoftMinEvictableIdleTimeMillis(int softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public static class JedisClusterNodesConfig {

        @Element(required = true)
        private String ip;
        @Element(required = true)
        private int port;

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
        
    }
}
