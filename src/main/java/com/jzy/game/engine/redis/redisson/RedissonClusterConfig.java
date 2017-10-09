package com.jzy.game.engine.redis.redisson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * redisson 集群配置
 * @see https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95<br>
 * TODO 完善配置
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月15日 下午3:37:46
 */
@Root
public class RedissonClusterConfig {

	/**节点url连接字符串  redis://127.0.0.1:7000*/
	@ElementList(required=false)
	private List<String> nodes=new ArrayList<>();
	
	/**集群扫描间隔*/
	@Element(required=false)
	private int scanInterval=2000;
	
	/**读取操作的负载均衡模式*/
	@Element(required=false)
	private ReadMode readMode=ReadMode.SLAVE;
	
	/**订阅操作的负载均衡模式*/
	@Element(required=false)
	private SubscriptionMode subscriptionMode=SubscriptionMode.SLAVE;
	
	/**从节点发布和订阅连接的最小空闲连接数*/
	@Element(required=false)
	private int subscriptionConnectionMinimumIdleSize=1;
	
	/**从节点发布和订阅连接池大小*/
	@Element(required=false)
	private int subscriptionConnectionPoolSize=50;
	
	/**从节点最小空闲连接数*/
	@Element(required=false)
	private int slaveConnectionMinimumIdleSize=10;
	
	/**从节点连接池大小*/
	@Element(required=false)
	private int slaveConnectionPoolSize=64;

	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}

	public int getScanInterval() {
		return scanInterval;
	}

	public void setScanInterval(int scanInterval) {
		this.scanInterval = scanInterval;
	}

	public ReadMode getReadMode() {
		return readMode;
	}

	public void setReadMode(ReadMode readMode) {
		this.readMode = readMode;
	}

	public SubscriptionMode getSubscriptionMode() {
		return subscriptionMode;
	}

	public void setSubscriptionMode(SubscriptionMode subscriptionMode) {
		this.subscriptionMode = subscriptionMode;
	}

	public int getSubscriptionConnectionMinimumIdleSize() {
		return subscriptionConnectionMinimumIdleSize;
	}

	public void setSubscriptionConnectionMinimumIdleSize(int subscriptionConnectionMinimumIdleSize) {
		this.subscriptionConnectionMinimumIdleSize = subscriptionConnectionMinimumIdleSize;
	}

	public int getSubscriptionConnectionPoolSize() {
		return subscriptionConnectionPoolSize;
	}

	public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
		this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
	}

	public int getSlaveConnectionMinimumIdleSize() {
		return slaveConnectionMinimumIdleSize;
	}

	public void setSlaveConnectionMinimumIdleSize(int slaveConnectionMinimumIdleSize) {
		this.slaveConnectionMinimumIdleSize = slaveConnectionMinimumIdleSize;
	}

	public int getSlaveConnectionPoolSize() {
		return slaveConnectionPoolSize;
	}

	public void setSlaveConnectionPoolSize(int slaveConnectionPoolSize) {
		this.slaveConnectionPoolSize = slaveConnectionPoolSize;
	}
	
	
}
