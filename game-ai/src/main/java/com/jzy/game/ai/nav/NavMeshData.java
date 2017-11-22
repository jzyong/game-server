package com.jzy.game.ai.nav;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.math.Vector3;

/**
 * 导航网格地图 json数据
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午5:21:54
 */
public class NavMeshData {
	/** 地图ID */
	private int mapID;
	/** 开始X坐标 */
	private float startX;
	private float startZ;
	/** 结束X坐标 */
	private float endX;
	private float endZ;
	/** 阻挡顶点下标 */
	private List<Integer> blockTriangles;
	/** 阻挡坐标 */
	private List<Vector3> blockVertices;

	/** 寻路顶点下标 */
	private List<Integer> pathTriangles;
	/** 寻路坐标 */
	private List<Vector3> pathVertices;

	public int getMapID() {
		return mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public float getStartX() {
		return startX;
	}

	public void setStartX(float startX) {
		this.startX = startX;
	}

	public float getStartZ() {
		return startZ;
	}

	public void setStartZ(float startZ) {
		this.startZ = startZ;
	}

	public float getEndX() {
		return endX;
	}

	public void setEndX(float endX) {
		this.endX = endX;
	}

	public float getEndZ() {
		return endZ;
	}

	public void setEndZ(float endZ) {
		this.endZ = endZ;
	}

	public List<Integer> getBlockTriangles() {
		return blockTriangles;
	}

	public void setBlockTriangles(List<Integer> blockTriangles) {
		this.blockTriangles = blockTriangles;
	}

	public List<Vector3> getBlockVertices() {
		return blockVertices;
	}

	public void setBlockVertices(List<Vector3> blockVertices) {
		this.blockVertices = blockVertices;
	}

	public List<Integer> getPathTriangles() {
		return pathTriangles;
	}

	public void setPathTriangles(List<Integer> pathTriangles) {
		this.pathTriangles = pathTriangles;
	}

	public List<Vector3> getPathVertices() {
		return pathVertices;
	}

	public void setPathVertices(List<Vector3> pathVertices) {
		this.pathVertices = pathVertices;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	
}
