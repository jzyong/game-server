package com.jzy.game.ai.nav.polygon;

import org.junit.Before;
import org.junit.Test;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.TimeUtil;

/**
 * 多边形寻路测试
 * @author JiangZhiYong
 * @date 2018年2月20日 
 * @mail 359135103@qq.com
 */
public class PolygonNavMeshTest {
    private static final String meshPath = "E:\\Project\\game-server2\\game-server\\game-ai\\101.navmesh";
    PolygonNavMesh navMesh;
    @Before
    public void init() {
        long start = TimeUtil.currentTimeMillis();
        String navMeshStr = FileUtil.readTxtFile(meshPath);
        navMesh = new PolygonNavMesh(navMeshStr);
        System.out.println("加载地图耗时：" + (TimeUtil.currentTimeMillis() - start));
    }
    
    /**
     * 查询获取路径
     */
    @Test
    public void testFindPath() {
        PolygonGraphPath path=new PolygonGraphPath();
        navMesh.findPath(new Vector3(104, 138), new Vector3(190, 125), path);
        if(path.nodes!=null) {
            path.nodes.forEach(p->System.out.println(p.toString()));
        }
    }
}
