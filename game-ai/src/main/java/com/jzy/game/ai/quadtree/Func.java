package com.jzy.game.ai.quadtree;

import com.jzy.game.ai.quadtree.point.PointQuadTree;

/**
 * 功能函数
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 * @param <K>
 * @param <V>
 */
public interface Func<V> {
    public void call(PointQuadTree<V> quadTree, Node<V> node);
}
