package com.jjy.game.gate.script.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.filter.firewall.BlacklistFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.gate.script.IGateServerScript;
import com.jjy.game.gate.server.GateTcpUserServer;
import com.jzy.game.engine.script.IInitScript;

/**
 * 设置IP黑名单
 * TODO 是否需要存入数据库，通过后台调用设置
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月4日 上午11:24:58
 */
public class IpBlackListScript implements IGateServerScript,IInitScript{
	private static final Logger LOGGER=LoggerFactory.getLogger(IpBlackListScript.class);
	private List<InetAddress> blackList;

	@Override
	public void init() {
		//添加黑名单列表
		blackList=new ArrayList<>();
		try {
//			blackList.add(InetAddress.getByName("192.168.0.17"));	//TODO 测试
		} catch (Exception e) {
			LOGGER.warn("添加IP黑名单",e);
		}
		
		
		//设置用户tcp黑名单
		GateTcpUserServer.getBlacklistFilter().setBlacklist(blackList);
		
	}

	@Override
	public void setIpBlackList(BlacklistFilter filter) {
		filter.setBlacklist(blackList);
	}
	
	

}
