package com.jzy.game.ai.nav.polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.NavMesh;
import com.jzy.game.ai.pfa.IndexedAStarPathFinder;
import com.jzy.game.engine.math.MathUtil;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.TimeUtil;

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
 * <p>unity多边形寻路网格导出脚本{@code PolygonNavMeshWindow}</p>
 * 
 * TODO 空间分割算法，四叉数;多边形额外参数，如草地，水，公路等消耗标识
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
		// 起点终点在同一个多边形中
		if (fromPolygon != null && fromPolygon.isInnerPoint(toPoint)) {
			toPolygon = fromPolygon;
		} else {
			toPolygon = getPolygon(toPoint);
		}
		
		if(fromPolygon==null) {
			LOGGER.warn("起始坐标：{}不在寻路层中",fromPoint.toString());
			return false;
		}
		
		if(toPolygon==null) {
			LOGGER.warn("终点坐标：{}不在寻路层中",toPoint.toString());
			return false;
		}
		
		if (pathFinder.searchConnectionPath(fromPolygon, toPolygon, heuristic, path)) {
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
//		LOGGER.debug("起始点：{}-->目标点：{} 多边形个数{}",fromPoint.toString(),toPoint.toString(),polygonGraphPath.getCount());
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
//		Optional<Polygon> findFirst = graph.getPolygons().stream().filter(p -> p.isInnerPoint(point)).findFirst();
//		if (findFirst.isPresent()) {
//			return findFirst.get();
//		}
		
		Polygon polygon = graph.getQuadTree().get(point, null);
		
		return polygon;
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

	/**
	 * 获取矩形
	 * 
	 * @param position
	 *            当前位置，一般为玩家坐标
	 * @param distance
	 *            矩形最近边中点到当前位置的距离
	 * @param sourceDirection
	 *            方向向量
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @return
	 */
	public Polygon getRectangle(Vector3 position, float distance, Vector3 sourceDirection, float width, float height) {
		Vector3 source = position.unityTranslate(sourceDirection, 0f, distance); // 中心坐标
		Vector3 corner_1 = source.unityTranslate(sourceDirection, -90, width / 2);
		Vector3 corner_2 = source.unityTranslate(sourceDirection, 90, width / 2);
		Vector3 corner_3 = corner_2.unityTranslate(sourceDirection, 0, height);
		Vector3 corner_4 = corner_1.unityTranslate(sourceDirection, 0, height);
		List<Vector3> list = new ArrayList<>(4);
		list.add(corner_1);
		list.add(corner_4);
		list.add(corner_3);
		list.add(corner_2);
		return new Polygon(0,list,null,false);
	}

	/**
	 * 获取扇形 <br>
	 * 由多边形组成
	 * 
	 * @param position
	 *            当前位置，一般为玩家坐标
	 * @param sourceDirection
	 *            方向向量
	 * @param distance
	 *            扇形起点到当前位置的距离
	 * @param radius
	 *            扇形半径
	 * @param degrees
	 *            扇形角度
	 * @return
	 */
	public final Polygon getSector(Vector3 position, Vector3 sourceDirection, float distance, float radius,
			float degrees) {
		Vector3 source = position.unityTranslate(sourceDirection, 0, distance); // 中心坐标
		Vector3 forward_l = source.unityTranslate(sourceDirection, -degrees / 2, radius);
		Vector3 forward_r = source.unityTranslate(sourceDirection, degrees / 2, radius);
		List<Vector3> sectors = new ArrayList<>(6);
		sectors.add(source);
		sectors.add(forward_l);
		int size = (int) (degrees / 10) / 2 - 1;
		for (int i = -size; i <= size; i++) {
			Vector3 forward = source.unityTranslate(sourceDirection, i * 10, radius);
			sectors.add(forward);
		}
		sectors.add(forward_r);
		return new Polygon(0,sectors,null,false);
	}

	/**
	 * 获取N正多边形 <br>
	 * N大于15基本上接近圆
	 * 
	 * @param center
	 *            中心点
	 * @param radius
	 *            半径
	 * @param vertexCount
	 *            顶点个数
	 * @return
	 */
	public final Polygon getNPolygon(Vector3 center, float radius, int vertexCount) {
		if (vertexCount < 3) {
			vertexCount = 3;
		}
		List<Vector3> sectors = new ArrayList<>(vertexCount);
		float degrees = 360f / vertexCount;
		// float randomDegrees =MathUtil.random() * 360; //随机转向
		for (int i = 0; i < vertexCount; i++) {
			Vector3 source = center.translateCopy(i * degrees /* + randomDegrees */, radius);
			sectors.add(source);
		}
		return new Polygon(0,sectors,null,false);
	}

	@Override
	public List<Vector3> getRandomPointsInPath(Vector3 center, float radius, float minDisToCenter) {

		int x = (int) center.x;
		int z = (int) center.z;
		int offset = (int) Math.ceil(radius);
		List<Vector3> targets = new ArrayList<>();
		Set<Entry<Integer, Map<Integer, List<Vector3>>>> entrySet = this.graph.getAllRandomPointsInPath().entrySet();
		
		for(Entry<Integer, Map<Integer, List<Vector3>>> entry:entrySet) {
			if(Math.abs(entry.getKey().intValue() - x) <= offset) {
				Set<Entry<Integer, List<Vector3>>> entrySet2 = entry.getValue().entrySet();
				for(Entry<Integer, List<Vector3>> entry2:entrySet2) {
					if(Math.abs(entry2.getKey()-z)<=radius) {
						targets.addAll(entry2.getValue());
					}
				}
			}
		}
		return targets;

	}

	/**
	 * <p>
	 * 效率不高，远距离寻路耗时
	 * <p>
	 */
	@Override
	public Vector3 getRandomPointInPath(Vector3 center, float radius, float minDisToCenter) {
		List<Vector3> list = getRandomPointsInPath(center, radius, minDisToCenter);
		return MathUtil.random(list);
	}

	public PolygonGraph getGraph() {
		return graph;
	}

}
