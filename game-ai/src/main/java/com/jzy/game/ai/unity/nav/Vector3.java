package com.jzy.game.ai.unity.nav;

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

	/** 零向量 */
	public static final Vector3 ZERO = new Vector3();

	// 坐标
	public volatile double x;
	public volatile double y;
	public volatile double z;

	public Vector3() {
		super();
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

	/***
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
	 * extends right, positive z-axis extends up). Returns a negative double if (px,
	 * py) is clockwise to (x2, z2) relative to (x1, z1). Returns a 0.0 if (px, py),
	 * (x1, z1) and (x2, z2) are collinear. Note that this method gives different
	 * results to java.awt.geom.Line2D.relativeCCW() since Java2D uses a different
	 * coordinate system (positive x-axis extends right, positive z-axis extends
	 * down).
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
	 * @param sourceDirection
	 *            方向向量
	 * @param degrees
	 *            方向度数
	 * @param distance
	 * @return
	 */
	public Vector3 unityTranslate(Vector3 sourceDirection, double degrees, double distance) {
		return unityTranslate(sourceDirection.y + degrees, distance);
	}

	/**
	 * Unity 平移
	 * 
	 * @param degrees
	 *            unity角度
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

	/**移动并返回新对象
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
}
