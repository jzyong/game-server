package com.jzy.game.bydr.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.bydr.script.IRoomScript;
import com.jzy.game.bydr.struct.Fish;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.bydr.struct.room.ClassicsRoom;
import com.jzy.game.bydr.struct.room.Room;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.message.bydr.BydrRoomMessage;
import com.jzy.game.message.bydr.BydrRoomMessage.FishEnterRoomResponse;
import com.jzy.game.message.bydr.BydrRoomMessage.FishInfo;
import com.jzy.game.message.bydr.BydrRoomMessage.RoomInfo;
import com.jzy.game.message.bydr.BydrRoomMessage.RoomRoleInfo;
import com.jzy.game.message.bydr.BydrRoomMessage.RoomType;

/**
 * 房间管理类
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月14日 下午2:27:59
 */
public class RoomManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoomManager.class);
	private static volatile RoomManager roomManager;
	private final Map<Long, Room> rooms = new ConcurrentHashMap<Long, Room>();
	private final Map<RoomType, List<Room>> roomTypes = new ConcurrentHashMap<>();

	private RoomManager() {

	}

	public static RoomManager getInstance() {
		if (roomManager == null) {
			synchronized (RoomManager.class) {
				if (roomManager == null) {
					roomManager = new RoomManager();
				}
			}
		}
		return roomManager;
	}

	/**
	 * 进入房间
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月14日 下午2:32:40
	 * @param role
	 * @param roomType
	 * @param rank
	 */
	public void enterRoom(Role role, RoomType roomType, int rank) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IRoomScript.class,
				script -> script.enterRoom(role, roomType, rank));
	}

	/**
	 * 退出房间
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月26日 下午4:08:55
	 * @param role
	 * @param roomId
	 */
	public void quitRoom(Role role, long roomId) {
		Room room = getRoom(roomId);
		if (room == null) {
			return;
		}
		Thread currentThread = Thread.currentThread();
		if (currentThread.equals(room.getRoomThread())) {
			ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IRoomScript.class,
					script -> script.quitRoom(role, room));
		} else {
			room.getRoomThread().execute(() -> ScriptManager.getInstance().getBaseScriptEntry()
					.executeScripts(IRoomScript.class, script -> script.quitRoom(role, room)));
		}

	}

	/**
	 * 添加房间
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月14日 下午2:45:19
	 * @param room
	 */
	public void addRoom(Room room) {
		rooms.put(room.getId(), room);
		if (room instanceof ClassicsRoom) {
			if (!roomTypes.containsKey(RoomType.CLASSICS)) {
				roomTypes.put(RoomType.CLASSICS, new ArrayList<Room>());
			}
			roomTypes.get(RoomType.CLASSICS).add(room);
		}
	}

	/**
	 * 获取房间
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月14日 下午2:56:29
	 * @param roomId
	 * @return
	 */
	public Room getRoom(long roomId) {
		return rooms.get(roomId);
	}

	/**
	 * 获取房间列表
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月14日 下午3:11:11
	 * @param roomType
	 * @return
	 */
	public List<Room> getRooms(RoomType roomType) {
		if (roomTypes.containsKey(roomType)) {
			return roomTypes.get(roomType);
		}
		List<Room> list = new ArrayList<>();
		roomTypes.put(roomType, list);
		return list;
	}

	/**
	 * 广播鱼进入房间
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月25日 下午4:13:23
	 * @param room
	 * @param fishs
	 */
	public void broadcastFishEnter(Room room, Fish... fishs) {
		if (fishs == null) {
			return;
		}
		FishEnterRoomResponse.Builder builder = FishEnterRoomResponse.newBuilder();
		FishInfo.Builder fishInfo = FishInfo.newBuilder();
		for (Fish fish : fishs) {
			builder.addFishInfo(buildFishInfo(fishInfo, fish));
		}

		FishEnterRoomResponse response = builder.build();
		room.getRoles().values().forEach(r -> {
			r.sendMsg(response);
		});
		// LOGGER.debug("鱼：{}", response.toString());
	}

	/**
	 * 构建鱼信息
	 * 
	 * @param builder
	 * @param fish
	 *            单个鱼
	 * @return
	 */
	private FishInfo buildFishInfo(FishInfo.Builder builder, Fish fish) {
		builder.clear();
		builder.addId(fish.getId());
		builder.addConfigId(fish.getConfigId());
		builder.addAllTrackId(fish.getTrackIds());
		builder.setFormation(fish.getFormationId()); // 0普通鱼 1boss鱼群 100~200
														// 鱼潮阵型ID
		builder.setBornTime(fish.getBornTime());
		builder.setServerTime(System.currentTimeMillis());
		builder.setSpeed(fish.getSpeed());
		builder.setTopSpeed(fish.getTopSpeed());
		builder.setTopSpeedStartTime(fish.getTopSpeedStartTime());
		return builder.build();
	}

	/**
	 * 构建房间信息
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月25日 下午5:59:28
	 * @param role
	 * @return
	 */
	public RoomRoleInfo buildRoomRoleInfo(Role role) {
		RoomRoleInfo.Builder builder = RoomRoleInfo.newBuilder();
		builder.setDiamond(role.getGem());
		builder.setGold(role.getGold());
		builder.setIcon(role.getHead() == null ? "" : role.getHead());
		builder.setNick(role.getNick() == null ? "" : role.getNick());
		builder.setLevel(role.getLevel());
		builder.setRid(role.getId());
		builder.setVip(role.isIs_vip());
		return builder.build();
	}
	
	/**
	 * 构建房间信息
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月20日 上午10:51:45
	 * @param room
	 * @return
	 */
	public RoomInfo buildRoomInfo(Room room) {
		RoomInfo.Builder builder=RoomInfo.newBuilder();
		builder.setId(room.getId());
		builder.setType(room.getType());
		builder.setState(room.getState());
		
		return builder.build();
	}
}
