package com.jjy.game.hall.script.packet;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.manager.PacketManager;
import com.jjy.game.hall.script.IPacketScript;
import com.jjy.game.message.hall.HallPacketMessage.UseItemResponse;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.struct.Item;
import com.jjy.game.model.struct.Role;

/**
 * 使用道具脚本
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月18日 下午4:55:43
 */
public class UseItemScript implements IPacketScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(UseItemScript.class);
	
	@Override
	public void useItem(Role role,long itemId, int num, Reason reason, Consumer<Item> itemConsumer) {
		Item item = PacketManager.getInstance().getItem(role, itemId);
		if(item==null) {
			return;
		}
		if(item.getNum()<num) {
			LOGGER.warn("道具数量{}，请求{}",item.getNum(),num);
			return;
		}
		
		item.setNum(item.getNum()-num);
		item.saveToRedis(role.getId());
		UseItemResponse.Builder builder=UseItemResponse.newBuilder();
		builder.setResult(0);
		role.sendMsg(builder.build());
	}

}
