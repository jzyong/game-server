
package com.jzy.game.ai.pfa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 默认图路径 
 * <br>
 * Default implementation of a {@link GraphPath} that uses an internal
 * {@link ArrayList} to store nodes or connections.
 * 
 * @param <N>
 *            Type of node
 * 
 * @author davebaol
 */
public class DefaultGraphPath<N> implements GraphPath<N> {
	public final List<N> nodes;

	/** Creates a {@code DefaultGraphPath} with no nodes. */
	public DefaultGraphPath() {
		this(new ArrayList<N>());
	}

	/** Creates a {@code DefaultGraphPath} with the given capacity and no nodes. */
	public DefaultGraphPath(int capacity) {
		this(new ArrayList<N>(capacity));
	}

	/** Creates a {@code DefaultGraphPath} with the given nodes. */
	public DefaultGraphPath(ArrayList<N> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void clear() {
		nodes.clear();
	}

	@Override
	public int getCount() {
		return nodes.size();
	}

	@Override
	public void add(N node) {
		nodes.add(node);
	}

	@Override
	public N get(int index) {
		return nodes.get(index);
	}

	@Override
	public void reverse() {
		Collections.reverse(nodes);
	}

	@Override
	public Iterator<N> iterator() {
		return nodes.iterator();
	}
}
