package com.jzy.game.ai.nav.node;



import java.io.Serializable;

import com.jzy.game.engine.math.Vector3;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * 计算多边形周围缓冲区多边形<br>
 * 如将阻挡多边形边界向外移动
 * @author JiangZhiYong
 * @date 2017年12月3日 
 * @mail 359135103@qq.com
 */
public class PolygonBufferer implements Serializable {
    private static final long serialVersionUID = 1L;
    public PolygonConverter polygonConverter = new PolygonConverter();

    public PolygonBufferer() {
    }

    /**计算周围缓冲区
     * 
     * @param originalPolygon 原始多边形
     * @param bufferAmount 缓存数量，向外移动距离
     * @param numPointsInAQuadrant 象限
     * @return
     */
    public KPolygon buffer(KPolygon originalPolygon, double bufferAmount, int numPointsInAQuadrant) {
        if (bufferAmount == 0) {    //返回原始多边形的克隆体
            return originalPolygon.copy();
        }
        Polygon polygon = polygonConverter.makePolygonFrom(originalPolygon);
        Polygon buffererPolygon = null; //缓存的多边形
        polygon.setSRID(numPointsInAQuadrant);
        Geometry bufferedGeometry = polygon.buffer(bufferAmount, numPointsInAQuadrant);
        if (bufferedGeometry instanceof Polygon) {
            buffererPolygon = (Polygon) bufferedGeometry;
        } else if (bufferedGeometry instanceof MultiPolygon) {
            MultiPolygon multiPolygon = (MultiPolygon) bufferedGeometry;
            // use the first polygon
            buffererPolygon = (Polygon) multiPolygon.getGeometryN(0);
        } else {
            System.err.println(this.getClass().getSimpleName() + ": JTS didn't make a proper polygon, this might be because the outerPolygon is too small, so that after it's shrunk, it disappears or makes more than one Polygon.");
            return null;
        }
        KPolygon bufferedPolygon = polygonConverter.makeKPolygonFromExterior(buffererPolygon);
        if (bufferedPolygon == null) {
            System.out.println(this.getClass().getSimpleName() + ": bufferedPolygon == null");
            System.out.println(this.getClass().getSimpleName() + ": bufferedGeometry.getClass().getSimpleName() == " + bufferedGeometry.getClass().getSimpleName());
            System.out.println(this.getClass().getSimpleName() + ": bufferedJTSPolygon.getCoordinates().length == " + buffererPolygon.getCoordinates().length);
            for (int j = 0; j < buffererPolygon.getCoordinates().length; j++) {
                System.out.println(this.getClass().getSimpleName() + ": " + buffererPolygon.getCoordinates()[j].x + ", " + buffererPolygon.getCoordinates()[j].y);
            }
            System.out.println(this.getClass().getSimpleName() + ": originalPolygon.toString() == " + originalPolygon.toString());
            return null;
        }
        //顺序不同，选转坐标方向
        if (bufferedPolygon.isCounterClockWise() != originalPolygon.isCounterClockWise()) {
            bufferedPolygon.reversePointOrder();
        }
        //重新设置y坐标高度
        for (Vector3 point : bufferedPolygon.getPoints()) {
            for (Vector3 ori : originalPolygon.getPoints()) {
                if (Math.abs(point.x - ori.x) <= TriangleBlock.BUFFER_AMOUNT && Math.abs(point.z - ori.z) <= TriangleBlock.BUFFER_AMOUNT) {
                    point.y = ori.y;
                    break;
                }
            }
            if (point.y != point.y) {
                point.y = 0;
            }
        }
        return bufferedPolygon;
    }
}
