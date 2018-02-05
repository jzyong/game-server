package com.jzy.game.ai.nav.edge;
//package com.jzy.game.ai.nav;

import java.util.Optional;

import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.NavMesh;
import com.jzy.game.ai.nav.NavMeshData;
import com.jzy.game.ai.pfa.IndexedAStarPathFinder;
import com.jzy.game.engine.math.Vector3;

/**
 * 寻路网格
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午4:40:36
 */
public class EdgeNavMesh extends NavMesh {

	private final NavMeshGraph graph; // 导航数据图
	private final NavMeshHeuristic heuristic; // 寻路消耗计算
	private final IndexedAStarPathFinder<Triangle> pathFinder; // A*寻路算法

	/**
	 * 
	 * @param navMeshStr
	 *            导航网格数据
	 */
	public EdgeNavMesh(String navMeshStr) {
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

	/**
	 * 查询路径
	 * 
	 * @param fromPoint
	 * @param toPoint
	 * @param path
	 */
	public boolean findPath(Vector3 fromPoint, Vector3 toPoint, NavMeshGraphPath path) {
		path.clear();
		Triangle fromTriangle = getTriangle(fromPoint);
		if (pathFinder.searchConnectionPath(fromTriangle, getTriangle(toPoint), heuristic, path)) {
			path.start = new Vector3(fromPoint);
			path.end = new Vector3(toPoint);
			path.startTri = fromTriangle;
			return true;
		}
		return false;
	}

	/**
	 * 获取坐标点所在的三角形
	 *  @note 很耗时，迭代所有三角形寻找
	 * @param point
	 * @return
	 */
	public Triangle getTriangle(Vector3 point) {
		Optional<Triangle> optional = graph.getTriangles().stream().filter(t -> t.isInnerPoint(point)).findFirst();
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
}
