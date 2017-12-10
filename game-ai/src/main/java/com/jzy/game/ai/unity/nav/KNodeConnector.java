package com.jzy.game.ai.unity.nav;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.jzy.game.ai.unity.nav.path.BlockDistanceComarable;

/**
 * 节点连接关系处理
 * 
 * @author JiangZhiYong
 * @date 2017年12月3日
 * @mail 359135103@qq.com
 * @param <T>
 *            连接关系
 */
public class KNodeConnector<T extends Block> implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;


    /**
     * 计算节点连接关系
     * 
     * @param block
     *            当前方块
     * @param blocks
     *            所有方块
     * @param maxConnectionDistance
     *            两节点关系之间的最大距离
     */
    public void calculateKNodeConnection(T block, List<T> blocks, double maxConnectionDistance) {
        if (blocks.contains(block)) {    // 当前块不在块组中
            return;
        }
        // 清空块中节点数据
        resetBlockNodes(block);

        // 标记在多边形内部的节点，不进行关联关系处理
        checkKPolygonContained(block, blocks);

        // 检测周围任意方块顶点坐标相连的线段和当前方块是否相交，如果相交，则移除和多边形相交的顶点节点，即保证任何线段都不能和多边形相交
        checkKPolygonIntersection(block, blocks);

        // 计算连接当前方块和周围所有节点的关系
        for (TriangleKNode knode : block.getNodes()) {
            knode.clearConnectedNodes();
            // 凹节点和多边形内部的节点忽略不要
            if (knode.isConcave() || knode.getContained() == TriangleKNode.TRUE_VALUE) {
                continue;
            }
            connectKNode(knode, block, maxConnectionDistance, blocks);
        }
    }

    /**
     * 标记检测在多边形内部的节点，不进行关联关系处理
     * 
     * @param block
     *            当前块
     * @param nearBlocks
     *            周围所有快
     */
    private void checkKPolygonContained(T block, List<T> nearBlocks) {
        KPolygon kPolygon = block.getInnerPolygon(); // 原始多边形
        for (T nearBlock : nearBlocks) { // 遍历周围方块
            if (nearBlock == block) {    // 本身
                continue;
            }
            for (TriangleKNode node : nearBlock.getNodes()) { // 变量多边形的节点
                if (kPolygon.getCenter().distanceSq(node.getPoint()) <= kPolygon.getRadiusSq()) { // 当前多边形中心点到其他节点距离的平方<=小于多边形半径的平方
                    boolean contained = kPolygon.contains(node.getPoint());
                    if (contained) {
                        node.setContained(TriangleKNode.TRUE_VALUE); // 节点在多边形内部
                        node.clearConnectedNodes();     // 清除当前节点和其他节点的关系
                    }
                }
            }
        }
    }

    /**
     * 检测周围任意方块顶点坐标相连的线段和当前方块是否相交，如果相交，则移除和多边形相交的顶点节点，
     * 即保证任何线段都不能和多边形相交
     * 
     * @param block
     *            当前块
     * @param nearBlocks
     *            周围所有快
     */
    private void checkKPolygonIntersection(T block, List<T> nearBlocks) {
        KPolygon kPolygon = block.getInnerPolygon(); // 原始多边形
        for (int i = 0; i < nearBlocks.size(); i++) {
            T nearBlock = nearBlocks.get(i); // 周围方块
            if (nearBlock == block) {
                continue;
            }
            for (int j = 0; j < nearBlock.getNodes().size(); j++) { // 遍历所有其他节点
                TriangleKNode node = nearBlock.getNodes().get(j); // 节点1
                List<KNode> reachableNodes = node.getConnectedNodes();
                for (int k = 0; k < reachableNodes.size(); k++) {
                    KNode node2 = reachableNodes.get(k); // 节点2=和节点1想连接的点
                    if (kPolygon.intersectionPossible(node.getPoint(), node2.getPoint()) // 周围任意顶点线段和该多边形是否相交
                            && kPolygon.intersectsLine(node.getPoint(), node2.getPoint())) {
                        // delete startNode's reachable KNode.
                        reachableNodes.remove(k);
                        // delete node2's reachable KNode too.
                        int index = node2.getConnectedNodes().indexOf(node);
                        node2.getConnectedNodes().remove(index);
                        k--;
                        continue;
                    }
                }
            }
        }
    }

    /**
     * 生成当前节点和周围节点的关系
     * 
     * @param knode
     *            节点
     * @param block
     *            当前节点所在方块
     * @param maxConnectionDistance
     *            最大连接距离
     * @param blocks
     *            所有方块
     */
    public void connectKNode(TriangleKNode knode, Block block, double maxConnectionDistance, List<T> blocks) {
        // make new connections between new block and every other block.
        // To optimise the line-block intersection testing, we'll order the
        // block list by their distance to the startNode, smallest first.
        // These closer block are more likely to intersect any lines from
        // the startNode to the far away block nodes. Dump the block in a new list,
        // along with their distance to the startNode.
        
        
        List<BlockDistanceComarable> blockDistances = new ArrayList<>();    // 当前节点和所有多边形的距离
        Vector3 point = knode.getPoint();   //节点坐标
        for (int n = 0; n < blocks.size(); n++) {
            Block nearBlock = blocks.get(n);
            double distance = point.distance(nearBlock.getInnerPolygon().getCenter()) - nearBlock.getInnerPolygon().getRadius(); // 外边距
            blockDistances.add(new BlockDistanceComarable(nearBlock, distance));
        }
        
        // Sort the list.
        Collections.sort(blockDistances);

        // 检测节点是否在多边形内，在多边形内跳出处理
        if (knode.getContained() == TriangleKNode.UNKNOWN_VALUE) {
            // Calculate if the startNode is contained and cache the result.
            // This speeds up this method and the 'makeReachableNodesFor' method
            // significantly.
            for (int i = 0; i < blockDistances.size(); i++) {
                Block nearBlock = blockDistances.get(i).getBlock();
                if (block == nearBlock) {
                    continue;
                }
                if (blockDistances.get(i).getDistance() > 0) {    // 距离最近的多边形到当前顶点大于0，即不相交
                    // Break this checking loop since all of the rest of the
                    // block must be too far away to possibly overlap any
                    // points in testOb1's polygon.
                    break;
                }
                KPolygon nearKPolygon = nearBlock.getInnerPolygon();
                if (nearKPolygon.contains(knode.getPoint())) {
                    knode.setContained(TriangleKNode.TRUE_VALUE);
                    break;
                }
            }
            //多边形内部节点，忽略
            if (knode.getContained() == TriangleKNode.TRUE_VALUE) {
                return;
            } else {
                knode.setContained(TriangleKNode.FALSE_VALUE);
            }
        }
        
//        //TODO 2017-12-10 {@link reConnectNodeAfterChecks}
//        // Test the startNode for straight lines to nodes in other
//        // polygons (including testOb1 itself).
//        for (int k = 0; k < obstaclesToIntersect.size(); k++) {
//            PathBlockingObstacle testOb2 = obstaclesToIntersect.get(k);
//            ArrayList<KNodeOfObstacle> testOb2Nodes = testOb2.getNodes();   // 周围多边形的节点
//            // float halfNodeToNode2Dist =
//            // startNode.getPoint().distance(testOb2.getInnerPolygon().getCenter())
//            // + polygon.getRadius() + testOb2.getInnerPolygon().getRadius();
//            NodeLoop: for (int m = 0; m < testOb2Nodes.size(); m++) {
//                // Don't test a 'line' from the exact same points in the same
//                // polygon.
//                KNodeOfObstacle node2 = testOb2Nodes.get(m);    // 其他节点
//                // Test to see if it's ok to ignore this startNode since it's
//                // concave (inward-pointing) or it's contained by an obstacle.
//                if (node2 == node || node2.isConcave() || node2.getContained() == KNodeOfObstacle.TRUE_VALUE) {
//                    continue;
//                }
//                Vector3 p2 = node2.getPoint();
//                double nodeToNode2Dist = point.distance(p2);
//                if (nodeToNode2Dist > maxConnectionDistance) {  // 距离大于最大连接距离
//                    continue;
//                }
//
//                // Only connect the nodes if the connection will be useful.
//                if (isConnectionPossibleAndUseful(node, node.getPointNum(), obstacle.getNodes(), node2, m, testOb2Nodes) == false) {
//                    continue;
//                }
//
//                if (testOb2.getInnerPolygon().intersectsLine(node.getPoint(), node2.getPoint())) {
//                    continue NodeLoop;
//                }
//
//                // Need to test if line from startNode to node2 intersects any
//                // obstacles
//                // Also test if any startNode is contained in any obstacle.
//                ObstacleLoop: for (int n = 0; n < blockDistances.size(); n++) {
//                    if (blockDistances.get(n).getDist() > nodeToNode2Dist) {
//                        // Break this checking loop since all of the rest of the
//                        // obstacles
//                        // must be too far away to possibly overlap the line
//                        // from startNode to node2
//                        // System.out.println("NodeConnector: breaking at i == "+n+" out of obstAndDists.size() == "+obstAndDists.size());
//                        break;
//                    }
//                    PathBlockingObstacle testOb3 = blockDistances.get(n).getObst();
//                    KPolygon innerPolygon = testOb3.getInnerPolygon();
//                    if (testOb3 == testOb2) {
//                        continue;
//                    }
//                    if (innerPolygon.intersectionPossible(node.getPoint(), node2.getPoint()) == false) {
//                        continue;
//                    }
//                    // Check that startNode is not inside testOb3
//                    if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE && innerPolygon.contains(node.getPoint())) {
//                        continue NodeLoop;
//                    }
//                    if (innerPolygon.intersectsLine(node.getPoint(), node2.getPoint())) {
//                        continue NodeLoop;
//                    }
//                }
//                if (node.getConnectedNodes().contains(node2)) {
//                    continue;
//                }
//                node.getConnectedNodes().add(node2);
//                if (node2.getConnectedNodes().contains(node)) {
//                    continue;
//                }
//                node2.getConnectedNodes().add(node);
//            }
//        }
    }

    /**
     * 重置块中的节点数据
     * 
     * @param block
     */
    public void resetBlockNodes(T block) {
        for (int j = 0; j < block.getNodes().size(); j++) {
            KNode node = block.getNodes().get(j);
            node.getConnectedNodes().clear();
            node.getTempConnectedNodes().clear();
            if (node instanceof TriangleKNode) {
                ((TriangleKNode) node).resetContainedToUnknown();
            }
        }
    }
}
