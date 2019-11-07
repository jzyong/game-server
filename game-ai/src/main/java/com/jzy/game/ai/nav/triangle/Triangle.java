package com.jzy.game.ai.nav.triangle;
//package com.jzy.game.ai.nav;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.jzy.game.ai.pfa.Connection;
import com.jzy.game.engine.math.MathUtil;
import com.jzy.game.engine.math.Vector3;

/**
 * 三角形
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午4:41:27
 */
public class Triangle implements Shape{
	/** 三角形序号 */
	public int index;
	public Vector3 a;
	public Vector3 b;
	public Vector3 c;
	public float y;	//三角形高度，三个顶点的平均高度
	/** 中点 */
	public Vector3 center;
	/** 三角形和其他三角形的共享边 */
	public transient List<Connection<Triangle>> connections;
	/**三角形顶点序号*/
	public int[] vectorIndex;

	public Triangle(Vector3 a, Vector3 b, Vector3 c, int index,int ...vectorIndex) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.y=(a.y+b.y+c.y)/3;
		this.index = index;
		this.center = new Vector3(a).add(b).add(c).scl(1f / 3f);
		this.connections = new ArrayList<Connection<Triangle>>();
		this.vectorIndex=vectorIndex;
	}

	@Override
	public String toString() {
		return "Triangle [index=" + index + ", a=" + a + ", b=" + b + ", c=" + c + ", center=" + center + "]";
	}

	public int getIndex() {
		return this.index;
	}

	public List<Connection<Triangle>> getConnections() {
		return connections;
	}

	/**
	 * Calculates the angle in radians between a reference vector and the (plane)
	 * normal of the triangle.
	 *
	 * @param reference
	 * @return
	 */
	public float getAngle(Vector3 reference) {
		float x = reference.x;
		float y = reference.y;
		float z = reference.z;
		Vector3 normal = reference;
		normal.set(a).sub(b).cross(b.x - c.x, b.y - c.y, b.z - c.z).nor();
		float angle = (float) Math.acos(normal.dot(x, y, z) / (normal.len() * Math.sqrt(x * x + y * y + z * z)));
		reference.set(x, y, z);
		return angle;
	}

	/**
	 * Calculates a random point in this triangle.
	 *
	 * @param out
	 *            Output vector
	 * @return Output for chaining
	 */
	public Vector3 getRandomPoint(Vector3 out) {
		final float sr1 = (float) Math.sqrt(MathUtil.random());
		final float r2 = MathUtil.random();
		final float k1 = 1 - sr1;
		final float k2 = sr1 * (1 - r2);
		final float k3 = sr1 * r2;
		out.x = k1 * a.x + k2 * b.x + k3 * c.x;
		out.y = k1 * a.y + k2 * b.y + k3 * c.y;
		out.z = k1 * a.z + k2 * b.z + k3 * c.z;
		return out;
	}

	/**
	 * Calculates the area of the triangle.
	 *
	 * @return
	 */
	public float area() {
		final float abx = b.x - a.x;
		final float aby = b.y - a.y;
		final float abz = b.z - a.z;
		final float acx = c.x - a.x;
		final float acy = c.y - a.y;
		final float acz = c.z - a.z;
		final float r = aby * acz - abz * acy;
		final float s = abz * acx - abx * acz;
		final float t = abx * acy - aby * acx;
		return 0.5f * (float) Math.sqrt(r * r + s * s + t * t);
	}

	/**
	 * 三角形2D平面面积
	 * @return
	 */
	public float area2D(){
		final float abx=b.x-a.x;
		final float abz=b.z-a.z;
		final float acx=c.x-a.x;
		final float acz=c.z-a.z;
		return acx*abz-abx*acz;
	}

	
	/**
	 * 判断一个点是否在三角形内,二维判断
	 * <br> http://www.yalewoo.com/in_triangle_test.html
	 */
	public boolean isInnerPoint(Vector3 point) {
		boolean res=Vector3.pointInLineLeft(a, b, point);
		if(res!=Vector3.pointInLineLeft(b, c, point)) {
			return false;
		}
		if(res!=Vector3.pointInLineLeft(c, a, point)) {
			return false;
		}
		if(Vector3.cross2D(a, b, c)==0) {	//三点共线
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triangle other = (Triangle) obj;
		if (index != other.index)
			return false;
		return true;
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
	public boolean contains(double x, double y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Point2D p) {
		throw new UnsupportedOperationException();
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
		return new TrianglePathIterator(this, at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new TrianglePathIterator(this, at);
	}
	
	public class TrianglePathIterator implements PathIterator {

        int type = PathIterator.SEG_MOVETO;
        int index = 0;
        Triangle triangle;
        Vector3 currentPoint;
        AffineTransform affine;

        double[] singlePointSetDouble = new double[2];

        TrianglePathIterator(Triangle triangle) {
            this(triangle, null);
        }

        TrianglePathIterator(Triangle triangle, AffineTransform at) {
            this.triangle = triangle;
            this.affine = at;
            currentPoint =triangle.a;
        }

        public int getWindingRule() {
            return PathIterator.WIND_EVEN_ODD;
        }

        @Override
        public boolean isDone() {
            if (index == 4) {	
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
                currentPoint =triangle.a;
                type = PathIterator.SEG_MOVETO;
            } else if (index == 3) {
                type = PathIterator.SEG_CLOSE;
            } else {
            	if(index==0) {
            		currentPoint=triangle.a;
            	}else if(index==1) {
            		currentPoint=triangle.b;
            	}else {
            		currentPoint=triangle.c;
            	}
//                currentPoint = polygon.getPoint(index);
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
