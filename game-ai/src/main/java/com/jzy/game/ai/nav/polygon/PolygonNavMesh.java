package com.jzy.game.ai.nav.polygon;

import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.NavMesh;
import com.jzy.game.ai.nav.triangle.TriangleData;
import com.jzy.game.engine.math.Vector3;

/**
 * 多边形寻路
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PolygonNavMesh extends NavMesh{
	private final PolygonGraph graph;
	
	
	public PolygonNavMesh(String navMeshStr) {
		this(navMeshStr,1);
	}
	
	/**
	 * 
	 * @param navMeshStr
	 *            导航网格数据
	 *            @param scale 放大倍数
	 */
	public PolygonNavMesh(String navMeshStr,int scale) {
		graph = new PolygonGraph(JSON.parseObject(navMeshStr, TriangleData.class),scale);
//		pathFinder = new IndexedAStarPathFinder<Triangle>(graph);
//		heuristic = new TriangleHeuristic();
	}


	@Override
	public Vector3 getPointInPath(float x, float z) {
		// TODO Auto-generated method stub
		return null;
	}

	public PolygonGraph getGraph() {
		return graph;
	}

	
}
