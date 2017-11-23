//package com.jzy.game.ai.nav;
//
//import com.alibaba.fastjson.JSON;
//import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
//
///**
// * 寻路网格
// * 
// * @author JiangZhiYong
// * @QQ 359135103 2017年11月7日 下午4:40:36
// */
//public class NavMesh {
//
//	private final NavMeshGraph graph;
//	private final NavMeshHeuristic heuristic;
//	private final IndexedAStarPathFinder<Triangle> pathFinder;
//	
//	/**
//	 * 
//	 * @param navMeshStr
//	 *            导航网格数据
//	 */
//	public NavMesh(String navMeshStr) {
//		graph = new NavMeshGraph(JSON.parseObject(navMeshStr, NavMeshData.class));
//		pathFinder = new IndexedAStarPathFinder<Triangle>(graph);
//		heuristic = new NavMeshHeuristic();
//	}
//
//	public NavMeshGraph getGraph() {
//		return graph;
//	}
//
//	public NavMeshHeuristic getHeuristic() {
//		return heuristic;
//	}
//
//	public IndexedAStarPathFinder<Triangle> getPathFinder() {
//		return pathFinder;
//	}
//	
//	
//}
