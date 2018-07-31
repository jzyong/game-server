package com.jzy.game.ai.quadtree;

import java.util.List;

/**
 * 四叉树
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public abstract class QuadTree<K, V> {
	/** 根节点 */
	protected Node<V> root;
	/** 数量 */
	protected int count;
	
	public QuadTree(float minX, float minZ, float maxX, float maxZ) {
		this.root = new Node<>(minX, minZ, maxX - minX, maxZ - minZ, null);
	}

	/**
	 * 获取根节点
	 * 
	 * @return
	 */
	public Node<V> getRootNode() {
		return root;
	}

	/**
	 * 设置四叉树的值
	 * 
	 * @param k
	 *            键
	 * @param v
	 *            键对应值
	 */
	public abstract void set(K k, V v);

	/**
	 * 获取数据
	 * 
	 * @param k
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public abstract V get(K k, V defaultValue);

	/**
	 * 查询当前节点包含key的值
	 * 
	 * @param node
	 *            当前节点
	 * @param k
	 *            键
	 * @return
	 */
	public abstract Node<V> find(Node<V> node, K k);

	/**
	 * 移除数据
	 * 
	 * @param k
	 * @return
	 */
	public abstract V remove(K k);

	/**
	 * 是否包含值
	 * 
	 * @param k
	 * @return
	 */
	public boolean contains(K k) {
		return get(k, null) != null;
	}

	/**
	 * @return Whether the tree is empty.
	 */
	public boolean isEmpty() {
		return this.root.getNodeType() == NodeType.EMPTY;
	}

	/**
	 * 获取数据数量
	 * 
	 * @return
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Removes all items from the tree.
	 */
	public void clear() {
		this.root.setNw(null);
		this.root.setNe(null);
		this.root.setSw(null);
		this.root.setSe(null);
		this.root.setNodeType(NodeType.EMPTY);
		this.root.setData(null);
		this.count = 0;
	}
	
	/**
	 * 获取键值对数据
	 * @return
	 */
	public abstract  <T>  List<T> getKeyValues();
	
	/**
	 * 获取键
	 * @return
	 */
	public abstract List<K> getKeys();
	
	/**
	 * 获取值
	 * @return
	 */
	public abstract List<V> getValues();
	
	/**
	 * 是否相交
	 * 
	 * @param left
	 * @param bottom
	 * @param right
	 * @param top
	 * @param node
	 * @return
	 */
	protected boolean intersects(double left, double bottom, double right, double top, Node<V> node) {
		return !(node.getX() > right || (node.getX() + node.getW()) < left || node.getZ() > bottom
				|| (node.getZ() + node.getH()) < top);
	}
}
