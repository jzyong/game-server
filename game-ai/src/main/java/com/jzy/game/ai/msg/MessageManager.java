
package com.jzy.game.ai.msg;

/** 
 * 电报管理
 * The {@code MessageManager} is a singleton {@link MessageDispatcher} in charge of the creation, dispatch, and management of
 * telegrams.
 * 
 * @author davebaol */
public final class MessageManager extends MessageDispatcher {

	private static final MessageManager INSTANCE = new MessageManager();

	/** Don't let anyone else instantiate this class */
	private MessageManager () {
	}

	/** Returns the singleton instance of the message dispatcher. */
	public static MessageManager getInstance () {
		return INSTANCE;
	}

}
