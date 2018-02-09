package com.jzy.game.ai.nav.edge.ui;

import java.util.ArrayList;
import java.util.List;

import com.jzy.game.ai.nav.edge.EdgeNavMesh;
import com.jzy.game.ai.nav.edge.NavMeshPointPath;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.TimeUtil;


/**
 * 移动玩家
 * @fix JiangZhiYong
 */
public class MovePlayer {

    protected Vector3 pos = new Vector3();
    protected Vector3 target = new Vector3();
    protected Vector3 targetAdjusted = new Vector3();
    protected List<Vector3> paths=new ArrayList<>();
    protected NavMeshPointPath navMeshPointPath=new NavMeshPointPath();
    protected float speed;
    protected float speedX;
    protected float speedZ;
    protected float moveAngle;
    protected Vector3 currentTargetPoint = null;
    protected EdgeNavMesh map;

    public MovePlayer(EdgeNavMesh map) {
        speed = 200;
        this.map = map;
    }

    public void path() {
        long time = TimeUtil.currentTimeMillis();
        paths= map.findPath(pos, targetAdjusted, navMeshPointPath);
        time = TimeUtil.currentTimeMillis() - time;
        System.out.println("寻路耗时：" + time);
    }

    public void update(double seconds) {
        if (this.map == null) {
            return;
        }
//        pos = map.getNearestPointInPaths(pos);	//TODO 获取不在路径中的周围坐标点
//        targetAdjusted = map.getNearestPointInPaths(target);
        targetAdjusted=target;
        if (speed == 0) {
            return;
        }
        double secondsLeft = seconds;
        for (int i = 0; i <paths.size(); i++) {
            currentTargetPoint =paths.get(i);
            Vector3 oldPos = new Vector3();
            oldPos.x = pos.x;
            oldPos.z = pos.z;
            double distUntilTargetReached =Vector3.dst(currentTargetPoint.x, currentTargetPoint.z, pos.x, pos.z);
            double timeUntilTargetReached = distUntilTargetReached / speed;
            if (timeUntilTargetReached < 0) {
                return;
            }
            float xCoordToWorkOutAngle = currentTargetPoint.x - pos.x;
            float yCoordToWorkOutAngle = currentTargetPoint.z - pos.z;
            if (xCoordToWorkOutAngle != 0 || yCoordToWorkOutAngle != 0) {
                double dirAngle = Vector3.findAngle(0, 0, xCoordToWorkOutAngle, yCoordToWorkOutAngle);//(float)Math.atan(yCoordToWorkOutAngle/xCoordToWorkOutAngle);
                moveAngle = (float) dirAngle;
                speedX = (float) Math.cos(moveAngle) * speed;
                speedZ = (float) Math.sin(moveAngle) * speed;
            } else {
                speedX = 0f;
                speedZ = 0f;
            }
            if (secondsLeft >= timeUntilTargetReached) {
                pos.x = currentTargetPoint.x;
                pos.z = currentTargetPoint.z;
                speedX = 0f;
                speedZ = 0f;
                secondsLeft -= timeUntilTargetReached;
                if (i < 0) {
                    break;
                }
                paths.remove(i);
                i--;
            } else {
                //s = t(u + v)/2
                pos.x = (float) (oldPos.x + secondsLeft * speedX);
                pos.z = (float) (oldPos.z + secondsLeft * speedZ);
                secondsLeft = 0;
                break;
            }
        }
    }

    /**
     * @return the pos
     */
    public Vector3 getPos() {
        return pos;
    }

    /**
     * @return the targetAdjusted
     */
    public Vector3 getTargetAdjusted() {
        return targetAdjusted;
    }

    /**
     * @return the target
     */
    public Vector3 getTarget() {
        return target;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Vector3 target) {
        this.target = target;
    }

    /**
     * @param targetAdjusted the targetAdjusted to set
     */
    public void setTargetAdjusted(Vector3 targetAdjusted) {
        this.targetAdjusted = targetAdjusted;
    }

    /**
     * @return the map
     */
    public EdgeNavMesh getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(EdgeNavMesh map) {
        this.map = map;
    }

	public List<Vector3> getPaths() {
		return paths;
	}
    
    
}
