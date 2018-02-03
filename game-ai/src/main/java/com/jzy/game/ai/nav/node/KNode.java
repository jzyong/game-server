package com.jzy.game.ai.nav.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.jzy.game.ai.util.Tracker;
import com.jzy.game.engine.util.math.Vector3;

/**
 * 节点（多边形的顶点坐标+寻路需要的属性）
 * 
 * @author JiangZhiYong
 * @date 2017年12月3日
 * @mail 359135103@qq.com
 */
public class KNode implements Serializable, Comparable<KNode> {
	private static final long serialVersionUID = 1L;
	private static final AtomicLong ID_CREATE = new AtomicLong(0);
	public final static double G_COST_NOT_CALCULATED_FLAG = -Double.MAX_VALUE; // g消耗未计算标示
	public static int UNPROCESSED = 100; // 未处理
	public static int OPEN = 101; // 开列表
	public static int CLOSED = 102; // 关闭列表

	protected long id = 0; // 里的id是防止clone后，地址变更，无法查找
	protected List<KNode> connectedNodes; // 周围连接的节点
	protected List<KNode> tempConnectedNodes; // 临时保存周围连接的节点，A*寻路临时保存数据
	protected Vector3 point; // 当前节点代表的坐标点
	protected double gCost; // distance to startPoint, via the parent nodes.
	protected double hCost; // distance straight to endPoint.
	protected double fCost; // gCost+hCost. This is what the the A* algorithm uses to sort the openList in
							// PathFinder.
	protected double distToParent;// 到父节点的距离
	protected KNode parent; // 父节点
	protected int trackerStatus = UNPROCESSED; // 寻路状态
	public int trackerID = -1; // hash唯一值
	public long trackerCounter = -1; // 计数

	public KNode() {
		id = ID_CREATE.getAndIncrement();
		connectedNodes = new ArrayList<>();
		tempConnectedNodes = new ArrayList<>();
		gCost = G_COST_NOT_CALCULATED_FLAG;
	}

	public KNode(Vector3 point) {
		this();
		this.point = point;
	}

	@Override
	public int compareTo(KNode o) {
		double otherNodeFCost = o.getFCost();
		if (fCost > otherNodeFCost) {
			return 1;
		} else if (fCost < otherNodeFCost) {
			return -1;
		} else {
			return 0;
		}
	}

	public double getFCost() {
		return parent != null ? fCost : 0;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<KNode> getConnectedNodes() {
		return connectedNodes;
	}

	public List<KNode> getTempConnectedNodes() {
		return tempConnectedNodes;
	}

	public Vector3 getPoint() {
		return point;
	}

	public double getgCost() {
		return gCost;
	}

	public double gethCost() {
		return hCost;
	}

	public double getfCost() {
		return fCost;
	}

	/**
	 * 清除当前节点连接点，及该节点在其他节点中的对象
	 */
	public void clearConnectedNodes() {
		// 移除其他节点中的关系
		for (int k = connectedNodes.size() - 1; k >= 0; k--) {
			List<KNode> otherConnectedNodes = connectedNodes.get(k).getConnectedNodes();
			int index = otherConnectedNodes.indexOf(this);
			if (index != -1) {
				otherConnectedNodes.remove(index);
			}
		}
		connectedNodes.clear();
	}

	/**
	 * 临时， 清除当前节点连接点，及该节点在其他节点中的对象
	 */
	public void clearTempConnectedNodes() {
		for (int k = tempConnectedNodes.size() - 1; k >= 0; k--) {
			List<KNode> otherConnectedNodes = tempConnectedNodes.get(k).getTempConnectedNodes();
			otherConnectedNodes.remove(this);
		}
		tempConnectedNodes.clear();
	}

	/**
	 * 清除之前的寻路数据
	 */
	public void clearForReuse() {
		clearConnectedNodes();
		clearTempConnectedNodes();
		gCost = G_COST_NOT_CALCULATED_FLAG;
		hCost = 0;
		fCost = 0;
		distToParent = 0;
		parent = null;
	}

	public void setPoint(Vector3 p) {
		this.point = p;
	}

	/**
	 * 计算G消耗，到父节点的距离+父节点距离....
	 */
	public void calcGCost() {
		if (parent == null) {
			gCost = 0;
		} else {
			gCost = (this.getDistanceToParent() + getParent().getGCost());
		}
	}

	public double getGCost() {
		return gCost;
	}

	/**
	 * 到父节点距离
	 * 
	 * @return
	 */
	double getDistanceToParent() {
		return point.distance(getParent().getPoint());
	}

	public KNode getParent() {
		return parent;
	}

	public void setParent(KNode parent) {
		this.parent = parent;
		this.distToParent = this.getDistanceToParent();
	}

	/**
	 * 设置寻路状态
	 * 
	 * @param trackerStatus
	 *            寻路状态
	 * @param tracker
	 */
	public void setPathFinderStatus(int trackerStatus, Tracker tracker) {
		this.trackerStatus = trackerStatus;
		this.trackerCounter = tracker.getCounter();
		this.trackerID = tracker.getID();
	}

	/**
	 * 获取寻路状态
	 * 
	 * @param tracker
	 * @return
	 */
	public int getPathFinderStatus(Tracker tracker) {
		if (this.trackerCounter == tracker.getCounter() && this.trackerID == tracker.getID()) {
			return trackerStatus;
		} else {
			return UNPROCESSED;
		}
	}

	/**
	 * 计算h消耗
	 * 
	 * @param endNode
	 */
	public void calcHCost(KNode endNode) {
		hCost = point.distance(endNode.getPoint());
	}

	public double getHCost() {
		return hCost;
	}

	/**
	 * f消耗
	 */
	public void calcFCost() {
		fCost = getGCost() + getHCost();
	}

	public void setGCost(double gCost) {
		this.gCost = gCost;
	}
}
