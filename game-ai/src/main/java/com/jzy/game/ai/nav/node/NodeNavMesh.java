package com.jzy.game.ai.nav.node;

import java.awt.Toolkit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.NavMesh;
import com.jzy.game.ai.nav.NavMeshData;
import com.jzy.game.engine.math.MathUtil;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.TimeUtil;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.shape.random.RandomPointsBuilder;

/**
 * 寻路navmesh地图 <br>
 * 数据分为阻挡层和行走层,以三角形网格数据寻路
 *
 * @author JiangZhiYong
 */
public class NodeNavMesh extends NavMesh implements Serializable, Cloneable {

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeNavMesh.class);
	private static final long serialVersionUID = 1L;

	/**
	 * 是否为编辑器模式true会被缩放
	 */
	private final boolean editor;

	private float startX;
	private float startZ;
	private float endX;
	private float endZ;
	/**
	 * 缩放比例
	 */
	private float scale;
	/**
	 * 三角形顶点之间的最大距离
	 */
	private float maxDistanceBetweenNode;

	/**
	 * 阻挡，不可行走区域
	 */
	private KNodeConnector<TriangleBlock> blockConnector = new KNodeConnector<>();
	private final List<TriangleBlock> blockAreas = new ArrayList<>();

	/**
	 * 心走区，寻路层
	 */
	private KNodeConnector<TriangleBlock> pathConnector = new KNodeConnector<>();
	private List<TriangleBlock> pathAreas = new ArrayList<>();

	/**
	 * 多边形转换工具
	 */
	private PolygonConverter polygonConverter = new PolygonConverter();

	/**
	 * A*算法寻路
	 */
	private PathFinder pathFinder = new PathFinder();

	/**
	 * @param filePath
	 *            文件路径
	 */
	public NodeNavMesh(String filePath) {
		this(filePath, false);
	}

	/**
     * @param filePath 文件路径
     * @param editor 是否为编辑器模式true会被缩放
     */
    public NodeNavMesh(String filePath, boolean editor) {
        LOGGER.info("ss");
        this.editor = editor;
        String txtFile = FileUtil.readTxtFile(filePath);
        if (txtFile == null) {
            LOGGER.warn("navmesh数据{}读取失败", filePath);
            return;
        }
        NavMeshData data = JSON.parseObject(txtFile, NavMeshData.class);
        this.width = Math.abs(data.getEndX() - data.getStartX());
        this.height = Math.abs(data.getEndZ() - data.getStartZ());
        this.startX = data.getStartX();
        this.startZ = data.getStartZ();
        this.endX = data.getEndX();
        this.endZ = data.getEndZ();
        this.mapId = data.getMapID();
        LOGGER.info("加载地图{}宽{}高{}，开始坐标({},{}),结束坐标({},{})", mapId, width, height, startX, startZ, endX, endZ);
        if (this.editor) {
        	 double w=Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.7;
             double h=Toolkit.getDefaultToolkit().getScreenSize().getHeight()*0.7;
            scale=(float)Math.min(w/width, h/height);
        } else {
            scale = 1;
        }
        // TODO 是否应该取对角线？？？
        maxDistanceBetweenNode = Math.max(width, height) * scale;

        
        createPolygons(blockConnector, blockAreas, data.getBlockTriangles(), data.getBlockVertices(), true);
        
        
        createPolygons(pathConnector, pathAreas, data.getPathTriangles(), data.getPathVertices(), false);
    }

	/**
	 * 创建三角形形 ，地图数据预处理 <br>
	 *
	 * @param nodeConnector
	 *            节点连接关系处理器
	 * @param triangleBlocks
	 *            三角形连接关系
	 * @param triangles
	 *            三角形顶点需要，依次三个序号为一组
	 * @param vertices
	 *            顶点坐标
	 * @param bufferOuter
	 *            TriangleConnection ture 非行走区原始多边缓存为inner多边形，外多边形为包围该三角形形的六边形 false
	 *            outer多边形，内外相同
	 * @param editor
	 *            true为编辑器模式
	 */
	private final void createPolygons(KNodeConnector<TriangleBlock> nodeConnector, List<TriangleBlock> triangleBlocks,
			int[] triangles, Vector3[] vertices, boolean bufferOuter) {
		if (triangles == null || vertices == null) {
			LOGGER.warn("数据异常，数据为空");
			return;
		}

		// 坐标缩放
		if (editor) {
			for (Vector3 vector : vertices) {
				vector.addX(-this.startX);
				vector.addZ(-this.startZ);
				vector.scl(scale);
			}
		}

		// 三角形内创建随机点
		RandomPointsBuilder randomPointsBuilder = new RandomPointsBuilder();

		List<Vector3> list = new ArrayList<>(3); // 缓存三角形顶点坐标
		for (int i = 0; i < triangles.length; i += 3) { // 每三个连续顶点确定一个三角形
			if (triangles.length <= i + 2) { // 跳过多余点
				break;
			}
			list.clear();
			list.add(vertices[triangles[i]]);
			list.add(vertices[triangles[i + 1]]);
			list.add(vertices[triangles[i + 2]]);

			KPolygon kPolygon = new KPolygon(list);
			Polygon polygon = polygonConverter.makePolygonFrom(kPolygon);
			if (polygon == null) {
				continue;
			}
			TriangleBlock triangleBlock = null;
			if (bufferOuter) { // 不可行走区
				triangleBlock = TriangleBlock.createBlockFromInnerPolygon(kPolygon);
			} else { // 可行走区
				triangleBlock = TriangleBlock.createBlockFromOuterPolygon(kPolygon);
			}
			if (triangleBlock == null) {
				continue;
			}

			int randomPointNum = 1;
			if (editor) {
				randomPointNum = (int) ((Math.sqrt(polygon.getArea() / scale / scale) + 1) * 5);
			} else {
				randomPointNum = (int) ((Math.sqrt(polygon.getArea()) + 1) * 5);
			}
			// System.out.println("随机点数:" + randomPointNum);
			randomPointsBuilder.setNumPoints(randomPointNum);
			randomPointsBuilder.setExtent(polygon);
			// 生成三角形快中的随机坐标
			randomPointsBuilder.setExtent(polygon);
			triangleBlock.addRandomPoints(randomPointsBuilder.getGeometry().getCoordinates());
			triangleBlocks.add(triangleBlock);
			nodeConnector.calculateKNodeConnection(triangleBlock, triangleBlocks, maxDistanceBetweenNode);
		}
	}

	/**
	 * 添加一个阻挡
	 *
	 * @param vertices
	 * @return
	 */
	public final TriangleBlock addBlock(ArrayList<Vector3> vertices) {
		return addBlock(vertices, true, false);
	}

	/**
	 * 添加一个阻挡
	 *
	 * @param vertices
	 * @param bufferOuter
	 * @param editor
	 * @return
	 */
	public final TriangleBlock addBlock(List<Vector3> vertices, boolean bufferOuter, boolean editor) {

		if (vertices == null || vertices.isEmpty()) {
			return null;
		}

		if (editor) {
			for (Vector3 li : vertices) {
				li.scl(scale);
			}
		}

		KPolygon kPolygon = new KPolygon(vertices);
		Polygon polygon = polygonConverter.makePolygonFrom(kPolygon);
		if (polygon == null) {
			return null;
		}

		TriangleBlock block = null;

		if (bufferOuter) {
			block = TriangleBlock.createBlockFromInnerPolygon(kPolygon);
		} else {
			block = TriangleBlock.createBlockFromOuterPolygon(kPolygon);
		}

		if (block == null) {
			return null;
		}
		// 三角形内创建随机点
		RandomPointsBuilder randomPointsBuilder = new RandomPointsBuilder();
		int randomPointNum = 1;
		if (editor) {
			randomPointNum = (int) ((Math.sqrt(polygon.getArea() / scale / scale) + 1) * 5);
		} else {
			randomPointNum = (int) ((Math.sqrt(polygon.getArea()) + 1) * 5);
		}
		System.out.println("随机点数:" + randomPointNum);
		randomPointsBuilder.setNumPoints(randomPointNum);
		randomPointsBuilder.setExtent(polygon);
		block.addRandomPoints(randomPointsBuilder.getGeometry().getCoordinates());
		synchronized (blockAreas) {
			this.blockAreas.add(block);
			this.blockConnector.calculateKNodeConnection(block, this.blockAreas, this.maxDistanceBetweenNode);
		}

		return block;
	}

	/**
	 * 删除一个阻挡,块
	 *
	 * @param block
	 */
	public final void removeBlock(TriangleBlock block) {
		if (block != null) {
			synchronized (blockAreas) {
				this.blockAreas.remove(block);
				this.blockConnector.removeBlock(block, maxDistanceBetweenNode, blockAreas);
			}
		}
	}

	/**
	 * 获取矩形
	 *
	 * @param position
	 *            当前位置
	 * @param distance
	 *            距离
	 * @param sourceDirection
	 *            当前方向，注意是unity的方向
	 * @param width
	 * @param height
	 * @return
	 */
	public final KPolygon getKPolygon(Vector3 position, float distance, Vector3 sourceDirection, float width,
			float height) {
		Vector3 source = position.unityTranslate(sourceDirection, 0, distance);
		Vector3 corner_1 = source.unityTranslate(sourceDirection, -90, width / 2);
		Vector3 corner_2 = source.unityTranslate(sourceDirection, 90, width / 2);
		Vector3 corner_3 = corner_2.unityTranslate(sourceDirection, 0, height);
		Vector3 corner_4 = corner_1.unityTranslate(sourceDirection, 0, height);
		List<Vector3> sectors = new ArrayList<>(4);
		sectors.add(corner_1);
		sectors.add(corner_4);
		sectors.add(corner_3);
		sectors.add(corner_2);
		return new KPolygon(sectors);
	}

	/**
	 * 根据当前位置获取扇形
	 *
	 * @param position
	 * @param sourceDirection
	 * @param distance
	 * @param radius
	 * @param degrees
	 * @return
	 */
	public final KPolygon getKPolygon(Vector3 position, Vector3 sourceDirection, float distance, float radius,
			float degrees) {
		Vector3 source = position.unityTranslate(sourceDirection, 0, distance);
		Vector3 forward_l = position.unityTranslate(sourceDirection, -degrees / 2, radius);
		Vector3 forward_r = position.unityTranslate(sourceDirection, degrees / 2, radius);
		List<Vector3> sectors = new ArrayList<>(4);
		sectors.add(source);
		sectors.add(forward_l);
		int size = (int) (degrees / 10) / 2 - 1;
		for (int i = -size; i <= size; i++) {
			Vector3 forward = position.unityTranslate(sourceDirection, i * 10, radius);
			sectors.add(forward);
		}
		sectors.add(forward_r);
		return new KPolygon(sectors);
	}

	/**
	 * 根据半径获取一个多边形,度数随机
	 *
	 * @param center
	 * @param radius
	 * @param vertexCount
	 *            订点数
	 * @return
	 */
	public final KPolygon getKPolygon(Vector3 center, float radius, int vertexCount) {
		if (vertexCount < 3) {
			vertexCount = 3;
		}
		List<Vector3> sectors = new ArrayList<>(vertexCount);
		double degrees = 360d / vertexCount;
		Random random = new Random(TimeUtil.currentTimeMillis());
		double randomDegrees = random.nextFloat() * 360;
		for (int i = 0; i < vertexCount; i++) {
			Vector3 source = center.translateCopy(i * degrees + randomDegrees, radius);
			sectors.add(source);
		}
		return new KPolygon(sectors);
	}

	/**
	 * 寻路
	 *
	 * @param start
	 *            开始坐标
	 * @param end
	 *            结束坐标
	 * @return
	 */
	public PathData path(Vector3 start, Vector3 end) {
		PathData data;
		synchronized (pathFinder) {
			data = pathFinder.calc(start, end, this.maxDistanceBetweenNode, this.blockConnector, this.blockAreas);
		}
		return data;
	}

	/**
	 * 获取行走区域的点
	 *
	 * @param x
	 * @param z
	 * @return
	 */
	public Vector3 getPointInPaths(float x, float z) {
		Vector3 movedPoint = new Vector3(x, z);
		for (TriangleBlock block : this.pathAreas) {
			if (block.getInnerPolygon().contains(movedPoint)) {
				KPolygon poly = block.getInnerPolygon();
				if (poly != null) {
					movedPoint.y = poly.getY();
					return movedPoint;
				}
			}
		}
		return null;
	}

	/**
	 * 在路径中获取随机点
	 *
	 * @param center
	 *            中心点
	 * @param radius
	 *            半径
	 * @param minDistanceToCenter
	 *            离中心点的最小距离
	 * @return
	 */
	public Vector3 getRandomPointInPaths(Vector3 center, float radius, float minDistanceToCenter) {
		List<TriangleBlock> list = new ArrayList<>();
		float dis2 = radius * radius + minDistanceToCenter;
		double dis = 0;
		for (TriangleBlock block : this.pathAreas) {
			if (block.getInnerPolygon().contains(center)) {
				list.add(block);
			} else {
				for (Vector3 point : block.getInnerPolygon().getPoints()) {
					dis = point.distanceSqlt2D(center);
					if (dis <= dis2 && dis >= minDistanceToCenter) {
						list.add(block);
						break;
					}
				}
			}
		}
		ArrayList<Vector3> targets = new ArrayList<>();
		for (TriangleBlock pb : list) {
			for (Vector3 p : pb.getRandomPoints()) {
				if (p.distanceSqlt2D(center) <= dis2) {
					targets.add(p);
				}
			}
		}
		Vector3 point = MathUtil.random(targets);
		if (point == null) {
			return center;
		}
		return point;
	}

	/**
	 * 点是否在路径中
	 *
	 * @param movedPoint
	 * @return
	 */
	public boolean isPointInPaths(Vector3 movedPoint) {
		return isPointInPaths(movedPoint.x, movedPoint.z);
	}

	/**
	 * 点是否在路径中
	 *
	 * @param x
	 * @param z
	 * @return
	 */
	public boolean isPointInPaths(double x, double z) {
		for (TriangleBlock block : this.pathAreas) {
			if (block.getInnerPolygon().contains(x, z)) {
				KPolygon poly = block.getInnerPolygon();
				if (poly != null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 点是否在阻挡中
	 *
	 * @param movedPoint
	 * @return
	 */
	public boolean isPointInBlocks(Vector3 movedPoint) {
		return isPointInBlocks(movedPoint.x, movedPoint.z);
	}

	/**
	 * 点是否在阻挡中
	 *
	 * @param x
	 * @param z
	 * @return
	 */
	public boolean isPointInBlocks(double x, double z) {
		for (TriangleBlock block : this.blockAreas) {
			if (block.getInnerPolygon().contains(x, z)) {
				KPolygon poly = block.getInnerPolygon();
				if (poly != null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 修正y坐标，即高度
	 *
	 * @param movedPoint
	 */
	public void amendPointY(Vector3 movedPoint) {
		for (TriangleBlock block : this.pathAreas) {
			if (block.getInnerPolygon().contains(movedPoint)) {
				KPolygon poly = block.getInnerPolygon();
				if (poly != null) {
					movedPoint.y = poly.getY();
					break;
				}
			}
		}
	}

	/**
	 * 获取移动点所在的多边形
	 *
	 * @param movedPoint
	 * @return
	 */
	public KPolygon getPolygonInPaths(Vector3 movedPoint) {
		for (TriangleBlock block : this.pathAreas) {
			if (block.getInnerPolygon().contains(movedPoint)) {
				KPolygon poly = block.getInnerPolygon();
				if (poly != null) {
					return poly;
				}
			}
		}
		return null;
	}

	/**
	 * 在阻挡中获取路径中附近的点
	 *
	 * @param point
	 *            阻挡中的点
	 * @return
	 */
	public Vector3 getNearestPointInPaths(Vector3 point) {
		Vector3 movedPoint = point.copy();
		boolean targetIsInsideBlock = false;
		int count = 0;
		while (true) {
			for (TriangleBlock block : this.blockAreas) {
				if (block.getOuterPolygon().contains(movedPoint)) {
					targetIsInsideBlock = true;
					KPolygon poly = block.getOuterPolygon();
					Vector3 p = poly.getBoundaryPointClosestTo(movedPoint);
					if (p != null) {
						movedPoint.x = p.x;
						movedPoint.z = p.z;
					}
				}
			}
			count++;
			if (targetIsInsideBlock == false || count >= 3) {
				break;
			}
		}
		return movedPoint;
	}

	public List<TriangleBlock> getBlockAreas() {
		return blockAreas;
	}

	public List<TriangleBlock> getPathAreas() {
		return pathAreas;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getMapId() {
		return mapId;
	}

	public float getStartX() {
		return startX;
	}

	public float getStartZ() {
		return startZ;
	}

	public float getEndX() {
		return endX;
	}

	public float getEndZ() {
		return endZ;
	}

	public float getScale() {
		return scale;
	}

}
