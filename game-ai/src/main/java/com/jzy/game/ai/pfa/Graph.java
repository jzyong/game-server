
package com.jzy.game.ai.pfa;

import java.util.List;

/**
 * 图接口 
 * <br>
 * A graph is a collection of nodes, each one having a collection of
 * outgoing {@link Connection connections}.
 * 
 * @param <N>
 *            Type of node
 * 
 * @author davebaol
 */
public interface Graph<N> {

	/**和当前节点相连的连接关系
	 * Returns the connections outgoing from the given node.
	 * 
	 * @param fromNode
	 *            the node whose outgoing connections will be returned
	 * @return the array of connections outgoing from the given node.
	 */
	public List<Connection<N>> getConnections(N fromNode);
}
