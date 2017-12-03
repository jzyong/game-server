package com.jzy.game.ai.unity.nav;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jzy.game.engine.util.FileUtil;

/**
 * 寻路navmesh地图 <br>
 * 数据分为阻挡层和行走层
 * 
 * @author JiangZhiYong
 *
 */
public class NavMesh implements Serializable, Cloneable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NavMesh.class);
	private static final long serialVersionUID = 1L;

	/** 是否为编辑器模式true会被缩放 */
	private final boolean editor;
	/** 地图宽x轴 */
	private float width;
	/** 地图高y轴 */
	private float height;
	private int mapID;
	private float startX;
	private float startZ;
	private float endX;
	private float endZ;
	/**缩放比例*/
	private float scale;
	/**三角形之间的最大距离*/
	private float maxDistanceBetweenTriangle;

	/**
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public NavMesh(String filePath) {
		this(filePath, false);
	}

	/**
	 *
	 * @param filePath
	 *            文件路径
	 * @param editor
	 *            是否为编辑器模式true会被缩放
	 */
	public NavMesh(String filePath, boolean editor) {
		LOGGER.info("ss");
		this.editor = editor;
		String txtFile = FileUtil.readTxtFile(filePath);
		if (txtFile == null) {
			LOGGER.warn("navmesh数据{}读取失败", filePath);
			return;
		}
		NavMeshData data = JSON.parseObject(txtFile, NavMeshData.class);
		this.width = Math.abs(data.getEndX() - data.getStartX());
		this.height = Math.abs(data.getEndZ() - data.getStartZ());
		this.startX = data.getStartX();
		this.startZ = data.getStartZ();
		this.endX = data.getEndX();
		this.endZ = data.getEndZ();
		this.mapID = data.getMapID();
		LOGGER.info("加载地图{}宽{}高{}，开始坐标({},{}),结束坐标({},{})", mapID, width,
				height, startX, startZ, endX, endZ);
		if (this.editor) {
			scale = 850 / height;
		} else {
			scale = 1;
		}
		maxDistanceBetweenTriangle = Math.max(width, height) * scale;
	}
}
