package com.jzy.game.ai.nav.edge;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.edge.NavMesh;
import com.jzy.game.engine.util.FileUtil;

/**
 * 测试寻路
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月7日 下午5:38:46
 */
public class NavMeshTest {
	/** 地图数据路径 */
//	private static final String meshPath = "E:\\Java\\game-server\\game-ai\\src\\test\\resources\\navmesh\\1000.navmesh";
	private static final String meshPath = "E:\\ldlh\\client\\Config\\Nav_build\\101.navmesh";

	/**
	 * 加载NavMesh
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月7日 下午5:39:30
	 */
	@Test
	public void testLoadNavMesh() {
		String navMeshStr = FileUtil.readTxtFile(meshPath);
		NavMesh navMesh = new NavMesh(navMeshStr);

		System.out.println(JSON.toJSONString(navMesh.getGraph().getTriangles()));
	}
	
	@Test
	public void test() {
		System.out.println(1 << 9);
	}
}
