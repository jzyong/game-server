package com.jzy.game.ai.nav.polygon;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.pfa.Connection;
import com.jzy.game.engine.math.Intersector;
import com.jzy.game.engine.math.MathUtil;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.script.IIDScript;

/**
 * 多边形,用于navmesh寻路
 * 
 * @author JiangZhiYong
 * @date 2018年2月20日
 * @mail 359135103@qq.com
 */
public class Polygon implements Shape {
	private static final Logger LOGGER = LoggerFactory.getLogger(Polygon.class);

	/** 多边形序号 */
	public int index;
	/** 顶点坐标 */
	public List<Vector3> points;
	/** y轴，所有顶点平均高度 */
	public float y;
	/** 中心坐标 */
	public Vector3 center;
	/** 面积 */
	public float area;
	/** 逆时针方向 */
	public boolean counterClockWise;
	/** 凸多边形 */
	public boolean convex;
	/** 半径 */
	public float radius;
	/** 半径平方 */
	public float radiusSq;
	/** 顶点坐标索引 */
	public int[] vectorIndexs;
	/** 和其他多边形的共享变 */
	public transient List<Connection<Polygon>> connections;
	/** 预先生成的随机点 */
	public List<Vector3> randomPoints = new ArrayList<>();

	/**
	 * 
	 * @param index
	 *            寻路索引编号
	 * @param points
	 *            坐标点
	 * @param vectorIndexs
	 *            坐标顶点序号
	 */
	public Polygon(int index, List<Vector3> points, int[] vectorIndexs) {
		this.index = index;
		this.vectorIndexs = vectorIndexs;
		this.points = points;
		this.connections = new ArrayList<>();
		initCalculate();
	}
	
	/**
	 * 
	 * @param index
	 *            寻路索引编号
	 * @param points
	 *            坐标点
	 * @param vectorIndexs
	 *            坐标顶点序号
	 */
	public Polygon(int index, List<Vector3> points, int[] vectorIndexs,boolean initCalculate) {
		this.index = index;
		this.vectorIndexs = vectorIndexs;
		this.points = points;
		if(initCalculate) {
			initCalculate();
		}
		
	}
	

	public Polygon(int index, Vector3... point) {
		this(index, Arrays.asList(point), null);
	}

	public Polygon(List<Vector3> points) {
		this(0, points, null);
	}

	/**
	 * 初始化计算
	 */
	private void initCalculate() {
		calculateArea();
		calculateCenter();
		calculateRadius();
		calculateIsConvex();
		// y坐标
		for (Vector3 point : points) {
			y += point.y;
		}
		y = y / points.size();
	}

	/**
	 * 计算中心坐标
	 */
	public void calculateCenter() {
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
		float cy = 0.0f;
		Vector3 pointIBefore = (!points.isEmpty() ? points.get(points.size() - 1) : null);
		for (int i = 0; i < points.size(); i++) {
			Vector3 pointI = points.get(i);
			double multiplier = (pointIBefore.z * pointI.x - pointIBefore.x * pointI.z);
			cx += (pointIBefore.x + pointI.x) * multiplier;
			cz += (pointIBefore.z + pointI.z) * multiplier;
			cy += pointI.y;
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
		center.y = cy / points.size();
	}

	/**
	 * 计算半径
	 */
	public void calculateRadius() {
		if (center == null) {
			calculateCenter();
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
		radius = (center.dst(points.get(furthestPointIndex)));
		radiusSq = radius * radius;
	}

	/**
	 * 计算面积
	 */
	public void calculateArea() {
		float signedArea = getAndCalcSignedArea();
		if (signedArea < 0) {
			counterClockWise = false;
		} else {
			counterClockWise = true;
		}
		area = (float) Math.abs(signedArea);
	}

	/**
	 * 计算面积
	 * 
	 * @return 小于0坐标点顺时针排序，大于0逆时针
	 */
	public float getAndCalcSignedArea() {
		float totalArea = 0;
		for (int i = 0; i < points.size() - 1; i++) {
			totalArea += ((points.get(i).x - points.get(i + 1).x)
					* (points.get(i + 1).z + (points.get(i).z - points.get(i + 1).z) / 2));
		}
		// need to do points[point.length-1] and points[0].
		totalArea += ((points.get(points.size() - 1).x - points.get(0).x)
				* (points.get(0).z + (points.get(points.size() - 1).z - points.get(0).z) / 2));
		return totalArea;
	}

	/**
	 * 计算是否为凸多边形
	 * 
	 * @return
	 */
	public boolean calculateIsConvex() {
		int size = points.size();
		for (int i = 0; i < size; i++) {
			Vector3 point1 = getPoint(i % size); // 前一顶点
			Vector3 point2 = getPoint((i + 1) % size); // 中间顶点
			Vector3 point3 = getPoint((i + 2) % size); // 后一顶点
			int relCCW = point2.relCCW(point1, point3);
			// 凹点
			if ((counterClockWise && relCCW > 0) || (!counterClockWise && relCCW < 0)) {
				convex = false;
				return convex;
			}
		}
		convex = true;
		return convex;
	}

	public float getArea() {
		return area;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * 获取坐标点
	 * 
	 * @param i
	 *            坐标序号
	 * @return
	 */
	public Vector3 getPoint(int i) {
		return points.get(i);
	}

	/**
	 * 坐标点是否在多边形内部 <br>
	 * 在多边形边上返回false，顶点为true
	 * 
	 * @param point
	 * @return
	 */
	public boolean isInnerPoint(Vector3 point) {
		return Intersector.isPointInPolygon(points, point);
	}

	/**
	 * 点是否在多边形上
	 * 
	 * @param point
	 * @return
	 */
	public boolean isOnEdge(Vector3 point) {
		for (int i = 0; i < points.size(); i++) {
			if (Vector3.isPointOnSegment(points.get(i), points.get((i + 1) % points.size()), point)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否包含另外一个多边形
	 * 
	 * @param polygon
	 * @return
	 */
	public boolean contains(Polygon polygon) {
		if (intersectsEdge(polygon)) {
			return false;
		}

		if (contains(polygon.getPoint(0)) == false) {
			return false;
		}
		return true;
	}

	/**
	 * 坐标点是否在多边形内部
	 * <p>
	 * 比{@code isInnerPoint}速度慢
	 * 
	 * @param p
	 * @return
	 */
	public boolean contains(Vector3 p) {
		return contains(p.x, p.z);
	}

	/**
	 * 两多边形的边是否相交
	 * 
	 * @param polygon
	 * @return
	 */
	public boolean intersectsEdge(Polygon polygon) {
		Vector3 pointIBefore = (points.size() != 0 ? points.get(points.size() - 1) : null);
		Vector3 pointJBefore = (polygon.points.size() != 0 ? polygon.points.get(polygon.points.size() - 1) : null);
		for (int i = 0; i < points.size(); i++) {
			Vector3 pointI = points.get(i);
			for (int j = 0; j < polygon.points.size(); j++) {
				Vector3 pointJ = polygon.points.get(j);
				// The below linesIntersect could be sped up slightly since many things are
				// recalc'ed over and over again.
				if (Vector3.linesIntersect(pointI, pointIBefore, pointJ, pointJBefore)) {
					return true;
				}
				pointJBefore = pointJ;
			}
			pointIBefore = pointI;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Polygon [index=" + index + ", points=" + points + ", y=" + y + ", center=" + center + ", area=" + area
				+ ", counterClockWise=" + counterClockWise + ", convex=" + convex + ", radius=" + radius + ", radiusSq="
				+ radiusSq + ", vectorIndexs=" + Arrays.toString(vectorIndexs) + "]";
	}

	@Override
	public Rectangle getBounds() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Rectangle2D getBounds2D() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(double x, double z) {
		Vector3 pointIBefore = (points.size() != 0 ? points.get(points.size() - 1) : null);
		int crossings = 0;
		for (int i = 0; i < points.size(); i++) {
			Vector3 pointI = points.get(i);
			if (((pointIBefore.z <= z && z < pointI.z) || (pointI.z <= z && z < pointIBefore.z))
					&& x < ((pointI.x - pointIBefore.x) / (pointI.z - pointIBefore.z) * (z - pointIBefore.z)
							+ pointIBefore.x)) {
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
	public boolean intersects(double x, double y, double w, double h) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Rectangle2D r) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new PolygonIterator(this, at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new PolygonIterator(this, at);
	}

	public String print() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n");
		sb.append("序号:" + index);
		sb.append("\r\n");
		sb.append("坐标:" + points.toString());
		sb.append("\r\n");
		sb.append("顶点序号:" + JSON.toJSONString(vectorIndexs));
		return sb.toString();
	}

	public class PolygonIterator implements PathIterator {

		int type = PathIterator.SEG_MOVETO;
		int index = 0;
		Polygon polygon;
		Vector3 currentPoint;
		AffineTransform affine;

		double[] singlePointSetDouble = new double[2];

		PolygonIterator(Polygon polygon) {
			this(polygon, null);
		}

		PolygonIterator(Polygon kPolygon, AffineTransform at) {
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
