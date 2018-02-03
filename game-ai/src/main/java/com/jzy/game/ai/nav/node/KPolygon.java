package com.jzy.game.ai.nav.node;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jzy.game.engine.util.math.Vector3;

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
    protected float y = -1;            // 多边形的平均y轴高度
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
     * 复制创建
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
        float cx = 0.0f;
        float cz = 0.0f;
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
            double currentRadiusSq = (center.dst2(points.get(i)));
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
     * 
     * @param i
     *            序号
     * @return
     */
    public Vector3 getPoint(int i) {
        return getPoints().get(i);
    }

    /**
     * 克隆复制
     * 
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

    public float getY() {
        return y;
    }

    public double getRadiusSq() {
        return radiusSq;
    }

    public double getArea() {
        return area;
    }

    /**
     * @ 坐标p是否在多边形内
     * 
     * @param p
     *            顶点坐标
     * @return
     */
    public boolean contains(Vector3 p) {
        return contains(p.x, p.z);
    }

    /**
     * 点p1 p2所在线段和多边形是否相交
     * 
     * @param p1
     * @param p2
     * @return
     */
    public boolean intersectionPossible(Vector3 p1, Vector3 p2) {
        return intersectionPossible(p1.x, p1.z, p2.x, p2.z);
    }

    /**
     * 点p1 p2所在线段和多边形是否相交
     * 
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public boolean intersectionPossible(double x1, double z1, double x2, double z2) {
        if (center.ptSegDistSq(x1, z1, x2, z2) > radiusSq) {       // 中心点到线段的距离大于半径，不相交，不能绝对确定，由于中心点的计算可能有误差，
            return false;
        }
        return true;
    }

    /**
     * 是否为相交线
     * 
     * @param p1
     * @param p2
     * @return
     */
    public boolean intersectsLine(Vector3 p1, Vector3 p2) {
        return intersectsLine(p1.x, p1.z, p2.x, p2.z);
    }

    /**
     * 坐标点和多边形否线性相交
     * 
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public boolean intersectsLine(double x1, double z1, double x2, double z2) {

        // Sometimes this method fails if the 'lines'
        // start and end on the same point, so here we check for that.
        if (x1 == x2 && z1 == z2) {
            return false;
        }
        double ax = x2 - x1;
        double ay = z2 - z1;
        Vector3 pointIBefore = points.get(points.size() - 1);
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            double x3 = pointIBefore.x;
            double z3 = pointIBefore.z;
            double x4 = pointI.x;
            double y4 = pointI.z;

            double bx = x3 - x4;
            double by = z3 - y4;
            double cx = x1 - x3;
            double cy = z1 - z3;

            double alphaNumerator = by * cx - bx * cy;
            double commonDenominator = ay * bx - ax * by;
            if (commonDenominator > 0) {
                if (alphaNumerator < 0 || alphaNumerator > commonDenominator) {
                    pointIBefore = pointI;
                    continue;
                }
            } else if (commonDenominator < 0) {
                if (alphaNumerator > 0 || alphaNumerator < commonDenominator) {
                    pointIBefore = pointI;
                    continue;
                }
            }
            double betaNumerator = ax * cy - ay * cx;
            if (commonDenominator > 0) {
                if (betaNumerator < 0 || betaNumerator > commonDenominator) {
                    pointIBefore = pointI;
                    continue;
                }
            } else if (commonDenominator < 0) {
                if (betaNumerator > 0 || betaNumerator < commonDenominator) {
                    pointIBefore = pointI;
                    continue;
                }
            }
            if (commonDenominator == 0) {
                // This code wasn't in Franklin Antonio's method. It was added
                // by Keith Woodward.
                // The lines are parallel.
                // Check if they're collinear.
                double collinearityTestForP3 = x1 * (z2 - z3) + x2 * (z3 - z1) + x3 * (z1 - z2); // see
                                                                                                 // http://mathworld.wolfram.com/Collinear.html
                // If p3 is collinear with p1 and p2 then p4 will also be
                // collinear, since p1-p2 is parallel with p3-p4
                if (collinearityTestForP3 == 0) {
                    // The lines are collinear. Now check if they overlap.
                    if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 || x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 || x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2) {
                        if (z1 >= z3 && z1 <= y4 || z1 <= z3 && z1 >= y4 || z2 >= z3 && z2 <= y4 || z2 <= z3 && z2 >= y4 || z3 >= z1 && z3 <= z2 || z3 <= z1 && z3 >= z2) {
                            return true;
                        }
                    }
                }
                pointIBefore = pointI;
                continue;
            }
            return true;
        }
        return false;
    }
    
    /**
     * 获取最靠近边界的点
     * @param p
     * @return
     */
    public Vector3 getBoundaryPointClosestTo(Vector3 p) {
        return getBoundaryPointClosestTo(p.x, p.z);
    }
    
    /**
     * 获取最靠近边界的点
     * @param x
     * @param z
     * @return
     */
    public Vector3 getBoundaryPointClosestTo(float x, float z) {
        double closestDistanceSq = Double.MAX_VALUE;
        int closestIndex = -1;
        int closestNextIndex = -1;

        int nextI;
        for (int i = 0; i < points.size(); i++) {	//获取点到多边形边最近的两个点
            nextI = (i + 1 == points.size() ? 0 : i + 1);
            Vector3 p = this.getPoints().get(i);
            Vector3 pNext = this.getPoints().get(nextI);
            double ptSegDistSq = Vector3.ptSegDistSq(p.x, p.z, pNext.x, pNext.z, x, z);
            if (ptSegDistSq < closestDistanceSq) {
                closestDistanceSq = ptSegDistSq;
                closestIndex = i;
                closestNextIndex = nextI;
            }
        }
        Vector3 p = this.getPoints().get(closestIndex);
        Vector3 pNext = this.getPoints().get(closestNextIndex);
        return Vector3.getClosestPointOnSegment(p.x, p.z, pNext.x, pNext.z, x, z);
    }

    public double[] getBoundsArray() {
        return getBoundsArray(new double[4]);
    }

    public double[] getBoundsArray(double[] bounds) {
        double leftX = Double.MAX_VALUE;
        double botY = Double.MAX_VALUE;
        double rightX = -Double.MAX_VALUE;
        double topY = -Double.MAX_VALUE;

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).x < leftX) {
                leftX = points.get(i).x;
            }
            if (points.get(i).x > rightX) {
                rightX = points.get(i).x;
            }
            if (points.get(i).z < botY) {
                botY = points.get(i).z;
            }
            if (points.get(i).z > topY) {
                topY = points.get(i).z;
            }
        }
        bounds[0] = leftX;
        bounds[1] = botY;
        bounds[2] = rightX;
        bounds[3] = topY;
        return bounds;
    }
    

    // ===========java图像显示==============

    @Override
    public Rectangle getBounds() {
    	 double[] bounds = getBoundsArray();
         return new Rectangle((int) (bounds[0]), (int) (bounds[1]), (int) Math.ceil(bounds[2]), (int) Math.ceil(bounds[3]));
    }

    @Override
    public Rectangle2D getBounds2D() {
    	 double[] bounds = getBoundsArray();
         return new Rectangle2D.Double(bounds[0], bounds[1], bounds[2], bounds[3]);
    }

    // The essence of the ray-crossing method is as follows. Think of standing
    // inside a field with a fence representing the polygon. Then walk north. If
    // you have to jump the fence you know you are now outside the poly. If you
    // have to cross again you know you are now inside again; i.e., if you were
    // inside the field to start with, the total number of fence jumps you would
    // make will be odd, whereas if you were ouside the jumps will be even.
    // The code below is from Wm. Randolph Franklin <wrf@ecse.rpi.edu> with some
    // minor modifications for speed. It returns 1 for strictly interior points,
    // 0 for strictly exterior, and 0 or 1 for points on the boundary. The
    // boundary behavior is complex but determined; | in particular, for a
    // partition of a region into polygons, each point | is "in" exactly one
    // polygon. See the references below for more detail
    // The code may be further accelerated, at some loss in clarity, by avoiding
    // the central computation when the inequality can be deduced, and by
    // replacing the division by a multiplication for those processors with slow
    // divides.
    // References:
    @Override
    public boolean contains(double x, double z) {
        Vector3 pointIBefore = (points.size() != 0 ? points.get(points.size() - 1) : null);
        int crossings = 0;
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            if (((pointIBefore.z <= z && z < pointI.z) || (pointI.z <= z && z < pointIBefore.z)) && x < ((pointI.x - pointIBefore.x) / (pointI.z - pointIBefore.z) * (z - pointIBefore.z) + pointIBefore.x)) {
                crossings++;
            }
            pointIBefore = pointI;
        }
        return (crossings % 2 != 0);
    }

    @Override
    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    @Override
    public boolean intersects(double x, double z, double w, double h) {
    	 if (x + w < center.x - radius
                 || x > center.x + radius
                 || z + h < center.z - radius
                 || z > center.z + radius) {
             return false;
         }
         for (int i = 0; i < points.size(); i++) {
             int nextI = (i + 1 >= points.size() ? 0 : i + 1);
             if (Vector3.linesIntersect(x, z, x + w, z, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                     || Vector3.linesIntersect(x, z, x, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                     || Vector3.linesIntersect(x, z + h, x + w, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                     || Vector3.linesIntersect(x + w, z, x + w, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)) {
                 return true;
             }
         }
         double px = points.get(0).x;
         double py = points.get(0).z;
         if (px > x && px < x + w && py > z && py < z + h) {
             return true;
         }
         if (contains(x, z) == true) {
             return true;
         }
         return false;
    }

    @Override
    public boolean intersects(Rectangle2D r) {
    	 return this.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public boolean contains(double x, double z, double w, double h) {
    	if (x + w < center.x - radius
                || x > center.x + radius
                || z + h < center.z - radius
                || z > center.z + radius) {
            return false;
        }
        for (int i = 0; i < points.size(); i++) {
            int nextI = (i + 1 >= points.size() ? 0 : i + 1);
            if (Vector3.linesIntersect(x, z, x + w, z, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x, z, x, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x, z + h, x + w, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x + w, z, x + w, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)) {
                return false;
            }
        }
        double px = points.get(0).x;
        double py = points.get(0).z;
        if (px > x && px < x + w && py > z && py < z + h) {
            return false;
        }
        return contains(x, z) == true;
    }

    @Override
    public boolean contains(Rectangle2D r) {
    	 return this.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
    	return new KPolygonIterator(this, at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
    	 return new KPolygonIterator(this, at);
    }

    @Override
    public KPolygon getPolygon() {
        return this;
    }

    public class KPolygonIterator implements PathIterator {

        int type = PathIterator.SEG_MOVETO;
        int index = 0;
        KPolygon polygon;
        Vector3 currentPoint;
        AffineTransform affine;

        double[] singlePointSetDouble = new double[2];

        KPolygonIterator(KPolygon kPolygon) {
            this(kPolygon, null);
        }

        KPolygonIterator(KPolygon kPolygon, AffineTransform at) {
            this.polygon = kPolygon;
            this.affine = at;
            currentPoint = polygon.getPoint(0);
        }

        public int getWindingRule() {
            return PathIterator.WIND_EVEN_ODD;
        }

        @Override
        public boolean isDone() {
            if (index == polygon.points.size() + 1) {
                return true;
            }
            return false;
        }

        @Override
        public void next() {
            index++;
        }

        public void assignPointAndType() {
            if (index == 0) {
                currentPoint = polygon.getPoint(0);
                type = PathIterator.SEG_MOVETO;
            } else if (index == polygon.points.size()) {
                type = PathIterator.SEG_CLOSE;
            } else {
                currentPoint = polygon.getPoint(index);
                type = PathIterator.SEG_LINETO;
            }
        }

        @Override
        public int currentSegment(float[] coords) {
            assignPointAndType();
            if (type != PathIterator.SEG_CLOSE) {
                if (affine != null) {
                    float[] singlePointSetFloat = new float[2];
                    singlePointSetFloat[0] = (float) currentPoint.x;
                    singlePointSetFloat[1] = (float) currentPoint.z;
                    affine.transform(singlePointSetFloat, 0, coords, 0, 1);
                } else {
                    coords[0] = (float) currentPoint.x;
                    coords[1] = (float) currentPoint.z;
                }
            }
            return type;
        }

        @Override
        public int currentSegment(double[] coords) {
            assignPointAndType();
            if (type != PathIterator.SEG_CLOSE) {
                if (affine != null) {
                    singlePointSetDouble[0] = currentPoint.x;
                    singlePointSetDouble[1] = currentPoint.z;
                    affine.transform(singlePointSetDouble, 0, coords, 0, 1);
                } else {
                    coords[0] = currentPoint.x;
                    coords[1] = currentPoint.z;
                }
            }
            return type;
        }
    }
}
