package com.jzy.game.hall.tcp.guild;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallGuildMessage.CreateGuildRequest;
import com.jzy.game.model.mongo.hall.dao.GuildDao;
import com.jzy.game.model.struct.Guild;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 创建公会
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月22日 上午11:51:15
 */
@HandlerEntity(mid=MID.CreateGuildReq_VALUE,msg=CreateGuildRequest.class)
public class CreateGuildHandler extends TcpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(CreateGuildHandler.class);

	@Override
	public void run() {
		CreateGuildRequest req=getMsg();
		Guild guild=new Guild();
		guild.setMasterId(rid);
		guild.getMembers().add(rid);
		guild.setCreateTime(new Date());
		GuildDao.saveGuild(guild);
	}

	
	
}
