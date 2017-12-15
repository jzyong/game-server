package com.jzy.game.ai.unity.nav;

import java.io.Serializable;


/**
 * 块排序类
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class BlockDistanceAndQuad implements Serializable, Comparable<BlockDistanceAndQuad> {

	private static final long serialVersionUID = 1L;
	public TriangleBlock block;	//方块
    public double distNodeToCenterLessRadiusSqSigned; //到起始几点的距离
    public int xIndicator;
    public int yIndicator;

    public BlockDistanceAndQuad(TriangleBlock block, double distNodeToCenterLessRadiusSqSigned, int xIndicator, int yIndicator) {
        this.block = block;
        this.distNodeToCenterLessRadiusSqSigned = distNodeToCenterLessRadiusSqSigned;
        this.xIndicator = xIndicator;
        this.yIndicator = yIndicator;
    }

    @Override
    public int compareTo(BlockDistanceAndQuad ob) {
        double obDist = ((BlockDistanceAndQuad) ob).getDistNodeToCenterLessRadiusSqSigned();
        if (distNodeToCenterLessRadiusSqSigned > obDist) {
            return 1;
        } else if (distNodeToCenterLessRadiusSqSigned < obDist) {
            return -1;
        } else {
            return 0;
        }
    }

    public double getDistNodeToCenterLessRadiusSqSigned() {
        return distNodeToCenterLessRadiusSqSigned;
    }


    public int getXIndicator() {
        return xIndicator;
    }

    public int getYIndicator() {
        return yIndicator;
    }

	public TriangleBlock getBlock() {
		return block;
	}

	public void setBlock(TriangleBlock block) {
		this.block = block;
	}

	public int getxIndicator() {
		return xIndicator;
	}

	public void setxIndicator(int xIndicator) {
		this.xIndicator = xIndicator;
	}

	public int getyIndicator() {
		return yIndicator;
	}

	public void setyIndicator(int yIndicator) {
		this.yIndicator = yIndicator;
	}

	public void setDistNodeToCenterLessRadiusSqSigned(double distNodeToCenterLessRadiusSqSigned) {
		this.distNodeToCenterLessRadiusSqSigned = distNodeToCenterLessRadiusSqSigned;
	}

}
