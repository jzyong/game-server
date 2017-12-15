package com.jzy.game.ai.unity.nav;

import java.io.Serializable;
import java.util.*;

import com.jzy.game.ai.util.BinaryHeap;
import com.jzy.game.ai.util.Tracker;

/**
 * 通过阻挡区域寻路，A*算法
 *
 * @author Keith Woodward
 * 
 * @fix JiangZhiYong
 */
public class PathFinder implements Serializable {

	private static final long serialVersionUID = 1L;
	public KNode startNode;	//开始节点
    public KNode endNode;	//结束节点
    public BinaryHeap<KNode> openList;

    // Tracker is used in conjunction with the KNodes to detect if the Nodes are in the open or closed state.
    Tracker tracker = new Tracker();

    // for debugging only:
    public boolean debug = false;
    public Vector3 startPointDebug;
    public Vector3 endPointDebug;
    public List<KNode> startNodeTempReachableNodesDebug = new ArrayList<>();
    public List<KNode> endNodeTempReachableNodesDebug = new ArrayList<>();

    public PathFinder() {
        openList = new BinaryHeap<>();
        startNode = new KNode();
        endNode = new KNode();
    }

    /**
     * 计算获取寻路数据
     * @param start
     * @param end
     * @param maxTempNodeConnectionDist 最大连接距离
     * @param nodeConnector
     * @param blocks
     * @return
     */
    public PathData calc(Vector3 start, Vector3 end, double maxTempNodeConnectionDist, KNodeConnector<TriangleBlock> nodeConnector, List<TriangleBlock> blocks) {
        return calc(start, end, maxTempNodeConnectionDist, Double.MAX_VALUE, nodeConnector, blocks);
    }

    /**
     * @param start
     * @param end
     * @param maxTempNodeConnectionDist Maximum connection distance from start
     * to block and end to block. The smaller the distance, the faster
     * the algorithm.
     * @param maxSearchDistStartToEnd Maximum distance from start to end. Any
     * paths with a longer distance won't be returned. The smaller the value the
     * faster the algorithm.
     * @param nodeConnector
     * @param blocks
     * @return
     */
    public PathData calc(Vector3 start, Vector3 end, double maxTempNodeConnectionDist, double maxSearchDistStartToEnd, KNodeConnector<TriangleBlock> nodeConnector, List<TriangleBlock> blocks) {
        synchronized (this.openList) {

            if (tempReachableNodesExist(blocks)) {
                return null;
            }
            double startToEndDist = start.distance(end);
            if (startToEndDist > maxSearchDistStartToEnd) {
                // no point doing anything since startToEndDist is greater than maxSearchDistStartToEnd.
                PathData pathData = new PathData(PathData.Result.ERROR1);
                return pathData;
            }
            startNode.clearForReuse();
            startNode.setPoint(start);
            // Set startNode gCost to zero
            startNode.calcGCost();
            KNode currentNode = startNode;
            endNode.clearForReuse();
            endNode.setPoint(end);

            // Check for straight line path between start and end.
            // Note that this assumes start and end are not both contained in the same polygon.
            boolean intersection = false;
            for (TriangleBlock block : blocks) {
                KPolygon innerPolygon = block.getInnerPolygon();
                // Test if polygon intersects the line from start to end
                if (innerPolygon.intersectionPossible(start, end) && innerPolygon.intersectsLine(start, end)) {
                    intersection = true;
                    break;
                }
            }
            if (intersection == false) {
                // No intersections, so the straight-line path is fine!
                endNode.setParent(currentNode);
                PathData pathData = this.makePathData();
                clearTempReachableNodes();
                tracker.incrementCounter();
                return pathData;
            }
            {
                // Connect the startNode to its reachable nodes and vice versa
                List<KNode> reachableNodes = nodeConnector.makeReachableNodesFor(startNode, maxTempNodeConnectionDist, blocks);
                if (reachableNodes.isEmpty()) {
                    // path from start node is not possible since there are no connections to it.
                    PathData pathData = new PathData(PathData.Result.ERROR2);
                    clearTempReachableNodes();
                    tracker.incrementCounter();
                    return pathData;
                }
                startNode.getTempConnectedNodes().addAll(reachableNodes);
                for (KNode node : reachableNodes) {
                    node.getTempConnectedNodes().add(startNode);
                }

                // Connect the endNode to its reachable nodes and vice versa
                reachableNodes = nodeConnector.makeReachableNodesFor(endNode, maxTempNodeConnectionDist, blocks);
                if (reachableNodes.isEmpty()) {
                    // path to end node is not possible since there are no connections to it.
                    PathData pathData = new PathData(PathData.Result.ERROR3);
                    clearTempReachableNodes();
                    tracker.incrementCounter();
                    return pathData;
                }
                endNode.getTempConnectedNodes().addAll(reachableNodes);
                for (KNode node : reachableNodes) {
                    node.getTempConnectedNodes().add(endNode);
                }
            }

            //TODO 2017-12-14
            // Here we start the A* algorithm!
            openList.makeEmpty();
            while (true) {
                // put the current node in the closedSet and take it out of the openList.
                currentNode.setPathFinderStatus(KNode.CLOSED, tracker);
                if (openList.isEmpty() == false) {
                    openList.deleteMin();
                }
                // add reachable nodes to the openList if they're not already there.
                List<KNode> reachableNodes = currentNode.getConnectedNodes();
                for (int i = 0; i < reachableNodes.size(); i++) {
                    KNode reachableNode = reachableNodes.get(i);
                    if (reachableNode.getPathFinderStatus(tracker) == KNode.UNPROCESSED) {	//未处理
                        reachableNode.setParent(currentNode);
                        reachableNode.calcHCost(endNode);
                        reachableNode.calcGCost();
                        reachableNode.calcFCost();
                        if (reachableNode.getFCost() <= maxSearchDistStartToEnd) {
                            openList.add(reachableNode);
                            reachableNode.setPathFinderStatus(KNode.OPEN, tracker);
                        }
                    } else if (reachableNode.getPathFinderStatus(tracker) == KNode.OPEN) {
                        if (reachableNode.getGCost() == KNode.G_COST_NOT_CALCULATED_FLAG) {
                            continue;
                        }
                        double currentGCost = reachableNode.getGCost();
                        double newGCost = currentNode.getGCost() + currentNode.getPoint().distance(reachableNode.getPoint());
                        if (newGCost < currentGCost) {
                            reachableNode.setParent(currentNode);
                            reachableNode.setGCost(newGCost);	//reachableNode.calcGCost();
                            reachableNode.calcFCost();
                            // Since the g-cost of the node has changed,
                            // must re-sort the list to reflect this.
                            int index = openList.indexOf(reachableNode);
                            openList.percolateUp(index);
                        }
                    }
                }
                List<KNode> tempReachableNodes = currentNode.getTempConnectedNodes();
                for (int i = 0; i < tempReachableNodes.size(); i++) {
                    KNode reachableNode = tempReachableNodes.get(i);
                    if (reachableNode.getPathFinderStatus(tracker) == KNode.UNPROCESSED) {
                        reachableNode.setParent(currentNode);
                        reachableNode.calcHCost(endNode);
                        reachableNode.calcGCost();
                        reachableNode.calcFCost();
                        if (reachableNode.getFCost() <= maxSearchDistStartToEnd) {
                            openList.add(reachableNode);
                            reachableNode.setPathFinderStatus(KNode.OPEN, tracker);
                        }
                    } else if (reachableNode.getPathFinderStatus(tracker) == KNode.OPEN) {
                        if (reachableNode.getGCost() == KNode.G_COST_NOT_CALCULATED_FLAG) {
                            continue;
                        }
                        double currentGCost = reachableNode.getGCost();
                        double newGCost = currentNode.getGCost() + currentNode.getPoint().distance(reachableNode.getPoint());
                        if (newGCost < currentGCost) {
                            reachableNode.setParent(currentNode);
                            reachableNode.setGCost(newGCost);	//reachableNode.calcGCost();
                            reachableNode.calcFCost();
                            // Since the g-cost of the node has changed,
                            // must re-sort the list to reflect this.
                            int index = openList.indexOf(reachableNode);
                            openList.percolateUp(index);
                        }
                    }
                }
                if (openList.size() == 0) {
                    //System.out.println(this.getClass().getSimpleName()+": openList.size() == 0, returning");
                    PathData pathData = new PathData(PathData.Result.ERROR4);
                    clearTempReachableNodes();
                    tracker.incrementCounter();
                    return pathData;
                }

                currentNode = openList.peekMin();
                if (currentNode == endNode) {
                    //System.out.println(this.getClass().getSimpleName()+": currentNode == endNode, returning");
                    break;
                }
            }
            PathData pathData = makePathData();
            clearTempReachableNodes();
            tracker.incrementCounter();
            return pathData;
        }
    }


    /**
     * 清除临时节点数据
     */
    protected void clearTempReachableNodes() {
        if (debug) {
            startPointDebug = startNode.getPoint().copy();
            endPointDebug = endNode.getPoint().copy();
            startNodeTempReachableNodesDebug.clear();
            endNodeTempReachableNodesDebug.clear();
            startNodeTempReachableNodesDebug.addAll(startNode.getTempConnectedNodes());
            endNodeTempReachableNodesDebug.addAll(endNode.getTempConnectedNodes());
        }

        // Erase all nodes' tempConnectedNodes
        if (startNode != null) {
            startNode.clearTempConnectedNodes();
        }
        if (endNode != null) {
            endNode.clearTempConnectedNodes();
        }
    }

    /**
     * 生成寻路数据
     * @return
     */
    protected PathData makePathData() {
    	//反向寻找
        KNode currentNode = getEndNode();
        HashSet<KNode> nodes = new HashSet<>();
        ArrayList<Vector3> points = new ArrayList<>();
        while (true) {
        	System.out.println(currentNode.getPoint().toString());
            nodes.add(currentNode);
            points.add(currentNode.getPoint());
            KNode parentNode = currentNode.getParent();
            if (parentNode == null || nodes.contains(parentNode)) {
                break;
            }
            currentNode = parentNode;
        }
        Collections.reverse(points);
        PathData pathData = new PathData(points, nodes);
        nodes.clear();
        points.clear();
        return pathData;
    }

    public boolean pathExists() {
        if (getEndNode() != null && getEndNode().getParent() != null) {
            return true;
        }
        return false;
    }

    public KNode getEndNode() {
        return endNode;
    }

    public KNode getStartNode() {
        return startNode;
    }

    // used only for assertion checks
    /**
     * 是否存在临时连接节点
     * @param blocks
     * @return
     */
    protected boolean tempReachableNodesExist(List<TriangleBlock> blocks) {
        for (int i = 0; i < blocks.size(); i++) {
        	TriangleBlock block = (TriangleBlock) blocks.get(i);
            for (int j = 0; j < block.getNodes().size(); j++) {
                TriangleKNode node = block.getNodes().get(j);
                if (node.getTempConnectedNodes().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
