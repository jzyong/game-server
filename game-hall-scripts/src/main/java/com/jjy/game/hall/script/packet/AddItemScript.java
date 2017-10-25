package com.jjy.game.hall.script.packet;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.manager.PacketManager;
import com.jjy.game.hall.script.IPacketScript;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.struct.Item;
import com.jjy.game.model.struct.Role;

/**
 * 添加道具
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月18日 下午4:58:18
 */
public class AddItemScript implements IPacketScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(AddItemScript.class);

	@Override
	public Item addItem(Role role,int configId, int num, Reason reason, Consumer<Item> itemConsumer) {
		// TODO 具体逻辑,叠加方式等？
		Item item=new Item();
		item.setConfigId(configId);
		item.setNum(item.getNum()+num);
		item.saveToRedis(role.getId());
		return item;
	}

	
}
