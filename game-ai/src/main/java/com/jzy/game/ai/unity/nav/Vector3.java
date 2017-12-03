package com.jzy.game.ai.unity.nav;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * unity向量
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
	private volatile double x;
	private volatile double y;
	private volatile double z;

	public Vector3() {
		super();
	}

	public Vector3(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
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

}
