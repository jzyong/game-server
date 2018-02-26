package com.jzy.game.ai.nav.polygon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.ai.pfa.Connection;
import com.jzy.game.ai.pfa.IndexedGraph;
import com.jzy.game.engine.math.Vector3;

/**
 * 多边形图对象
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PolygonGraph implements IndexedGraph<Polygon> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PolygonGraph.class);

	private List<Polygon> polygons = new ArrayList<>();

	private Map<Polygon, List<PolygonEdge>> sharedEdges;
	private Set<IndexConnection> indexConnections = new HashSet<>();

	private PolygonData polygonData;

	public PolygonGraph(PolygonData polygonData, int scale) {
		this.polygonData = polygonData;
		this.polygonData.check(scale);
		initCalculate(polygonData, scale);
	}

	/**
	 * 初始化计算
	 * 
	 * @param polygonData
	 * @param scale
	 */
	private void initCalculate(PolygonData polygonData, int scale) {
		createPolygons(polygonData, scale);
		calculateIndexConnections(polygonData.getPathPolygonIndexs());
		sharedEdges = createSharedEdgesMap(indexConnections, polygons);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Connection<Polygon>> getConnections(Polygon fromNode) {
		return (List<Connection<Polygon>>) (List<?>) sharedEdges.get(fromNode);
	}

	@Override
	public int getIndex(Polygon node) {
		return node.getIndex();
	}

	@Override
	public int getNodeCount() {
		return sharedEdges.size();
	}

	/**
	 * 计算共享边
	 * 
	 * @note 两个多边形只存一个共享边
	 * @param indices
	 */
	private void calculateIndexConnections(Map<Integer, Set<Integer>> polygonVectorIndexs) {
		int i = 0, j = 0, m = 0;
		Vector3[] edge = { null, null };
		int[][] indices = new int[polygonVectorIndexs.size()][];

		for (Entry<Integer, Set<Integer>> entry : polygonVectorIndexs.entrySet()) {
			Set<Integer> value = entry.getValue();
			indices[m] = new int[value.size()];
			int n = 0;
			for (Integer index : value) {
				indices[m][n++] = index;
			}
			m++;
		}

		while (i < indices.length) {
			int[] polygonAIndex = indices[i];
			j = 0;
			while (j < indices.length) {
				if (i == j) {
					j++;
					continue;
				}
				int[] polygonBIndex = indices[j];
				if (hasSharedEdgeIndices(polygonAIndex, polygonBIndex, edge)) {
					IndexConnection indexConnection1 = new IndexConnection(edge[0], edge[1], i, j);
					IndexConnection indexConnection2 = new IndexConnection(edge[1], edge[0], j, i);
					indexConnections.add(indexConnection1);
					indexConnections.add(indexConnection2);
					// LOGGER.debug("共享边1：{}", indexConnection1.toString());
					// LOGGER.debug("共享边2：{}", indexConnection2.toString());
					edge[0] = null;
					edge[1] = null;
				}
				j++;
			}
			i++;
		}
	}

	/**
	 * 多边形是否有共享边
	 * 
	 * @param polygonAIndex
	 * @param polygonBIndex
	 * @param edge
	 *            共享边顶点坐标
	 * @return
	 */
	private boolean hasSharedEdgeIndices(int[] polygonAIndex, int[] polygonBIndex, Vector3[] edge) {
		int aLength = polygonAIndex.length;
		int bLength = polygonBIndex.length;
		float precision = 0.001f;
		for (int i = 0; i < polygonAIndex.length; i++) {
			Vector3 av1 = polygonData.getPathVertices()[polygonAIndex[i]];
			Vector3 av2 = polygonData.getPathVertices()[polygonAIndex[(i + 1) % aLength]];

			for (int j = 0; j < polygonBIndex.length; j++) {
				Vector3 bv0 = null;
				if (j > 0) {
					bv0 = polygonData.getPathVertices()[polygonBIndex[j - 1]];
				} else {
					bv0 = polygonData.getPathVertices()[polygonBIndex[polygonBIndex.length - 1]];
				}

				Vector3 bv1 = polygonData.getPathVertices()[polygonBIndex[j]];
				Vector3 bv2 = polygonData.getPathVertices()[polygonBIndex[(j + 1) % bLength]];

				// 顺序相等
				if (av1.equal(bv1, precision) && av2.equal(bv2, precision)) {
					edge[0] = av1;
					edge[1] = av2;
					return true;

					// 逆序相等
				} else if (/* bv0 != null && */ av1.equal(bv1, precision) && av2.equal(bv0, precision)) {
					edge[0] = av1;
					edge[1] = av2;
					return true;

				}
				/** unity自带navmesh导出工具存在共享边包含关系(共享边存在三个顶点)，强制加成共享边 
				*   TODO 能解决部分问，但是unity自带工具导出地图还存在找不到共边的其他问题
				**/
				else if (av1.equal(bv1, precision) && !av2.equal(bv0, precision)
						&& Vector3.relCCW(av1.x, av1.z, av2.x, av2.z, bv0.x, bv0.z) == 0) {
					Vector3 dirVector1 = Vector3.dirVector(av1, av2);
					Vector3 dirVector2 = Vector3.dirVector(av1, bv0);
					edge[0] = av1;
					if (dirVector1.len2() > dirVector2.len2()) {
						edge[1] = bv0;
					}
					edge[1] = av2;
					if (dirVector1.nor().equal(dirVector2.nor(), 0.001f)) {
						return true;
					}
				} else if (!av1.equal(bv1, precision) && av2.equal(bv0, precision)
						&& Vector3.relCCW(av1.x, av1.z, av2.x, av2.z, bv1.x, bv1.z) == 0) {
					Vector3 dirVector1 = Vector3.dirVector(av2, av1);
					Vector3 dirVector2 = Vector3.dirVector(av2, bv1);
					edge[0] = av1;
					if (dirVector1.len2() > dirVector2.len2()) {
						edge[1] = bv1;
					}
					edge[1] = av2;
					if (dirVector1.nor().equal(dirVector2.nor(), 0.001f)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/***
	 * 创建多边形
	 * 
	 * @param scale
	 * @return
	 */
	private List<Polygon> createPolygons(PolygonData polygonData, int scale) {
		Set<Entry<Integer, Set<Integer>>> entrySet = polygonData.getPathPolygonIndexs().entrySet();
		for (Entry<Integer, Set<Integer>> entry : entrySet) {
			Integer key = entry.getKey();
			Set<Integer> value = entry.getValue();
			List<Vector3> points = new ArrayList<>();
			int[] vectorIndexs = new int[value.size()];
			int i = 0;
			for (Integer index : value) {
				points.add(polygonData.getPathVertices()[index]);
				vectorIndexs[i++] = index;
			}
			Polygon polygon = new Polygon(key, points, vectorIndexs);
			if (!polygon.convex) {
				LOGGER.debug("多边形{}不是凸多边形", polygon.toString());
				continue;
			}
			if (LOGGER.isDebugEnabled()) {
				// polygon.print();
				// polygons.forEach(p->{
				// if(p.contains(polygon)) {
				// LOGGER.warn("多边形{}是多边形{}的内嵌多边形",polygon.getIndex(),p.getIndex());
				// }else if(polygon.contains(p)) {
				// LOGGER.warn("多边形{}是多边形{}的内嵌多边形",p.getIndex(),polygon.getIndex());
				// }
				// if(p.intersectsEdge(polygon)) {
				// LOGGER.warn("多边形{}与多边形{}的边相交",p.getIndex(),polygon.getIndex());
				// }
				// });
			}

			polygons.add(polygon);

		}
		return polygons;
	}

	public PolygonData getPolygonData() {
		return polygonData;
	}

	public List<Polygon> getPolygons() {
		return polygons;
	}

	private static Map<Polygon, List<PolygonEdge>> createSharedEdgesMap(Set<IndexConnection> indexConnections,
			List<Polygon> polygons) {

		Map<Polygon, List<PolygonEdge>> connectionMap = new TreeMap<Polygon, List<PolygonEdge>>(
				(o1, o2) -> o1.getIndex() - o2.getIndex());
		// connectionMap.ordered = true;

		for (Polygon polygon : polygons) {
			connectionMap.put(polygon, new ArrayList<PolygonEdge>());
		}

		for (IndexConnection indexConnection : indexConnections) {
			Polygon fromNode = polygons.get(indexConnection.fromPolygonIndex);
			Polygon toNode = polygons.get(indexConnection.toPolygonIndex);
			Vector3 edgeVertexA = indexConnection.edgeVector1;
			Vector3 edgeVertexB = indexConnection.edgeVector2;

			PolygonEdge edge = new PolygonEdge(fromNode, toNode, edgeVertexA, edgeVertexB);
			connectionMap.get(fromNode).add(edge);
			fromNode.connections.add(edge);
		}
		return connectionMap;
	}

	/**
	 * 存储相互连接多边形的关系 Class for storing the edge connection data between two adjacent
	 * triangles.
	 */
	private static class IndexConnection {
		// // The vertex indices which makes up the edge shared between two triangles.
		// int edgeVertexIndex1;
		// int edgeVertexIndex2;
		Vector3 edgeVector1;
		Vector3 edgeVector2;
		// The indices of the two polygon sharing this edge.
		int fromPolygonIndex;
		int toPolygonIndex;

		public IndexConnection(Vector3 edgeVector1, Vector3 edgeVector2, int fromPolygonIndex, int toPolygonIndex) {
			this.edgeVector1 = edgeVector1;
			this.edgeVector2 = edgeVector2;
			this.fromPolygonIndex = fromPolygonIndex;
			this.toPolygonIndex = toPolygonIndex;
		}

		@Override
		public String toString() {
			return "IndexConnection [edgeVector1=" + edgeVector1 + ", edgeVector2=" + edgeVector2
					+ ", fromPolygonIndex=" + fromPolygonIndex + ", toPolygonIndex=" + toPolygonIndex + "]";
		}

	}

}
