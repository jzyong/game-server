package com.jzy.game.ai.nav;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jzy.game.engine.math.Vector3;

/**
 * navmesh寻路三角形网格数据 <br>
 * 依次三个顶点确定一个三角形
 * 
 * @author JiangZhiYong
 *
 */
public class NavMeshData implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NavMeshData.class);
	private static final long serialVersionUID = 1L;

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

	private transient float width;	//宽
	private transient float height;	//高

	/**
	 * 数据检测，客户端的顶点坐标和三角形数据有可能是重复的
	 * <br>
	 * TODO 小三角形合并成大三角形或多边形；判断顶点是否在寻路层中，寻路层中的顶点不能作为路径点；两点所连线段是否穿过阻挡区，不穿过，直接获取坐标点
	 */
	public void check(int scale) {
		amendmentSameVector(pathTriangles, pathVertices);
		scaleVector(pathVertices, scale);

		this.width = Math.abs(this.getEndX() - this.getStartX());
		this.height = Math.abs(this.getEndZ() - this.getStartZ());
	}

	/**
	 * 缩放向量
	 * 
	 * @param scale
	 */
	protected void scaleVector(Vector3[] vertices, int scale) {
		if (vertices == null || scale == 1) {
			return;
		}
		for (Vector3 vector3 : vertices) {
			vector3.addX(-this.startX); // 缩放移动
			vector3.addZ(-this.startZ);
			vector3.scl(scale);
		}
	}

	/**
	 * 修正重复坐标，使坐标相同的下标修改为一致
	 */
	public void amendmentSameVector(int[] indexs, Vector3[] vertices) {
		if (indexs == null || vertices == null) {
			return;
		}
		Map<Vector3, Integer> map = new HashMap<>();
		// 检测路径重复点
		for (int i = 0; i < vertices.length; i++) {
			// 重复出现的坐标
			if (map.containsKey(vertices[i])) {
				for (int j = 0; j < indexs.length; j++) {
					if (indexs[j] == i) { // 修正重复的坐标
						// System.out.println(String.format("坐标重复为%s",
						// indexs[j],i,vertices[i].toString()));
						indexs[j] = map.get(vertices[i]);
					}
				}
				// vertices[i] = null;
			} else {
				map.put(vertices[i], i);
			}
		}
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

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
