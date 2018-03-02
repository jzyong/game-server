package com.jzy.game.ai.nav.polygon;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.NavMesh;
import com.jzy.game.ai.nav.triangle.Triangle;
import com.jzy.game.ai.nav.triangle.TriangleData;
import com.jzy.game.ai.nav.triangle.TriangleGraphPath;
import com.jzy.game.ai.pfa.IndexedAStarPathFinder;
import com.jzy.game.engine.math.Vector3;

/**
 * 多边形寻路
 * 
 * <h3>思路</h3>
 * <p>
 * 1、整个寻路的网格是由多个互相连接的凸多边形组成<br>
 * 2、初始化网格的时候，计算出凸多边形互相相邻的边，和通过互相相邻的边到达另外一个多边形的距离。<br>
 * 3、开始寻路时，首先肯定是会得到一个开始点的坐标和结束点的坐标<br>
 * 4、判断开始点和结束点分别位于哪个多边形的内部（如果没有上下重叠的地形，可以只通过x和z坐标，高度先忽略掉），把多边形的编号记录下来<br>
 * 5、使用A*寻路，找到从开始的多边形到结束的多边形将会经过哪几个多边形，记录下来。<br>
 * 6、在得到途径的多边形后， 从开始点开始，根据拐点算法，计算出路径的各个拐点组成了路径点坐标。（忽略高度，只计算出x和z）。<br>
 * 7、到上一步，2D寻路部分结束。人物根据路径点做移动。<br>
 * 8、假如需要3D高度计算，那么在获得了刚才2D寻路的路径点之后，再分别和途径的多边形的边做交点计算，得出经过每一个边时的交点，那么当多边形与多边形之间有高低变化，路径点也就通过边的交点同样的产生高度的变化。<br>
 * <p>
 * 
 * @author JiangZhiYong
 * @date 2018年2月23日
 * @mail 359135103@qq.com
 */
public final class PolygonNavMesh extends NavMesh {
	private static final Logger LOGGER = LoggerFactory.getLogger(PolygonNavMesh.class);
	private final PolygonGraph graph;
	private final PolygonHeuristic heuristic;
	private final IndexedAStarPathFinder<Polygon> pathFinder;

	public PolygonNavMesh(String navMeshStr) {
		this(navMeshStr, 1);
	}

	/**
	 * @param navMeshStr
	 *            导航网格数据
	 * @param scale
	 *            放大倍数
	 */
	public PolygonNavMesh(String navMeshStr, int scale) {
		graph = new PolygonGraph(JSON.parseObject(navMeshStr, PolygonData.class), scale);
		pathFinder = new IndexedAStarPathFinder<Polygon>(graph);
		heuristic = new PolygonHeuristic();
	}

	/**
     * 查询路径
     * 
     * @param fromPoint
     * @param toPoint
     * @param path
     */
    public boolean findPath(Vector3 fromPoint, Vector3 toPoint, PolygonGraphPath path) {
        path.clear();
        Polygon fromPolygon = getPolygon(fromPoint);
        Polygon toPolygon;
        //起点终点在同一个多边形中
        if(fromPolygon!=null&&fromPolygon.isInnerPoint(toPoint)) {
        	toPolygon= fromPolygon;
        }else {
        	toPolygon=getPolygon(toPoint);
        }
        if (pathFinder.searchConnectionPath(fromPolygon,toPolygon , heuristic, path)) {
            path.start = new Vector3(fromPoint);
            path.end = new Vector3(toPoint);
            path.startPolygon = fromPolygon;
            return true;
        }
        return false;
    }

	/**
	 * 查询路径
	 * <p>
	 * 丢失部分多边形坐标，有高度误差，运算速度较快
	 * </p>
	 * 
	 * @param fromPoint
	 * @param toPoint
	 * @param pointPath
	 * @return
	 */
	public List<Vector3> findPath(Vector3 fromPoint, Vector3 toPoint, PolygonPointPath pointPath) {
		PolygonGraphPath polygonGraphPath = new PolygonGraphPath();
		boolean find = findPath(fromPoint, toPoint, polygonGraphPath);
		if (!find) {
			return pointPath.getVectors();
		}
		// 计算坐标点
		pointPath.calculateForGraphPath(polygonGraphPath, false);

		return pointPath.getVectors();
	}

	/**
	 * 查询有高度路径
	 * 
	 * @param fromPoint
	 * @param toPoint
	 * @param pointPath
	 * @return
	 */
	public List<Vector3> find3DPath(Vector3 fromPoint, Vector3 toPoint, PolygonPointPath pointPath) {
		PolygonGraphPath polygonGraphPath = new PolygonGraphPath();
		boolean find = findPath(fromPoint, toPoint, polygonGraphPath);
		if (!find) {
			return pointPath.getVectors();
		}
		// 计算坐标点
		pointPath.calculateForGraphPath(polygonGraphPath, true);

		return pointPath.getVectors();
	}

	/**
	 * 坐标点所在的多边形
	 * 
	 * @param point
	 * @return
	 */
	public Polygon getPolygon(Vector3 point) {
		// TODO 高度判断，有可能有分层重叠多边形
		Optional<Polygon> findFirst = graph.getPolygons().stream().filter(p -> p.isInnerPoint(point)).findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		return null;
	}

	@Override
	public Vector3 getPointInPath(float x, float z) {
		Vector3 vector3 = new Vector3(x, z);
		Polygon polygon = getPolygon(vector3);
		if (polygon == null) {
			LOGGER.info("坐标{},{}不在路径中", x, z);
			return null;
		}
		vector3.y = polygon.y;
		return vector3;
	}

	public PolygonGraph getGraph() {
		return graph;
	}

}
