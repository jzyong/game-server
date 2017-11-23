//package com.jzy.game.ai.nav;
//
//import com.badlogic.gdx.ai.pfa.Connection;
//import com.badlogic.gdx.math.Vector3;
//
///**
// * 相连接三角形的共享边
// * 
// * @author JiangZhiYong
// * @QQ 359135103 2017年11月7日 下午4:50:11
// */
//public class Edge implements Connection<Triangle> {
//	/** 右顶点 */
//	public Vector3 rightVertex;
//	public Vector3 leftVertex;
//
//	/** 源三角形 */
//	public Triangle fromNode;
//	/** 指向的三角形 */
//	public Triangle toNode;
//
//	public Edge(Triangle fromNode, Triangle toNode, Vector3 rightVertex, Vector3 leftVertex) {
//		this.fromNode = fromNode;
//		this.toNode = toNode;
//		this.rightVertex = rightVertex;
//		this.leftVertex = leftVertex;
//	}
//
//	@Override
//	public float getCost() {
//		return 1;
//	}
//
//	@Override
//	public Triangle getFromNode() {
//		return fromNode;
//	}
//
//	@Override
//	public Triangle getToNode() {
//		return toNode;
//	}
//
//	@Override
//	public String toString() {
//		final StringBuffer sb = new StringBuffer("Edge{");
//		sb.append("fromNode=").append(fromNode.index);
//		sb.append(", toNode=").append(toNode == null ? "null" : toNode.index);
//		sb.append(", rightVertex=").append(rightVertex);
//		sb.append(", leftVertex=").append(leftVertex);
//		sb.append('}');
//		return sb.toString();
//	}
//
//}
