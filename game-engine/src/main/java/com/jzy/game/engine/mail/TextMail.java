package com.jzy.game.engine.mail;

/**
 * 文本邮件
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月22日 下午5:14:12
 */
public class TextMail implements Runnable{
	
	/**标题*/
	private String title;
	/**内容*/
	private String content;
	
	

	public TextMail(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
