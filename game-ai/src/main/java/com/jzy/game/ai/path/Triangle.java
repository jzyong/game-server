package com.jzy.game.ai.path;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * 三角形
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午4:41:27
 */
public class Triangle {
	/** 三角形序号 */
	public int index;
	public Vector3 a;
	public Vector3 b;
	public Vector3 c;
	/** 中点 */
	public Vector3 center;
	/** 三角形和其他三角形的共享边 */
	public transient Array<Connection<Triangle>> connections;
	
	public Triangle(Vector3 a, Vector3 b, Vector3 c, int index) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.index = index;
		this.center = new Vector3(a).add(b).add(c).scl(1f / 3f);
		this.connections = new Array<Connection<Triangle>>();
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public int getIndex() {
		return this.index;
	}

	public Array<Connection<Triangle>> getConnections() {
		return connections;
	}

	/**
	 * Calculates the angle in radians between a reference vector and the (plane) normal of the triangle.
	 *
	 * @param reference
	 * @return
	 */
	public float getAngle(Vector3 reference) {
		float x = reference.x;
		float y = reference.y;
		float z = reference.z;
		Vector3 normal = reference;
		normal.set(a).sub(b).crs(b.x - c.x, b.y - c.y, b.z - c.z).nor();
		float angle = (float) Math.acos(normal.dot(x, y, z) / (normal.len() * Math.sqrt(x * x + y * y + z * z)));
		reference.set(x, y, z);
		return angle;
	}

	/**
	 * Calculates a random point in this triangle.
	 *
	 * @param out Output vector
	 * @return Output for chaining
	 */
	public Vector3 getRandomPoint(Vector3 out) {
		final float sr1 = (float) Math.sqrt(MathUtils.random());
		final float r2 = MathUtils.random();
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

}
