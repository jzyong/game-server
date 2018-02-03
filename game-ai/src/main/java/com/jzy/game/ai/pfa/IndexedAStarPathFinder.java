
package com.jzy.game.ai.pfa;


import java.util.List;

import com.jzy.game.engine.util.TimeUtil;

/** 
 * A*寻路
 * <br>
 * A fully implemented {@link PathFinder} that can perform both interruptible and non-interruptible pathfinding.
 * <p>
 * This implementation is a common variation of the A* algorithm that is faster than the general A*.
 * <p>
 * In the general A* implementation, data are held for each node in the open or closed lists, and these data are held as a
 * NodeRecord instance. Records are created when a node is first considered and then moved between the open and closed lists, as
 * required. There is a key step in the algorithm where the lists are searched for a node record corresponding to a particular
 * node. This operation is something time-consuming.
 * <p>
 * The indexed A* algorithm improves execution speed by using an array of all the node records for every node in the graph. Nodes
 * must be numbered using sequential integers (see {@link IndexedGraph#getIndex(Object)}), so we don't need to search for a node in the
 * two lists at all. We can simply use the node index to look up its record in the array (creating it if it is missing). This
 * means that the close list is no longer needed. To know whether a node is open or closed, we use the {@link NodeRecord#category
 * category} of the node record. This makes the search step very fast indeed (in fact, there is no search, and we can go straight
 * to the information we need). Unfortunately, we can't get rid of the open list because we still need to be able to retrieve the
 * element with the lowest cost. However, we use a {@link NodeBinaryHeap} for the open list in order to keep performance as high as
 * possible.
 * 
 * @param <N> Type of node
 * 
 * @author davebaol */
public class IndexedAStarPathFinder<N> implements PathFinder<N> {
	IndexedGraph<N> graph;	//图数据
	NodeRecord<N>[] nodeRecords;
	NodeBinaryHeap<NodeRecord<N>> openList;
	NodeRecord<N> current;	//当前节点
	public Metrics metrics;

	/** The unique ID for each search run. Used to mark nodes. */
	private int searchId;

	private static final int UNVISITED = 0;
	private static final int OPEN = 1;
	private static final int CLOSED = 2;	//已访问的节点

	public IndexedAStarPathFinder (IndexedGraph<N> graph) {
		this(graph, false);
	}

	@SuppressWarnings("unchecked")
	public IndexedAStarPathFinder (IndexedGraph<N> graph, boolean calculateMetrics) {
		this.graph = graph;
		this.nodeRecords = (NodeRecord<N>[])new NodeRecord[graph.getNodeCount()];
		this.openList = new NodeBinaryHeap<NodeRecord<N>>();
		if (calculateMetrics) this.metrics = new Metrics();
	}

	@Override
	public boolean searchConnectionPath (N startNode, N endNode, Heuristic<N> heuristic, GraphPath<Connection<N>> outPath) {

		// Perform AStar
		boolean found = search(startNode, endNode, heuristic);

		if (found) {
			// Create a path made of connections
			generateConnectionPath(startNode, outPath);
		}

		return found;
	}

	@Override
	public boolean searchNodePath (N startNode, N endNode, Heuristic<N> heuristic, GraphPath<N> outPath) {

		// Perform AStar
		boolean found = search(startNode, endNode, heuristic);

		if (found) {
			// Create a path made of nodes
			generateNodePath(startNode, outPath);
		}

		return found;
	}

	/**
	 * 搜寻路径
	 * @param startNode
	 * @param endNode
	 * @param heuristic
	 * @return <code>true</code> 查找到路径
	 */
	protected boolean search (N startNode, N endNode, Heuristic<N> heuristic) {

		initSearch(startNode, endNode, heuristic);

		// Iterate through processing each node
		// 迭代开列表，依次从中取出消耗最小的节点，直到找到最终目的地或路径查询失败
		do {
			// Retrieve the node with smallest estimated total cost from the open list，取出消耗最小节点，暂时标识为关列表
			current = openList.pop();
			current.category = CLOSED;	

			// Terminate if we reached the goal node
			if (current.node == endNode) return true;

			visitChildren(endNode, heuristic);

		} while (openList.size > 0);

		// We've run out of nodes without finding the goal, so there's no solution
		return false;
	}

	@Override
	public boolean search (PathFinderRequest<N> request, long timeToRun) {

		long lastTime = TimeUtil.nanoTime();

		// We have to initialize the search if the status has just changed
		if (request.statusChanged) {
			initSearch(request.startNode, request.endNode, request.heuristic);
			request.statusChanged = false;
		}

		// Iterate through processing each node
		do {

			// Check the available time
			long currentTime = TimeUtil.nanoTime();
			timeToRun -= currentTime - lastTime;
			if (timeToRun <= 100) return false;	//忽略100纳秒计算

			// Retrieve the node with smallest estimated total cost from the open list
			current = openList.pop();
			current.category = CLOSED;

			// Terminate if we reached the goal node; we've found a path.
			if (current.node == request.endNode) {
				request.pathFound = true;

				generateNodePath(request.startNode, request.resultPath);

				return true;
			}

			// Visit current node's children
			visitChildren(request.endNode, request.heuristic);

			// Store the current time
			lastTime = currentTime;

		} while (openList.size > 0);

		// The open list is empty and we've not found a path.
		request.pathFound = false;
		return true;
	}

	/**
	 * 初始化查询
	 * @param startNode
	 * @param endNode
	 * @param heuristic
	 */
	protected void initSearch (N startNode, N endNode, Heuristic<N> heuristic) {
		if (metrics != null) metrics.reset();

		// Increment the search id
		if (++searchId < 0) searchId = 1;

		// Initialize the open list
		openList.clear();

		// Initialize the record for the start node and add it to the open list
		NodeRecord<N> startRecord = getNodeRecord(startNode);
		startRecord.node = startNode;
		startRecord.connection = null;
		startRecord.costSoFar = 0;
		addToOpenList(startRecord, heuristic.estimate(startNode, endNode));

		current = null;
	}

	/**
	 * 访问孩子节点
	 * @param endNode
	 * @param heuristic
	 */
	protected void visitChildren (N endNode, Heuristic<N> heuristic) {
		// Get current node's outgoing connections
		List<Connection<N>> connections = graph.getConnections(current.node);

		// Loop through each connection in turn
		for (int i = 0; i < connections.size(); i++) {
			if (metrics != null) metrics.visitedNodes++;

			Connection<N> connection = connections.get(i);

			// Get the cost estimate for the node
			N node = connection.getToNode();	//周围目标节点
			float nodeCost = current.costSoFar + connection.getCost();	//节点到目标的消耗

			float nodeHeuristic;
			NodeRecord<N> nodeRecord = getNodeRecord(node);
			if (nodeRecord.category == CLOSED) { // The node is closed

				// If we didn't find a shorter route, skip 已经是消耗最小的目标点
				if (nodeRecord.costSoFar <= nodeCost) continue;

				// We can use the node's old cost values to calculate its heuristic
				// without calling the possibly expensive heuristic function
				nodeHeuristic = nodeRecord.getEstimatedTotalCost() - nodeRecord.costSoFar;
			} else if (nodeRecord.category == OPEN) { // The node is open

				// If our route is no better, then skip
				if (nodeRecord.costSoFar <= nodeCost) continue;

				// Remove it from the open list (it will be re-added with the new cost)
				openList.remove(nodeRecord);

				// We can use the node's old cost values to calculate its heuristic
				// without calling the possibly expensive heuristic function
				nodeHeuristic = nodeRecord.getEstimatedTotalCost() - nodeRecord.costSoFar;
			} else { // the node is unvisited

				// We'll need to calculate the heuristic value using the function,
				// since we don't have a node record with a previously calculated value
				nodeHeuristic = heuristic.estimate(node, endNode);
			}

			// Update node record's cost and connection
			nodeRecord.costSoFar = nodeCost;
			nodeRecord.connection = connection;

			// Add it to the open list with the estimated total cost
			addToOpenList(nodeRecord, nodeCost + nodeHeuristic);
		}

	}

	/**
	 * 生成链接关系路径
	 * @param startNode
	 * @param outPath
	 */
	protected void generateConnectionPath (N startNode, GraphPath<Connection<N>> outPath) {

		// Work back along the path, accumulating connections
		// outPath.clear();
		while (current.node != startNode) {
			outPath.add(current.connection);
			current = nodeRecords[graph.getIndex(current.connection.getFromNode())];
		}

		// Reverse the path
		outPath.reverse();
	}

	/**
	 * 生成链接节点路径
	 * @param startNode
	 * @param outPath
	 */
	protected void generateNodePath (N startNode, GraphPath<N> outPath) {

		// Work back along the path, accumulating nodes
		// outPath.clear();
		while (current.connection != null) {
			outPath.add(current.node);
			current = nodeRecords[graph.getIndex(current.connection.getFromNode())];
		}
		outPath.add(startNode);

		// Reverse the path
		outPath.reverse();
	}

	/**
	 * 加入开列表
	 * @param nodeRecord
	 * @param estimatedTotalCost	预估的消耗
	 */
	protected void addToOpenList (NodeRecord<N> nodeRecord, float estimatedTotalCost) {
		openList.add(nodeRecord, estimatedTotalCost);
		nodeRecord.category = OPEN;
		if (metrics != null) {
			metrics.openListAdditions++;
			metrics.openListPeak = Math.max(metrics.openListPeak, openList.size);
		}
	}

	/**
	 * 获取节点记录对象
	 * @param node
	 * @return
	 */
	protected NodeRecord<N> getNodeRecord (N node) {
		int index = graph.getIndex(node);
		NodeRecord<N> nr = nodeRecords[index];
		if (nr != null) {
			if (nr.searchId != searchId) {
				nr.category = UNVISITED;
				nr.searchId = searchId;
			}
			return nr;
		}
		nr = nodeRecords[index] = new NodeRecord<N>();
		nr.node = node;
		nr.searchId = searchId;
		return nr;
	}

	/** 寻路搜索节点记录
	 * <br>
	 * This nested class is used to keep track of the information we need for each node during the search.
	 * 
	 * @param <N> Type of node
	 * 
	 * @author davebaol */
	static class NodeRecord<N> extends NodeBinaryHeap.Node {
		/** The reference to the node. */
		N node;

		/** The incoming connection to the node */
		Connection<N> connection;

		/** The actual cost from the start node. */
		float costSoFar;

		/** The node category: {@link #UNVISITED}, {@link #OPEN} or {@link #CLOSED}. */
		int category;

		/** ID of the current search. */
		int searchId;

		/** Creates a {@code NodeRecord}. */
		public NodeRecord () {
			super(0);
		}

		/** Returns the estimated total cost. */
		public float getEstimatedTotalCost () {
			return getValue();
		}
	}

	/**度量统计
	 *  A class used by {@link IndexedAStarPathFinder} to collect search metrics.
	 * 
	 * @author davebaol */
	public static class Metrics {
		public int visitedNodes;		//访问节点次数
		public int openListAdditions;	//记录开列表次数
		public int openListPeak;		//记录开列表最大个数值

		public Metrics () {
		}

		public void reset () {
			visitedNodes = 0;
			openListAdditions = 0;
			openListPeak = 0;
		}
	}
}
