package com.jzy.game.ai.nav.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jzy.game.engine.util.math.Vector3;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * 三角形块
 * <br>
 * 将原始的三角形网格进行加工后的数据，包含原始多边形，重新计算后的多边线，顶点坐标节点，块中的随机坐标点
 * 
 * @author JiangZhiYong
 * @date 2017年12月3日
 * @mail 359135103@qq.com
 */
public class TriangleBlock implements Serializable, Block, Cloneable {
    private static final long serialVersionUID = 1L;
    public static float BUFFER_AMOUNT = 3f; // 缓存数量及矫正y坐标的误差
    public static int NUM_POINTS_IN_A_QUADRANT = 0;

    protected KPolygon outerPolygon; // 外多边形 重新计算后的多边形，阻挡层是为六边形
    protected KPolygon innerPolygon; // 内多边形 原始三角形

    public List<TriangleKNode> nodes;   // 三角形顶点节点
    private List<Vector3> randomPoints = new ArrayList<>();  // 多边形内部的随机点
    private int randomNum;  // 随机个数

    /**
     * @param outerPolygon
     *            重新计算后的多边形，阻挡层是为六边形
     * @param innerPolygon
     *            原始三角形
     */
    public TriangleBlock(KPolygon outerPolygon, KPolygon innerPolygon) {
        this.outerPolygon = outerPolygon;
        this.innerPolygon = innerPolygon;
        resetNodes();
    }

    /**
     * 重置节点
     */
    public void resetNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
            for (int i = 0; i < this.outerPolygon.getPoints().size(); i++) {
                nodes.add(new TriangleKNode(this, i));
            }
        } else if (nodes.size() != getOuterPolygon().getPoints().size()) {
            nodes.clear();
            for (int i = 0; i < this.outerPolygon.getPoints().size(); i++) {
                nodes.add(new TriangleKNode(this, i));
            }
        } else {
            for (int j = 0; j < nodes.size(); j++) {
                TriangleKNode node = nodes.get(j);
                Vector3 outerPolygonPoint = getOuterPolygon().getPoint(j);
                node.getPoint().x = outerPolygonPoint.x;    // 修正坐标
                node.getPoint().z = outerPolygonPoint.z;
            }
        }
    }


    @Override
    public KPolygon getPolygon() {
        return this.innerPolygon;
    }

    @Override
    public List<TriangleKNode> getNodes() {
        return this.nodes;
    }

    @Override
    public KPolygon getOuterPolygon() {
        return this.outerPolygon;
    }

    @Override
    public KPolygon getInnerPolygon() {
        return this.innerPolygon;
    }

    /**
     * 新建多边形连接关系，参数作为inner多边形对象，outer多边形包围inner的多边形(六边形)
     * 
     * @param outerPolygon
     *            作为inner多边形对象
     * @return
     */
    public static TriangleBlock createBlockFromInnerPolygon(KPolygon innerPolygon) {
        PolygonBufferer polygonBufferer = new PolygonBufferer();
        KPolygon outerPolygon = polygonBufferer.buffer(innerPolygon, BUFFER_AMOUNT, NUM_POINTS_IN_A_QUADRANT);
        if (outerPolygon == null) {
            // there was an error so return null;
            return null;
        }
        TriangleBlock triangleBlock = new TriangleBlock(outerPolygon, innerPolygon);
        return triangleBlock;
    }

    /**
     * 新建多边形连接对象，参数作为外多边形对象
     * <br>
     * 内外多边形一样
     * 
     * @param outerPolygon
     *            作为外多边形对象
     * @return
     */
    public static TriangleBlock createBlockFromOuterPolygon(KPolygon outerPolygon) {
        PolygonBufferer polygonBufferer = new PolygonBufferer();
        KPolygon innerPolygon = polygonBufferer.buffer(outerPolygon, 0, NUM_POINTS_IN_A_QUADRANT);  // 内外多边形为完全重合
        if (innerPolygon == null) {
            // there was an error so return null;
            return null;
        }
        TriangleBlock pathBlockingObstacleImpl = new TriangleBlock(outerPolygon, innerPolygon);
        return pathBlockingObstacleImpl;
    }

    /**
     * 创建随机点
     * 
     * @param points
     */
    public void addRandomPoints(Coordinate[] points) {
        for (Coordinate point : points) {
            Vector3 p = new Vector3((float)point.x, getOuterPolygon().getY(), (float)point.y);
            this.randomPoints.add(p);
        }
        randomNum = this.randomPoints.size();
    }

    public List<Vector3> getRandomPoints() {
        return randomPoints;
    }

    public int getRandomNum() {
        return randomNum;
    }
    
    
}
