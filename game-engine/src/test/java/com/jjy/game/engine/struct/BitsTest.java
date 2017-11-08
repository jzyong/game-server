package com.jjy.game.engine.struct;

import org.junit.Assert;
import org.junit.Test;

import com.jzy.game.engine.struct.Bits;

/**
 * 测试位
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年11月8日 下午3:15:08
 */
public class BitsTest {
	
	@Test
	public void test() {
		Bits bits=new Bits();
		bits.set(3);
		bits.set(13);
		bits.set(35);
		Assert.assertEquals(33, bits.nextClearBit(33));
		Assert.assertEquals(35, bits.nextSetBit(33));
	}
}
