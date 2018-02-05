package com.jzy.game.ai.nav.edge;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.edge.EdgeNavMesh;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.TimeUtil;

/**
 * 测试寻路
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午5:38:46
 */
public class EdgeNavMeshTest {
	/** 地图数据路径 */
	// private static final String meshPath =
	// "E:\\Java\\game-server\\game-ai\\src\\test\\resources\\navmesh\\1000.navmesh";
	private static final String meshPath = "E:\\ldlh\\client\\Config\\Nav_build\\101.navmesh";
	EdgeNavMesh navMesh;

	@Before
	public void init() {
		long start = TimeUtil.currentTimeMillis();
		String navMeshStr = FileUtil.readTxtFile(meshPath);
		navMesh = new EdgeNavMesh(navMeshStr);
		System.out.println("加载地图耗时：" + (TimeUtil.currentTimeMillis() - start));
	}

	/**
	 * 加载NavMesh
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月7日 下午5:39:30
	 */
	@Test
	public void testLoadNavMesh() {
		NavMeshGraph graph = navMesh.getGraph();
		System.out.println(String.format("三角形个数：%d 所有边%s 共享边：%d 独立边：%d", graph.getTriangleCont(),
				graph.getNumTotalEdges(), graph.getNumConnectedEdges(), graph.getNumDisconnectedEdges()));
//		System.out.println(JSON.toJSONString(navMesh.getGraph().getTriangles()));
	}

	/**
	 * 获取三角形
	 */
	@Test
	public void testTriangle() {
		long start = TimeUtil.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			Triangle triangle = navMesh.getTriangle(new Vector3(264, 18, 117));
			if (triangle != null) {
				System.err.println(triangle.toString());
			}
			Assert.assertNotNull(triangle);
		}
		
		System.out.println("获取三角形耗时：" + (TimeUtil.currentTimeMillis() - start));
	}

	/**
	 * 查找路径
	 * TODO 寻路地图数据存在问题，图连接不上
	 */
	@Test
	public  void testFindPath() {
		NavMeshGraphPath path=new NavMeshGraphPath();
		navMesh.findPath(new Vector3(61,13,191), new Vector3(107,11,146), path);
		System.out.println(path.nodes.toString());
	}
	
	@Test
	public void test() {
		System.out.println(1 << 9);
	}
}
