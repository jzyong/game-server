package com.jzy.game.ai.nav.polygon;

import com.jzy.game.ai.nav.NavMeshData;

/**
 * 多边形数据
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PolygonData extends NavMeshData{
	private static final long serialVersionUID = 1L;
	
	/**多边形顶点序号，*/
	private int[][] pathPolygons;

	
	
	@Override
	public void check(int scale) {
		super.check(scale);
	}

	public int[][] getPathPolygons() {
		return pathPolygons;
	}

	public void setPathPolygons(int[][] pathPolygons) {
		this.pathPolygons = pathPolygons;
	}
	
	

}
