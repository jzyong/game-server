package com.jzy.game.ai.quadtree.point;

import java.util.ArrayList;
import java.util.List;

import com.jzy.game.ai.quadtree.Func;
import com.jzy.game.ai.quadtree.Node;
import com.jzy.game.ai.quadtree.NodeType;
import com.jzy.game.ai.quadtree.QuadTree;
import com.jzy.game.ai.quadtree.QuadTreeException;
import com.jzy.game.engine.math.Vector3;

/**
 * 点四叉树
 * <br>一个象限只有一个点,不限深度，比较适合存储地图对象，如快速检测玩家和周围玩家或怪物的距离，是否碰撞
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PointQuadTree<V> extends QuadTree<Vector3, V> {

	public PointQuadTree(float minX, float minZ, float maxX, float maxZ) {
		super(minX, minZ, maxX, maxZ);
	}
	
	public PointQuadTree(Vector3 startPoint,Vector3 endPoint) {
		this(startPoint.x, startPoint.z, endPoint.x, endPoint.z);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void set(Vector3 k, V v) {

		Node<V> r = this.root;
		if (k.x < r.getX() || k.z < r.getZ() || k.x > r.getX() + r.getW() || k.z > r.getZ() + r.getH()) {
			throw new QuadTreeException(String.format("坐标越界:(%f,%f)", k.x, k.z));
		}

		if (this.insert(r, new PointData(k, v))) {
			this.count++;
		}
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
	private boolean insert(Node<V> parent, PointData<V> point) {
		Boolean result = false;
		switch (parent.getNodeType()) {
		case EMPTY:
			this.setPointForNode(parent, point);
			result = true;
			break;
		case LEAF:
			PointData<V> data = (PointData<V>) parent.getData();
			if (data.getPoint().getX() == point.getPoint().getX()
					&& data.getPoint().getZ() == point.getPoint().getZ()) {
				this.setPointForNode(parent, point);
				result = false;
			} else {
				this.split(parent);
				result = this.insert(parent, point);
			}
			break;
		case POINTER:
			result = this.insert(this.getQuadrantForPoint(parent, point.getPoint().getX(), point.getPoint().getZ()),
					point);
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
	private void setPointForNode(Node<V> node, PointData<V> point) {
		if (node.getNodeType() == NodeType.POINTER) {
			throw new QuadTreeException("Can not set point for node of type POINTER");
		}
		node.setNodeType(NodeType.LEAF);
		node.setData(point);
	}

	/**
	 * Converts a leaf node to a pointer node and reinserts the node's point into
	 * the correct child.
	 * 
	 * @param {QuadTree.Node}
	 *            node The node to split.
	 * @private
	 */
	private void split(Node<V> node) {
		PointData<V> oldPoint = (PointData<V>) node.getData();
		node.setData(null);

		node.setNodeType(NodeType.POINTER);

		float x = node.getX();
		float y = node.getZ();
		float hw = node.getW() / 2;
		float hh = node.getH() / 2;

		node.setNw(new Node<V>(x, y, hw, hh, node));
		node.setNe(new Node<V>(x + hw, y, hw, hh, node));
		node.setSw(new Node<V>(x, y + hh, hw, hh, node));
		node.setSe(new Node<V>(x + hw, y + hh, hw, hh, node));

		this.insert(node, oldPoint);
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
	private Node<V> getQuadrantForPoint(Node<V> parent, float x, float y) {
		float mx = parent.getX() + parent.getW() / 2;
		float mz = parent.getZ() + parent.getH() / 2;
		if (x < mx) {
			return y < mz ? parent.getNw() : parent.getSw();
		} else {
			return y < mz ? parent.getNe() : parent.getSe();
		}
	}

	@Override
	public V get(Vector3 k, V defaultValue) {
		Node<V> node = this.find(this.root, k);
		return node != null ? node.getData().getValue() : defaultValue;
	}

	@Override
	public Node<V> find(Node<V> node, Vector3 vector3) {
		Node<V> resposne = null;
		switch (node.getNodeType()) {
		case EMPTY:
			break;

		case LEAF:
			Vector3 oldPoint = ((PointData<V>) node.getData()).getPoint();
			resposne = oldPoint.getX() == vector3.x && oldPoint.getZ() == vector3.z ? node : null;
			break;

		case POINTER:
			resposne = this.find(this.getQuadrantForPoint(node, vector3.x, vector3.z), vector3);
			break;

		default:
			throw new QuadTreeException("Invalid nodeType");
		}
		return resposne;
	}

	@Override
	public V remove(Vector3 k) {

		Node<V> node = this.find(this.root, k);
		if (node != null) {
			V value = node.getData().getValue();
			node.setData(null);
			node.setNodeType(NodeType.EMPTY);
			this.balance(node);
			this.count--;
			return value;
		} else {
			return null;
		}
	}

	/**
	 * Attempts to balance a node. A node will need balancing if all its children
	 * are empty or it contains just one leaf.
	 * 
	 * @param {QuadTree.Node}
	 *            node The node to balance.
	 * @private
	 */
	private void balance(Node<V> node) {
		switch (node.getNodeType()) {
		case EMPTY:
		case LEAF:
			if (node.getParent() != null) {
				this.balance(node.getParent());
			}
			break;

		case POINTER: {
			Node<V> nw = node.getNw();
			Node<V> ne = node.getNe();
			Node<V> sw = node.getSw();
			Node<V> se = node.getSe();
			Node<V> firstLeaf = null;

			// Look for the first non-empty child, if there is more than one then we
			// break as this node can't be balanced.
			if (nw.getNodeType() != NodeType.EMPTY) {
				firstLeaf = nw;
			}
			if (ne.getNodeType() != NodeType.EMPTY) {
				if (firstLeaf != null) {
					break;
				}
				firstLeaf = ne;
			}
			if (sw.getNodeType() != NodeType.EMPTY) {
				if (firstLeaf != null) {
					break;
				}
				firstLeaf = sw;
			}
			if (se.getNodeType() != NodeType.EMPTY) {
				if (firstLeaf != null) {
					break;
				}
				firstLeaf = se;
			}

			if (firstLeaf == null) {
				// All child nodes are empty: so make this node empty.
				node.setNodeType(NodeType.EMPTY);
				node.setNw(null);
				node.setNe(null);
				node.setSw(null);
				node.setSe(null);

			} else if (firstLeaf.getNodeType() == NodeType.POINTER) {
				// Only child was a pointer, therefore we can't rebalance.
				break;

			} else {
				// Only child was a leaf: so update node's point and make it a leaf.
				node.setNodeType(NodeType.LEAF);
				node.setNw(null);
				node.setNe(null);
				node.setSw(null);
				node.setSe(null);
				node.setData(firstLeaf.getData());
			}

			// Try and balance the parent as well.
			if (node.getParent() != null) {
				this.balance(node.getParent());
			}
		}
			break;
		}
	}

	/**
	 * 遍历所有节点
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PointData<V>> getKeyValues() {
		final List<PointData<V>> arr = new ArrayList<PointData<V>>();
		this.traverse(this.root, new Func<V>() {
			@Override
			public void call(PointQuadTree<V> quadTree, Node<V> node) {
				arr.add((PointData<V>) node.getData());
			}
		});
		return arr;
	}

	@Override
	public List<Vector3> getKeys() {
		final List<Vector3> arr = new ArrayList<Vector3>();
		this.traverse(this.root, new Func<V>() {
			@Override
			public void call(PointQuadTree<V> quadTree, Node<V> node) {
				arr.add(((PointData<V>) node.getData()).getPoint());
			}
		});
		return arr;
	}

	@Override
	public List<V> getValues() {
		final List<V> arr = new ArrayList<V>();
		this.traverse(this.root, new Func<V>() {
			@Override
			public void call(PointQuadTree<V> quadTree, Node<V> node) {
				arr.add(node.getData().getValue());
			}
		});
		return arr;
	}

	/**
	 * Traverses the tree depth-first, with quadrants being traversed in clockwise
	 * order (NE, SE, SW, NW). The provided function will be called for each leaf
	 * node that is encountered.
	 * 
	 * @param {QuadTree.Node}
	 *            node The current node.
	 * @param {function(QuadTree.Node)}
	 *            fn The function to call for each leaf node. This function takes
	 *            the node as an argument, and its return value is irrelevant.
	 * @private
	 */
	public void traverse(Node<V> node, Func<V> func) {
		switch (node.getNodeType()) {
		case LEAF:
			func.call(this, node);
			break;

		case POINTER:
			this.traverse(node.getNe(), func);
			this.traverse(node.getSe(), func);
			this.traverse(node.getSw(), func);
			this.traverse(node.getNw(), func);
			break;
		default:
			break;
		}
	}

	/**
	 * 搜查相交点
	 * 
	 * @param xmin
	 * @param ymin
	 * @param xmax
	 * @param ymax
	 * @return
	 */
	public List<PointData<V>> searchIntersect(final double xmin, final double ymin, final double xmax,
			final double ymax) {
		final List<PointData<V>> arr = new ArrayList<PointData<V>>();
		this.navigate(this.root, new Func<V>() {
			@Override
			public void call(PointQuadTree<V> quadTree, Node<V> node) {
				PointData<V> pt = (PointData<V>) node.getData();
				if (pt.getPoint().getX() < xmin || pt.getPoint().getX() > xmax || pt.getPoint().getZ() < ymin
						|| pt.getPoint().getZ() > ymax) {
					// Definitely not within the polygon!
				} else {
					arr.add((PointData<V>) node.getData());
				}

			}
		}, xmin, ymin, xmax, ymax);
		return arr;
	}

	private void navigate(Node<V> node, Func<V> func, double xmin, double ymin, double xmax, double ymax) {
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


	/**
	 * 获取矩形内部数据
	 * 
	 * @param xmin
	 * @param xmax
	 * @return
	 */
	public List<PointData<V>> searchWithin(final float xmin, final float zmin, final float xmax, final float zmax) {
		final List<PointData<V>> arr = new ArrayList<PointData<V>>();
		this.navigate(this.root, new Func<V>() {
			@Override
			public void call(PointQuadTree<V> quadTree, Node<V> node) {
				PointData<V> pt = (PointData<V>) node.getData();
				if (pt.getPoint().getX() > xmin && pt.getPoint().getX() < xmax && pt.getPoint().getZ() > zmin
						&& pt.getPoint().getZ() < zmax) {
					arr.add((PointData<V>) node.getData());
				}
			}
		}, xmin, zmin, xmax, zmax);
		return arr;
	}

	/**
	 * 获取矩形内部数据
	 * @param radius 坐标半径范围
	 * @return
	 */
	public List<PointData<V>> searchWithin(Vector3 point, float radius) {
		final float xmin = point.x - radius;
		final float zmin = point.z - radius;
		final float xmax = point.x + radius;
		final float zmax = point.z + radius;
		final List<PointData<V>> arr = new ArrayList<PointData<V>>();
		this.navigate(this.root, new Func<V>() {
			@Override
			public void call(PointQuadTree<V> quadTree, Node<V> node) {
				PointData<V> pt = (PointData<V>) node.getData();
				if (pt.getPoint().getX() > xmin && pt.getPoint().getX() < xmax && pt.getPoint().getZ() > zmin
						&& pt.getPoint().getZ() < zmax) {
					arr.add((PointData<V>) node.getData());
				}
			}
		}, xmin, zmin, xmax, zmax);
		return arr;
	}

	 /**
     * Clones the quad-tree and returns the new instance.
     * @return {QuadTree} A clone of the tree.
     */
    public PointQuadTree<V> clone() {
        float x1 = this.root.getX();
        float y1 = this.root.getZ();
        float x2 = x1 + this.root.getW();
        float y2 = y1 + this.root.getH();
        final PointQuadTree<V> clone = new PointQuadTree<V>(x1, y1, x2, y2);
        // This is inefficient as the clone needs to recalculate the structure of the
        // tree, even though we know it already.  But this is easier and can be
        // optimized when/if needed.
        this.traverse(this.root, new Func<V>() {
            @Override
            public void call(PointQuadTree<V> quadTree, Node<V> node) {
            	PointData<V> data=(PointData<V>) node.getData();
                clone.set(data.getPoint().copy(), data.getValue());
            }
        });


        return clone;
    }
}
