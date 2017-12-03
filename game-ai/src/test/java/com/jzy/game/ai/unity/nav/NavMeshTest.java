package com.jzy.game.ai.unity.nav;

import org.junit.Test;

/**
 * 测试寻路
 * @author JiangZhiYong
 *
 */
public class NavMeshTest {
	private static final String FILE_PATH="E:\\Project\\game-server\\trunk\\game-ai\\src\\test\\resources\\navmesh\\1000.navmesh";
	
	@Test
	public void test(){
		NavMesh navMesh=new NavMesh(FILE_PATH);
	}
}
