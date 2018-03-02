package com.jzy.game.ai.nav.voronoi;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jzy.game.ai.nav.voronoi.DelaunayTriangle;
import com.jzy.game.engine.math.MathUtil;
import com.jzy.game.engine.math.Vector3;

/**
 * Delaunay三角形测试
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class DelaunayTriangleTest {

	@Test
	public void testBuildDelaunayTriangle() {
		List<DelaunayTriangle> triangles = new ArrayList<>();
		triangles.add(new DelaunayTriangle(new Vector3(250, -5000), new Vector3(-5000, 400), new Vector3(5000, 400)));
		List<Vector3> vectors = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			vectors.add(new Vector3(MathUtil.random(500), MathUtil.random(400)));
		}
		vectors.sort((o1, o2) -> {
			if (o1.x > o2.x) {
				return 1;
			} else if (o1.x < o2.x) {
				return -1;
			}
			return 0;
		});
		DelaunayTriangle.buildDelaunayTriangle(triangles, vectors);
		
		vectors.forEach(v->System.err.println(v.toString()));
		triangles.forEach(triangle -> System.out.println(triangle.toString()));

	}
}
