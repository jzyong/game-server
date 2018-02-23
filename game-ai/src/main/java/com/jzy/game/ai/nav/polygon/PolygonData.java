package com.jzy.game.ai.nav.polygon;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import com.jzy.game.ai.nav.NavMeshData;
import com.jzy.game.engine.math.Vector3;

/**
 * 多边形数据
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PolygonData extends NavMeshData {
    private static final long serialVersionUID = 1L;

//    /** 多边形顶点序号， */
//    private int[][] pathPolygons;
    /** 多边形顶点序号， */
    private Map<Integer, Set<Integer>> pathPolygonIndexs;

    /**
     * TODO
     * <p>
     * Unity的NavMeshData有一些共边的三角形，共边的三角形其实不是连通关系，
     * 共边的三角形只是他们共同构成一个凸多边形，并且这种共边的三角形，全部都是扇形排列。
     * </p>
     * <p>
     * 首先先以此划分，生成多边形列表。这个多边形列表，当然没有共边。
     * </p>
     * <p>
     * Unity的NavMeshData 那些不共边的多边形只是index索引不共边，从坐标上还是有共边的，所以我们合并掉重合顶点，重新排列多边形的index索引，就可以恢复到有共边的多边形列表和顶点列表
     * </p>
     */
    @Override
    public void check(int scale) {
        scaleVector(pathVertices, scale);
        pathPolygonIndexs=buildUnityPolygonIndex(this.pathTriangles);
        this.width = Math.abs(this.getEndX() - this.getStartX());
        this.height = Math.abs(this.getEndZ() - this.getStartZ());
    }
    
    
    /**
     * 构建多边形索引
     * <p>
     * Unity的NavMeshData有一些共边的三角形，共边的三角形其实不是连通关系，
     * 共边的三角形只是他们共同构成一个凸多边形，并且这种共边的三角形，全部都是扇形排列。
     * </p>
     * @param indexs
     * @return
     */
    private Map<Integer, Set<Integer>> buildUnityPolygonIndex(int[] indexs){
        Map<Integer, Set<Integer>> map=new TreeMap<>();
        int index=0;
        for(int i=0;i<indexs.length;) {
            Set<Integer> set=new TreeSet<>();
            set.add(indexs[i]);
            set.add(indexs[i+1]);
            set.add(indexs[i+2]);
            int jIndex=i+3;
            for(int j=jIndex;j<indexs.length;j+=3) {
                if(set.contains(indexs[j])||set.contains(indexs[j+1])||set.contains(indexs[j+2])) {
                    set.add(indexs[j]);
                    set.add(indexs[j+1]);
                    set.add(indexs[j+2]);
                    i+=3;
                }else {
                    i+=3;
                    break;
                }
            }
            map.put(index++, set);
            if(jIndex==indexs.length) {
                break;
            }
        }
        
        return map;
    }


    public Map<Integer, Set<Integer>> getPathPolygonIndexs() {
        return pathPolygonIndexs;
    }


    public void setPathPolygonIndexs(Map<Integer, Set<Integer>> pathPolygonIndexs) {
        this.pathPolygonIndexs = pathPolygonIndexs;
    }
    

//    public int[][] getPathPolygons() {
//        return pathPolygons;
//    }
//
//    public void setPathPolygons(int[][] pathPolygons) {
//        this.pathPolygons = pathPolygons;
//    }


}
