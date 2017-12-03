package com.jzy.game.ai.unity.nav;

import java.io.Serializable;
import java.util.List;

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
        resetObstacleNodes(block);  //清空块中节点数据
        KPolygon kPolygon = block.getInnerPolygon(); // 原始多边形
        List<T> nearBlocks = blocks; // 周围方块
        // Any nodes that may be contained need to be marked as so.
        //TODO 待完成2017-12-3
//        for (T nearBlock : nearBlocks) { // 遍历周围方块
//            if (nearBlock == block) {    // 本身
//                continue;
//            }
//            for (TriangleKNode node : nearBlock.getNodes()) { // 变量多边形的节点
//                if (kPolygon.getCenter().distanceSq(node.getPoint()) <= kPolygon.getRadiusSq()) { // 当前多边形中心点到其他节点距离的平方<=小于多边形半径的平方
//                    boolean contained = kPolygon.contains(node.getPoint());
//                    if (contained) {
//                        node.setContained(KNodeOfObstacle.TRUE_VALUE); // 节点在多边形内部
//                        node.clearConnectedNodes();
//                    }
//                }
//            }
//        }
//
//        // check if the new obstacle obstructs any startNode connections, and if
//        // it does, delete them.
//        KPolygon polygon = obst.getInnerPolygon();
//        for (int i = 0; i < nearBlocks.size(); i++) {
//            PathBlockingObstacle testOb1 = nearBlocks.get(i); // 多边形关系类
//            if (testOb1 == obst) {
//                continue;
//            }
//            for (int j = 0; j < testOb1.getNodes().size(); j++) { // 遍历所有其他节点
//                KNodeOfObstacle node = testOb1.getNodes().get(j); // 节点1
//                ArrayList<KNode> reachableNodes = node.getConnectedNodes();
//                for (int k = 0; k < reachableNodes.size(); k++) {
//                    KNode node2 = reachableNodes.get(k); // 节点2=和节点1想连接的点
//                    if (polygon.intersectionPossible(node.getPoint(), node2.getPoint()) // 周围任意顶点线段和该多边形是否相交
//                            && polygon.intersectsLine(node.getPoint(), node2.getPoint())) {
//                        // delete startNode's reachable KNode.
//                        reachableNodes.remove(k);
//                        // delete node2's reachable KNode too.
//                        int index = node2.getConnectedNodes().indexOf(node);
//                        // if (index > -1) {
//                        node2.getConnectedNodes().remove(index);
//                        // }
//                        k--;
//                        continue;
//                    }
//                }
//            }
//        }
//        // connect the obstacle's nodes with all nearby nodes.
//        for (KNodeOfObstacle node : obst.getNodes()) {
//            reConnectNode(node, obst, maxConnectionDistance, obstacles);
//        }
//        // long endTime = System.nanoTime();
//        // System.out.println(this.getClass().getSimpleName()+".addObstacle running time = "+((endTime
//        // - startTime)/1000000000f));系类
    }

    /**
     * 重置块中的节点数据
     * 
     * @param block
     */
    public void resetObstacleNodes(T block) {
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
