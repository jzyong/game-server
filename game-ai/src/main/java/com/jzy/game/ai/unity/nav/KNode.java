package com.jzy.game.ai.unity.nav;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 节点（多边形的顶点坐标+寻路需要的属性）
 * 
 * @author JiangZhiYong
 * @date 2017年12月3日
 * @mail 359135103@qq.com
 */
public class KNode implements Serializable, Comparable<KNode> {
    private static final long serialVersionUID = 1L;
    private static final AtomicLong ID_CREATE=new AtomicLong(0);
    public final static double G_COST_NOT_CALCULATED_FLAG = -Double.MAX_VALUE;  //g消耗未计算标示
    
    protected long id = 0;  // 里的id是防止clone后，地址变更，无法查找
    protected List<KNode> connectedNodes;   //周围连接的节点
    protected List<KNode> tempConnectedNodes; //临时保存周围连接的节点
    protected Vector3 point; //当前节点代表的坐标点
    protected double gCost;       // distance to startPoint, via the parent nodes.
    protected double hCost;       // distance straight to endPoint.
    protected double fCost;       // gCost+hCost. This is what the the A* algorithm uses to sort the openList in PathFinder.
    
    public KNode() {
        id = ID_CREATE.getAndIncrement();
        connectedNodes = new ArrayList<>();
        tempConnectedNodes = new ArrayList<>();
        gCost = G_COST_NOT_CALCULATED_FLAG;
    }

    public KNode(Vector3 point) {
        this();
        this.point = point;
    }



    @Override
    public int compareTo(KNode o) {
        // TODO Auto-generated method stub
        return 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<KNode> getConnectedNodes() {
        return connectedNodes;
    }

    public List<KNode> getTempConnectedNodes() {
        return tempConnectedNodes;
    }

    public Vector3 getPoint() {
        return point;
    }

    public double getgCost() {
        return gCost;
    }

    public double gethCost() {
        return hCost;
    }

    public double getfCost() {
        return fCost;
    }
    
    /**
     * 清除当前节点连接点，及该节点在其他节点中的对象
     */
    public void clearConnectedNodes() {
        //移除其他节点中的关系
        for (int k = connectedNodes.size() - 1; k >= 0; k--) {
            List<KNode> otherConnectedNodes = connectedNodes.get(k).getConnectedNodes();
            int index = otherConnectedNodes.indexOf(this);
            if (index != -1) {
                otherConnectedNodes.remove(index);
            }
        }
        connectedNodes.clear();
    }
    
}
