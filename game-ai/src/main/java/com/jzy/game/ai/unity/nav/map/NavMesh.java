package com.jzy.game.ai.unity.nav.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.jzy.game.ai.unity.nav.KNodeConnector;
import com.jzy.game.ai.unity.nav.KPolygon;
import com.jzy.game.ai.unity.nav.PolygonConverter;
import com.jzy.game.ai.unity.nav.TriangleBlock;
import com.jzy.game.ai.unity.nav.Vector3;
import com.jzy.game.engine.util.FileUtil;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.shape.random.RandomPointsBuilder;

/**
 * 寻路navmesh地图 <br>
 * 数据分为阻挡层和行走层,以三角形网格数据寻路
 * 
 * @author JiangZhiYong
 */
public class NavMesh implements Serializable, Cloneable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NavMesh.class);
    private static final long serialVersionUID = 1L;

    /** 是否为编辑器模式true会被缩放 */
    private final boolean editor;
    /** 地图宽x轴 */
    private float width;
    /** 地图高y轴 */
    private float height;
    private int mapID;
    private float startX;
    private float startZ;
    private float endX;
    private float endZ;
    /** 缩放比例 */
    private float scale;
    /** 三角形顶点之间的最大距离 */
    private float maxDistanceBetweenNode;

    /** 阻挡，不可行走区域 */
    private KNodeConnector<TriangleBlock> blockConnector = new KNodeConnector<>();
    private List<TriangleBlock> blockConnections = new ArrayList<>();

    /** 心走区，寻路层 */
    private KNodeConnector<TriangleBlock> pathConnector = new KNodeConnector<>();
    private List<TriangleBlock> pathConnections = new ArrayList<>();
    
    /**多边形转换工具*/
    private PolygonConverter polygonConverter = new PolygonConverter();

    /**
     * @param filePath
     *            文件路径
     */
    public NavMesh(String filePath) {
        this(filePath, false);
    }

    /**
     * @param filePath
     *            文件路径
     * @param editor
     *            是否为编辑器模式true会被缩放
     */
    public NavMesh(String filePath, boolean editor) {
        LOGGER.info("ss");
        this.editor = editor;
        String txtFile = FileUtil.readTxtFile(filePath);
        if (txtFile == null) {
            LOGGER.warn("navmesh数据{}读取失败", filePath);
            return;
        }
        NavMeshData data = JSON.parseObject(txtFile, NavMeshData.class);
        this.width = Math.abs(data.getEndX() - data.getStartX());
        this.height = Math.abs(data.getEndZ() - data.getStartZ());
        this.startX = data.getStartX();
        this.startZ = data.getStartZ();
        this.endX = data.getEndX();
        this.endZ = data.getEndZ();
        this.mapID = data.getMapID();
        LOGGER.info("加载地图{}宽{}高{}，开始坐标({},{}),结束坐标({},{})", mapID, width, height, startX, startZ, endX, endZ);
        if (this.editor) {
            scale = 850 / height;
        } else {
            scale = 1;
        }
        // TODO 是否应该取对角线？？？
        maxDistanceBetweenNode = Math.max(width, height) * scale;

        createPolygons(blockConnector, blockConnections, data.getBlockTriangles(), data.getBlockVertices(), true);
        createPolygons(blockConnector, blockConnections, data.getPathTriangles(), data.getPathVertices(), false);
    }


    /**
     * 创建三角形形 ，地图数据预处理
     * <br>TODO 先创建KPolygon 转换为Polygon再转换KPolygon ，能否优化？
     * 
     * @param nodeConnector
     *            节点连接关系处理器
     * @param triangleBlocks
     *            三角形连接关系
     * @param triangles
     *            三角形顶点需要，依次三个序号为一组
     * @param vertices
     *            顶点坐标
     * @param bufferOuter
     *            TriangleConnection ture 非行走区原始多边缓存为inner多边形，外多边形为包围该三角形形的六边形  false outer多边形，内外相同
     * @param editor
     *            true为编辑器模式
     */
    private final void createPolygons(KNodeConnector<TriangleBlock> nodeConnector, List<TriangleBlock> triangleBlocks, int[] triangles, Vector3[] vertices, boolean bufferOuter) {
        if (triangles == null || vertices == null) {
            LOGGER.warn("数据异常，数据为空");
            return;
        }
       
        //坐标缩放
        if (editor) {
            for (Vector3 vector : vertices) {
                vector.scale(scale);
            }
        }
        
        //三角形内创建随机点
        RandomPointsBuilder randomPointsBuilder=new RandomPointsBuilder();
        
        List<Vector3> list = new ArrayList<>(3);    //缓存三角形顶点坐标
        for (int i = 0; i < triangles.length; i += 3) {  //每三个连续顶点确定一个三角形
            if (triangles.length <= i + 2) {    //跳过多余点
                break;
            }
            list.clear();
            list.add(vertices[triangles[i]]);
            list.add(vertices[triangles[i + 1]]);
            list.add(vertices[triangles[i + 2]]);
            
            KPolygon kPolygon=new KPolygon(list);
            Polygon polygon = polygonConverter.makePolygonFrom(kPolygon);
            if (polygon == null) {
                continue;
            }
            TriangleBlock triangleBlock = null;
            if (bufferOuter) { //不可行走区
                triangleBlock = TriangleBlock.createBlockFromInnerPolygon(kPolygon);
            } else { //可行走区
                triangleBlock = TriangleBlock.createBlockFromOuterPolygon(kPolygon);
                //生成三角形快中的随机坐标，只需要行走区生成，如随机刷怪需要
                randomPointsBuilder.setExtent(polygon);
                triangleBlock.addRandomPoints(randomPointsBuilder.getGeometry().getCoordinates());
            }
            if (triangleBlock == null) {
                continue;
            }
            if (editor) {
                randomPointsBuilder.setNumPoints((int) ((Math.sqrt(polygon.getArea() / scale / scale) + 1) * 5));
            } else {
                randomPointsBuilder.setNumPoints((int) ((Math.sqrt(polygon.getArea()) + 1) * 5));
            }
           
            triangleBlocks.add(triangleBlock);
            nodeConnector.calculateKNodeConnection(triangleBlock, triangleBlocks, maxDistanceBetweenNode);
        }
    }

}
