package com.jzy.game.hall.manager;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.hall.HallChatMessage.MailInfo;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.hall.script.IMailScript;
import com.jzy.game.model.mongo.hall.dao.MailDao;
import com.jzy.game.model.struct.Mail;
import com.jzy.game.model.struct.Mail.MailType;

/**
 * 邮件
 * <p>
 * 个人邮件单独存储，系统通用邮件只存一封,直接操作mongodb，不缓存
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月21日 下午3:25:17
 */
public class MailManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailManager.class);
	private static volatile MailManager mailManager;

	private MailManager() {

	}

	public static MailManager getInstance() {
		if (mailManager == null) {
			synchronized (MailManager.class) {
				if (mailManager == null) {
					mailManager = new MailManager();
				}
			}
		}
		return mailManager;
	}

	public Mail getMail(long mailId) {
		return MailDao.getMail(mailId);
	}

	/**
	 * 发送邮件
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月21日 下午4:26:31
	 * @param title
	 * @param content
	 * @param type
	 * @param mailConsumer
	 */
	public void sendMail(long senderId,long receiverId,String title, String content, MailType type, Consumer<Mail> mailConsumer) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IMailScript.class,
				script -> script.sendMail(senderId,receiverId,title, content, type, mailConsumer));
	}

	/**
	 * 构建邮箱信息
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月21日 下午5:45:09
	 * @param mail
	 * @return
	 */
	public MailInfo buildMailInfo(Mail mail) {
		MailInfo.Builder builder = MailInfo.newBuilder();
		builder.setContent(mail.getContent());
		builder.setTitle(mail.getTitle());
		builder.setCreateTime(mail.getCreateTime().getTime());
		builder.setId(mail.getId());
		builder.setSenderId(mail.getSenderId());
		builder.setState(mail.getState());
		return builder.build();
	}
}
