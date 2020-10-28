package com.jzy.game.ai.nav.polygon;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

//import com.jzy.game.ai.nav.node.NodeNavMesh;
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
	private static final String meshPath = "E:\\game-server\\game-server\\game-ai\\20.navmesh";
//	private static final String meshPath = "E:\\ldlh\\client\\Config\\Nav_build\\119.navmesh";
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
	 * 
	 * <h3>2018-12</h3>
	 * <p>
	 * 对比腾讯《仙剑奇侠传 online》游戏后台优化  jps，A*寻路，环境不一致，不具代表性 <br>
	 * 腾讯10000次，起点到终点网格个数：200 A*平均：26ms JPS平均分别为：1.7ms、0.32ms、0.23ms、0.2ms、0.095ms
	 * <p>
	 * 7.多边形个数1007，寻路10000次 起始点：{x=60.27303, y=0.0, z=495.56827}-->目标点：{x=429.0, y=0.0, z=125.0} 起点到终点网格个数100<br>
	 * {@link NodeNavMesh}运行异常ms，{@link TriangleNavMesh}平均耗时0.36ms,{@link PolygonNavMesh}平均耗时：0.37ms
	 * </p>
	 * <p>
	 * 8.多边形个数4439，三角形个数7257 寻路10000次 起始点：{x=12.0, y=0.0, z=505.0}-->目标点：{x=407.0, y=0.0, z=95.0} 起点到终点网格个数169<br>
	 * {@link NodeNavMesh}运行异常ms，{@link TriangleNavMesh}平均耗时9.2ms,{@link PolygonNavMesh}平均耗时：1.8ms
	 * </p>
	 * <p>
	 * 9.多边形个数4439，三角形个数7257 寻路10000次 起始点：{x=373.0, y=0.0, z=247.0}-->目标点：{x=353.0, y=0.0, z=213.0} 起点到终点网格个数7<br>
	 * {@link NodeNavMesh}运行异常ms，{@link TriangleNavMesh}平均耗时0.27ms,{@link PolygonNavMesh}平均耗时：0.03ms
	 *  
	 * </p>
	 * <p>总结：网格数增多的情况下，A*寻路性能下降，在格子总数很大情况下使用四叉树分割的PolygonNavmesh性能更好，短距离无影响</p>
	 * 
	 * <h3>2019-3</h3>
	 * <p>10.多边形个数5433,三角形个数9347，寻路10000次 起始点：Vector3(27,16f,482f)-->目标点：Vector3(87f,13f,898f) 起点到终点网格个数127<br>
	 * {@link TriangleNavMesh}平均耗时0.53ms，加载耗时805ms;{@link PolygonNavMesh}平均耗时0.28ms,加载耗时10547</p>
	 * <p>在大地图中（二十个地图放在一起连接寻路），多边形个数增加对性能影响较少，使用四叉树进行预计算耗时严重</p>
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
//        	paths = navMesh.findPath(new Vector3(66f,70f,146f), new Vector3(232f,37f,186f),  pointPath);		//6
//        	paths = navMesh.findPath(new Vector3(60.27f,0f,495.56f), new Vector3(429.0f,0f,125.0f),  pointPath);		//7
//        	paths = navMesh.findPath(new Vector3(12f,0f,505f), new Vector3(407f,0f,95f),  pointPath);		//8
//        	paths = navMesh.findPath(new Vector3(373f,0f,247f), new Vector3(353f,0f,213f),  pointPath);		//9
        	paths = navMesh.findPath(new Vector3(27,16f,482f), new Vector3(87f,13f,898f),  pointPath);		//10
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
