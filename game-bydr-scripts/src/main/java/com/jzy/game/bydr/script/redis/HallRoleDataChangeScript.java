package com.jzy.game.bydr.script.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.bydr.manager.RoleManager;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.engine.redis.IPubSubScript;
import com.jzy.game.engine.redis.jedis.JedisPubSubMessage;
import com.jzy.game.model.constant.Config;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.model.redis.channel.BydrChannel;

/**
 * 大厅通知角色数据改变
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月17日 上午10:20:00
 */
public class HallRoleDataChangeScript implements IPubSubScript {
	private static final Logger LOGGER=LoggerFactory.getLogger(HallRoleDataChangeScript.class);

	@Override
	public void onMessage(String channel, JedisPubSubMessage message) {
		if (!channel.startsWith("Hall")) { // channel必须以Hall开头
			return;
		}
		if(message.getServer()!=Config.SERVER_ID) {
			return;
		}
		Role role = RoleManager.getInstance().getRole(message.getId());
		if(role==null) {
			LOGGER.warn("角色[{}]已退出游戏:{} {}更新失败",message.getId(),channel,message.toString());
			return;
		}
		
		switch (BydrChannel.valueOf(channel)) {
		case HallGoldChange:
			role.changeGold(message.getTarget(), Reason.HallGoldChange);
			break;

		default:
			break;
		}

	}

	
}
