package com.jzy.game.bydr.struct.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import com.jzy.game.bydr.struct.Fish;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.bydr.thread.RoomThread;
import com.jzy.game.model.constant.Config;


/**
 * 房间信息
 * 
 * @author JiangZhiYong
 * @date 2017-02-28 QQ:359135103
 */
@Entity(value = "room", noClassnameStored = true)
public class Room{
	@Id
	private long id;
	
	/**类型*/
	private int type; 
	/**级别*/
	private int rank; 

	/**所在线程*/
	private transient RoomThread roomThread;
	private transient long state; // 房间状态
	private transient long robotCreateTime; // 机器人创建时间
	/**房间位置、角色ID*/
	private transient Map<Integer, Role> roles = new HashMap<>(); 
	private transient Map<Long, Fish> fishMap = new HashMap<>(); // 鱼
	// 多个刷新可能同时存在 value 单条鱼为List<Fish> 鱼群List<List<Fish>>
	private transient Map<Integer, Object> groupFishMap = new HashMap<>();
	private transient Set<Integer> refreshedBoom = new HashSet<>(); // 已经刷新的鱼潮ID
	private transient Map<Integer, Long> refreshTimes = new HashMap<>(); // 鱼刷新时间记录

	private transient long frozenEndTime; // 冰冻结束时间
	private transient long bossStartTime; // boss结束时间
	private transient long bossEndTime; // boss结束时间
	private transient long boomEndTime; // 鱼潮结束时间
	private transient long boomStartTime; // 鱼潮开始时间
	private transient int boomConfigId; // 鱼潮配置ID
	private transient int formationIndex; // 待刷新鱼潮阵型下标

	// 测试统计用
	private long allFireCount; // 房间总共开炮数
	private long hitFireCount; // 房间真实命中鱼数
	private long fireResultCount; // 房间结果请求数


	public Room() {
		super();
		this.id = Config.getId();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getBoomConfigId() {
		return boomConfigId;
	}

	public void setBoomConfigId(int boomConfigId) {
		this.boomConfigId = boomConfigId;
	}

	public Map<Long, Fish> getFishMap() {
		return fishMap;
	}

	public void setFishMap(Map<Long, Fish> fishMap) {
		this.fishMap = fishMap;
	}


	public Set<Integer> getRefreshedBoom() {
		return refreshedBoom;
	}

	public void setRefreshedBoom(Set<Integer> refreshedBoom) {
		this.refreshedBoom = refreshedBoom;
	}

	public Map<Integer, Long> getRefreshTimes() {
		return refreshTimes;
	}

	public long getState() {
		return state;
	}

	public void setState(long state) {
		this.state = state;
	}

	public void addState(long state) {
		this.state = this.state | state;
	}

	public void removeState(long state) {
		this.state = this.state & (~state);
	}

	public long getRobotCreateTime() {
		return robotCreateTime;
	}

	public void setRobotCreateTime(long robotCreateTime) {
		this.robotCreateTime = robotCreateTime;
	}

	public long getBoomStartTime() {
		return boomStartTime;
	}

	public void setBoomStartTime(long boomStartTime) {
		this.boomStartTime = boomStartTime;
	}

	public int getFormationIndex() {
		return formationIndex;
	}

	public void setFormationIndex(int formationIndex) {
		this.formationIndex = formationIndex;
	}


	public void setRefreshTimes(Map<Integer, Long> refreshTimes) {
		this.refreshTimes = refreshTimes;
	}

	public long getBossStartTime() {
		return bossStartTime;
	}

	public void setBossStartTime(long bossStartTime) {
		this.bossStartTime = bossStartTime;
	}

	/**
	 * 更新刷新时间
	 * 
	 * @param refreshId
	 *            刷新ID
	 * @param addTime
	 *            下次刷新增加时间
	 */
	public void updateRefreshTime(int refreshId, int addTime) {
		refreshTimes.put(refreshId, System.currentTimeMillis() + addTime);
	}


	/**
	 * 添加房间中的鱼
	 * 
	 * @param fish
	 */
	@SuppressWarnings("unchecked")
	public void addFish(Object obj) {
		if (obj instanceof Fish) {
			Fish fish = (Fish) obj;
			fishMap.put(fish.getId(), fish);
			if (!groupFishMap.containsKey(fish.getRefreshId())) {
				List<Fish> list = new ArrayList<>();
				list.add(fish);
				groupFishMap.put(fish.getRefreshId(), list);
			} else {
				((List<Fish>) groupFishMap.get(fish.getRefreshId())).add(fish);
			}
		} else if (obj instanceof List) {
			List<Fish> fishs = (List<Fish>) obj;
			if (fishs.size() < 1) {
				return;
			}

			if (!groupFishMap.containsKey(fishs.get(0).getRefreshId())) {
				List<List<Fish>> list = new ArrayList<>();
				list.add(fishs);
				groupFishMap.put(fishs.get(0).getRefreshId(), list);
			} else {
				((List<List<Fish>>) groupFishMap.get(fishs.get(0).getRefreshId())).add(fishs);
			}
			fishs.forEach(f -> fishMap.put(f.getId(), f));
		}
	}

	public Map<Integer, Object> getGroupFishMap() {
		return groupFishMap;
	}

	public void setGroupFishMap(Map<Integer, Object> groupFishMap) {
		this.groupFishMap = groupFishMap;
	}

	public Map<Integer, Role> getRoles() {
		return roles;
	}

	public void setRoles(Map<Integer, Role> roles) {
		this.roles = roles;
	}

	public long getFrozenEndTime() {
		return frozenEndTime;
	}

	public void setFrozenEndTime(long frozenEndTime) {
		this.frozenEndTime = frozenEndTime;
	}

	public long getBossEndTime() {
		return bossEndTime;
	}

	public void setBossEndTime(long bossEndTime) {
		this.bossEndTime = bossEndTime;
	}

	public long getBoomEndTime() {
		return boomEndTime;
	}

	public void setBoomEndTime(long boomEndTime) {
		this.boomEndTime = boomEndTime;
	}

	public long getAllFireCount() {
		return allFireCount;
	}

	public long addAllFireCount() {
		return this.allFireCount++;
	}

	public void setAllFireCount(long allFireCount) {
		this.allFireCount = allFireCount;
	}

	public long getHitFireCount() {
		return hitFireCount;
	}

	public void setHitFireCount(long hitFireCount) {
		this.hitFireCount = hitFireCount;
	}

	public long addHitFireCount() {
		return this.hitFireCount++;
	}

	public long getFireResultCount() {
		return fireResultCount;
	}

	public void setFireResultCount(long fireResultCount) {
		this.fireResultCount = fireResultCount;
	}

	public long addFireResultCount() {
		return this.fireResultCount++;
	}
	

//	/**
//	 * 开始冰冻
//	 */
//	public void startFrozen(ConfigFish configFish) {
//		this.addState(RoomState.ON_FROZEN.getValue());
//		ConfigFishSpecial fishSpecial = configFish.getFishSpecial();
//		this.setFrozenEndTime(TimeUtil.currentTimeMillis() + fishSpecial.getFrozenTime());
//		RoomManager.getInstance().broadcastRoomChange(this);
//		// 修改鱼死亡时间
//		this.getFishMap().values().forEach(fish -> fish.setDieTime(fish.getDieTime() + fishSpecial.getFrozenTime()));
//		// 刷鱼时间后延
//		RoomManager.getInstance().updateRefreshTime(this, fishSpecial.getFrozenTime());
//	}

	public RoomThread getRoomThread() {
		return roomThread;
	}

	public void setRoomThread(RoomThread roomThread) {
		this.roomThread = roomThread;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * 清除房间
	 */
	public void clearRoom() {
//		boomConfigId = 0;
//		boomEndTime = 0;
//		boomStartTime = 0;
//		bossStartTime = 0;
//		bossEndTime = 0;
//		Fish[] fishs = fishMap.values().toArray(new Fish[fishMap.size()]);
//		for (Fish fish : fishs) {
//			FishManager.getInstance().getFishPool().put(fish);
//		}
//		fishMap.clear();
//		fishMap.clear();
//		formationIndex = 0;
//		frozenEndTime = 0;
//		groupFishMap.clear();
//		refreshedBoom.clear();
//		refreshTimes.clear();
//		robotCreateTime = 0;
//		roles.clear();
//		state = 0;
	}

}