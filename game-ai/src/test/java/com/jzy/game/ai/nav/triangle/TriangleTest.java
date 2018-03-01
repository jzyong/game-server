package com.jzy.game.ai.nav.triangle;

import org.junit.Assert;
import org.junit.Test;

import com.jzy.game.engine.math.Vector3;

/**
 * 三角形测试
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class TriangleTest {

	/**
	 * 测试点是否在三角形内部
	 */
	@Test
	public void testPointIntTriangle() {
		Vector3 point1=new Vector3(4, 1);
		Vector3 point2=new Vector3(4, 5);
		Triangle triangle=new Triangle(new Vector3(1, 1), new Vector3(6, 3), new Vector3(4, 7), 1);
	
		Assert.assertFalse(triangle.isInnerPoint(point1));
		Assert.assertTrue(triangle.isInnerPoint(point2));
	}
	
	/**
	 * 获取三角形内部随机点
	 */
	@Test
	public void testGetRandomPoint() {
		Triangle triangle=new Triangle(new Vector3(0, 0), new Vector3(10, 0), new Vector3(0, 10), 1);
		
		Vector3 vector3=new Vector3();
		for(int i=0;i<100000000;i++) {
			triangle.getRandomPoint(vector3);
//			System.out.println();
		}
		
	}
}
