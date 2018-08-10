package com.jzy.game.ai.nav.polygon;

import com.jzy.game.ai.pfa.Heuristic;

/**
 * 多边形消耗计算
 * 
 * @author JiangZhiYong
 * @date 2018年2月20日
 * @mail 359135103@qq.com
 */
public class PolygonHeuristic implements Heuristic<Polygon> {

	@Override
	public float estimate(Polygon node, Polygon endNode) {
		// 多边形中点坐标距离 是否需要各个边的距离取最小值？
		//理论曼哈顿要快要欧几里得，单实际测试不是
		

		// 曼哈顿启发因子
		// return Math.abs(node.center.x - endNode.center.x) + Math.abs(node.center.z -
		// endNode.center.z);

		// 欧几里得 距离启发因子
		return node.center.dst(endNode.center);
	}

}
