package com.jzy.game.engine.util;

import com.jzy.game.engine.util.CipherUtil;
import org.junit.Assert;
import org.junit.Test;

public class CipherUtilTest {

  @Test
  public void MD5Encode() throws Exception {
    Assert.assertEquals("0cc175b9c0f1b6a831c399e269772661", CipherUtil.MD5Encode("a"));
  }

  @Test
  public void MD5Bytes() throws Exception {
    final byte[] v = { 97 };
    final byte[] expect = { 48, 99, 99, 49, 55, 53, 98, 57, 99, 48, 102, 49, 98, 54, 97, 56, 51, 49, 99, 51, 57, 57, 101, 50, 54, 57, 55, 55, 50, 54, 54, 49 };
    Assert.assertArrayEquals(expect, CipherUtil.MD5Bytes(v));
  }
}
