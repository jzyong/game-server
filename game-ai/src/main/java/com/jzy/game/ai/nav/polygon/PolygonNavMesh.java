package com.jzy.game.ai.nav.polygon;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.nav.NavMesh;
import com.jzy.game.ai.nav.triangle.Triangle;
import com.jzy.game.ai.nav.triangle.TriangleData;
import com.jzy.game.ai.nav.triangle.TriangleGraphPath;
import com.jzy.game.ai.pfa.IndexedAStarPathFinder;
import com.jzy.game.engine.math.Vector3;

/**
 * 多边形寻路
 * 
 * @author JiangZhiYong
 * @date 2018年2月23日
 * @mail 359135103@qq.com
 */
public final class PolygonNavMesh extends NavMesh {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolygonNavMesh.class);
    private final PolygonGraph graph;
    private final PolygonHeuristic heuristic;
    private final IndexedAStarPathFinder<Polygon> pathFinder;


    public PolygonNavMesh(String navMeshStr) {
        this(navMeshStr, 1);
    }

    /**
     * @param navMeshStr
     *            导航网格数据
     * @param scale
     *            放大倍数
     */
    public PolygonNavMesh(String navMeshStr, int scale) {
        graph = new PolygonGraph(JSON.parseObject(navMeshStr, PolygonData.class), scale);
        pathFinder = new IndexedAStarPathFinder<Polygon>(graph);
        heuristic = new PolygonHeuristic();
    }

    /**
     * 查询路径
     * 
     * @param fromPoint
     * @param toPoint
     * @param path
     */
    public boolean findPath(Vector3 fromPoint, Vector3 toPoint, PolygonGraphPath path) {
        path.clear();
        Polygon fromPolygon = getPolygon(fromPoint);
        if (pathFinder.searchConnectionPath(fromPolygon, getPolygon(toPoint), heuristic, path)) {
            path.start = new Vector3(fromPoint);
            path.end = new Vector3(toPoint);
            path.startPolygon = fromPolygon;
            return true;
        }
        return false;
    }

    /**
     * 查询路径
     * 
     * @param fromPoint
     * @param toPoint
     * @param pointPath
     * @return
     */
    public List<Vector3> findPath(Vector3 fromPoint, Vector3 toPoint, PolygonPointPath pointPath) {
        PolygonGraphPath polygonGraphPath = new PolygonGraphPath();
        boolean find = findPath(fromPoint, toPoint, polygonGraphPath);
        if (!find) {
            return pointPath.getVectors();
        }
        // 计算坐标点
        pointPath.calculateForGraphPath(polygonGraphPath, false);

        return pointPath.getVectors();
    }


    /**
     * 坐标点所在的多边形
     * 
     * @param point
     * @return
     */
    public Polygon getPolygon(Vector3 point) {
        Optional<Polygon> findFirst = graph.getPolygons().stream().filter(p -> p.isInnerPoint(point)).findFirst();
        if (findFirst.isPresent()) {
            return findFirst.get();
        }
        return null;
    }

    @Override
    public Vector3 getPointInPath(float x, float z) {
        Vector3 vector3 = new Vector3(x, z);
        Polygon polygon = getPolygon(vector3);
        if (polygon == null) {
            LOGGER.info("坐标{},{}不在路径中", x, z);
            return null;
        }
        vector3.y = polygon.y;
        return vector3;
    }

    public PolygonGraph getGraph() {
        return graph;
    }


}
