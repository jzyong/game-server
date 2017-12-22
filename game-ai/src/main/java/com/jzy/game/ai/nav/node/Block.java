package com.jzy.game.ai.nav.node;

import java.util.List;

/**
 * 对多边形重新计算的块
 * <br>包含了多边形、顶点节点
 * @author JiangZhiYong
 * @date 2017年12月3日
 * @mail 359135103@qq.com
 */
public interface Block extends PolygonHolder{

    /**
     * 节点列表
     * @return
     */
    public List<TriangleKNode> getNodes();

    /**
     *指向多边形，阻挡层：是原始多边形向往扩展后的多边形
     * @return
     */
    public KPolygon getOuterPolygon();

    /**
     * 
     * 起点多边形，原始多边形
     * @return
     */
    public KPolygon getInnerPolygon();
}
