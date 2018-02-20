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
        // 多边形中点坐标距离 是否需要各个变的距离取最小值？
        return node.center.dst(endNode.center);
    }

}
