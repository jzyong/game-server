package com.jzy.game.ai.unity.nav;

import java.io.Serializable;

/**
 * 三角形顶点节点
 * 
 * @author JiangZhiYong
 * @date 2017年12月3日
 * @mail 359135103@qq.com
 */
public class TriangleKNode extends KNode implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    public static final int FALSE_VALUE = 0;
    public static final int TRUE_VALUE = 1;
    public static final int UNKNOWN_VALUE = 2;

    protected int pointNum; // 多边形kPolygon 坐标点序号 0,1,2
    protected boolean concave; // 是否为凹点
    protected int contained;  //包含状态 当前节点是否在其他多边形内部

    /**
     * @param block
     *            所属块
     * @param pointNum
     *            节点坐标序号 0,1,2
     */
    public TriangleKNode(Block block, int pointNum) {
        super(block.getOuterPolygon().getPoint(pointNum).copy());
        // this.obstacle = obstacle;
        this.pointNum = pointNum;
        calcConcave(block);
        contained = UNKNOWN_VALUE;
    }
    
    /**
     * 
     * 是否为凹多边形
     *  @note 应该都为false，三角形不可能是凹的
     * @param triangleKNode
     */
    public void calcConcave(Block block) {
        KPolygon polygon = block.getOuterPolygon();
        int pointBeforeNum = (pointNum - 1 < 0 ? polygon.getPoints().size() - 1 : pointNum - 1);
        int pointAfterNum = (pointNum + 1 >= polygon.getPoints().size() ? 0 : pointNum + 1);
        Vector3 pointBefore = polygon.getPoint(pointBeforeNum); //前一顶点
        Vector3 pointAfter = polygon.getPoint(pointAfterNum); //后一顶点
        if (polygon.isCounterClockWise()) {     //顶点是否为逆时针排序
            if (point.relCCW(pointBefore, pointAfter) > 0) {
                concave = true;
            } else {
                concave = false;
            }

        } else {
            if (point.relCCW(pointBefore, pointAfter) < 0) {
                concave = true;
            } else {
                concave = false;
            }
        }
    }
    
    /**
     * 标示节点为未处理
     */
    public void resetContainedToUnknown() {
        contained = UNKNOWN_VALUE;
    }

    public void setContained(int contained) {
        this.contained = contained;
    }
    
    public int getContained() {
        return contained;
    }
    
    /**
     * 是否为凹点
     * @return
     */
    public boolean isConcave() {
        return concave;
    }

}
