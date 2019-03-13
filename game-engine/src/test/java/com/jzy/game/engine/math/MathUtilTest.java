package com.jzy.game.engine.math;

import java.util.Random;

import org.junit.Test;

import com.jzy.game.engine.math.MathUtil;
import com.jzy.game.engine.math.Vector3;

/**
 * 数学计算工具
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class MathUtilTest {

	/**
	 * 随机坐标
	 */
	@Test
	public void testRandomVector3() {
		for(int i=0;i<1000;i++) {
			System.out.println(MathUtil.random(new Vector3(), new Vector3(10f, 10f, 10f)));
		}
	}
	
	/**
	 * 随机种子测试
	 */
	@Test
	public void testRandomSeed() {
		Random random1=new Random(10000);
		Random random2=new Random(10000);
		for(int i=0;i<200;i++) {
			System.out.println(random1.nextInt(100));
			System.out.println(random2.nextInt(100));
			System.out.println();
		}
	}
	
}
