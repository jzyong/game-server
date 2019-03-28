package com.jzy.game.engine.math;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * 数学计算工具
 *
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class MathUtilTest {

    private float PI = (float) Math.PI;
    private float HALF_PI = (float) Math.PI / 2;

    /**
     * 随机坐标
     */
    @Test
    public void testRandomVector3() {
        for (int i = 0; i < 1000; i++) {
            System.out.println(MathUtil.random(new Vector3(), new Vector3(10f, 10f, 10f)));
        }
    }

    /**
     * 随机种子测试
     */
    @Test
    public void testRandomSeed() {
        Random random1 = new Random(10000);
        Random random2 = new Random(10000);
        for (int i = 0; i < 200; i++) {
            System.out.println(random1.nextInt(100));
            System.out.println(random2.nextInt(100));
            System.out.println();
        }
    }

    @Test
    public void testFloor() {
        Assert.assertEquals(0, MathUtil.floor(0));
        Assert.assertEquals(1, MathUtil.floor(1.6f));
        Assert.assertEquals(-1, MathUtil.floor(-0.5f));
    }

    @Test
    public void testFloorPositive() {
        Assert.assertEquals(0, MathUtil.floorPositive(0f));
        Assert.assertEquals(1, MathUtil.floorPositive(1.5f));
    }

    @Test
    public void testCeil() {
        Assert.assertEquals(0, MathUtil.ceil(0));
        Assert.assertEquals(2, MathUtil.ceil(1.4f));
        Assert.assertEquals(-1, MathUtil.ceil(-1.6f));
    }

    @Test
    public void testCeilPositive() {
        Assert.assertEquals(0, MathUtil.ceilPositive(0));
        Assert.assertEquals(2, MathUtil.ceilPositive(1.4f));
    }

    @Test
    public void testRoundPositive() {
        Assert.assertEquals(0, MathUtil.roundPositive(0));
        Assert.assertEquals(1, MathUtil.roundPositive(1.3f));
        Assert.assertEquals(2, MathUtil.roundPositive(1.7f));
    }

    @Test
    public void testSin() {
        Assert.assertEquals(0, MathUtil.sin(0.0f), 0.01);
        Assert.assertEquals(0.5f, MathUtil.sin(PI / 6), 0.01);
        Assert.assertEquals(1, MathUtil.sin(HALF_PI), 0.01);
        Assert.assertEquals(0, MathUtil.sin(PI * 2), 0.01);
    }

    @Test
    public void testSinDeg() {
        Assert.assertEquals(0, MathUtil.sinDeg(0.0f), 0.01);
        Assert.assertEquals(0.5, MathUtil.sinDeg(30), 0.01);
        Assert.assertEquals(1, MathUtil.sinDeg(90), 0.01);
        Assert.assertEquals(0, MathUtil.sinDeg(180), 0.01);
    }

    @Test
    public void testCos() {
        Assert.assertEquals(1, MathUtil.cos(0), 0);
        Assert.assertEquals(0.5, MathUtil.cos(PI / 3), 0.01);
        Assert.assertEquals(0, MathUtil.cos(HALF_PI), 0.01);
        Assert.assertEquals(1, MathUtil.cos(PI * 2), 0.01);
    }

    @Test
    public void testCosDeg() {
        Assert.assertEquals(1, MathUtil.cosDeg(0), 0.01);
        Assert.assertEquals(0.5, MathUtil.cosDeg(60), 0.01);
        Assert.assertEquals(-1, MathUtil.cosDeg(180), 0.01);
        Assert.assertEquals(1, MathUtil.cosDeg(360), 0.01);
    }

    @Test
    public void testIsPowerOfTwo() {
        Assert.assertFalse(MathUtil.isPowerOfTwo(3));
        Assert.assertTrue(MathUtil.isPowerOfTwo(2));
    }

    @Test
    public void testNextPowerOfTwo() {
        Assert.assertEquals(1, MathUtil.nextPowerOfTwo(0));
        Assert.assertEquals(16, MathUtil.nextPowerOfTwo(13));
        Assert.assertEquals(1024, MathUtil.nextPowerOfTwo(1024));
        Assert.assertEquals(0, MathUtil.nextPowerOfTwo(-25));
    }

    @Test
    public void testRound() {
        Assert.assertEquals(0, MathUtil.round(0.0f));

        Assert.assertEquals(0, MathUtil.round(0.4f));
        Assert.assertEquals(1, MathUtil.round(0.5f));
        Assert.assertEquals(1, MathUtil.round(0.6f));
        Assert.assertEquals(3, MathUtil.round(3f));

        Assert.assertEquals(0, MathUtil.round(-0.4f));
        Assert.assertEquals(0, MathUtil.round(-0.5f));
        Assert.assertEquals(-1, MathUtil.round(-0.6f));
        Assert.assertEquals(-8, MathUtil.round(-8f));
    }

    @Test
    public void testClampShort() {
        Assert.assertEquals((short) 5, MathUtil.clamp((short) 20, (short) 1, (short) 5));
        Assert.assertEquals((short) 2, MathUtil.clamp((short) -6, (short) 2, (short) 8));
        Assert.assertEquals((short) 4, MathUtil.clamp((short) 4, (short) -7, (short) 19));
        Assert.assertEquals((short) 3, MathUtil.clamp((short) 3, (short) 3, (short) 6));
        Assert.assertEquals((short) 12, MathUtil.clamp((short) 12, (short) 9, (short) 12));
    }

    @Test
    public void testClampInt() {
        Assert.assertEquals(5, MathUtil.clamp(20, 1, 5));
        Assert.assertEquals(2, MathUtil.clamp(-6, 2, 8));
        Assert.assertEquals(4, MathUtil.clamp(4, -7, 19));
        Assert.assertEquals(3, MathUtil.clamp(3, 3, 6));
        Assert.assertEquals(12, MathUtil.clamp(12, 9, 12));
    }

    @Test
    public void testClampFloat() {
        Assert.assertEquals(5.1f, MathUtil.clamp(20.6f, 1.8f, 5.1f), 0);
        Assert.assertEquals(2.2f, MathUtil.clamp(-6.1f, 2.2f, 8.3f), 0);
        Assert.assertEquals(4.3f, MathUtil.clamp(4.3f, -7.1f, 19.7f), 0);
        Assert.assertEquals(3.4f, MathUtil.clamp(3.4f, 3.4f, 6.8f), 0);
        Assert.assertEquals(12.5f, MathUtil.clamp(12.5f, 9.3f, 12.5f), 0);
    }

    @Test
    public void testClampDouble() {
        Assert.assertEquals(5.1d, MathUtil.clamp(20.6d, 1.8d, 5.1d), 0);
        Assert.assertEquals(2.2d, MathUtil.clamp(-6.1d, 2.2d, 8.3d), 0);
        Assert.assertEquals(4.3d, MathUtil.clamp(4.3d, -7.1d, 19.7d), 0);
        Assert.assertEquals(3.4d, MathUtil.clamp(3.4d, 3.4d, 6.8d), 0);
        Assert.assertEquals(12.5d, MathUtil.clamp(12.5d, 9.3d, 12.5d), 0);
    }

    @Test
    public void testClampLong() {
        Assert.assertEquals(5L, MathUtil.clamp(20L, 1L, 5));
        Assert.assertEquals(2L, MathUtil.clamp(-6L, 2L, 8));
        Assert.assertEquals(4L, MathUtil.clamp(4L, -7L, 19));
        Assert.assertEquals(3L, MathUtil.clamp(3L, 3L, 6));
        Assert.assertEquals(12L, MathUtil.clamp(12L, 9L, 12));
    }

    @Test
    public void testIsZero() {
        Assert.assertFalse(MathUtil.isZero(0x1p-19f));

        Assert.assertTrue(MathUtil.isZero(0.0f));
        Assert.assertTrue(MathUtil.isZero(-0.0f));

        Assert.assertFalse(MathUtil.isZero(25f, 10));

        Assert.assertTrue(MathUtil.isZero(0.0f, 0.0f));
        Assert.assertTrue(MathUtil.isZero(-0.0f, 0.0f));
        Assert.assertTrue(MathUtil.isZero(-5f, 10));
    }

    @Test
    public void testIsEqual() {
        Assert.assertFalse(MathUtil.isEqual(-4f, 4f));
        Assert.assertTrue(MathUtil.isEqual(0.0f, -0.0f));
        Assert.assertTrue(MathUtil.isEqual(21f, 21f));

        Assert.assertFalse(MathUtil.isEqual(1f, 2f, 0.5f));
        Assert.assertTrue(MathUtil.isEqual(1, 2, 1));
    }

    @Test
    public void testLerp() {
        Assert.assertEquals(3, MathUtil.lerp(1, 5, 0.5f), 0);
    }

    @Test
    public void testLerpAngle() {
        Assert.assertEquals(4.73, MathUtil.lerpAngle(0.5f, 10.5f, 0.8f), 0.01);
    }

    @Test
    public void testLerpAngleDeg() {
        Assert.assertEquals(90, MathUtil.lerpAngleDeg(30f, 180f, 10f), 0.01);
    }

    @Test
    public void testAtan2() {
        //Tested to within error specified
        Assert.assertEquals(HALF_PI, MathUtil.atan2(1, 0), 0.00488);
        Assert.assertEquals(0, MathUtil.atan2(0, 0), 0.00488);
        Assert.assertEquals(-HALF_PI, MathUtil.atan2(-1, 0), 0.00488);
        Assert.assertEquals(0.545, MathUtil.atan2(3, 5), 0.00488);
        Assert.assertEquals(-2.597, MathUtil.atan2(-3, -5), 0.00488);
        Assert.assertEquals(1.025, MathUtil.atan2(5, 3), 0.00488);
    }

    @Test
    public void testLog(){
        Assert.assertEquals(1, MathUtil.log(10, 10), 0);
        Assert.assertEquals(0, MathUtil.log(10, 1), 0);

        Assert.assertEquals(2, MathUtil.log2(4), 0);
    }

}
