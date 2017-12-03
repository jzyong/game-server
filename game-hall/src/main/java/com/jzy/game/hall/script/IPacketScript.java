package com.jzy.game.hall.script;

import java.util.function.Consumer;
import com.jzy.game.engine.script.IScript;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.model.struct.Item;
import com.jzy.game.model.struct.Role;

/**
 * 道具
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月18日 下午4:18:44
 */
public interface IPacketScript extends IScript {

	/**
	 * 使用道具
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月18日 下午4:20:16
	 * @param id 道具Id
	 * @param num 数量
	 * @param itemConsumer
	 */
	default void useItem(Role role,long id,int num,Reason reason,Consumer<Item> itemConsumer) {
		
	}
	
	/**
	 * 添加道具
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月18日 下午4:23:47
	 * @param configId
	 * @param num 数量
	 * @param reason
	 * @param itemConsumer
	 */
	default Item addItem(Role role,int configId,int num,Reason reason,Consumer<Item> itemConsumer) {
		return null;
	}
}
