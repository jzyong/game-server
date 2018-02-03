
package com.jzy.game.ai.msg;

/**提供額外信息接口 
 * Telegram providers respond to {@link MessageDispatcher#addListener} by providing optional {@link Telegram#extraInfo} to be sent
 * in a Telegram of a given type to the newly registered {@link Telegraph}.
 * @author avianey */
public interface TelegramProvider {
	/** Provides {@link Telegram#extraInfo} to dispatch immediately when a {@link Telegraph} is registered for the given message
	 * type.
	 * @param msg the message type to provide
	 * @param receiver the newly registered Telegraph. Providers can provide different info depending on the targeted Telegraph.
	 * @return extra info to dispatch in a Telegram or null if nothing to dispatch
	 * @see MessageDispatcher#addListener(Telegraph, int)
	 * @see MessageDispatcher#addListeners(Telegraph, int...) */
	Object provideMessageInfo (int msg, Telegraph receiver);
}
