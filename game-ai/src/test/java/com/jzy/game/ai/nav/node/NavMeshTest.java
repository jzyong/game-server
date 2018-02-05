package com.jzy.game.ai.nav.node;

import org.junit.Test;

import com.jzy.game.ai.nav.node.NodeNavMesh;
import com.jzy.game.engine.math.Vector3;

/**
 * 测试寻路
 * @author JiangZhiYong
 *
 */
public class NavMeshTest {
//	private static final String FILE_PATH="E:\\Project\\game-server\\trunk\\game-ai\\src\\test\\resources\\navmesh\\1000.navmesh";
	private static final String FILE_PATH="E:\\code\\sf\\trunk\\net.sz.framework.java\\net.sz.framework\\net.sz.framework.way.navmesh\\target\\1100.navmesh";
	
	@Test
	public void test(){
		NodeNavMesh navMesh=new NodeNavMesh(FILE_PATH,true);
		navMesh.path(new Vector3(291, 0, 90), new Vector3(728, 0, 550));
//		navMesh.path(new Vector3(44, 0, 14), new Vector3(108, 0, 77));
	}
}
