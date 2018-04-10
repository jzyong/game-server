package com.jjy.game.engine.math;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}
