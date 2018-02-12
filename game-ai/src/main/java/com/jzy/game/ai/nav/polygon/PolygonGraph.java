package com.jzy.game.ai.nav.polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.ai.nav.triangle.TriangleData;
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
	private static final Logger LOGGER=LoggerFactory.getLogger(PolygonGraph.class);

	private List<Polygon> polygons = new ArrayList<>();

	private Map<Polygon, List<PolygonEdge>> sharedEdges;
	private Map<Polygon, List<PolygonEdge>> isolatedEdges;
	private Set<IndexConnection> indexConnections = new HashSet<>();

	private int numDisconnectedEdges; // 不相连边的个数
	private int numConnectedEdges; // 相互连接边的数目
	private int numTotalEdges; // 总边数
	
	private PolygonData polygonData;

	/**
	 * 需要将三角形数据合并为多边形数据
	 * 
	 * @param triangleData
	 * @param scale
	 */
	public PolygonGraph(TriangleData triangleData, int scale) {
		triangleData.check(scale);
		PolygonData polygonData = triangleConvertPolygon(triangleData);
		calculateIndexConnections(polygonData.getPathPolygons());
		for(int i=0;i<20;i++) {
			mergePolygon(polygonData);
		}
		
		initCalculate(polygonData, scale);
	}

	public PolygonGraph(PolygonData polygonData, int scale) {
		initCalculate(polygonData, scale);
	}
	
	/**
	 * 初始化计算
	 * @param polygonData
	 * @param scale
	 */
	private void initCalculate(PolygonData polygonData, int scale) {
		createPolygons(polygonData, scale);
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
	 * 三角形数据转换为多边形数据
	 * 
	 * @param triangleData
	 * @return
	 */
	public PolygonData triangleConvertPolygon(TriangleData triangleData) {
		PolygonData polygonData = new PolygonData();
		polygonData.setMapID(triangleData.getMapID());
		polygonData.setEndX(triangleData.getEndX());
		polygonData.setEndZ(triangleData.getEndZ());
		polygonData.setHeight(triangleData.getHeight());
		polygonData.setWidth(triangleData.getWidth());
		polygonData.setPathVertices(triangleData.getPathVertices());
		polygonData.setStartX(triangleData.getStartX());
		polygonData.setEndZ(triangleData.getEndZ());
		int[] pathTriangles = triangleData.getPathTriangles();
		int[][] pathPolygons = new int[pathTriangles.length / 3][3];
		for (int i = 0; i < pathTriangles.length;) {
			int polygonIndex = i / 3;
			pathPolygons[polygonIndex][0] = pathTriangles[i++];
			pathPolygons[polygonIndex][1] = pathTriangles[i++];
			pathPolygons[polygonIndex][2] = pathTriangles[i++];

		}
		polygonData.setPathPolygons(pathPolygons);
		return polygonData;
	}

	/**
	 * 合并多边形 <br>
//	 * TODO 算法优化？？ 两个多边形合成一个多边形的判断？
	 * 
	 * @param polygonData
	 */
	public void mergePolygon(PolygonData polygonData) {
		// TODO
		Iterator<IndexConnection> iterator = indexConnections.iterator();
		while (iterator.hasNext()) {
			IndexConnection indexConnection = iterator.next();
			// b合并到a
			int[] aIndex = polygonData.getPathPolygons()[indexConnection.fromPolygonIndex];
			int[] bIndex = polygonData.getPathPolygons()[indexConnection.toPolygonIndex];
			if (bIndex == null || aIndex == null) {
				continue;
			}
			
			
			// TODO 顶点共线问题？
			
			//TODO 顶点重组问题？？？
			List<Integer> vectorIndexs = new ArrayList<>();
			for (int i = 0; i < aIndex.length; i++) {
				if (!vectorIndexs.contains(aIndex[i])) {
					// 顶点不是共享顶点
					if (aIndex[i] != indexConnection.edgeVertexIndex1
							&& aIndex[i] != indexConnection.edgeVertexIndex2) {
						vectorIndexs.add(aIndex[i]);
						
					} 
					
//					//第一位为起始共享，将a全部先先加入，在全部加入b
//					if(i==0&&(aIndex[i] == indexConnection.edgeVertexIndex1
//							|| aIndex[i] == indexConnection.edgeVertexIndex2)) {
//						for (int n = 0; n < aIndex.length; n++) {
//							vectorIndexs.add(aIndex[n]);
//						}
//						for (int m = 0; m < bIndex.length; m++) {
//							if(!vectorIndexs.contains(bIndex[m])){
//								vectorIndexs.add(bIndex[m]);
//							}
//						}
//						
//						break;
//					}
					
					
					// 第二位置开始始共享点，将b所有顶点加入
					if (/*i==1&&*/(aIndex[i] == indexConnection.edgeVertexIndex1
							|| aIndex[i] == indexConnection.edgeVertexIndex2)) {
						for (int m = 0; m < bIndex.length; m++) {
							vectorIndexs.add(bIndex[m]);
						}
					}
				}
			}
			
			//调式
			for(int i=0;i<aIndex.length;i++) {
				LOGGER.debug("A:"+aIndex[i]);
			}
			for(int i=0;i<bIndex.length;i++) {
				LOGGER.debug("B:"+bIndex[i]);
			}
			LOGGER.debug("C:{}",vectorIndexs.toString());

			List<Vector3> list = new ArrayList<>();
			vectorIndexs.forEach(i -> {
				list.add(polygonData.getPathVertices()[i]);
			});

			Polygon polygon = new Polygon(indexConnection.fromPolygonIndex, list,null);
			// 是多边形
			if (polygon.calculateIsConvex()) {
				LOGGER.debug("合并多边形:{}",indexConnection.toString());
				iterator.remove(); // 移除该共享边

				// 设置新的坐标序号
				int[] newIndex = new int[vectorIndexs.size()];
				for (int i = 0; i < vectorIndexs.size(); i++) {
					newIndex[i] = vectorIndexs.get(i);
				}
				polygonData.getPathPolygons()[indexConnection.fromPolygonIndex] = newIndex;
				polygonData.getPathPolygons()[indexConnection.toPolygonIndex] = null;
				
			}else {
				LOGGER.debug("共享边{} 多边形{} 是凹多边形",indexConnection.toString(),polygon.toString());
			}
			System.out.println("\r\n");
		}

	}

	/**
	 * 计算共享边
	 * 
	 * @note 两个多边形只存一个共享边
	 * @param indices
	 */
	private void calculateIndexConnections(int[][] indices) {
		int i = 0, j = 0;
		int[] edge = { -1, -1 };
		while (i < indices.length) {
			int[] polygonAIndex = indices[i];
			j = i;
			while (j < indices.length) {
				int[] polygonBIndex = indices[j];
				if (hasSharedEdgeIndices(polygonAIndex, polygonBIndex, edge)) {
					IndexConnection indexConnection1 = new IndexConnection(edge[0], edge[1], i, j);
					// IndexConnection indexConnection2 = new IndexConnection(edge[1], edge[0], j,
					// i);
					indexConnections.add(indexConnection1);
					// indexConnections.add(indexConnection2);
					edge[0] = -1;
					edge[1] = -1;
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
	 * @return
	 */
	private boolean hasSharedEdgeIndices(int[] polygonAIndex, int[] polygonBIndex, int[] edge) {
		int aLength = polygonAIndex.length;
		int bLength = polygonBIndex.length;
		for (int i = 0; i < polygonAIndex.length; i++) {
			for (int j = 0; j < polygonBIndex.length; j++) {
				// 相同边顶点序号相反
				if (polygonAIndex[i] == polygonBIndex[(j + 1) % bLength]
						&& polygonAIndex[(i + 1) % aLength] == polygonBIndex[j]) {
					edge[0] = polygonAIndex[i];
					edge[1] = polygonAIndex[(i + 1) % aLength];
					return true;
				}else if(polygonAIndex[i]==polygonBIndex[j]&&(j>0&&polygonAIndex[(i + 1) % aLength]==polygonBIndex[(j-1)%bLength])) {
					edge[0] = polygonAIndex[i];
					edge[1] = polygonAIndex[(i + 1) % aLength];
				}
			}
		}
		return false;
	}
	
	
	/***
	 * 创建多边形
	 * @param scale
	 * @return
	 */
	private List<Polygon> createPolygons(PolygonData polygonData, int scale){
		int polygonIndex=0;
		for(int i=0;i<polygonData.getPathPolygons().length;i++) {
			int[] vectorIndexs = polygonData.getPathPolygons()[i];
			if(vectorIndexs==null) {
				continue;
			}
			List<Vector3> points=new ArrayList<>();
			for(int j=0;j<vectorIndexs.length;j++) {
				points.add(polygonData.getPathVertices()[vectorIndexs[j]]);
			}
			
			Polygon polygon=new Polygon(polygonIndex++, points,vectorIndexs);
			polygons.add(polygon);
		}
		this.polygonData=polygonData;
		return polygons;
	}
	
	
	public PolygonData getPolygonData() {
		return polygonData;
	}


	public List<Polygon> getPolygons() {
		return polygons;
	}




	/**
	 * 存储相互连接多边形的关系 Class for storing the edge connection data between two adjacent
	 * triangles.
	 */
	private static class IndexConnection {
		// The vertex indices which makes up the edge shared between two triangles.
		int edgeVertexIndex1;
		int edgeVertexIndex2;
		// The indices of the two polygon sharing this edge.
		int fromPolygonIndex;
		int toPolygonIndex;

		public IndexConnection(int sharedEdgeVertex1Index, int edgeVertexIndex2, int fromPolygonIndex,
				int toPolygonIndex) {
			this.edgeVertexIndex1 = sharedEdgeVertex1Index;
			this.edgeVertexIndex2 = edgeVertexIndex2;
			this.fromPolygonIndex = fromPolygonIndex;
			this.toPolygonIndex = toPolygonIndex;
		}

		@Override
		public String toString() {
			return "IndexConnection [edgeVertexIndex1=" + edgeVertexIndex1 + ", edgeVertexIndex2=" + edgeVertexIndex2
					+ ", fromTriIndex=" + fromPolygonIndex + ", toTriIndex=" + toPolygonIndex + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + edgeVertexIndex1;
			result = prime * result + edgeVertexIndex2;
			result = prime * result + fromPolygonIndex;
			result = prime * result + toPolygonIndex;
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
			if (fromPolygonIndex != other.fromPolygonIndex)
				return false;
			if (toPolygonIndex != other.toPolygonIndex)
				return false;
			return true;
		}

	}

}
