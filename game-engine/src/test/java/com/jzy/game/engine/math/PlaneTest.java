package com.jzy.game.engine.math;

import com.jzy.game.engine.math.Plane;
import com.jzy.game.engine.math.Vector3;
import org.junit.Assert;
import org.junit.Test;

public class PlaneTest {

  @Test
  public void testConstructorFromNormalAndDistance() {
    final Plane plane = new Plane(new Vector3(0.0f, 0.1f, 0.2f), 0.3f);

    Assert.assertEquals(new Vector3(0.0f, 0.44721356f, 0.8944271f), plane.getNormal());
    Assert.assertEquals(0.3f, plane.getD(), 0);
  }
  
  @Test
  public void testConstructorFromNormalAndPoint() {
    final Plane plane = new Plane(new Vector3(0.0f, 0.1f, 0.2f), new Vector3(0.3f, 0.4f, 0.5f));

    Assert.assertEquals(new Vector3(0.0f, 0.44721356f, 0.8944271f), plane.getNormal());
    Assert.assertEquals(-0.626099f, plane.getD(), 0);
  }

  @Test
  public void testConstructorFromThreePoints() {
    final Plane plane = new Plane(new Vector3(0.0f, 0.1f, 0.2f), new Vector3(0.3f, 0.4f, 0.5f), new Vector3(0.6f, 0.7f, 0.8f));

    Assert.assertEquals(new Vector3(0.70710677f, 0.0f, -0.70710677f), plane.getNormal());
    Assert.assertEquals(0.14142136f, plane.getD(), 0);
  }
    
  @Test
  public void testSetFromPlane() {
    final Plane plane = new Plane();
    plane.set(new Plane());

    Assert.assertEquals(new Vector3(0.0f, 0.0f, 0.0f), plane.getNormal());
    Assert.assertEquals(0.0, plane.getD(), 0);
  }

  @Test
  public void testSetFromNormalAndDistance() {
    final Plane plane = new Plane();
    plane.set(0.0f, 0.1f, 0.2f, 0.3f);

    Assert.assertEquals(new Vector3(0.0f, 0.1f, 0.2f), plane.getNormal());
    Assert.assertEquals(0.30000001192092896, plane.getD(), 0);
  }

  @Test
  public void testSetFromPointAndNormalVector() {
    final Plane plane = new Plane();
    plane.set(new Vector3(0.0f, 0.1f, 0.2f), new Vector3(0.3f, 0.4f, 0.5f));

    Assert.assertEquals(new Vector3(0.3f, 0.4f, 0.5f), plane.getNormal());
    Assert.assertEquals(-0.14000000059604645, plane.getD(), 0);
  }

  @Test
  public void testSetFromPointAndNormalFloat() {
    final Plane plane = new Plane();
    plane.set(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f);

    Assert.assertEquals(new Vector3(0.3f, 0.4f, 0.5f), plane.getNormal());
    Assert.assertEquals(-0.14000000059604645, plane.getD(), 0);
  }

  @Test
  public void testSetFromThreePoints() {
    final Plane plane = new Plane();
    plane.set(new Vector3(0.0f, 0.1f, 0.2f), new Vector3(0.3f, 0.4f, 0.5f), new Vector3(0.6f, 0.7f, 0.8f));

    Assert.assertEquals(new Vector3(0.70710677f, 0.0f, -0.70710677f), plane.getNormal());
    Assert.assertEquals(0.1414213627576828, plane.getD(), 0);
  }

  @Test
  public void testDistance() {
    final Plane plane = new Plane(new Vector3(0.0f, 0.1f, 0.2f), 0.3f);
    Assert.assertEquals(1.0602632f, plane.distance(new Vector3(0.4f, 0.5f, 0.6f)), 0);
  }

  @Test
  public void testTestPointFromVector() {
    final Plane plane = new Plane();
    Assert.assertEquals("OnPlane", plane.testPoint(new Vector3(0.0f, 0.0f, 0.0f)).toString());

    plane.set(0.0f, 0.0f, 0.0f, -0.1f);
    Assert.assertEquals("Back", plane.testPoint(new Vector3(0.0f, 0.0f, 0.0f)).toString());

    plane.set(0.0f, 0.0f, 0.0f, 0.1f);
    Assert.assertEquals("Front", plane.testPoint(new Vector3(0.0f, 0.0f, 0.0f)).toString());
  }

  @Test
  public void testTestPointFromFloat() {
    final Plane plane = new Plane();
    Assert.assertEquals(Plane.PlaneSide.OnPlane, plane.testPoint(0.0f, 0.0f, 0.0f));

    plane.set(0.0f, 0.0f, 0.0f, -0.1f);
    Assert.assertEquals(Plane.PlaneSide.Back, plane.testPoint(0.0f, 0.0f, 0.0f));

    plane.set(0.0f, 0.0f, 0.0f, 0.1f);
    Assert.assertEquals(Plane.PlaneSide.Front, plane.testPoint(0.0f, 0.0f, 0.0f));
  }

  @Test
  public void testFrontFacing() {
    final Plane plane = new Plane(new Vector3(0.0f, 0.1f, 0.2f), 0.3f);

    Assert.assertEquals(false, plane.isFrontFacing(new Vector3(0.4f, 0.5f, 0.6f)));
    Assert.assertEquals(true, plane.isFrontFacing(new Vector3(0.0f, 0.0f, 0.0f)));
  }

  @Test
  public void testToString() {
    final Plane plane = new Plane();
    Assert.assertEquals("{x=0.0, y=0.0, z=0.0}, 0.0", plane.toString());
  }
}
