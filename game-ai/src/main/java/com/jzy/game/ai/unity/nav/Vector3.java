package com.jzy.game.ai.unity.nav;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * unity三维向量 <br>
 * 基于unity，部分计算基于2D平面，x，z轴
 *
 * @author JiangZhiYong
 *
 */
public class Vector3 implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Vector3.class);

    static public final double TWO_PI = Math.PI * 2;
    static public final double degreesToRadians = Math.PI / 180;
    static public final double radiansToDegrees = 180f / Math.PI;

    /**
     * 零向量
     */
    public static final Vector3 ZERO = new Vector3();

    // 坐标
    public volatile double x;
    public volatile double y;
    public volatile double z;

    public Vector3() {
        super();
    }

    public Vector3(double degree) {
        degree = 90 - degree;
        x = (float) Math.cos(degreesToRadians * degree);
        z = (float) Math.sin(degreesToRadians * degree);
    }

    public Vector3(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public Vector3(double x, double y, double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * 复制一个坐标
     *
     * @param old
     */
    public Vector3(Vector3 old) {
        this.x = old.x;
        this.y = old.y;
        this.z = old.z;
    }

    /**
     * 复制点
     *
     * @return
     */
    public Vector3 copy() {
        return new Vector3(x, y, z);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    /**
     * 对坐标进行扩大或缩减
     *
     * @param scale
     */
    public void scale(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    /**
     * 平面距离平方
     *
     * @param p
     * @return
     */
    public double distanceSq(Vector3 p) {
        return distanceSq(this.x, this.z, p.x, p.z);
    }

    /**
     * 平面距离平方
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static double distanceSq(double x1, double z1, double x2, double z2) {
        x1 -= x2;
        z1 -= z2;
        return (x1 * x1 + z1 * z1);
    }

    /**
     *
     * 平面两坐标点距离
     *
     * @param p
     * @return
     */
    public double distance(Vector3 p) {
        return distance(this.x, this.z, p.x, p.z);
    }

    /**
     * *
     *
     * 平面两坐标点距离
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static double distance(double x1, double z1, double x2, double z2) {
        x1 -= x2;
        z1 -= z2;
        return Math.sqrt(x1 * x1 + z1 * z1);
    }

    /**
     * 相对旋转方向，以p1为参考点，当前点以p2的旋转方向 {@link #ccw(Vector3)}
     *
     * @param p1
     * @param p2
     * @return 1 逆时针 -1 顺时针 0 三点共线
     */
    public int relCCW(Vector3 p1, Vector3 p2) {
        return relCCW(p1.x, p1.z, p2.x, p2.z, x, z);
    }

    /**
     * Returns a positive double if (px, py) is counter-clockwise to (x2, z2)
     * relative to (x1, z1). in the cartesian coordinate space (positive x-axis
     * extends right, positive z-axis extends up). Returns a negative double if
     * (px, py) is clockwise to (x2, z2) relative to (x1, z1). Returns a 0.0 if
     * (px, py), (x1, z1) and (x2, z2) are collinear. Note that this method
     * gives different results to java.awt.geom.Line2D.relativeCCW() since
     * Java2D uses a different coordinate system (positive x-axis extends right,
     * positive z-axis extends down).
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @param px
     * @param py
     * @return
     */
    public static int relCCW(double x1, double z1, double x2, double z2, double px, double py) {
        double ccw = relCCWDouble(x1, z1, x2, z2, px, py);
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }

    /**
     * 两点相对某点的旋转方向
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @param px
     * @param py
     * @return
     */
    public static double relCCWDouble(double x1, double z1, double x2, double z2, double px, double py) {
        x2 -= x1;
        z2 -= z1;
        px -= x1;
        py -= z1;
        double ccw = py * x2 - px * z2;
        return ccw;
    }

    /**
     * 两点相对某点的旋转方向
     *
     * @param p1
     * @param p2
     * @return
     */
    public double relCCWDouble(Vector3 p1, Vector3 p2) {
        return relCCWDouble(p1.x, p1.z, p2.x, p2.z, x, z);
    }

    /**
     * 当前坐标和 两坐标直线的距离平方
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public double ptSegDistSq(double x1, double z1, double x2, double z2) {
        return ptSegDistSq(x1, z1, x2, z2, x, z);
    }

    /**
     * @ 当前坐标和 两坐标直线的距离平方
     *
     * @param start
     * @param end
     * @return
     */
    public double ptSegDistSq(Vector3 start, Vector3 end) {
        return ptSegDistSq(start.x, start.z, end.x, end.z, x, z);
    }

    /**
     * 点 p到线段的最近平方
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @param px
     * @param py
     * @return
     */
    public static double ptSegDistSq(double x1, double z1, double x2, double z2, double px, double py) {
        // from: Line2D.Float.ptSegDistSq(x1, z1, x2, z2, px, py);
        // Adjust vectors relative to x1,z1
        // x2,z2 becomes relative vector from x1,z1 to end of segment
        x2 -= x1;
        z2 -= z1;
        // px,py becomes relative vector from x1,z1 to test point
        px -= x1;
        py -= z1;
        double dotprod = px * x2 + py * z2;
        double projlenSq;
        if (dotprod <= 0.0) {
            // px,py is on the side of x1,z1 away from x2,z2
            // distance to segment is length of px,py vector
            // "length of its (clipped) projection" is now 0.0
            projlenSq = 0.0;
        } else {
            // switch to backwards vectors relative to x2,z2
            // x2,z2 are already the negative of x1,z1=>x2,z2
            // to get px,py to be the negative of px,py=>x2,z2
            // the dot product of two negated vectors is the same
            // as the dot product of the two normal vectors
            px = x2 - px;
            py = z2 - py;
            dotprod = px * x2 + py * z2;
            if (dotprod <= 0.0) {
                // px,py is on the side of x2,z2 away from x1,z1
                // distance to segment is length of (backwards) px,py vector
                // "length of its (clipped) projection" is now 0.0
                projlenSq = 0.0;
            } else {
                // px,py is between x1,z1 and x2,z2
                // dotprod is the length of the px,py vector
                // projected on the x2,z2=>x1,z1 vector times the
                // length of the x2,z2=>x1,z1 vector
                projlenSq = dotprod * dotprod / (x2 * x2 + z2 * z2);
            }
        }
        // Distance to line is now the length of the relative point
        // vector minus the length of its projection onto the line
        // (which is zero if the projection falls outside the range
        // of the line segment).
        double lenSq = px * px + py * py - projlenSq;
        if (lenSq < 0) {
            lenSq = 0;
        }
        return lenSq;
    }

    /**
     *
     * 点到执行距离的平方
     *
     * @param start
     * @param end
     * @return
     */
    public double ptLineDistSq(Vector3 start, Vector3 end) {
        return ptLineDistSq(start.x, start.z, end.x, end.z, x, z);
    }

    /**
     * 点到执行距离的平方
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @param px
     * @param py
     * @return
     */
    public static double ptLineDistSq(double x1, double z1, double x2, double z2, double px, double py) {
        // from: Line2D.Float.ptLineDistSq(x1, z1, x2, z2, px, py);
        // Adjust vectors relative to x1,z1
        // x2,z2 becomes relative vector from x1,z1 to end of segment
        x2 -= x1;
        z2 -= z1;
        // px,py becomes relative vector from x1,z1 to test point
        px -= x1;
        py -= z1;
        double dotprod = px * x2 + py * z2;
        // dotprod is the length of the px,py vector
        // projected on the x1,z1=>x2,z2 vector times the
        // length of the x1,z1=>x2,z2 vector
        double projlenSq = dotprod * dotprod / (x2 * x2 + z2 * z2);
        // Distance to line is now the length of the relative point
        // vector minus the length of its projection onto the line
        double lenSq = px * px + py * py - projlenSq;
        if (lenSq < 0) {
            lenSq = 0;
        }
        return lenSq;
    }

    /**
     * Unity 平移
     *
     * @param sourceDirection 方向向量
     * @param degrees 方向度数
     * @param distance
     * @return
     */
    public Vector3 unityTranslate(Vector3 sourceDirection, double degrees, double distance) {
        return unityTranslate(sourceDirection.y + degrees, distance);
    }

    /**
     * Unity 平移
     *
     * @param degrees unity角度
     * @param distance
     * @return
     */
    public Vector3 unityTranslate(double degrees, double distance) {
        Vector3 p = this.clone();
        double angle = unityDegreesToAngle(degrees);
        p.x = x + (distance * Math.cos(angle));
        p.z = z + (distance * Math.sin(angle));
        return p;
    }

    /**
     * 度数转弧度
     *
     * @param degrees
     * @return
     */
    public static double unityDegreesToAngle(double degrees) {
        return ((90 - degrees) % 360) * Math.PI / 180;
    }

    /**
     * 移动并返回新对象
     *
     * @param degrees
     * @param radius
     * @return
     */
    public Vector3 translateCopy(double degrees, double radius) {
        Vector3 p = this.clone();
        double angle = degrees * Math.PI / 180;
        p.x = x + (radius * Math.cos(angle));
        p.z = z + (radius * Math.sin(angle));
        return p;
    }

    @Override
    public Vector3 clone() {
        try {
            return (Vector3) super.clone();
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public String toString() {
        return "{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    /**
     * 2D 平面距离平方
     *
     * @param point
     * @return
     */
    public double distanceSqlt2D(Vector3 point) {
        final double a = point.x - x;
        final double c = point.z - z;
        return a * a + c * c;
    }

    public static Vector3 getClosestPointOnSegment(double x1, double z1, double x2, double z2, double px, double py) {
        Vector3 closestPoint = new Vector3();
        double x2LessX1 = x2 - x1;
        double y2LessY1 = z2 - z1;
        double lNum = x2LessX1 * x2LessX1 + y2LessY1 * y2LessY1;
        double rNum = ((px - x1) * x2LessX1 + (py - z1) * y2LessY1) / lNum;
        // double lNum = (x2 - x1)*(x2 - x1) + (z2 - z1)*(z2 - z1);
        // double rNum = ((px - x1)*(x2 - x1) + (py - z1)*(z2 - z1)) / lNum;
        if (rNum <= 0) {
            closestPoint.x = x1;
            closestPoint.z = z1;
        } else if (rNum >= 1) {
            closestPoint.x = x2;
            closestPoint.z = z2;
        } else {
            closestPoint.x = (x1 + rNum * x2LessX1);
            closestPoint.z = (z1 + rNum * y2LessY1);
        }
        return closestPoint;
    }

    /**
     * 两坐标的夹角
     *
     * @param dest
     * @return
     */
    public double findAngle(Vector3 dest) {
        return findAngle(this, dest);
    }

    /**
     * 两坐标的夹角
     *
     * @param start
     * @param dest
     * @return
     */
    public static double findAngle(Vector3 start, Vector3 dest) {
        return findAngle(start.x, start.z, dest.x, dest.z);
    }

    /**
     * 两坐标的夹角
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static double findAngle(double x1, double z1, double x2, double z2) {
        double angle = findSignedAngle(x1, z1, x2, z2);
        if (angle < 0) {
            angle += TWO_PI;
        }
        return angle;
    }

    /**
     * 两坐标的夹角
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static double findSignedAngle(double x1, double z1, double x2, double z2) {
        double x = x2 - x1;
        double z = z2 - z1;
        double angle = (Math.atan2(z, x));
        return angle;
    }

    /**
     * 根据度数和距离创建新坐标
     *
     * @param angle
     * @param distance
     * @return
     */
    public Vector3 createPointFromAngle(double angle, double distance) {
        return createPointFromAngle(this, angle, distance);
    }

    /**
     * 根据度数和距离创建新坐标
     *
     * @param source
     * @param angle
     * @param distance
     * @return
     */
    public static Vector3 createPointFromAngle(Vector3 source, double angle, double distance) {
        Vector3 p = new Vector3();
        double xDist = Math.cos(angle) * distance;
        double yDist = Math.sin(angle) * distance;
        p.x = (source.x + xDist);
        p.z = (source.z + yDist);
        p.y = source.y;
        return p;
    }

    /**
     * 是否在扇形中
     *
     * @param sourceDirection
     * @param target
     * @param radius
     * @param degrees
     * @return
     */
    public boolean isInSector(Vector3 sourceDirection, Vector3 target, float radius, float degrees) {
        if (this.equals(target)) {
            return true;
        }
        double dis = this.distanceSq(target);
        Vector3 vt = target.sub(this);
        vt.y = 0;
        if (vt.isZero()) {
            return true;
        }
        double targetAngle = angle(vt, new Vector3(sourceDirection.y));
        return (dis <= radius * radius && targetAngle <= degrees / 2 && targetAngle >= -(degrees / 2));
    }

    /**
     * 返回两个向量（从原点到两点）间的夹角
     *
     * @param v1
     * @param v2
     * @return Degrees
     */
    public static double angle(Vector3 v1, Vector3 v2) {
        if (v1.isZero()) {
            throw new IllegalArgumentException("v1为零向量");
        }
        if (v2.isZero()) {
            throw new IllegalArgumentException("v2为零向量");
        }
        return ((Math.acos(v1.dot(v2) / (v1.length() * v2.length()))) * radiansToDegrees);
    }

    /**
     * 点乘
     *
     * @param vector
     * @return
     */
    public double dot(final Vector3 vector) {
        return x * vector.x + y * vector.y + z * vector.z;
    }

    /**
     * 向量长度
     *
     * @return
     */
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * 坐标相减
     *
     * @param a_vec
     * @return
     */
    public Vector3 sub(final Vector3 a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z);
    }

    /**
     * 坐标相减
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Vector3 sub(double x, double y, double z) {
        Vector3 copy = this.copy();
        copy.x -= x;
        copy.y -= y;
        copy.z -= z;
        return copy;
    }

    /**
     * 是否为0向量
     *
     * @return
     */
    @JSONField(serialize = false)
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }
    
    public static boolean linesIntersect(double x1, double z1, double x2, double z2,
            double x3, double y3, double x4, double y4) {
        // Return false if either of the lines have zero length
        if (x1 == x2 && z1 == z2
                || x3 == x4 && y3 == y4) {
            return false;
        }
        // Fastest method, based on Franklin Antonio's "Faster Line Segment Intersection" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
        double ax = x2 - x1;
        double ay = z2 - z1;
        double bx = x3 - x4;
        double by = y3 - y4;
        double cx = x1 - x3;
        double cy = z1 - y3;

        double alphaNumerator = by * cx - bx * cy;
        double commonDenominator = ay * bx - ax * by;
        if (commonDenominator > 0) {
            if (alphaNumerator < 0 || alphaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (alphaNumerator > 0 || alphaNumerator < commonDenominator) {
                return false;
            }
        }
        double betaNumerator = ax * cy - ay * cx;
        if (commonDenominator > 0) {
            if (betaNumerator < 0 || betaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (betaNumerator > 0 || betaNumerator < commonDenominator) {
                return false;
            }
        }
        // if commonDenominator == 0 then the lines are parallel.
        if (commonDenominator == 0) {
            // This code wasn't in Franklin Antonio's method. It was added by Keith Woodward.
            // The lines are parallel.
            // Check if they're collinear.
            double collinearityTestForP3 = x1 * (z2 - y3) + x2 * (y3 - z1) + x3 * (z1 - z2);	// see http://mathworld.wolfram.com/Collinear.html
            // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
            if (collinearityTestForP3 == 0) {
                // The lines are collinear. Now check if they overlap.
                if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4
                        || x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4
                        || x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2) {
                    if (z1 >= y3 && z1 <= y4 || z1 <= y3 && z1 >= y4
                            || z2 >= y3 && z2 <= y4 || z2 <= y3 && z2 >= y4
                            || y3 >= z1 && y3 <= z2 || y3 <= z1 && y3 >= z2) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;

    }
}
