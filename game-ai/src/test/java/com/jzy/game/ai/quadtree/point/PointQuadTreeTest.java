package com.jzy.game.ai.quadtree.point;

import org.junit.Test;

import com.jzy.game.ai.quadtree.Data;
import com.jzy.game.ai.quadtree.Node;
import com.jzy.game.ai.quadtree.NodeType;
import com.jzy.game.ai.quadtree.QuadTree;
import com.jzy.game.ai.quadtree.point.PointQuadTree;
import com.jzy.game.engine.math.Vector3;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 点四叉树测试
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class PointQuadTreeTest extends junit.framework.TestCase {

	private PointQuadTree<String> getTree() {
		PointQuadTree<String> qt = new PointQuadTree<String>(0, 0, 100, 100);
		qt.set(new Vector3(5, 20), "Foo");
		qt.set(new Vector3(50, 32), "Bar");
		qt.set(new Vector3(47, 96), "Baz");
		qt.set(new Vector3(50, 50), "Bing");
		qt.set(new Vector3(12, 0), "Bong");
		qt.set(new Vector3(99, 99), "Bong");
		return qt;
	}

	private void assertTreesChildrenAreNull(PointQuadTree<String> qt) {
		Node<String> root = qt.getRootNode();
		assertNull("NE should be null", root.getNe());
		assertNull("NW should be null", root.getNw());
		assertNull("SE should be null", root.getSe());
		assertNull("SW should be null", root.getSw());
	}

	@Test
	public void testGetCount() {
		PointQuadTree<String> qt = getTree();
		assertEquals("Count should be 5", 6, qt.getCount());
		qt.remove(new Vector3(50, 32));
		assertEquals("Count should be 4", 5, qt.getCount());
	}

	@Test
	public void testGetKeys() {
		List<Vector3> list = getTree().getKeys();
		Collections.sort(list, (o1, o2) -> (int) (o1.getX() - o2.getX()));
		System.out.println(list.toString());
		List<PointData<String>> keyValues = getTree().getKeyValues();
		System.err.println(keyValues);
	}

	@Test
	public void testGetValues() {
		List<String> values = getTree().getValues();
		Collections.sort(values);
		String valueString = values.toString();
		assertEquals("Sorted values should be [Bar, Baz, Bing, Bong, Foo]", "[Bar, Baz, Bing, Bong, Foo]", valueString);
	}

	@Test
	public void testContains() {
		PointQuadTree<String> qt = getTree();
		assertTrue("Should contain (5, 20)", qt.contains(new Vector3(5, 20)));
		assertFalse("Should not contain (13, 13)", qt.contains(new Vector3(13, 13)));
	}

	@Test
	public void testSearchIntersects() {
		PointQuadTree<String> qt = getTree();
		List<PointData<String>> points = qt.searchIntersect(4, 0, 51, 98);
		Collections.sort(points);
		String keyString = Arrays.asList(points).toString();
		String expected = "[(5.0, 20.0), (12.0, 0.0), (47.0, 96.0), (50.0, 32.0), (50.0, 50.0)]";
		System.err.println(points.toString());

	}

	@Test
	public void testSearchWithin() {
		PointQuadTree<String> qt = getTree();
		List<PointData<String>> points = qt.searchWithin(4, -1, 51, 98);
		Collections.sort(points);
		String keyString = Arrays.asList(points).toString();
		String expected = "[(5.0, 20.0), (12.0, 0.0), (47.0, 96.0), (50.0, 32.0), (50.0, 50.0)]";
		System.out.println(points);
	}

	@Test
	public void testClear() {
		QuadTree<Vector3, String> qt = getTree();
		qt.clear();
		assertTrue("Tree should be empty", qt.isEmpty());
		assertFalse("Tree should not contain (5, 20)", qt.contains(new Vector3(5, 20)));
	}

	@Test
	public void testConstructor() {
		PointQuadTree<?> qt = new PointQuadTree<Object>(-10, -5, 6, 12);
		Node<?> root = qt.getRootNode();
		assertEquals("X of root should be -10.0", -10.0, root.getX());
		assertEquals("Y of root should be -5.0", -5.0, root.getZ());
		assertEquals("Width of root should be 16.0", 16.0, root.getW());
		assertEquals("Height of root should be 17.0", 17.0, root.getH());
		assertTrue("Tree should be empty", qt.isEmpty());
	}

	@Test
	public void testClone() {
		PointQuadTree<String> qt = getTree().clone();
		assertFalse("Clone should not be empty", qt.isEmpty());
		assertTrue("Should contain (47, 96)", qt.contains(new Vector3(47, 96)));
	}

	@Test
	public void testRemove() {
		PointQuadTree<String> qt = getTree();
		assertEquals("(5, 20) should be removed", "Foo", qt.remove(new Vector3(5, 20)));
		assertEquals("(5, 20) should be removed", "Bar", qt.remove(new Vector3(50, 32)));
		assertEquals("(5, 20) should be removed", "Baz", qt.remove(new Vector3(47, 96)));
		assertEquals("(5, 20) should be removed", "Bing", qt.remove(new Vector3(50, 50)));
		assertEquals("(5, 20) should be removed", "Bong", qt.remove(new Vector3(12, 0)));
		assertEquals("(5, 20) should be removed", "Bong", qt.remove(new Vector3(99, 99)));
		assertNull("(6, 6) wasn\"t there to remove", qt.remove(new Vector3(6, 6)));
		assertTrue("Tree should be empty", qt.isEmpty());
		assertTreesChildrenAreNull(qt);
	}

	@Test
	public void testIsEmpty() {
		PointQuadTree<String> qt = getTree();
		qt.clear();
		assertTrue(qt.isEmpty());
		assertEquals("Root should be empty node", NodeType.EMPTY, qt.getRootNode().getNodeType());
		assertTreesChildrenAreNull(qt);
	}

	@Test
	public void testBalancing() {
		PointQuadTree<String> qt = new PointQuadTree<String>(0, 0, 100, 100);
		Node<String> root = qt.getRootNode();

		// Add a point to the NW quadrant.
		qt.set(new Vector3(25, 25), "first");

		assertEquals("Root should be a leaf node.", NodeType.LEAF, root.getNodeType());
		assertTreesChildrenAreNull(qt);

		assertEquals("first", root.getData().getValue());

		// Add another point in the NW quadrant
		qt.set(new Vector3(25, 30), "second");

		assertEquals("Root should now be a pointer.", NodeType.POINTER, root.getNodeType());
		assertNotNull("NE should be not be null", root.getNe());
		assertNotNull("NW should be not be null", root.getNw());
		assertNotNull("SE should be not be null", root.getSe());
		assertNotNull("SW should be not be null", root.getSw());
		assertNull(root.getData());

		// Delete the second point.
		qt.remove(new Vector3(25, 30));

		assertEquals("Root should have been rebalanced and be a leaf node.", NodeType.LEAF, root.getNodeType());
		assertTreesChildrenAreNull(qt);
		assertEquals("first", root.getData().getValue());
	}

	@Test
	public void testTreeBounds() {
		PointQuadTree<String> qt = getTree();
		assertFails(qt, -10, -10, "1");
		assertFails(qt, -10, 10, "2");
		assertFails(qt, 10, -10, "3");
		assertFails(qt, -10, 110, "4");
		assertFails(qt, 10, 130, "5");
		assertFails(qt, 110, -10, "6");
		assertFails(qt, 150, 14, "7");
	}

	public <T> void assertFails(PointQuadTree<T> qt, float x, float y, T value) {
		try {
			qt.set(new Vector3(x, y), value);
			fail();
		} catch (Exception ex) {
			assertEquals("Out of bounds : (" + x + ", " + y + ")", ex.getMessage());
		}
	}
}
