package com.jjy.game.bydr.script.room;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.manager.RoomManager;
import com.jjy.game.bydr.script.IFishScript;
import com.jjy.game.bydr.struct.Fish;
import com.jjy.game.bydr.struct.room.Room;
import com.jzy.game.engine.util.MathUtil;

/**
 * 刷新房间鱼群
 * TODO 新刷新规则
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月14日 上午9:39:23
 */
public class RoomFishRefreshScript implements IFishScript{
	private static final Logger LOGGER=LoggerFactory.getLogger(RoomFishRefreshScript.class);

	@Override
	public void fishRefresh(Room iRoom) {
//		LOGGER.warn("刷新鱼");
		Room room=(Room) iRoom;
		//TODO 测试，每秒刷新一条鱼
		Fish fish1 = bornFish(room, MathUtil.random(1, 5), null);
		Fish fish2 = bornFish(room, MathUtil.random(1, 5), null);
		Fish fish3 = bornFish(room, MathUtil.random(1, 5), null);
		Fish fish4 = bornFish(room, MathUtil.random(1, 5), null);
		Fish fish5 = bornFish(room, MathUtil.random(1, 5), null);
		
		RoomManager.getInstance().broadcastFishEnter(room, fish1,fish2,fish3,fish4,fish5/*,fish1,fish2,fish3,fish4,fish5*/);
		
	}
	
	/**
	 * 出生鱼
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月25日 下午4:00:39
	 * @param configId
	 * @param fish
	 * @return
	 */
	private Fish bornFish(Room room, int configId,Consumer<Fish> fishConsumer) {
		Fish fish=new Fish();
		fish.setConfigId(configId);
		
		if(fishConsumer!=null) {
			fishConsumer.accept(fish);
		}
		fish.setRoom(room);
		fish.setTrackIds(new ArrayList<Integer>());
		room.getFishMap().put(fish.getId(), fish);
		return fish;
	}

}
