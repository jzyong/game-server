package com.jjy.game.bydr.script.redis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.manager.ConfigManager;
import com.jjy.game.bydr.manager.RoleManager;
import com.jjy.game.bydr.struct.role.Role;
import com.jjy.game.message.ServerMessage.ChangeRoleServerRequest;
import com.jjy.game.message.bydr.BydrRoomMessage.ApplyAthleticsResponse;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.redis.channel.BydrChannel;
import com.jzy.game.engine.redis.IPubSubScript;
import com.jzy.game.engine.redis.jedis.JedisPubSubMessage;
import com.jzy.game.engine.server.ServerType;

/**
 * 报名竞技赛结果
 * <p>广播人数变化 人数满后，进行跨服处理</p>
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月3日 上午9:44:01
 */
public class ApplyAthleticsResScript implements IPubSubScript{
	private static final Logger LOGGER=LoggerFactory.getLogger(ApplyAthleticsResScript.class);

	@Override
	public void onMessage(String channel, JedisPubSubMessage message) {
		if(!BydrChannel.ApplyAthleticsRes.name().equals(channel)){
			return;
		}
		LOGGER.info("竞技赛");
		List<Role> roles=new ArrayList<>();
		message.getIds().forEach(roleId->{
			Role role = RoleManager.getInstance().getRole(roleId);
			if(role!=null){
				roles.add(role);
			}
		});
		if(roles.size()<1){
			LOGGER.debug("不是该服务器跨服角色");
			return;
		}
		//广播玩家
		ApplyAthleticsResponse.Builder builder=ApplyAthleticsResponse.newBuilder();
		builder.addAllRoleId(message.getIds());
		ApplyAthleticsResponse response = builder.build();
		roles.forEach(role->role.sendMsg(response));
		
		//进行跨服处理
		if(message.getIds().size()>=ConfigManager.getInstance().getGameConfig().getRoomSize()){
			int targetServerId=message.getServer();	//目标服务器ID
			ChangeRoleServerRequest.Builder serverBuilder=ChangeRoleServerRequest.newBuilder();
			roles.forEach(role->{
				if(role.getGameId()!=targetServerId){
					//通知网关服，改变session
					serverBuilder.clear();
					serverBuilder.setRoleId(role.getId());
					serverBuilder.setServerType(ServerType.GAME_BYDR.getType());
					serverBuilder.setServerId(targetServerId);
					role.sendMsg(serverBuilder.build());
					LOGGER.info("角色{}跨服",role.getId());
				}
			});
		}
	}

}
