
package com.jzy.game.ai.pfa;

/** 
 * 消耗计算接口
 * A {@code Heuristic} generates estimates of the cost to move from a given node to the goal.
 * <p>
 * With a heuristic function pathfinding algorithms can choose the node that is most likely to lead to the optimal path. The
 * notion of "most likely" is controlled by a heuristic. If the heuristic is accurate, then the algorithm will be efficient. If
 * the heuristic is terrible, then it can perform even worse than other algorithms that don't use any heuristic function such as
 * Dijkstra.
 * 
 * @param <N> Type of node
 * 
 * @author davebaol */
public interface Heuristic<N> {

	/** Calculates an estimated cost to reach the goal node from the given node.
	 * @param node the start node
	 * @param endNode the end node
	 * @return the estimated cost */
	public float estimate (N node, N endNode);
}
