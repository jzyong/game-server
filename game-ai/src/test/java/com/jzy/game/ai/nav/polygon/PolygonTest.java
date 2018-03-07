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
    
    /**
     * 是否在多边形内性能测试
     * <p>
     * {@link Polygon.isInnerPoint} 1亿次 2867ms
     * {@link Polygon.contains} 1亿次 4227ms
     * </p>
     */
    @Test
    public void testPerformance() {
    	Polygon rectangle=new Polygon(0, new Vector3(0, 0), new Vector3(1, 0), new Vector3(1, 1),new Vector3(0,1 ));
    	Vector3 point=new Vector3(0.5f,0.5f);
//    	Assert.assertTrue(rectangle.isInnerPoint(point));
//    	Assert.assertTrue(rectangle.contains(point));
    	for(int i=0;i<100000000;i++) {
//    		rectangle.isInnerPoint(point);
    		rectangle.contains(point);
    	}
    	
		
//		Vector3 sectorinit = new Vector3(200f, 10f);
//		sector = player.getMap().getSector(sectorinit, dir, 30f, 80f, 60f);
//		sectorOriginPoint=sectorinit.unityTranslate(dir.y, 30);
		
    }
}
