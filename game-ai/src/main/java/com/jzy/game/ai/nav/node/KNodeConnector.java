package com.jzy.game.ai.nav.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jzy.game.engine.util.math.Vector3;

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
	double smallAmount = 0.01; // 距离修正系数 ，节点之间的最小距离，使用平方判定，因此0.1的误差

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
		if (!blocks.contains(block)) { // 当前块不在块组中
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
			if (nearBlock == block) { // 本身
				continue;
			}
			for (TriangleKNode node : nearBlock.getNodes()) { // 变量多边形的节点
				if (kPolygon.getCenter().dst2(node.getPoint()) <= kPolygon.getRadiusSq()) { // 当前多边形中心点到其他节点距离的平方<=小于多边形半径的平方
					boolean contained = kPolygon.contains(node.getPoint());
					if (contained) {
						node.setContained(TriangleKNode.TRUE_VALUE); // 节点在多边形内部
						node.clearConnectedNodes(); // 清除当前节点和其他节点的关系
					}
				}
			}
		}
	}

	/**
	 * 检测周围任意方块顶点坐标相连的线段和当前方块是否相交，如果相交，则移除和多边形相交的顶点节点， 即保证任何线段都不能和多边形相交
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

		List<BlockDistanceComarable> blockDistances = new ArrayList<>(); // 当前节点和所有多边形的距离
		Vector3 point = knode.getPoint(); // 节点坐标
		for (int n = 0; n < blocks.size(); n++) {
			Block nearBlock = blocks.get(n);
			double distance = point.distance(nearBlock.getInnerPolygon().getCenter())
					- nearBlock.getInnerPolygon().getRadius(); // 外边距
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
				if (blockDistances.get(i).getDistance() > 0) { // 距离最近的多边形到当前顶点大于0，即不相交
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
			// 多边形内部节点，忽略
			if (knode.getContained() == TriangleKNode.TRUE_VALUE) {
				return;
			} else {
				knode.setContained(TriangleKNode.FALSE_VALUE);
			}
		}

		// Test the startNode for straight lines to nodes in other
		// polygons (including testOb1 itself).
		for (int k = 0; k < blocks.size(); k++) {
			Block nearBlock = blocks.get(k);
			List<TriangleKNode> nearBlockNodes = nearBlock.getNodes(); // 周围多边形的节点
			NodeLoop: for (int m = 0; m < nearBlockNodes.size(); m++) {
				// Don't test a 'line' from the exact same points in the same
				// polygon.
				TriangleKNode knode2 = nearBlockNodes.get(m); // 其他节点
				// Test to see if it's ok to ignore this startNode since it's
				// concave (inward-pointing) or it's contained by an block.
				if (knode2 == knode || knode2.isConcave() || knode2.getContained() == TriangleKNode.TRUE_VALUE) {
					continue;
				}
				Vector3 point2 = knode2.getPoint();
				double nodeToNode2Dist = point.distance(point2); // 两节点距离
				if (nodeToNode2Dist > maxConnectionDistance) { // 距离大于最大连接距离
					continue;
				}

				// Only connect the nodes if the connection will be useful.
				if (isConnectionPossibleAndUseful(knode, knode.getPointNum(), block.getNodes(), knode2, m,
						nearBlockNodes) == false) {
					continue;
				}

				// 当前节点和多变形相交，跳出循环，继续检测其他节点
				if (nearBlock.getInnerPolygon().intersectsLine(knode.getPoint(), knode2.getPoint())) {
					continue NodeLoop;
				}

				// Need to test if line from startNode to node2 intersects any
				// block
				// Also test if any startNode is contained in any block.
				for (int n = 0; n < blockDistances.size(); n++) {
					if (blockDistances.get(n).getDistance() > nodeToNode2Dist) { // 距离超过最大距离
						// Break this checking loop since all of the rest of the blocks
						// must be too far away to possibly overlap the line
						// from startNode to node2
						break;
					}
					Block nearBlock2 = blockDistances.get(n).getBlock();
					KPolygon innerPolygon = nearBlock2.getInnerPolygon();
					if (nearBlock2 == nearBlock) { // 不检测自身方块
						continue;
					}
					// 开始节点和目标节点之间无多边形，跳出循环，目标节点合法
					if (innerPolygon.intersectionPossible(knode.getPoint(), knode2.getPoint()) == false) {
						continue;
					}
					// Check that startNode is not inside nearBlock2
					// 当前节点在连线多边形内部，目标点非法，检测下一个目标点
					if (knode.getContained() == TriangleKNode.UNKNOWN_VALUE
							&& innerPolygon.contains(knode.getPoint())) {
						continue NodeLoop;
					}
					if (innerPolygon.intersectsLine(knode.getPoint(), knode2.getPoint())) {
						continue NodeLoop;
					}
				}
				// 已经包含
				if (knode.getConnectedNodes().contains(knode2)) {
					continue;
				}
				knode.getConnectedNodes().add(knode2);
				if (knode2.getConnectedNodes().contains(knode)) {
					continue;
				}
				knode2.getConnectedNodes().add(knode);
			}
		}
	}

	/**
	 * 连接点是否有效，拐点算法
	 * 
	 * <br>
	 * 验证两顶点坐标所在直线是否在三角形另外两边之中，双向验证
	 * 
	 * @param node
	 *            当前节点
	 * @param nodePointNum
	 *            节点序号
	 * @param nodeList
	 * @param node2
	 *            周围节点
	 * @param node2PointNum
	 * @param node2List
	 * @return
	 */
	protected boolean isConnectionPossibleAndUseful(TriangleKNode node, int nodePointNum, List<TriangleKNode> nodeList,
			TriangleKNode node2, int node2PointNum, List<TriangleKNode> node2List) {
		Vector3 p = node.getPoint();
		Vector3 p2 = node2.getPoint();
		{
			// test if startNode is in the reject region of node2's block
			// 验证当前节点node和节点node2的连线是否在当前节点和三角形另外两个顶点连线之中
			// Only connect the nodes if the connection will be useful.
			// See the comment in the method makeReachableNodes for a full explanation.
			KNode node2Minus = node2List.get(node2PointNum - 1 < 0 ? node2List.size() - 1 : node2PointNum - 1);
			KNode node2Plus = node2List.get(node2PointNum + 1 >= node2List.size() ? 0 : node2PointNum + 1);
			Vector3 p2Minus = node2Minus.getPoint(); // 周围方块前节点坐标
			Vector3 p2Plus = node2Plus.getPoint(); // 周围方块后节点坐标

			double p2MinusToP2RCCW = p.relCCWDouble(p2Minus, p2);
			double p2ToP2PlusRCCW = p.relCCWDouble(p2, p2Plus);
			if (p2MinusToP2RCCW * p2ToP2PlusRCCW > 0) { // 节点1和节点2连线在三角形2另外两个顶点之中
				// To avoid floating point error problems we should only return false
				// if p is well away from the lines. If it's close, then return
				// true just to be safe. Returning false when the connection is
				// actually useful is a much bigger problem than returning true
				// and sacrificing some performance.
				double p2MinusToP2LineDistSq = p.ptLineDistSq(p2Minus, p2); // 开始节点到多边形左边距离
				if (p2MinusToP2LineDistSq < smallAmount) {
					return true;
				}
				double p2ToP2PlusLineDistSq = p.ptLineDistSq(p2, p2Plus); // 开始节点到多边形右边距离
				if (p2ToP2PlusLineDistSq < smallAmount) {
					return true;
				}
				// Since p is anti-clockwise to both lines p2MinusToP2 and p2ToP2Plus
				// (or it is clockwise to both lines) then the connection betwen
				// them will not be useful so return .
				return false;
			}
		}

		// 反方向验证
		// test if node2 is in the reject region of node1's obstacle
		{
			// Only connect the nodes if the connection will be useful.
			// See the comment in the method makeReachableNodes for a full explanation.
			KNode nodeMinus = nodeList.get(nodePointNum - 1 < 0 ? nodeList.size() - 1 : nodePointNum - 1);
			KNode nodePlus = nodeList.get(nodePointNum + 1 >= nodeList.size() ? 0 : nodePointNum + 1);
			Vector3 pMinus = nodeMinus.getPoint();
			// KPoint p2 = node2.getPoint();
			Vector3 pPlus = nodePlus.getPoint();
			double pMinusToPRCCW = p2.relCCWDouble(pMinus, p);
			double pToPPlusRCCW = p2.relCCWDouble(p, pPlus);
			if (pMinusToPRCCW * pToPPlusRCCW > 0) {
				// To avoid floating point error problems we should only return false
				// if p is well away from the lines. If it's close, then return
				// true just to be safe. Returning false when the connection is
				// actually useful is a much bigger problem than returning true
				// and sacrificing some performance.
				double pMinusToPLineDistSq = p2.ptLineDistSq(pMinus, p);
				if (pMinusToPLineDistSq < smallAmount) {
					return true;
				}
				double pToPPlusLineDistSq = p2.ptLineDistSq(p, pPlus);
				if (pToPPlusLineDistSq < smallAmount) {
					return true;
				}
				// Since p is anti-clockwise to both lines p2MinusToP2 and p2ToP2Plus
				// (or it is clockwise to both lines) then the connection betwen
				// them will not be useful so return .
				return false;
			}
		}
		return true;
	}

	/**
	 * 连接点是否有效，拐点算法
	 * 
	 * @param node
	 *            当前节点
	 * @param node2
	 *            远处节点
	 * @param node2PointNum
	 *            远处节点序号
	 * @param node2List
	 *            远处节点所在多边形的节点列表
	 * @return
	 */
	protected boolean isConnectionPossibleAndUseful(KNode node, KNode node2, int node2PointNum,
			List<TriangleKNode> node2List) {
		Vector3 p = node.getPoint();
		Vector3 p2 = node2.getPoint();
		// test if startNode is in the reject region of node2's block
		{
			// Only connect the nodes if the connection will be useful.
			// See the comment in the method makeReachableNodes for a full explanation.
			KNode node2Minus = node2List.get(node2PointNum - 1 < 0 ? node2List.size() - 1 : node2PointNum - 1);
			KNode node2Plus = node2List.get(node2PointNum + 1 >= node2List.size() ? 0 : node2PointNum + 1);
			Vector3 p2Minus = node2Minus.getPoint();
			Vector3 p2Plus = node2Plus.getPoint();

			// double p2MinusToP2RCCW = p.c(p2Minus, p2);
			double p2LessP2MinusX = p2.x - p2Minus.x;
			double p2LessP2MinusY = p2.z - p2Minus.z;
			double pLessP2MinusX = p.x - p2Minus.x;
			double pLessP2MinusY = p.z - p2Minus.z;
			double p2MinusToP2RCCW = pLessP2MinusY * p2LessP2MinusX - pLessP2MinusX * p2LessP2MinusY;

			// double p2ToP2PlusRCCW = p.c(p2, p2Plus);
			double pLessP2X = p.x - p2.x;
			double pLessP2Y = p.z - p2.z;
			double p2PlusLessP2X = p2Plus.x - p2.x;
			double p2PlusLessP2Y = p2Plus.z - p2.z;
			double p2ToP2PlusRCCW = pLessP2Y * p2PlusLessP2X - pLessP2X * p2PlusLessP2Y;

			if (p2MinusToP2RCCW * p2ToP2PlusRCCW > 0) {

				// To avoid floating point error problems we should only return false
				// if p is well away from the lines. If it's close, then return
				// true just to be safe. Returning false when the connection is
				// actually useful is a much bigger problem than returning true
				// and sacrificing some performance.
				{
					double dotprod = pLessP2MinusX * p2LessP2MinusX + pLessP2MinusY * p2LessP2MinusY;
					// dotprod is the length of the px,py vector
					// projected on the x1,y1=>x2,y2 vector times the
					// length of the x1,y1=>x2,y2 vector
					double projlenSq = dotprod * dotprod
							/ (p2LessP2MinusX * p2LessP2MinusX + p2LessP2MinusY * p2LessP2MinusY);
					// Distance to line is now the length of the relative point
					// vector minus the length of its projection onto the line
					double p2MinusToP2LineDistSq = pLessP2MinusX * pLessP2MinusX + pLessP2MinusY * pLessP2MinusY
							- projlenSq;
					if (p2MinusToP2LineDistSq < smallAmount) {
						return true;
					}
				}

				{
					double dotprod = pLessP2X * p2PlusLessP2X + pLessP2Y * p2PlusLessP2Y;
					// dotprod is the length of the px,py vector
					// projected on the x1,y1=>x2,y2 vector times the
					// length of the x1,y1=>x2,y2 vector
					double projlenSq = dotprod * dotprod
							/ (p2PlusLessP2X * p2PlusLessP2X + p2PlusLessP2Y * p2PlusLessP2Y);
					// Distance to line is now the length of the relative point
					// vector minus the length of its projection onto the line
					double p2ToP2PlusLineDistSq = pLessP2X * pLessP2X + pLessP2Y * pLessP2Y - projlenSq;
					if (p2ToP2PlusLineDistSq < smallAmount) {
						return true;
					}
				}
				// Since p is anti-clockwise to both lines p2MinusToP2 and p2ToP2Plus
				// (or it is clockwise to both lines) then the connection betwen
				// them will not be useful so return .
				return false;
			}
		}
		return true;
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

	/**
	 * 移除一个块
	 * 
	 * @param block
	 * @param maxConnectionDistance
	 * @param blocks
	 */
	public void removeBlock(T block, double maxConnectionDistance, List<T> blocks) {
		for (TriangleKNode node : block.getNodes()) {
			node.clearConnectedNodes();
			node.setContained(TriangleKNode.UNKNOWN_VALUE);
			connectKNode(node, block, maxConnectionDistance, blocks);
		}
	}

	/**
	 * 寻路搜索可到达点
	 * 
	 * @param node
	 * @param maxConnectionDistance
	 *            最大连接距离
	 * @param blocks
	 * @return
	 */
	public List<KNode> makeReachableNodesFor(KNode node, double maxConnectionDistance, List<T> blocks) {
		// To optimise the line-block intersection testing, order the block list
		// by their distance to the startNode, smallest first.
		// These closer block are more likely to intersect any lines from the
		// startNode to the far away block nodes.
		// Dump the blocks in a new list, along with their distance to the startNode.
		List<BlockDistanceAndQuad> blockDistanceAndQuads = new ArrayList<>();
		// add the blocks to blockDistanceAndQuads
		Vector3 p = node.getPoint();
		for (int n = 0; n < blocks.size(); n++) {
			KPolygon poly = blocks.get(n).getInnerPolygon();
			double distEyeToCenterLessRadius = p.distance(poly.getCenter()) - poly.getRadius();
			// Note that distCenterToEyeLessCircBound can be negative.
			double distEyeToCenterLessRadiusSqSigned = distEyeToCenterLessRadius * distEyeToCenterLessRadius;
			if (distEyeToCenterLessRadius < 0) {
				// to preserve the sign of the original number:
				distEyeToCenterLessRadiusSqSigned *= -1;
			}
			int xIndicator = getXIndicator(p, poly);
			int yIndicator = getYIndicator(p, poly);
			BlockDistanceAndQuad obstDistAndQuad = new BlockDistanceAndQuad((TriangleBlock) blocks.get(n),
					distEyeToCenterLessRadiusSqSigned, xIndicator, yIndicator);
			blockDistanceAndQuads.add(obstDistAndQuad);
		}
		// Sort the list.
		Collections.sort(blockDistanceAndQuads);
		// codeTimer.click("connect");

		double maxConnectionDistanceSq = maxConnectionDistance * maxConnectionDistance;

		// Test for straight lines between the startNode and nodes of blocks that don't
		// intersect any blocks.
		List<KNode> reachableNodes = new ArrayList<>();
		for (int k = 0; k < blockDistanceAndQuads.size(); k++) { // 遍历所有周围块
			BlockDistanceAndQuad blockDistAndQuad = blockDistanceAndQuads.get(k);
			TriangleBlock nearBlock = blockDistAndQuad.getBlock();
			List<TriangleKNode> nearBlockNodes = nearBlock.getNodes();
			NodeLoop: for (int m = 0; m < nearBlockNodes.size(); m++) { // 遍历块节点
				TriangleKNode nearBlockNode = nearBlockNodes.get(m);
				// Ignore this startNode since it's concave (inward-pointing)
				if (nearBlockNode.isConcave() || nearBlockNode.getContained() == TriangleKNode.TRUE_VALUE) {
					continue;
				}

				// 不是有效连接点，跳出检测其他节点
				if (isConnectionPossibleAndUseful(node, nearBlockNode, m, nearBlockNodes) == false) {
					continue;
				}
				Vector3 p2 = nearBlockNode.getPoint();
				// Need to test if line from startNode to node2 intersects any
				// blocks (including nearBlock itself).
				// Should check closest node first, and block whose points were found to be
				// reachable
				double nodeToNode2DistSq = p.dst2(p2);
				if (nodeToNode2DistSq > maxConnectionDistanceSq) { // 超过最大距离
					continue;
				}

				if (nearBlock.getInnerPolygon().intersectsLine(p, p2)) { // 相交，检测其他节点
					continue NodeLoop;
				}
				for (int n = 0; n < blockDistanceAndQuads.size(); n++) {
					BlockDistanceAndQuad obstDistAndQuad2 = blockDistanceAndQuads.get(n);
					TriangleBlock nearBlock2 = obstDistAndQuad2.getBlock();
					KPolygon innerPolygon = nearBlock2.getInnerPolygon();
					if (nearBlock2 == nearBlock) {
						continue;
					}
					// Test if nearBlock2.getInnerPolygon() intersects the line from startNode to
					// node2
					if (blockDistanceAndQuads.get(n).getDistNodeToCenterLessRadiusSqSigned() > nodeToNode2DistSq) {
						// Break this checking loop since all of the rest of the blocks
						// must be too far away to possibly overlap the line from startNode to node2
						break;
					}
					// 不在同一个方向
					if (blockDistAndQuad.getXIndicator() * obstDistAndQuad2.getXIndicator() == -1
							|| blockDistAndQuad.getYIndicator() * obstDistAndQuad2.getYIndicator() == -1) {
						continue;
					}
					if (innerPolygon.intersectionPossible(p, p2) == false) {
						continue;
					}
					// Check that node2 is not inside testOb3
					if (nearBlockNode.getContained() == TriangleKNode.UNKNOWN_VALUE && innerPolygon.contains(p2)) {
						continue NodeLoop;
					}
					if (innerPolygon.intersectsLine(p, p2)) {
						continue NodeLoop;
					}
				}
				if (!reachableNodes.contains(nearBlockNode)) {
					reachableNodes.add(nearBlockNode);
				}
			}
		}
		return reachableNodes;
	}

	/**
	 * x位置标识，unity坐标
	 * 
	 * @param p
	 *            坐标点
	 * @param poly
	 *            多边形
	 * @return -1 点p在多边形上面 1 p在多边形下面
	 */
	protected int getXIndicator(Vector3 p, KPolygon poly) {
		int xIndicator;
		double relX = poly.getCenter().x - p.getX();
		if (relX - poly.getRadius() > 0) {
			xIndicator = 1;
		} else if (relX + poly.getRadius() < 0) {
			xIndicator = -1;
		} else {
			xIndicator = 0;
		}
		return xIndicator;
	}

	/**
	 * 对应unity中的z坐标
	 * 
	 * @param p
	 * @param poly
	 * @return
	 */
	protected int getYIndicator(Vector3 p, KPolygon poly) {
		int yIndicator;
		double relY = poly.getCenter().z - p.getZ();
		if (relY - poly.getRadius() > 0) {
			yIndicator = 1;
		} else if (relY + poly.getRadius() < 0) {
			yIndicator = -1;
		} else {
			yIndicator = 0;
		}
		return yIndicator;
	}
}
