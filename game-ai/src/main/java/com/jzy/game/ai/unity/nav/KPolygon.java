package com.jzy.game.ai.unity.nav;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义多边形,包括三角形<br>
 * 封装了在java2D图像显示，多边形可凸或凹，但不能相交
 * 
 * @author JiangZhiYong
 * @date 2017年12月3日
 * @mail 359135103@qq.com
 */
public class KPolygon implements Serializable, Cloneable, PolygonHolder, Shape {
    private static final long serialVersionUID = 1L;
    private List<Vector3> points;       // 顶点坐标
    protected boolean counterClockWise; // 多边形顶点是否为逆时针
    protected double area;              // 面积
    protected Vector3 center;           // 中心坐标
    protected double radius;            // 半径，中点到顶点的最大距离
    protected double y = -1;            //多边形的平均y轴高度
    protected double radiusSq;          // 半径的平方

    /**
     * @param pointsList
     *            顶点坐标
     */
    public KPolygon(List<Vector3> pointsList) {
        this(pointsList, true);
    }

    /**
     * @param pointsList
     *            顶点坐标
     * @param copyPoints
     *            true否复制坐标点
     */
    public KPolygon(List<Vector3> pointsList, boolean copyPoints) {
        if (pointsList.size() < 3) {
            throw new RuntimeException("最少需要三个顶点，当前为 " + pointsList.size());
        }
        this.points = new ArrayList<>(pointsList.size());
        for (int i = 0; i < pointsList.size(); i++) {
            Vector3 existingPoint = pointsList.get(i);
            if (copyPoints) {
                points.add(new Vector3(existingPoint));
            } else {
                points.add(existingPoint);
            }
        }
        this.calcAll();
    }
    
    /**
     *  复制创建
     * 
     * @param polygon
     */
    public KPolygon(KPolygon polygon) {
        points = new ArrayList<>(polygon.getPoints().size());
        for (int i = 0; i < polygon.getPoints().size(); i++) {
            Vector3 existingPoint = polygon.getPoints().get(i);
            points.add(new Vector3(existingPoint));
        }
        area = polygon.getArea();
        counterClockWise = polygon.isCounterClockWise();
        radius = polygon.getRadius();
        radiusSq = polygon.getRadiusSq();
        center = new Vector3(polygon.getCenter());
    }


    /**
     * @ 计算面积，中心点，半径
     */
    public void calcAll() {
        this.calcArea();
        this.calcCenter();
        this.calcRadius();
    }

    /**
     * 计算面积
     */
    public void calcArea() {
        double signedArea = getAndCalcSignedArea();
        if (signedArea < 0) {
            counterClockWise = false;
        } else {
            counterClockWise = true;
        }
        area = Math.abs(signedArea);
    }

    /**
     * 中心点
     */
    public void calcCenter() {
        if (center == null) {
            center = new Vector3();
        }
        if (getArea() == 0) {
            center.x = points.get(0).x;
            center.z = points.get(0).z;
            return;
        }
        double cx = 0.0f;
        double cz = 0.0f;
        Vector3 pointIBefore = (!points.isEmpty() ? points.get(points.size() - 1) : null);
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            double multiplier = (pointIBefore.z * pointI.x - pointIBefore.x * pointI.z);
            cx += (pointIBefore.x + pointI.x) * multiplier;
            cz += (pointIBefore.z + pointI.z) * multiplier;
            pointIBefore = pointI;
        }
        cx /= (6 * getArea());
        cz /= (6 * getArea());
        if (counterClockWise == true) {
            cx *= -1;
            cz *= -1;
        }
        center.x = cx;
        center.z = cz;
    }
    
    /**
     * 半径，中点到顶点的最大距离
     */
    public void calcRadius() {
        if (center == null) {
            calcCenter();
        }
        double maxRadiusSq = -1;
        int furthestPointIndex = 0;
        for (int i = 0; i < points.size(); i++) {
            double currentRadiusSq = (center.distanceSq(points.get(i)));
            if (currentRadiusSq > maxRadiusSq) {
                maxRadiusSq = currentRadiusSq;
                furthestPointIndex = i;
            }
        }
        radius = (center.distance(points.get(furthestPointIndex)));
        radiusSq = radius * radius;
    }



    /**
     * 计算多边形面积
     * 
     * @return 面积 负数标示顺时针，正数顶点逆时针
     */
    public double getAndCalcSignedArea() {
        double totalArea = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            totalArea += ((points.get(i).x - points.get(i + 1).x) * (points.get(i + 1).z + (points.get(i).z - points.get(i + 1).z) / 2));
        }
        // need to do points[point.length-1] and points[0].
        totalArea += ((points.get(points.size() - 1).x - points.get(0).x) * (points.get(0).z + (points.get(points.size() - 1).z - points.get(0).z) / 2));
        return totalArea;
    }
    
    /**
     * 倒置坐标点的顺序
     */
    public void reversePointOrder() {
        counterClockWise = !counterClockWise;
        List<Vector3> tempPoints = new ArrayList<>(points.size());
        for (int i = points.size() - 1; i >= 0; i--) {
            tempPoints.add(points.get(i));
        }
        points.clear();
        points.addAll(tempPoints);
    }
    
    /**
     * 获取坐标点
     * @param i 序号 
     * @return
     */
    public Vector3 getPoint(int i) {
        return getPoints().get(i);
    }
    
    /**
     * 克隆复制
     * @return
     */
    public KPolygon copy() {
        KPolygon polygon = new KPolygon(this);
        return polygon;
    }
    
    public List<Vector3> getPoints() {
        return points;
    }

    public boolean isCounterClockWise() {
        return counterClockWise;
    }

    public Vector3 getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public double getY() {
        return y;
    }

    public double getRadiusSq() {
        return radiusSq;
    }

    public double getArea() {
        return area;
    }


    // ===========java图像显示==============

    @Override
    public Rectangle getBounds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Rectangle2D getBounds2D() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean contains(double x, double y) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(Point2D p) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(Rectangle2D r) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public KPolygon getPolygon() {
        // TODO Auto-generated method stub
        return null;
    }

}
