package com.jzy.game.engine.util;

import com.jzy.game.engine.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

  @Test
  public void testIsNullOrEmptyFalse() {
    Assert.assertFalse(StringUtil.isNullOrEmpty("     a"));
  }

  @Test
  public void testIsNullOrEmptyTrue() {
    Assert.assertTrue(StringUtil.isNullOrEmpty(null));
    Assert.assertTrue(StringUtil.isNullOrEmpty("      "));
  }

  @Test
  public void testIsIntegerNullOrEmpty() {
    Assert.assertFalse(StringUtil.isInteger(null));
    Assert.assertFalse(StringUtil.isInteger(""));
  }

  @Test
  public void testIsIntegerTrue() {
    Assert.assertTrue(StringUtil.isInteger("-1"));
    Assert.assertTrue(StringUtil.isInteger("1"));
    Assert.assertTrue(StringUtil.isInteger("+1"));
    Assert.assertTrue(StringUtil.isInteger("+"));
    Assert.assertTrue(StringUtil.isInteger("-"));
  }

  @Test
  public void testIsIntegerFalse() {
    Assert.assertFalse(StringUtil.isInteger("--"));
    Assert.assertFalse(StringUtil.isInteger("++"));
    Assert.assertFalse(StringUtil.isInteger("a"));
  }

  @Test
  public void testIsDoubleNullOrEmpty() {
    Assert.assertFalse(StringUtil.isDouble(null));
    Assert.assertFalse(StringUtil.isDouble(""));
  }

  @Test
  public void testIsDoubleTrue() {
    Assert.assertTrue(StringUtil.isDouble("-1"));
    Assert.assertTrue(StringUtil.isDouble("-1."));
    Assert.assertTrue(StringUtil.isDouble("1."));
    Assert.assertTrue(StringUtil.isDouble("1."));
    Assert.assertTrue(StringUtil.isDouble("+1"));
    Assert.assertTrue(StringUtil.isDouble("+1."));
    Assert.assertTrue(StringUtil.isDouble("+"));
    Assert.assertTrue(StringUtil.isDouble("-"));
    Assert.assertTrue(StringUtil.isDouble("+."));
    Assert.assertTrue(StringUtil.isDouble("-."));
  }

  @Test
  public void testIsDoubleFalse() {
    Assert.assertFalse(StringUtil.isDouble("--"));
    Assert.assertFalse(StringUtil.isDouble("++"));
    Assert.assertFalse(StringUtil.isDouble("a"));
  }

  @Test
  public void testLowerFirstChar() {
    Assert.assertEquals("aA", StringUtil.lowerFirstChar("AA"));
    Assert.assertEquals("!", StringUtil.lowerFirstChar("!"));
  }

  @Test
  public void testUpFirstChar() {
    Assert.assertEquals("Aa", StringUtil.upFirstChar("aa"));
    Assert.assertEquals("!", StringUtil.upFirstChar("!"));
  }
}
