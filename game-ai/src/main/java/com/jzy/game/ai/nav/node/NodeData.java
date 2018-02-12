package com.jzy.game.ai.nav.node;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.NavMeshData;
import com.jzy.game.engine.math.Vector3;

/**
 * navmesh寻路三角形网格数据 <br>
 * 依次三个顶点确定一个三角形
 * 
 * @author JiangZhiYong
 *
 */
public class NodeData extends NavMeshData {
	private static final Logger LOGGER = LoggerFactory.getLogger(NodeData.class);
	private static final long serialVersionUID = 1L;
	/** 阻挡顶点序号 */
	private int[] blockTriangles;
	/** 阻挡坐标 */
	private Vector3[] blockVertices;

	
	/**
	 * 数据检测，客户端的顶点坐标和三角形数据有可能是重复的
	 * <br>
	 * TODO 小三角形合并成大三角形或多边形；判断顶点是否在寻路层中，寻路层中的顶点不能作为路径点；两点所连线段是否穿过阻挡区，不穿过，直接获取坐标点
	 */
	public void check(int scale) {
		super.check(scale);
		amendmentSameVector(blockTriangles, blockVertices);
		scaleVector(blockVertices, scale);

	}



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

	

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
