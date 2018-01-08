package com.jzy.game.ai.nav.edge;
//package com.jzy.game.ai.nav;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;

/**
 * 寻路网格
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午4:40:36
 */
public class NavMesh {

	private final NavMeshGraph graph;	//导航数据图
	private final NavMeshHeuristic heuristic;	//寻路消耗计算
	private final IndexedAStarPathFinder<Triangle> pathFinder;	//A*寻路算法
	
	/**
	 * 
	 * @param navMeshStr
	 *            导航网格数据
	 */
	public NavMesh(String navMeshStr) {
		graph = new NavMeshGraph(JSON.parseObject(navMeshStr, NavMeshData.class));
		pathFinder = new IndexedAStarPathFinder<Triangle>(graph);
		heuristic = new NavMeshHeuristic();
	}

	public NavMeshGraph getGraph() {
		return graph;
	}

	public NavMeshHeuristic getHeuristic() {
		return heuristic;
	}

	public IndexedAStarPathFinder<Triangle> getPathFinder() {
		return pathFinder;
	}
	
	
}
