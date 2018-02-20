package com.jzy.game.ai.nav.polygon;

import org.junit.Assert;
import org.junit.Test;
import com.jzy.game.engine.math.Vector3;

/**
 * 多边形测试
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PolygonTest {

    /**
     * 测试面积
     */
    @Test
    public void testCalculateArea() {
        Polygon polygon = new Polygon(1, new Vector3(0, 0), new Vector3(0, 1), new Vector3(1, 0));
        Assert.assertEquals(0.5, polygon.getArea(), 0.001);
    }

    /**
     * 测试是否为凸多边形
     */
    @Test
    public void testIsConvexPolygon() {
        Polygon polygon = new Polygon(1, new Vector3(0, 0), new Vector3(0, 1), new Vector3(1, 0));
        Assert.assertTrue(polygon.calculateIsConvex());

        Polygon polygon2 = new Polygon(1, new Vector3(0, 0), new Vector3(0, 1), new Vector3(1, 1), new Vector3(1, 0), new Vector3(0.5f, 0.5f));
        Assert.assertFalse(polygon2.calculateIsConvex());

        Polygon polygon3 = new Polygon(1, new Vector3(0, 0), new Vector3(0, 1), new Vector3(1, 1), new Vector3(1, 0));
        Assert.assertTrue(polygon3.calculateIsConvex());
    }

    /**
     * 测试点是否在多边形中
     */
    @Test
    public void testIsInnerPoint() {
        Polygon polygon = new Polygon(1, new Vector3(0, 0), new Vector3(0, 1), new Vector3(1, 0));
        Assert.assertTrue(polygon.isInnerPoint(new Vector3(0.5f,0.5f)));
        Assert.assertFalse(polygon.isInnerPoint(new Vector3(1f,1f)));
    }
}
