package com.jzy.game.engine.math;

/**
 * 向量接口
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public interface Vector<T extends Vector<T>> {

    /**
     * Adds the given vector to this vector
     * 
     * @param v
     *            The vector
     * @return This vector for chaining
     */
    T add(T v);

    /**
     * Scales this vector by a scalar
     * 
     * @param scalar
     *            The scalar
     * @return This vector for chaining
     */
    T scl(float scalar);

    /**
     * Scales this vector by another vector
     * 
     * @return This vector for chaining
     */
    T scl(T v);

    /**
     * Sets this vector from the given vector
     * 
     * @param v
     *            The vector
     * @return This vector for chaining
     */
    T set(T v);

    /**
     * Normalizes this vector. Does nothing if it is zero.
     * 
     * @return This vector for chaining
     */
    T nor();

    /** @return The euclidean length */
    float len();

    /**
     * This method is faster than {@link Vector#len()} because it avoids calculating
     * a square root. It is useful for comparisons, but not for getting exact
     * lengths, as the return value is the square of the actual length.
     * 
     * @return The squared euclidean length
     */
    float len2();

    /**
     * First scale a supplied vector, then add it to this vector.
     * 
     * @param v
     *            addition vector
     * @param scalar
     *            for scaling the addition vector
     */
    T mulAdd(T v, float scalar);

    /**
     * 坐标是否相等
     * 
     * @param precision
     *            精度
     * @return
     */
    default boolean equal(Vector3 vector3, float precision) {
        return false;
    }
}
