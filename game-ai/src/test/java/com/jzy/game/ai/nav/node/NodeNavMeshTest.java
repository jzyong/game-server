package com.jzy.game.ai.nav.node;

import org.junit.Before;
import org.junit.Test;

import com.jzy.game.ai.nav.node.NodeNavMesh;
import com.jzy.game.ai.nav.triangle.TriangleNavMesh;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.TimeUtil;

/**
 * 测试寻路
 * @author JiangZhiYong
 *
 */
public class NodeNavMeshTest {
//	private static final String FILE_PATH="E:\\Project\\game-server\\trunk\\game-ai\\src\\test\\resources\\navmesh\\1000.navmesh";
	private static final String FILE_PATH="E:\\ldlh\\client\\Config\\Nav_build\\102.navmesh";
	private NodeNavMesh navMesh;
	
	@Before
	public void init() {
		 navMesh=new NodeNavMesh(FILE_PATH,false);
	}
	
	@Test
	public void test(){
		NodeNavMesh navMesh=new NodeNavMesh(FILE_PATH,true);
		navMesh.path(new Vector3(291, 0, 90), new Vector3(728, 0, 550));
//		navMesh.path(new Vector3(44, 0, 14), new Vector3(108, 0, 77));
	}
	
	
	/**
	 * 
	 * <p>1.三角形个数296,短距离寻路10000次,{@link NodeNavMesh}平均耗时8500ms，{@link TriangleNavMesh}平均耗时450ms</p>
	 * <p>2.三角形个数296,长距离寻路10000次,{@link NodeNavMesh}平均耗时15000ms，{@link TriangleNavMesh}平均耗时1500ms</p>
	 * <p>3.三角形个数975,短距离寻路10000次,{@link NodeNavMesh}平均耗时1300ms，{@link TriangleNavMesh}平均耗时300ms</p>
	 * <p>4.三角形个数975,长距离寻路10000次,{@link NodeNavMesh}平均耗时90000ms，{@link TriangleNavMesh}未找到路径？？？</p>
	 * <p>5.三角形个数975,长距离寻路10000次,{@link NodeNavMesh}平均耗时136000ms，{@link TriangleNavMesh}4400ms</p>
	 */
	@Test
	public void testFindPath() {
		long start = TimeUtil.currentTimeMillis();
		for(int i=0;i<10000;i++) {
//			PathData path = navMesh.path(new Vector3(61,13,191), new Vector3(107,11,146));			//1
//			PathData path = navMesh.path(new Vector3(61,13,191),new Vector3(305,35,213));			//2
//			PathData path = navMesh.path(new Vector3(28f,27.6f,111f), new Vector3(50,28,100));		//3
//			PathData path = navMesh.path(new Vector3(28f,27.6f,111f), new Vector3(221.4f,70,161.3f));//4
			PathData path = navMesh.path(new Vector3(28f,27.6f,111f), new Vector3(176.5f,19.8f,41.3f));//5
//			if(path!=null&&path.points!=null) {
//				path.points.forEach(p->System.out.println(p.toString()));
//			}
		}
		System.err.println("耗时："+(TimeUtil.currentTimeMillis()-start));
	}
}
