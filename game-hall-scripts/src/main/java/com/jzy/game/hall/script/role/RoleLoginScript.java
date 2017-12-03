package com.jzy.game.hall.script.role;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.redisson.api.RMap;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.model.mongo.hall.dao.MailDao;
import com.jzy.game.model.mongo.hall.dao.RoleDao;
import com.jzy.game.model.redis.key.HallKey;
import com.jzy.game.model.struct.Item;
import com.jzy.game.model.struct.Mail;
import com.jzy.game.model.struct.Role;
import com.jzy.game.model.struct.Mail.MailState;
import com.jzy.game.model.struct.Mail.MailType;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.redisson.FastJsonCodec;
import com.jzy.game.engine.redis.redisson.RedissonManager;
import com.jzy.game.engine.util.JsonUtil;
import com.jzy.game.hall.manager.MailManager;
import com.jzy.game.hall.manager.PacketManager;
import com.jzy.game.hall.manager.RoleManager;
import com.jzy.game.hall.script.IRoleScript;

/**
 * 登陆脚本 <br>
 * 临时数据处理，如发临时发补偿，数据处理等
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月18日 下午6:02:29
 */
public class RoleLoginScript implements IRoleScript {

	@Override
	public void login(Role role, Reason reason) {
		RoleDao.saveRole(role);
		// 设置用户session
		RoleManager.getInstance().getRoles().put(role.getId(), role);
		loadData(role);
		tempInit(role);
	}

	/**
	 * 加载数据
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午5:28:12
	 * @param role
	 */
	public void loadData(Role role) {
		// 背包
		String key = HallKey.Role_Map_Packet.getKey(role.getId());
//		RMap<Long, Item> items = RedissonManager.getRedissonClient().getMap(key,new FastJsonCodec(Long.class, Item.class));
//		role.setItems(items);
		
		
		
//		Map<String, String> hgetAll = JedisManager.getJedisCluster().hgetAll(key);
//		if (hgetAll != null) {
//			hgetAll.forEach((k, value) -> {
//				Item item = JsonUtil.parseObject(value, Item.class);
//				role.getItems().put(item.getId(), item);
//			});
//		}
	}

	/**
	 * 临时初始化
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午6:30:10
	 * @param role
	 */
	private void tempInit(Role role) {

		// 道具
		if (role.getItemCount() < 1) {
			PacketManager.getInstance().addItem(role, 1, 20,Reason.UserLogin, null);
		}
		//邮件
		List<Mail> mails = MailDao.getMails(role.getId());
		if(mails==null||mails.size()<1) {
			MailManager.getInstance().sendMail(-1, role.getId(), "测试", "系统测试", MailType.PRIVATE, null);
		}

	}

}
