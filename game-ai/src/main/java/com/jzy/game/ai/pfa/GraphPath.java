
package com.jzy.game.ai.pfa;

/**图路径
 * <br>
 *  A {@code GraphPath} represents a path in a {@link Graph}. Note that a path can be defined in terms of nodes or
 * {@link Connection connections} so that multiple edges between the same pair of nodes can be discriminated.
 * 
 * @param <N> Type of node
 * 
 * @author davebaol */
public interface GraphPath<N> extends Iterable<N> {

	/** Returns the number of items of this path. */
	public int getCount ();

	/** Returns the item of this path at the given index. */
	public N get (int index);

	/** Adds an item at the end of this path. */
	public void add (N node);

	/** Clears this path. */
	public void clear ();

	/** Reverses this path. */
	public void reverse ();

}
