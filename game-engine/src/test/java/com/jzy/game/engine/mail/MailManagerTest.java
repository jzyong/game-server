package com.jzy.game.engine.mail;

import org.junit.Ignore;
import org.junit.Test;

import com.jzy.game.engine.mail.MailManager;

/**
 * 发送邮件测试
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月22日 下午6:02:34
 */
@Ignore
public class MailManagerTest {

	@Test
	public void testSendMail(){
		MailManager.getInstance().sendTextMailAsync("hh", "dd", "359135103@qq.com");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
