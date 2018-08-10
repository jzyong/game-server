package com.jzy.game.ai.quadtree.polygon;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jzy.game.ai.nav.polygon.Polygon;
import com.jzy.game.ai.nav.polygon.PolygonData;
import com.jzy.game.ai.nav.polygon.PolygonNavMesh;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.TimeUtil;

/**
 * 多边形四叉树测试
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PolygonQuadTreeTest {
	private static final String meshPath = "E:\\ldlh\\client\\Config\\Nav_build\\103.navmesh";
	PolygonNavMesh navMesh;

	@Before
	public void init() {
		long start = TimeUtil.currentTimeMillis();
		String navMeshStr = FileUtil.readTxtFile(meshPath);
		navMesh = new PolygonNavMesh(navMeshStr);
		System.out.println("加载地图耗时：" + (TimeUtil.currentTimeMillis() - start));
	}

	private PolygonGuadTree getTree() {
		PolygonData polygonData = navMesh.getGraph().getPolygonData();
		PolygonGuadTree qt = new PolygonGuadTree(polygonData.getStartX(), polygonData.getStartZ(),
				polygonData.getEndX(), polygonData.getEndZ(), /*(int) (polygonData.getWidth() / 40)*/5, 10);
		navMesh.getGraph().getPolygons().forEach(polygon -> {
			qt.set(polygon.center, polygon);
		});
		return qt;
	}

	@Test
	public void testGetCount() {
		PolygonGuadTree tree = getTree();
		System.out.println(String.format("个数%d,真实个数%d", tree.getCount(), tree.getRealCount()));
		Assert.assertEquals(navMesh.getGraph().getPolygons().size(), tree.getRealCount());
	}

	/**
	 * 测试获取
	 */
	@Test
	public void testGet() {
		PolygonGuadTree tree = getTree();
		
		//TODO map 101
//		Polygon polygon1 = tree.get(new Vector3(160, 96), null);
//		System.out.println(polygon1);
//		Assert.assertEquals("", navMesh.getPolygon(new Vector3(160, 96)), polygon1);
//
//		Polygon polygon2 = tree.get(new Vector3(74, 130), null);
//		System.out.println(polygon2);
//		Assert.assertEquals("", navMesh.getPolygon(new Vector3(74, 130)), polygon2);
//
//		Polygon polygon3 = tree.get(new Vector3(214, 123), null);
//		System.out.println(polygon3);
//		Assert.assertEquals("", navMesh.getPolygon(new Vector3(214, 123)), polygon3);
//
//		Polygon polygon4 = tree.get(new Vector3(287, 124), null);
//		System.out.println(polygon4);
//		Assert.assertEquals("", navMesh.getPolygon(new Vector3(287, 124)), polygon4);
//
//		Polygon polygon5 = tree.get(new Vector3(244, 217), null);
//		System.out.println(polygon5);
//		Assert.assertEquals("", navMesh.getPolygon(new Vector3(244, 217)), polygon5);
		
		
		// map 103
		Polygon polygon1 = tree.get(new Vector3(42, 230), null);
		System.out.println(polygon1);
		Assert.assertEquals("", navMesh.getPolygon(new Vector3(42, 230)), polygon1);

		Polygon polygon2 = tree.get(new Vector3(209, 41), null);
		System.out.println(polygon2);
		Assert.assertEquals("", navMesh.getPolygon(new Vector3(209, 41)), polygon2);

		Polygon polygon3 = tree.get(new Vector3(296, 527), null);
		System.out.println(polygon3);
		Assert.assertEquals("", navMesh.getPolygon(new Vector3(296, 527)), polygon3);
		
	}
	
	/**
	 * 测试查询
	 */
	@Test
	public void testSerarchWithIn() {
		long startTime=TimeUtil.currentTimeMillis();
		for(int i=0;i<1;i++) {
			List<Polygon> list = getTree().searchWithin(new Vector3(110.2f, 334.6f), 8);
//			if(list!=null) {
//				list.forEach(p->System.out.println(p.toString()));
//			}
		}
		System.out.println(TimeUtil.currentTimeMillis()-startTime);
	}

	/**
	 * 测试性能
	 * <h5>2018.8</h5><tbody> <br>
	 * 多边形个数186<br>
	 * 坐标new Vector3(160, 96) 1000000 四叉树 10子节点 800ms <br>
	 * 坐标new Vector3(160, 96) 1000000 循环 2200ms <br>
	 * <br>
	 * 坐标new Vector3(74, 130) 1000000 四叉树 10子节点 1000ms <br>
	 * 坐标new Vector3(74, 130) 1000000 循环 1200ms <br>
	 * <br>
	 * 坐标new Vector3(244, 217) 1000000 四叉树 10子节点 1000ms <br>
	 * 坐标new Vector3(244, 217) 1000000 循环 14500ms <br>
	 * <br><br>
	 * 多边形个数828<br>
	 * 坐标new Vector3(42, 230) 1000000 四叉树 10子节点 1100ms <br>
	 * 坐标new Vector3(42, 230) 1000000 循环 10000ms <br>
	 * <br>
	 * 坐标new Vector3(209, 41) 1000000 四叉树 10子节点 1000ms <br>
	 * 坐标new Vector3(209, 41) 1000000 循环 600ms <br>
	 * <br>
	 * 坐标new Vector3(296, 527) 1000000 四叉树 10子节点 1000ms <br>
	 * 坐标new Vector3(296, 527) 1000000 循环 60000ms <br>
	 * </tbody>
	 * 
	 * 
	 * 
	 * <h5>总结</h5>
	 * <p>
	 *  如果多边形在起始位置四叉树速度不一定比循环获取快，但是多边形在末尾位置，四叉树速度明显快于循环,多边形个数越多，性能差距越大<br>
	 *  实际在大地图中测试速度快了20倍左右
	 * </p>
	 */
	@Test
	public void testPerformance() {
		PolygonGuadTree tree = getTree();
		long startTime = TimeUtil.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			tree.get(new Vector3(296, 527), null);
		}
		System.err.println(TimeUtil.currentTimeMillis() - startTime);

		startTime = TimeUtil.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			navMesh.getPolygon(new Vector3(296, 527));
		}
		System.out.println(TimeUtil.currentTimeMillis() - startTime);
	}
}
