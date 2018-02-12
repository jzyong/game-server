package com.jzy.game.ai.nav;

import java.util.List;

import com.jzy.game.engine.math.Vector3;

/**
 * navmesh寻路入口
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public abstract class NavMesh {

	/** 地图宽x轴 */
	protected float width;
	/** 地图高y轴 */
	protected float height;
	/** 配置id */
	protected int mapId;

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	/**
	 * 或者在路径中的坐标点<br>
	 * 屏幕输入坐标点
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public abstract Vector3 getPointInPath(float x, float z);
}
