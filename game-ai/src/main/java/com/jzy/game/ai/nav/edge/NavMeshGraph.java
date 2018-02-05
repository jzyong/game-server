package com.jzy.game.ai.nav.edge;
//package com.jzy.game.ai.nav;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.NavMeshData;
import com.jzy.game.ai.pfa.Connection;
import com.jzy.game.ai.pfa.IndexedGraph;
import com.jzy.game.engine.math.Vector3;

/**
 * 导航网格图像数据 <br>
 * 数据对象预处理
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午4:43:33
 */
public class NavMeshGraph implements IndexedGraph<Triangle> {
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(NavMeshGraph.class);

	private final transient NavMeshData navMeshData;
	private List<Triangle> triangles = new ArrayList<>();

	/** 寻路三角形对应的共享边 */
	private final Map<Triangle, List<Edge>> sharedEdges;
	/** 独立边 */
	private final Map<Triangle, List<Edge>> isolatedEdgesMap;

	private int numDisconnectedEdges; // 不相连边的个数
	private int numConnectedEdges; // 相互连接边的数目
	private int numTotalEdges; // 三角形总边数

	public NavMeshGraph(NavMeshData navMeshData) {
		super();
		this.navMeshData = navMeshData;
		// 寻路三角形
		List<Triangle> pathTriangles = createTriangles();
		// 共享的连接边
		Set<IndexConnection> pathIndexConnections = getIndexConnections(navMeshData.getPathTriangles());
		// 三角形共享连接边
		sharedEdges = createSharedEdgesMap(pathIndexConnections, pathTriangles,
				Arrays.asList(navMeshData.getPathVertices()));
		isolatedEdgesMap = createIsolatedEdgesMap(sharedEdges);

		// Count edges of different types
		for (List<Edge> edges : isolatedEdgesMap.values()) {
			numDisconnectedEdges += edges.size();
		}
		for (List<Edge> edges : sharedEdges.values()) {
			numConnectedEdges += edges.size();
		}
		numConnectedEdges /= 2;
		numTotalEdges = numConnectedEdges + numDisconnectedEdges;
		LOGGER.debug("地图{} 三角形{} 总共边{} 共享边{} 独立边{}", navMeshData.getMapID(),getTriangleCont(), numTotalEdges, numConnectedEdges,
				numDisconnectedEdges);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Connection<Triangle>> getConnections(Triangle fromNode) {
		return (List<Connection<Triangle>>) (List<?>) sharedEdges.get(fromNode);
		// return (Array<Connection<Triangle>>) (Array<?>)
		// sharedEdges.getValueAt(fromNode.index);
	}

	@Override
	public int getIndex(Triangle node) {
		return node.getIndex();
	}

	@Override
	public int getNodeCount() {
		return sharedEdges.size();
	}

	public NavMeshData getNavMeshData() {
		return navMeshData;
	}

	/**
	 * 创建三角形列表
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月7日 下午5:58:20
	 * @return
	 */
	private List<Triangle> createTriangles() {
		int[] vertexIndexs; // 顶点
		Vector3[] vertices; // 坐标
		vertexIndexs = navMeshData.getPathTriangles();
		vertices = navMeshData.getPathVertices();
		int triangleIndex = 0; // 三角形下标
		int length=vertexIndexs.length-3;
		for (int i = 0; i <=length ;) {
			Triangle triangle = new Triangle(vertices[vertexIndexs[i++]], vertices[vertexIndexs[i ++]],
					vertices[vertexIndexs[i ++]], triangleIndex++);
			triangles.add(triangle);
		}

		return triangles;
	}

	/**
	 * 获得三角形顶点坐标的共享边
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月8日 下午4:12:33
	 * @param indices
	 *            顶点下标列表
	 * @return
	 */
	private static Set<IndexConnection> getIndexConnections(int[] indices) {
		Set<IndexConnection> indexConnections = new TreeSet<IndexConnection>(new Comparator<IndexConnection>() {

			@Override
			public int compare(IndexConnection o1, IndexConnection o2) {
				if(o1.fromTriIndex>o2.fromTriIndex) {
					return 1;
				}else if(o1.fromTriIndex<o2.fromTriIndex) {
					return -1;
				}else {
//					if(o1.toTriIndex>o2.toTriIndex) {
//						return 1;
//					}else if(o1.toTriIndex<o2.toTriIndex) {
//						return -1;
//					}
				}
				return 0;
			}
		});
		int[] edge = { -1, -1 };
		short i = 0;
		int j, a0, a1, a2, b0, b1, b2, triAIndex, triBIndex;
		while (i < indices.length) {
			triAIndex = (short) (i / 3); // A三角形编号
			a0 = indices[i++];
			a1 = indices[i++];
			a2 = indices[i++];
			j = i;
			while (j < indices.length) {
				
				triBIndex = (short) (j / 3); // B三角形编号
				b0 = indices[j++];
				b1 = indices[j++];
				b2 = indices[j++];
				if(triAIndex==triBIndex) {
//					j+=3;
					continue;
				}
				if (hasSharedEdgeIndices(a0, a1, a2, b0, b1, b2, edge)) {
					IndexConnection indexConnection1 = new IndexConnection(edge[0], edge[1], triAIndex, triBIndex);
					IndexConnection indexConnection2 = new IndexConnection(edge[1], edge[0], triBIndex, triAIndex);
					indexConnections.add(indexConnection1);
					indexConnections.add(indexConnection2);
					edge[0] = -1;
					edge[1] = -1;
					LOGGER.debug("共享边：{} -> {}",indexConnection1.toString(),indexConnection2.toString());
				}
			}
		}
		LOGGER.debug("连接个数：{}",indexConnections.size());
		return indexConnections;
	}

	/**
	 * 检测是否有共享边 Checks if the two triangles have shared vertex indices. The edge
	 * will always follow the vertex winding order of the triangle A. Since all
	 * triangles have the same winding order, triangle A should have the opposite
	 * edge direction to triangle B.
	 *
	 * @param a0
	 *            Vertex index on triangle A
	 * @param a1
	 * @param a2
	 * @param b0
	 *            Vertex index on triangle B
	 * @param b1
	 * @param b2
	 * @param edge
	 *            Output, the indices of the shared vertices in the winding order of
	 *            triangle A.
	 * @return True if the triangles share an edge.
	 */
	private static boolean hasSharedEdgeIndices(int a0, int a1, int a2, int b0, int b1, int b2, int[] edge) {
		boolean match0 = (a0 == b0 || a0 == b1 || a0 == b2);
		boolean match1 = (a1 == b0 || a1 == b1 || a1 == b2);
		if (!match0 && !match1) { // 无两个共享点
			return false;
		} else if (match0 && match1) {
			edge[0] = a0;
			edge[1] = a1;
			return true;
		}
		boolean match2 = (a2 == b0 || a2 == b1 || a2 == b2);
		if (match0 && match2) {
			edge[0] = a2;
			edge[1] = a0;
			return true;
		} else if (match1 && match2) {
			edge[0] = a1;
			edge[1] = a2;
			return true;
		}
		return false;
	}

	/**
	 * 创建每个三角形的共享边列表 Creates a map over each triangle and its Edge connections to
	 * other triangles. Each edge must follow the vertex winding order of the
	 * triangle associated with it. Since all triangles are assumed to have the same
	 * winding order, this means if two triangles connect, each must have its own
	 * edge connection data, where the edge follows the same winding order as the
	 * triangle which owns the edge data.
	 *
	 * @param indexConnections
	 * @param triangles
	 * @param vertexVectors
	 * @return
	 */
	private static Map<Triangle, List<Edge>> createSharedEdgesMap(Set<IndexConnection> indexConnections,
			List<Triangle> triangles, List<Vector3> vertexVectors) {

		Map<Triangle, List<Edge>> connectionMap = new TreeMap<Triangle, List<Edge>>((o1,o2)->o1.getIndex()-o2.getIndex());
		// connectionMap.ordered = true;

		for (Triangle tri : triangles) {
			connectionMap.put(tri, new ArrayList<Edge>());
		}

		for (IndexConnection indexConnection : indexConnections) {
			Triangle fromNode = triangles.get(indexConnection.fromTriIndex);
			Triangle toNode = triangles.get(indexConnection.toTriIndex);
			Vector3 edgeVertexA = vertexVectors.get(indexConnection.edgeVertexIndex1);
			Vector3 edgeVertexB = vertexVectors.get(indexConnection.edgeVertexIndex2);

			Edge edge = new Edge(fromNode, toNode, edgeVertexA, edgeVertexB);
			connectionMap.get(fromNode).add(edge);
			fromNode.connections.add(edge);
			LOGGER.debug("三角形：{} -->{}   {}-->{}",fromNode.getIndex(),toNode.getIndex(),fromNode.toString(),toNode.toString());
		}
		return connectionMap;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	/**
	 * 存储相互连接三角形的关系 Class for storing the edge connection data between two adjacent
	 * triangles.
	 */
	private static class IndexConnection {
		// The vertex indices which makes up the edge shared between two triangles.
		int edgeVertexIndex1;
		int edgeVertexIndex2;
		// The indices of the two triangles sharing this edge.
		int fromTriIndex;
		int toTriIndex;

		public IndexConnection(int sharedEdgeVertex1Index, int edgeVertexIndex2, int fromTriIndex, int toTriIndex) {
			this.edgeVertexIndex1 = sharedEdgeVertex1Index;
			this.edgeVertexIndex2 = edgeVertexIndex2;
			this.fromTriIndex = fromTriIndex;
			this.toTriIndex = toTriIndex;
		}

		@Override
		public String toString() {
			return "IndexConnection [edgeVertexIndex1=" + edgeVertexIndex1 + ", edgeVertexIndex2=" + edgeVertexIndex2
					+ ", fromTriIndex=" + fromTriIndex + ", toTriIndex=" + toTriIndex + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + edgeVertexIndex1;
			result = prime * result + edgeVertexIndex2;
			result = prime * result + fromTriIndex;
			result = prime * result + toTriIndex;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IndexConnection other = (IndexConnection) obj;
			if (edgeVertexIndex1 != other.edgeVertexIndex1)
				return false;
			if (edgeVertexIndex2 != other.edgeVertexIndex2)
				return false;
			if (fromTriIndex != other.fromTriIndex)
				return false;
			if (toTriIndex != other.toTriIndex)
				return false;
			return true;
		}
		
		
	}

	public Map<Triangle, List<Edge>> getPathSharedEdges() {
		return sharedEdges;
	}

	/**
	 * 获取所有三角形列表
	 * 
	 * @return
	 */
	public List<Triangle> getTriangles() {
		return triangles;
	}

	/**
	 * 创建有一条边与其他三角形无连接的边关系 Map the isolated edges for each triangle which does not
	 * have all three edges connected to other triangles.
	 *
	 * @param connectionMap
	 * @return
	 */
	private static Map<Triangle, List<Edge>> createIsolatedEdgesMap(Map<Triangle, List<Edge>> connectionMap) {

		Map<Triangle, List<Edge>> disconnectionMap = new HashMap<Triangle, List<Edge>>();

		for (Triangle tri : connectionMap.keySet()) {
			List<Edge> connectedEdges = connectionMap.get(tri);

			List<Edge> disconnectedEdges = new ArrayList<Edge>();
			disconnectionMap.put(tri, disconnectedEdges);

			if (connectedEdges.size() < 3) {
				// This triangle does not have all edges connected to other triangles
				boolean ab = true;
				boolean bc = true;
				boolean ca = true;
				for (Edge edge : connectedEdges) {
					if (edge.rightVertex == tri.a && edge.leftVertex == tri.b)
						ab = false;
					else if (edge.rightVertex == tri.b && edge.leftVertex == tri.c)
						bc = false;
					else if (edge.rightVertex == tri.c && edge.leftVertex == tri.a)
						ca = false;
				}
				if (ab)
					disconnectedEdges.add(new Edge(tri, null, tri.a, tri.b));
				if (bc)
					disconnectedEdges.add(new Edge(tri, null, tri.b, tri.c));
				if (ca)
					disconnectedEdges.add(new Edge(tri, null, tri.c, tri.a));
			}
			int totalEdges = (connectedEdges.size() + disconnectedEdges.size());
			if (totalEdges != 3) {
				LOGGER.debug("Wrong number of edges (" + totalEdges + ") in triangle " + tri.getIndex());
			}
		}
		return disconnectionMap;
	}

	public int getNumDisconnectedEdges() {
		return numDisconnectedEdges;
	}

	public int getNumConnectedEdges() {
		return numConnectedEdges;
	}

	public int getNumTotalEdges() {
		return numTotalEdges;
	}

	public int getTriangleCont() {
		return triangles.size();
	}
}
