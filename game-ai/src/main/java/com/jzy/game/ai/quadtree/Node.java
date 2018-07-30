package com.jzy.game.ai.quadtree;

/**
 * 节点
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class Node<T> {
	private float x;
	private float z;
	private float w;
	private float h;
	private Node<T> parent;
	private Data<T> data;
	private NodeType nodeType = NodeType.EMPTY;
	private Node<T> nw;
	private Node<T> ne;
	private Node<T> sw;
	private Node<T> se;

	public Node(float x, float z, float w, float h, Node<T> parent) {
		super();
		this.x = x;
		this.z = z;
		this.w = w;
		this.h = h;
		this.parent = parent;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}

	public Node<T> getParent() {
		return parent;
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public Data<T> getData() {
		return data;
	}

	public void setData(Data<T> data) {
		this.data = data;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public Node<T> getNw() {
		return nw;
	}

	public void setNw(Node<T> nw) {
		this.nw = nw;
	}

	public Node<T> getNe() {
		return ne;
	}

	public void setNe(Node<T> ne) {
		this.ne = ne;
	}

	public Node<T> getSw() {
		return sw;
	}

	public void setSw(Node<T> sw) {
		this.sw = sw;
	}

	public Node<T> getSe() {
		return se;
	}

	public void setSe(Node<T> se) {
		this.se = se;
	}

}
