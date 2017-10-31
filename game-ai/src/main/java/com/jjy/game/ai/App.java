package com.jjy.game.ai;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {

		MessageManager.getInstance().addListener(new Telegraph() {

			@Override
			public boolean handleMessage(Telegram msg) {
				System.out.println("Hello World!");
				return false;
			}
		}, 1);
		MessageManager.getInstance().dispatchMessage(1);
		MessageManager.getInstance().update();
	}
}
