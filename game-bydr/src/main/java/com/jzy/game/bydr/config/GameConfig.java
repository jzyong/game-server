package com.jzy.game.bydr.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import com.jzy.game.model.constant.Config;

/**
 * 游戏配置
 *
 * @author JiangZhiYong
 * @date 2017-04-14 QQ:359135103
 */
@Root
public class GameConfig extends Config {

	//线程房间数
	@Element(required=false)
	private int threadRoomNum =4;

	// 房间位置大小
	@Element(required = false)
	private int roomSize = 4;

	// 机器人个数
	@Element(required = false)
	private int robotNum = 30;

	
	

	public int getThreadRoomNum() {
		return threadRoomNum;
	}

	public void setThreadRoomNum(int threadRoomNum) {
		this.threadRoomNum = threadRoomNum;
	}

	public int getRoomSize() {
		return roomSize;
	}

	public void setRoomSize(int roomSize) {
		this.roomSize = roomSize;
	}

	public int getRobotNum() {
		return robotNum;
	}

	public void setRobotNum(int robotNum) {
		this.robotNum = robotNum;
	}


}
