package com.jzy.game.hall.manager;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.hall.script.IPacketScript;
import com.jzy.game.message.hall.HallPacketMessage.PacketItem;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.model.struct.Item;
import com.jzy.game.model.struct.Role;

/**
 * 背包
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月18日 下午2:49:17
 */
public class PacketManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketManager.class);
	private static volatile PacketManager packetManager;

	private PacketManager() {

	}

	public static PacketManager getInstance() {
		if (packetManager == null) {
			synchronized (PacketManager.class) {
				if (packetManager == null) {
					packetManager = new PacketManager();
				}
			}
		}
		return packetManager;
	}

	/**
	 * 使用道具
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午4:25:54
	 * @param id
	 * @param num
	 * @param reason
	 * @param itemConsumer
	 */
	public void useItem(Role role, long id, int num, Reason reason, Consumer<Item> itemConsumer) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IPacketScript.class,
				script -> script.useItem(role, id, num, reason, itemConsumer));
	}

	/**
	 * 添加道具
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午4:23:47
	 * @param configId
	 * @param num
	 *            数量
	 * @param reason
	 * @param itemConsumer
	 */
	public Item addItem(Role role, int configId, int num, Reason reason, Consumer<Item> itemConsumer) {
		return ScriptManager.getInstance().getBaseScriptEntry().functionScripts(IPacketScript.class,
				(IPacketScript script) -> script.addItem(role, configId, num, reason, itemConsumer));
	}

	/**
	 * 构建
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午4:07:49
	 * @param item
	 * @return
	 */
	public PacketItem buildPacketItem(Item item) {
		PacketItem.Builder builder = PacketItem.newBuilder();
		builder.setId(item.getId());
		builder.setConfigId(item.getConfigId());
		builder.setNum(item.getNum());
		return builder.build();
	}

	/**
	 * 获取物品
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午5:10:36
	 * @param rid
	 * @param itemId
	 * @return
	 */
	public Item getItem(Role role, long itemId) {
		return role.getItem(itemId);
	}
}
