
package com.jzy.game.ai.pfa;

/**
 * 连接关系
 * A connection between two nodes of the {@link Graph}. The connection has a
 * non-negative cost that often represents time or distance. However, the cost
 * can be anything you want, for instance a combination of time, distance, and
 * other factors.
 * 
 * @param <N>
 *            Type of node
 * 
 * @author davebaol
 */
public interface Connection<N> {

	/**通过消耗<br> 
	 * Returns the non-negative cost of this connection */
	public float getCost();

	/** Returns the node that this connection came from */
	public N getFromNode();

	/** Returns the node that this connection leads to */
	public N getToNode();

}
