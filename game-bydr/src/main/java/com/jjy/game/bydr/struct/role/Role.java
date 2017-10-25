package com.jjy.game.bydr.struct.role;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.redisson.api.RMap;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.annotation.JSONField;
import com.jjy.game.bydr.script.IRoleScript;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.redis.key.BydrKey;
import com.jjy.game.model.redis.key.HallKey;
import com.jjy.game.model.struct.Item;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.redisson.RedissonManager;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.struct.Person;
import com.jzy.game.engine.util.ReflectUtil;


/**
 * 角色信息
 *
 * @author JiangZhiYong
 * @date 2017-02-27 QQ:359135103
 */
@Entity(value = "role", noClassnameStored = true)
public class Role extends Person{
	private static final Logger LOGGER=LoggerFactory.getLogger(Role.class);
	
	/** setter 方法集合 */
	protected transient static final Map<String, Method> WRITEMETHODS = ReflectUtil.getReadMethod(Role.class);
	
	private String head;
	private int gender;
	private boolean is_vip;
	private String sign; // 签名
	private Date update_date;
	private Date cdate;
	private boolean is_sync_from_hall; // 是否已经和大厅同步

	private int giftGold; // 新手赠送金币
	private volatile long dayAwardGold; // 每日领奖金币
	private volatile long dayFireGold; // 每日开炮金币
	private int pay_rmb; // 充值额
	private long winGold; // 输赢的钱，需要同步更新到大厅数据库
	private long roomId; // 房间ID
	private int seatNo; // 座位编号
	private int roomType; // 房间类型
	@Embedded
	private List<Integer> presentFishs = new ArrayList<>(); // 赠送鱼，百分之百命中

	@Embedded
	private List<Integer> presentAccumulates = new ArrayList<>(); // 赠送累积奖，值为
																	// 房间类型*10+0|1
																	// 0从真池获取，1从假池获取
	@Embedded
	private List<Integer> fireGolds = new ArrayList<>(); // 开炮金币数
	private int betGold; // 当前下注金币总和
	private long enterRoomGold; // 进入房间时玩家的金币
	private long fireTime; // 玩家最后开炮时间

	// 玩家习惯统计
	private transient int fireCount; // 开炮次数
	private transient Map<Integer, Integer> fishHits = new HashMap<>(); // 命中鱼统计
	private transient Map<Integer, Integer> fishDies = new HashMap<>(); // 死亡鱼统计

	private transient int ownerId; // 机器人所属玩家ID
	private transient long leaveTime; // 机器人离开房间时间

	
//	private Map<Long, Item> items;
	
//	/**大厅角色数据*/
//	private RMap<String, Object> hallRole;


	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public boolean isIs_vip() {
		return is_vip;
	}

	public void setIs_vip(boolean is_vip) {
		this.is_vip = is_vip;
	}

	public Date getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}

	public Date getCdate() {
		return cdate;
	}

	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}

	public boolean isIs_sync_from_hall() {
		return is_sync_from_hall;
	}

	public void setIs_sync_from_hall(boolean is_sync_from_hall) {
		this.is_sync_from_hall = is_sync_from_hall;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public int getBetGold() {
		return betGold;
	}

	public void setBetGold(int betGold) {
		this.betGold = betGold;
	}

	public void addBetGold(int add) {
		this.betGold += add;
	}


	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}


	public int getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(int seatNo) {
		this.seatNo = seatNo;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	// public void addBulletCount() {
	// this.bulletCount++;
	// }
	//
	// public void descBulletCount() {
	// this.bulletCount--;
	// }
	//
	// public int getBulletCount() {
	// return bulletCount;
	// }
	//
	// public void setBulletCount(int bulletCount) {
	// this.bulletCount = bulletCount;
	// }

	public int getGiftGold() {
		return giftGold;
	}

	public void setGiftGold(int giftGold) {
		this.giftGold = giftGold;
	}

	public long getDayAwardGold() {
		return dayAwardGold;
	}

	public void setDayAwardGold(long dayWinGold) {
		this.dayAwardGold = dayWinGold;
	}

	public long getDayFireGold() {
		return dayFireGold;
	}

	public void setDayFireGold(long dayFireGold) {
		this.dayFireGold = dayFireGold;
	}


	public int getPay_rmb() {
		return pay_rmb;
	}

	public void setPay_rmb(int pay_rmb) {
		this.pay_rmb = pay_rmb;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public long getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(long leaveTime) {
		this.leaveTime = leaveTime;
	}

	public long getWinGold() {
		return winGold;
	}

	public void setWinGold(long winGold) {
		this.winGold = winGold;
	}

	public List<Integer> getFireGolds() {
		return fireGolds;
	}

	public void setFireGolds(List<Integer> fireGolds) {
		this.fireGolds = fireGolds;
	}

	public List<Integer> getPresentFishs() {
		return presentFishs;
	}

	public void setPresentFishs(List<Integer> presentFishs) {
		this.presentFishs = presentFishs;
	}

	public List<Integer> getPresentAccumulates() {
		return presentAccumulates;
	}

	public void setPresentAccumulates(List<Integer> presentAccumulates) {
		this.presentAccumulates = presentAccumulates;
	}

	public long getFireTime() {
		return fireTime;
	}

	public void setFireTime(long fireTime) {
		this.fireTime = fireTime;
	}

	public int getRoomType() {
		return roomType;
	}

	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}

	public long getEnterRoomGold() {
		return enterRoomGold;
	}

	public void setEnterRoomGold(long enterRoomGold) {
		this.enterRoomGold = enterRoomGold;
	}

	/**
	 * 每日赢取金币
	 * 
	 * @return
	 */
	public long getDayWinGold() {
		return this.dayAwardGold + this.dayFireGold;
	}


	public int getFireCount() {
		return fireCount;
	}

	public int addFireCount() {
		return this.fireCount++;
	}

	public void setFireCount(int fireCount) {
		this.fireCount = fireCount;
	}

	public Map<Integer, Integer> getFishHits() {
		return fishHits;
	}

	public void setFishHits(Map<Integer, Integer> fishHits) {
		this.fishHits = fishHits;
	}

	public void addFishHitsCount(int configId) {
		Integer count = this.fishHits.get(configId);
		if (count == null) {
			this.fishHits.put(configId, 1);
		} else {
			this.fishHits.put(configId, count + 1);
		}
	}

	public Map<Integer, Integer> getFishDies() {
		return fishDies;
	}

	public void setFishDies(Map<Integer, Integer> fishDies) {
		this.fishDies = fishDies;
	}
	
//	public Map<Long, Item> getItems() {
//		return items;
//	}
//
//	public void setItems(Map<Long, Item> items) {
//		this.items = items;
//	}
	

//	public RMap<String, Object> getHallRole() {
//		if(hallRole==null) {
//			this.hallRole = RedissonManager.getRedissonClient().getMap(HallKey.Role_Map_Info.getKey(this.id), new StringCodec());
//		}
//		return hallRole;
//	}
//
//	public void setHallRole(RMap<String, Object> hallRole) {
//		this.hallRole = hallRole;
//	}

	public void addFishDiesCount(int configId) {
		Integer count = this.fishDies.get(configId);
		if (count == null) {
			this.fishDies.put(configId, 1);
		} else {
			this.fishDies.put(configId, count + 1);
		}
	}
	

	/**
	 * 修改金币
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月25日 下午5:23:41
	 * @param gold
	 * @param reason
	 */
	public void changeGold(int gold,Reason reason) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IRoleScript.class, script->script.changeGold(this, gold, reason));
	}
	
	/**
	 * 同步金币
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月26日 上午10:42:24
	 * @param reason
	 */
	public void syncGold(Reason reason) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IRoleScript.class, script->script.syncGold(this, reason));
	}

	public void saveToRedis(String propertiesName) {
		if (this.id < 1) {
			throw new RuntimeException(String.format("角色ID %d 异常", this.id));
		}
		String key = BydrKey.Role_Map.getKey(this.id);
		Method method = WRITEMETHODS.get(propertiesName);
		if (method != null) {
			try {
				Object value = method.invoke(this);
				if (value != null) {
					JedisManager.getJedisCluster().hset(key, propertiesName, value.toString());
				} else {
					LOGGER.warn("属性{}值为null", propertiesName);
				}

			} catch (Exception e) {
				LOGGER.error("属性存储", e);
			}
		} else {
			LOGGER.warn("属性：{}未找到对应方法", propertiesName);
		}
	}
	
}