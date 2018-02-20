package com.jzy.game.ai.nav.triangle;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.triangle.TriangleNavMesh;
import com.jzy.game.ai.nav.triangle.TriangleGraph;
import com.jzy.game.ai.nav.triangle.TriangleGraphPath;
import com.jzy.game.ai.nav.triangle.TrianglePointPath;
import com.jzy.game.ai.nav.triangle.Triangle;
import com.jzy.game.ai.pfa.Connection;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.TimeUtil;

/**
 * 测试寻路
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午5:38:46
 */
public class TriangleNavMeshTest {
	/** 地图数据路径 */
	// private static final String meshPath =
	// "E:\\Java\\game-server\\game-ai\\src\\test\\resources\\navmesh\\1000.navmesh";
	private static final String meshPath = "E:\\ldlh\\client\\Config\\Nav_build\\102.navmesh";
	TriangleNavMesh navMesh;

	@Before
	public void init() {
		long start = TimeUtil.currentTimeMillis();
		String navMeshStr = FileUtil.readTxtFile(meshPath);
		navMesh = new TriangleNavMesh(navMeshStr);
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
		TriangleGraph graph = navMesh.getGraph();
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
	 */
	@Test
	public  void testFindPath() {
		TriangleGraphPath path=new TriangleGraphPath();
		TrianglePointPath pointPath=new TrianglePointPath();
		long start=TimeUtil.currentTimeMillis();
		
		for(int i=0;i<1;i++) {
//			navMesh.findPath(new Vector3(61,13,191), new Vector3(107,11,146), path);	
//			List<Vector3> list = navMesh.findPath(new Vector3(61,13,191), new Vector3(107,11,146), pointPath);				//1
//			List<Vector3> list = navMesh.findPath(new Vector3(61,13,191), new Vector3(305,35,213), pointPath);				//2
//			List<Vector3> list = navMesh.findPath(new Vector3(28f,27.6f,111f), new Vector3(50,28,100), pointPath);			//3
//			List<Vector3> list = navMesh.findPath(new Vector3(28f,27.6f,111f), new Vector3(221.4f,70,161.3f), pointPath);	//4 找不到路径？？
//			List<Vector3> list = navMesh.findPath(new Vector3(28f,27.6f,111f), new Vector3(116f,48.5f,177f), pointPath);	//4-1
//			List<Vector3> list = navMesh.findPath(new Vector3(116f,48.5f,177f), new Vector3(221.4f,70,161.3f), pointPath);	//4-2 //找不到路径
			List<Vector3> list = navMesh.findPath(new Vector3(28f,27.6f,111f), new Vector3(105.6f,56f,182), pointPath);	//4-3 //找不到路径
//			List<Vector3> list = navMesh.findPath(new Vector3(28f,27.6f,111f), new Vector3(176.5f,19.8f,41.3f), pointPath);	//5 
			if(list!=null) {
				list.forEach(v->System.out.println(v.toString()));
			}
		}
		System.err.println("耗时："+(TimeUtil.currentTimeMillis()-start));
		
		
	}
	
	@Test
	public void test() {
		System.out.println(1 << 9);
	}
}
