package com.jzy.game.ai.nav.edge;

import org.junit.Assert;
import org.junit.Test;

import com.jzy.game.ai.nav.triangle.Triangle;
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
}
