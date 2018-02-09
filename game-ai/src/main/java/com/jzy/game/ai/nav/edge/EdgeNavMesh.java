package com.jzy.game.ai.nav.edge;
//package com.jzy.game.ai.nav;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(EdgeNavMesh.class);

	private final NavMeshGraph graph; // 导航数据图
	private final NavMeshHeuristic heuristic; // 寻路消耗计算
	private final IndexedAStarPathFinder<Triangle> pathFinder; // A*寻路算法

	
	public EdgeNavMesh(String navMeshStr) {
		this(navMeshStr,1);
	}
	
	/**
	 * 
	 * @param navMeshStr
	 *            导航网格数据
	 *            @param scale 放大倍数
	 */
	public EdgeNavMesh(String navMeshStr,int scale) {
		graph = new NavMeshGraph(JSON.parseObject(navMeshStr, NavMeshData.class),scale);
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
	private boolean findPath(Vector3 fromPoint, Vector3 toPoint, NavMeshGraphPath path) {
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
	 * 获取路径
	 * 
	 * @param fromPoint
	 * @param toPoint
	 * @param navMeshPointPath
	 * @return
	 */
	public List<Vector3> findPath(Vector3 fromPoint, Vector3 toPoint, NavMeshPointPath navMeshPointPath) {
		NavMeshGraphPath navMeshGraphPath = new NavMeshGraphPath();
		boolean find = findPath(fromPoint, toPoint, navMeshGraphPath);
		if (!find) {
			return navMeshPointPath.getVectors();
		}
		navMeshPointPath.calculateForGraphPath(navMeshGraphPath, false);
		return navMeshPointPath.getVectors();
	}

	/**
	 * 获取坐标点所在的三角形
	 * 
	 * @note 很耗时，迭代所有三角形寻找
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

	/**
	 * 或者在路径中的坐标点<br>
	 * 屏幕输入坐标点
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public Vector3 getPointInPath(float x, float z) {
		Vector3 vector3 = new Vector3(x, z);
		Triangle triangle = getTriangle(vector3);
		if (triangle == null) {
			LOGGER.info("坐标{},{}不在路径中", x, z);
			return null;
		}
		vector3.y = triangle.y;
		return vector3;
	}

}
