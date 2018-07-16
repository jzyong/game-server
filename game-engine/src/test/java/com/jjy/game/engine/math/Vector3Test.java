package com.jjy.game.engine.math;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.jzy.game.engine.math.Vector3;

/**
 * 向量测试
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class Vector3Test {

	/**
	 * 测试向量距离是否靠近
	 */
	@Test
	public void testIsColseTo() {
		Vector3 vector3=new Vector3(1, 0, 1);
		Vector3 vector32=new Vector3(2, 2, 2);
		
		assertTrue(vector3.isColoseTo(vector32, 2));
		assertTrue(vector3.isColoseTo(vector32, 2.5f));
		assertFalse(vector3.isColoseTo(vector32, 1.99f));
	}
	
	
	/**
	 * 测试移动
	 */
	@Test
	public void testMove() {
		Vector3 vector3 = Vector3.move(2f, new Vector3(), new Vector3(10f, 10f, 10f), 2f);
		System.out.println(vector3.toString());
		
		Vector3 vector2 = Vector3.move(5f, new Vector3(), new Vector3(10f, 10f, 10f), 4f);
		System.out.println(vector2.toString());
	}
	
	/**
	 * 测试是否在线段上
	 */
	@Test
	public void testIsOnSegment() {
		Assert.assertTrue(Vector3.isPointOnSegment(new Vector3(0, 0), new Vector3(10, 10), new Vector3(5, 5)));
		Assert.assertFalse(Vector3.isPointOnSegment(new Vector3(0, 0), new Vector3(10, 10), new Vector3(15, 15)));
		Assert.assertFalse(Vector3.isPointOnSegment(new Vector3(0, 0), new Vector3(10, 10), new Vector3(-5, -5)));
		Assert.assertFalse(Vector3.isPointOnSegment(new Vector3(0, 0), new Vector3(10, 10), new Vector3(5, 3)));
	}
	
}
