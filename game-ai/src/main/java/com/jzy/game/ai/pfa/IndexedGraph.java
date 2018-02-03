
package com.jzy.game.ai.pfa;


/**A*算法索引图 
 * <br>
 * A graph for the {@link IndexedAStarPathFinder}.
 * 
 * @param <N> Type of node
 * 
 * @author davebaol */
public interface IndexedGraph<N> extends Graph<N> {

	/** Returns the unique index of the given node.
	 * @param node the node whose index will be returned
	 * @return the unique index of the given node. */
	public int getIndex (N node);

	/** Returns the number of nodes in this graph. */
	public int getNodeCount ();

}
