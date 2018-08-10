package com.jzy.game.ai.quadtree.polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.ai.nav.polygon.Polygon;
import com.jzy.game.ai.nav.polygon.PolygonData;
import com.jzy.game.ai.quadtree.Data;
import com.jzy.game.ai.quadtree.Func;
import com.jzy.game.ai.quadtree.Node;
import com.jzy.game.ai.quadtree.NodeType;
import com.jzy.game.ai.quadtree.QuadTree;
import com.jzy.game.ai.quadtree.QuadTreeException;
import com.jzy.game.ai.quadtree.point.PointData;
import com.jzy.game.ai.quadtree.point.PointQuadTree;
import com.jzy.game.engine.math.Vector3;

/**
 * 多边形定制四叉树,用于快速判断一个坐标点位于哪个多边形中 <br>
 * 多边形和和象限相交,包含与被包含者插入
 * 
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PolygonGuadTree extends QuadTree<Vector3, Polygon> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PolygonGuadTree.class);

	private int depth = 5;
	private int items = 10;
	/** 真实个数，包含重复多边形 */
	private int realCount;

	public PolygonGuadTree(float minX, float minZ, float maxX, float maxZ) {
		super(minX, minZ, maxX, maxZ);
	}

	/**
	 * 
	 * @param minX
	 * @param minZ
	 * @param maxX
	 * @param maxZ
	 * @param depth
	 *            深度
	 * @param items
	 *            个数 建议小于10，数量太大，影响效率
	 */
	public PolygonGuadTree(float minX, float minZ, float maxX, float maxZ, int depth, int items) {
		super(minX, minZ, maxX, maxZ);
		this.depth = depth;
		this.items = items;
	}

	/**
	 * 插入不按key分类，根据值进行插入到对应区域
	 */
	@Override
	public void set(Vector3 k, Polygon v) {
		Node<Polygon> r = this.root;
		if (k.x < r.getX() || k.z < r.getZ() || k.x > r.getX() + r.getW() || k.z > r.getZ() + r.getH()) {
			throw new QuadTreeException(String.format("坐标越界:(%f,%f),范围(%f,%f)-->(%f,%f)", k.x, k.z,
					getRootNode().getX(), getRootNode().getZ(), (getRootNode().getX() + getRootNode().getW()),
					(getRootNode().getZ() + getRootNode().getH())));
		}

		if (this.insert(r, new PointData<>(k, v))) {
			this.count++;
		}
	}

	/**
	 * 不重复个数唯一对象
	 */
	@Override
	public int getCount() {
		return super.getCount();
	}

	/**
	 * 获取当前坐标所在多边形
	 */
	@Override
	public Polygon get(Vector3 position, Polygon defaultValue) {
		List<Polygon> polygons = getPolygons(position);
		if (polygons == null || polygons.isEmpty()) {
			return defaultValue;
		}
		if (polygons.size() == 1) {
			return polygons.get(0);
		}
		float minDistance = Byte.MAX_VALUE;
		Polygon p = defaultValue;
		for (Polygon polygon : polygons) {
			float distance = Math.abs(polygon.center.y - position.y);
			if (distance < minDistance) {
				p = polygon;
				minDistance = distance;
			}
		}
		return p;
	}

	/**
	 * @param k
	 *            此处为查询的坐标
	 */
	@Override
	public Node<Polygon> find(Node<Polygon> node, Vector3 k) {
		Node<Polygon> resposne = null;
		switch (node.getNodeType()) {
		case EMPTY:
			break;

		case LEAF:
			if (node.getPolygon().isInnerPoint(k)) {
				resposne = node;
			}

			break;
		case POINTER:
			resposne = this.find(this.getQuadrantForPoint(node, k.x, k.z), k);
			break;

		default:
			throw new QuadTreeException("Invalid nodeType");
		}
		return resposne;
	}

	@Override
	public Polygon remove(Vector3 k) {
		throw new QuadTreeException(String.format("多边形四叉树不能移除节点内容"));
	}

	@Override
	public <T> List<T> getKeyValues() {
		throw new QuadTreeException(String.format("多边形四叉树不支持获取，请使用其他方式"));
	}

	@Override
	public List<Vector3> getKeys() {
		throw new QuadTreeException(String.format("多边形四叉树不支持获取，请使用其他方式"));
	}

	@Override
	public List<Polygon> getValues() {
		throw new QuadTreeException(String.format("多边形四叉树不支持获取，请使用其他方式"));
	}

	@Override
	public void clear() {
		super.clear();
		this.root.getDatas().clear();
	}

	/**
	 * Inserts a point into the tree, updating the tree's structure if necessary.
	 * 
	 * @param {.QuadTree.Node}
	 *            parent The parent to insert the point into.
	 * @param {QuadTree.Point}
	 *            point The point to insert.
	 * @return {boolean} True if a new node was added to the tree; False if a node
	 *         already existed with the correpsonding coordinates and had its value
	 *         reset.
	 * @private
	 */
	private boolean insert(Node<Polygon> parent, PointData<Polygon> point) {
		Boolean result = false;
		switch (parent.getNodeType()) {
		case EMPTY:
			this.setPointForNode(parent, point);
			result = true;
			break;
		case LEAF:
			List<Data<Polygon>> datas = parent.getDatas();
			// 未插满或者到达最大深度，直接加入
			if (datas.size() < items || parent.getDepth() >= depth) {
				if (datas.contains(point)) {
					return false;
				}
				this.setPointForNode(parent, point);
				result = true;
			} else {
				this.split(parent);
				result = this.insert(parent, point);
			}

			break;
		case POINTER:
			List<Node<Polygon>> nodes = this.getQuadrantForPolygon(parent, point.getValue());
			for (Node<Polygon> n : nodes) {
				if (this.insert(n, point)) {
					result = true;
				}
			}
			break;

		default:
			throw new QuadTreeException("Invalid nodeType in parent");
		}
		return result;
	}

	/**
	 * Sets the point for a node, as long as the node is a leaf or empty.
	 * 
	 * @param {QuadTree.Node}
	 *            node The node to set the point for.
	 * @param {QuadTree.Point}
	 *            point The point to set.
	 * @private
	 */
	private void setPointForNode(Node<Polygon> node, PointData<Polygon> point) {
		if (node.getNodeType() == NodeType.POINTER) {
			throw new QuadTreeException("Can not set point for node of type POINTER");
		}
		node.setNodeType(NodeType.LEAF);
		node.getDatas().add(point);
		realCount++;
		// LOGGER.debug("多边形{} 加入节点深度{}，个数{}", point.getValue().getIndex(),
		// node.getDepth(), node.getDatas().size());
	}

	/**
	 * 拆分节点，当前节点变为指针节点，将多边形按是否和矩形相交进行分配到指定区域，一个多边形可在多个区域
	 * 
	 * @param node
	 */
	private void split(Node<Polygon> node) {
		List<Data<Polygon>> datas = node.getDatas();
		node.setDatas(null);
		node.setNodeType(NodeType.POINTER);
		float x = node.getX();
		float z = node.getZ();
		float hw = node.getW() / 2;
		float hh = node.getH() / 2;
		int depth = node.getDepth() + 1;

		node.setNw(new Node<>(x, z, hw, hh, node, depth));
		node.setNe(new Node<>(x + hw, z, hw, hh, node, depth));
		node.setSw(new Node<>(x, z + hh, hw, hh, node, depth));
		node.setSe(new Node<>(x + hw, z + hh, hw, hh, node, depth));

		realCount -= datas.size();
		for (Data<Polygon> point : datas) {
			this.insert(node, (PointData<Polygon>) point);
		}

	}

	/**
	 * 获取多边形所在的象限
	 * 
	 * @param parent
	 * @param polygon
	 * @return
	 */
	private List<Node<Polygon>> getQuadrantForPolygon(Node<Polygon> parent, Polygon polygon) {
		List<Node<Polygon>> nodes = new ArrayList<>();

		// 相交，象限包含多边形，多边形包含象限
		if (parent.getNw().getPolygon().contains(polygon) || polygon.contains(parent.getNw().getPolygon())
				|| polygon.intersectsEdge(parent.getNw().getPolygon())) {
			nodes.add(parent.getNw());
		}

		// NE
		if (parent.getNe().getPolygon().contains(polygon) || polygon.contains(parent.getNe().getPolygon())
				|| polygon.intersectsEdge(parent.getNe().getPolygon())) {
			nodes.add(parent.getNe());
		}

		// SE
		if (parent.getSe().getPolygon().contains(polygon) || polygon.contains(parent.getSe().getPolygon())
				|| polygon.intersectsEdge(parent.getSe().getPolygon())) {
			nodes.add(parent.getSe());
		}

		// SW
		if (parent.getSw().getPolygon().contains(polygon) || polygon.contains(parent.getSw().getPolygon())
				|| polygon.intersectsEdge(parent.getSw().getPolygon())) {
			nodes.add(parent.getSw());
		}

		return nodes;
	}

	/**
	 * 真实个数，包含重复多边形
	 * 
	 * @return
	 */
	public int getRealCount() {
		return realCount;
	}

	/**
	 * 获取当前坐标所在多边形<br>
	 * 一个坐标点可能在多个多边形中，存在上下重叠，具体在哪个多边形自行判断高度，或三维使用八叉树
	 * 
	 * @param position
	 * @return
	 */
	public List<Polygon> getPolygons(Vector3 position) {
		Node<Polygon> nodes = find(this.root, position);
		if (nodes == null || nodes.getDatas() == null) {
			return null;
		}
		List<Polygon> list = nodes.getDatas().stream().map(n -> n.getValue()).filter(p -> p.isInnerPoint(position))
				.collect(Collectors.toList());
		return list;
	}

	/**
	 * 获取当前坐标所在象限所有的多边形
	 * 
	 * @param position
	 * @return
	 */
	public List<Polygon> getQuadrantPolygons(Vector3 position) {
		Node<Polygon> nodes = find(this.root, position);
		if (nodes == null || nodes.getDatas() == null) {
			return null;
		}
		List<Polygon> list = nodes.getDatas().stream().map(n -> n.getValue()).collect(Collectors.toList());
		return list;
	}

	/**
	 * Returns the child quadrant within a node that contains the given (x, y)
	 * coordinate.
	 * 
	 * @param {QuadTree.Node}
	 *            parent The node.
	 * @param {number}
	 *            x The x-coordinate to look for.
	 * @param {number}
	 *            y The y-coordinate to look for.
	 * @return {QuadTree.Node} The child quadrant that contains the point.
	 * @private
	 */
	private Node<Polygon> getQuadrantForPoint(Node<Polygon> parent, float x, float y) {
		float mx = parent.getX() + parent.getW() / 2;
		float mz = parent.getZ() + parent.getH() / 2;
		if (x < mx) {
			return y < mz ? parent.getNw() : parent.getSw();
		} else {
			return y < mz ? parent.getNe() : parent.getSe();
		}
	}

	/**
	 * 获取以当前坐标为中心，半径为radius的正方形相交的多边形 <br>
	 * 可以用于将进入阻挡区的对象移动到附近行走区 <br>
	 * 
	 * @note 非常耗时
	 * @param radius
	 *            坐标半径范围
	 * @return
	 */
	@Deprecated
	public List<Polygon> searchWithin(Vector3 point, float radius) {
		final float xmin = point.x - radius;
		final float zmin = point.z - radius;
		final float xmax = point.x + radius;
		final float zmax = point.z + radius;
		final List<Polygon> arr = new ArrayList<Polygon>();

		final Polygon p = new Polygon(0, new Vector3(xmin, zmin), new Vector3(xmin, zmax), new Vector3(xmax, zmax),
				new Vector3(xmax, zmin));
		this.navigate(this.root, new Func<Polygon>() {
			@Override
			public void call(PolygonGuadTree quadTree, Node<Polygon> node) {
				for (Data<Polygon> data : node.getDatas()) {
					Polygon polygon = data.getValue();
					if (polygon.contains(p) || p.contains(polygon) || p.intersectsEdge(polygon)) {
						arr.add(polygon);
					}

				}
			}
		}, xmin, zmin, xmax, zmax);
		return arr;
	}

	/**
	 * 遍历节点
	 * 
	 * @param node
	 * @param func
	 * @param xmin
	 * @param ymin
	 * @param xmax
	 * @param ymax
	 */
	private void navigate(Node<Polygon> node, Func<Polygon> func, double xmin, double ymin, double xmax, double ymax) {
		switch (node.getNodeType()) {
		case LEAF:
			func.call(this, node);
			break;

		case POINTER:
			if (intersects(xmin, ymax, xmax, ymin, node.getNe()))
				this.navigate(node.getNe(), func, xmin, ymin, xmax, ymax);
			if (intersects(xmin, ymax, xmax, ymin, node.getSe()))
				this.navigate(node.getSe(), func, xmin, ymin, xmax, ymax);
			if (intersects(xmin, ymax, xmax, ymin, node.getSw()))
				this.navigate(node.getSw(), func, xmin, ymin, xmax, ymax);
			if (intersects(xmin, ymax, xmax, ymin, node.getNw()))
				this.navigate(node.getNw(), func, xmin, ymin, xmax, ymax);
			break;
		default:
			break;
		}
	}
}
