
package com.jzy.game.ai.msg;

/**处理电报接口
 * Any object implementing the {@code Telegraph} interface can act as the sender
 * or the receiver of a {@link Telegram}.
 * 
 * @author davebaol
 */
public interface Telegraph {

	/**
	 * Handles the telegram just received.
	 * 
	 * @param msg
	 *            The telegram
	 * @return {@code true} if the telegram has been successfully handled;
	 *         {@code false} otherwise.
	 */
	public boolean handleMessage(Telegram msg);

}
