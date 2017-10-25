package com.jjy.game.hall.script;

import java.util.function.Consumer;

import com.jjy.game.model.struct.Mail;
import com.jjy.game.model.struct.Mail.MailType;
import com.jzy.game.engine.script.IScript;


/**
 * 邮件
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月21日 下午4:45:19
 */
public interface IMailScript extends IScript {

	/**
	 * 发送邮件
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月21日 下午4:26:31
	 * @param title
	 * @param content
	 * @param type
	 * @param mailConsumer
	 */
	default void sendMail(long senderId,long receiverId,String title,String content,MailType type,Consumer<Mail> mailConsumer) {
		
	}
}
