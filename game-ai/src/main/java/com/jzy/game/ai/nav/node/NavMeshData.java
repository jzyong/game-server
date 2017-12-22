package com.jzy.game.ai.nav.node;

import java.io.Serializable;

/**
 * navmesh寻路三角形网格数据 <br>
 * 依次三个顶点确定一个三角形
 * 
 * @author JiangZhiYong
 *
 */
public class NavMeshData implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 阻挡顶点序号 */
	private int[] blockTriangles;
	/** 阻挡坐标 */
	private Vector3[] blockVertices;

	/** 行走区顶点序号 */
	private int[] pathTriangles;
	/** 行走区坐标 */
	private Vector3[] pathVertices;

	/** 开始坐标 */
	private float startX;
	private float startZ;
	/** 结束坐标 */
	private float endX;
	private float endZ;
	/** navmesh地图id */
	private int mapID;

	public int[] getBlockTriangles() {
		return blockTriangles;
	}

	public void setBlockTriangles(int[] blockTriangles) {
		this.blockTriangles = blockTriangles;
	}

	public Vector3[] getBlockVertices() {
		return blockVertices;
	}

	public void setBlockVertices(Vector3[] blockVertices) {
		this.blockVertices = blockVertices;
	}

	public int[] getPathTriangles() {
		return pathTriangles;
	}

	public void setPathTriangles(int[] pathTriangles) {
		this.pathTriangles = pathTriangles;
	}

	public Vector3[] getPathVertices() {
		return pathVertices;
	}

	public void setPathVertices(Vector3[] pathVertices) {
		this.pathVertices = pathVertices;
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

	public int getMapID() {
		return mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

}
