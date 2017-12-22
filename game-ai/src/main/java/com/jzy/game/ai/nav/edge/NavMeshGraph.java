package com.jzy.game.ai.nav.edge;
//package com.jzy.game.ai.nav;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * 导航网格图像数据
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午4:43:33
 */
public class NavMeshGraph implements IndexedGraph<Triangle> {

	private final transient NavMeshData navMeshData;
	/** 寻路三角形对应的共享边 */
	private final ArrayMap<Triangle, Array<Edge>> pathSharedEdges;

	public NavMeshGraph(NavMeshData navMeshData) {
		super();
		this.navMeshData = navMeshData;
		// 寻路三角形
		Array<Triangle> pathTriangles = createTriangles(0);
		// 共享的连接边
		Array<IndexConnection> pathIndexConnections = getIndexConnections(
				navMeshData.getPathTriangles().toArray(new Integer[navMeshData.getPathTriangles().size()]));
		// 三角形共享连接边
		pathSharedEdges = createSharedEdgesMap(pathIndexConnections, pathTriangles, navMeshData.getPathVertices());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Array<Connection<Triangle>> getConnections(Triangle fromNode) {
		return (Array<Connection<Triangle>>) (Array<?>) pathSharedEdges.getValueAt(fromNode.index);
	}

	@Override
	public int getIndex(Triangle node) {
		return node.getIndex();
	}

	@Override
	public int getNodeCount() {
		return pathSharedEdges.size;
	}

	public NavMeshData getNavMeshData() {
		return navMeshData;
	}

	/**
	 * 创建
	 * 
	 * @param type
	 *            0寻路 1阻挡
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月7日 下午5:58:20
	 * @return
	 */
	private Array<Triangle> createTriangles(int type) {
		Array<Triangle> triangles = new Array<>();
		List<Integer> vertexIndexs; // 顶点
		List<Vector3> vertices; // 坐标
		if (type == 0) {
			vertexIndexs = navMeshData.getPathTriangles();
			vertices = navMeshData.getPathVertices();
		} else {
			vertexIndexs = navMeshData.getBlockTriangles();
			vertices = navMeshData.getBlockVertices();
		}
		int triangleIndex = 0; // 三角形下标
		for (int i = 0; i < vertexIndexs.size() - 3;) {
			Triangle triangle = new Triangle(vertices.get(vertexIndexs.get(i)), vertices.get(vertexIndexs.get(i + 1)),
					vertices.get(vertexIndexs.get(i + 2)), triangleIndex++);
			i = i + 3;
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
	private static Array<IndexConnection> getIndexConnections(Integer[] indices) {
		Array<IndexConnection> indexConnections = new Array<IndexConnection>();
		indexConnections.ordered = true;
		int[] edge = { -1, -1 };
		short i = 0;
		int j, a0, a1, a2, b0, b1, b2, triAIndex, triBIndex;
		while (i < indices.length) {
			triAIndex = (short) (i / 3);
			a0 = indices[i++];
			a1 = indices[i++];
			a2 = indices[i++];
			j = i;
			while (j < indices.length) {
				triBIndex = (short) (j / 3);
				b0 = indices[j++];
				b1 = indices[j++];
				b2 = indices[j++];
				if (hasSharedEdgeIndices(a0, a1, a2, b0, b1, b2, edge)) {
					indexConnections.add(new IndexConnection(edge[0], edge[1], triAIndex, triBIndex));
					indexConnections.add(new IndexConnection(edge[1], edge[0], triBIndex, triAIndex));
					edge[0] = -1;
					edge[1] = -1;
				}
			}
		}
		return indexConnections;
	}

	/**
	 * Checks if the two triangles have shared vertex indices. The edge will always
	 * follow the vertex winding order of the triangle A. Since all triangles have
	 * the same winding order, triangle A should have the opposite edge direction to
	 * triangle B.
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
	 * Creates a map over each triangle and its Edge connections to other triangles.
	 * Each edge must follow the vertex winding order of the triangle associated
	 * with it. Since all triangles are assumed to have the same winding order, this
	 * means if two triangles connect, each must have its own edge connection data,
	 * where the edge follows the same winding order as the triangle which owns the
	 * edge data.
	 *
	 * @param indexConnections
	 * @param triangles
	 * @param vertexVectors
	 * @return
	 */
	private static ArrayMap<Triangle, Array<Edge>> createSharedEdgesMap(Array<IndexConnection> indexConnections,
			Array<Triangle> triangles, List<Vector3> vertexVectors) {

		ArrayMap<Triangle, Array<Edge>> connectionMap = new ArrayMap<Triangle, Array<Edge>>();
		connectionMap.ordered = true;

		for (Triangle tri : triangles) {
			connectionMap.put(tri, new Array<Edge>());
		}

		for (IndexConnection i : indexConnections) {
			Triangle fromNode = triangles.get(i.fromTriIndex);
			Triangle toNode = triangles.get(i.toTriIndex);
			Vector3 edgeVertexA = vertexVectors.get(i.edgeVertexIndex1);
			Vector3 edgeVertexB = vertexVectors.get(i.edgeVertexIndex2);

			Edge edge = new Edge(fromNode, toNode, edgeVertexA, edgeVertexB);
			connectionMap.get(fromNode).add(edge);
			fromNode.connections.add(edge);
		}
		return connectionMap;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	/**
	 * Class for storing the edge connection data between two adjacent triangles.
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
	}

	public ArrayMap<Triangle, Array<Edge>> getPathSharedEdges() {
		return pathSharedEdges;
	}

	
}
