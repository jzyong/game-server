package com.jzy.game.ai.quadtree.point;

import com.jzy.game.ai.quadtree.Data;
import com.jzy.game.engine.math.Vector3;

/**
 * 坐标点数据结构
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PointData<T> extends Data<T> {

	private Vector3 point;

	public PointData(Vector3 point, T value) {
		super(value);
		this.point = point;
	}

	public Vector3 getPoint() {
		return point;
	}

	public void setPoint(Vector3 point) {
		this.point = point;
	}

	@Override
	public int compareTo(Data<T> data) {
		PointData<T> o=(PointData<T>) data;
		if (this.point.x < o.point.x) {
			return -1;
		} else if (this.point.x > o.point.x) {
			return 1;
		} else {
			if (this.point.z < o.point.z) {
				return -1;
			} else if (this.point.z > o.point.z) {
				return 1;
			}
			return 0;
		}
	}

	@Override
	public String toString() {
		return "PointData [point=" + point + ", getValue()=" + getValue() + "]";
	}
	
}
