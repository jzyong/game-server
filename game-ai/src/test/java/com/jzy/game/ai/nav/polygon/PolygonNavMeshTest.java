package com.jzy.game.ai.nav.polygon;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

import com.jzy.game.ai.nav.node.NodeNavMesh;
import com.jzy.game.ai.nav.triangle.TriangleNavMesh;
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
//	private static final String meshPath = "E:\\game-server\\game-server\\game-ai\\102.navmesh";
	private static final String meshPath = "E:\\ldlh\\client\\Config\\Nav_build\\110.navmesh";
//    private static final String meshPath = "E:\\Project\\game-server2\\game-server\\game-ai\\101.navmesh";
    PolygonNavMesh navMesh;
    @Before
    public void init() {
        long start = TimeUtil.currentTimeMillis();
        String navMeshStr = FileUtil.readTxtFile(meshPath);
        navMesh = new PolygonNavMesh(navMeshStr);
        System.out.println("加载地图耗时：" + (TimeUtil.currentTimeMillis() - start));
    }
    
    
    /**
	 * <h3>2018-2 </h3>
	 * <p>1.三角形个数260,短距离寻路10000次,{@link NodeNavMesh}平均耗时4900ms，{@link TriangleNavMesh}平均耗时200ms,{@link PolygonNavMesh}平均耗时：120ms</p>
	 * <p>2.三角形个数296,长距离寻路10000次,{@link NodeNavMesh}平均耗时7386ms，{@link TriangleNavMesh}平均耗时723ms,{@link PolygonNavMesh}平均耗时：385ms</p>
	 * <p>3.三角形个数789,短距离寻路10000次,{@link NodeNavMesh}平均耗时737ms，{@link TriangleNavMesh}平均耗时133ms,{@link PolygonNavMesh}平均耗时：110ms</p>
	 * <p>4.三角形个数789,长距离寻路10000次,{@link NodeNavMesh}平均耗时48000ms，{@link TriangleNavMesh}未处理unity共享边问题，查询失败,{@link PolygonNavMesh} 1215ms</p>
	 * <p>5.三角形个数789,长距离寻路10000次,{@link NodeNavMesh}平均耗时136000ms，{@link TriangleNavMesh}1029ms,{@link PolygonNavMesh}平均耗时：800ms</p>
	 */
    @Test
    public void testPerformance() {
        PolygonPointPath pointPath=new PolygonPointPath();
        List<Vector3> paths=null;
        long start=TimeUtil.currentTimeMillis();
        for(int i=0;i<10000;i++) {
//            paths = navMesh.findPath(new Vector3(61,13,191), new Vector3(107,11,146), pointPath);         //1
//            paths = navMesh.findPath(new Vector3(61,13,191), new Vector3(305,35,213),  pointPath);          //2
//            paths = navMesh.findPath(new Vector3(28f,27.6f,111f), new Vector3(50,28,100),  pointPath);          //3
//            paths = navMesh.findPath(new Vector3(28f,27.6f,111f), new Vector3(221.4f,70,161.3f),  pointPath);     //4 
//            paths = navMesh.findPath(new Vector3(28f,27.6f,111f), new Vector3(176.5f,19.8f,41.3f),  pointPath);     //5 
        	paths = navMesh.findPath(new Vector3(66f,70f,146f), new Vector3(232f,37f,186f),  pointPath);		//6
        }
        System.err.println("耗时："+(TimeUtil.currentTimeMillis()-start));
        if(paths!=null) {
            paths.forEach(p->System.out.println(p.toString()));
        }
    }
    
    /**
     * 查询获取路径
     */
    @Test
    public void testFindPath() {
//        PolygonGraphPath path=new PolygonGraphPath();
//        navMesh.findPath(new Vector3(104, 138), new Vector3(212.0f,232.0f), path);
//        if(path.nodes!=null) {
//            path.nodes.stream().filter(p->p!=null).forEach(p->{
//                PolygonEdge polygonEdge=(PolygonEdge)p;
//                System.err.println(polygonEdge.fromNode.index);
//            });
//        }
        PolygonPointPath pointPath=new PolygonPointPath();
        List<Vector3> paths = navMesh.findPath(new Vector3(168.7f,25.3f, 197.4f), new Vector3(159.7f,25.4f,199.2f), pointPath);
        if(paths!=null) {
            paths.forEach(p->System.out.println(p.toString()));
        }
    }
    
    /**
     * 测试获取随机点
     * <p>
     * 1.随机点范围跨多个多边形100000次，半径：300，耗时：8308;半径300,耗时： 9077
     * 2.随机点范围在一个多边形内100000次，半径：300，耗时：8308;半径300,耗时： 9077
     * 
     * <p>
     */
    @Test
    public void testFindRandomPointInPath() {
    	long begin=TimeUtil.currentTimeMillis();
    	for(int i=0;i<10000;i++) {
    		navMesh.getRandomPointsInPath(new Vector3(205,19,113), 5f, 0.2f);
//    		navMesh.getRandomPointsInPath(new Vector3(109.2f,10f,59.2f), 10f, 0.2f);
    	}
    	System.out.println("耗时："+(TimeUtil.currentTimeMillis()-begin));
    }
}
