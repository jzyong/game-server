
package com.jzy.game.ai.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.cache.MemoryPool;
import com.jzy.game.engine.util.TimeUtil;

/**
 * 电报分发管理器 <br>
 * A {@code MessageDispatcher} is in charge of the creation, dispatch, and
 * management of telegrams.
 *
 * @author davebaol
 * @fix JiangZhiYong
 */
public class MessageDispatcher implements Telegraph {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageDispatcher.class);

	private static final MemoryPool<Telegram> POOL = new MemoryPool<Telegram>(16);

	private PriorityQueue<Telegram> queue;

	private Map<Integer, List<Telegraph>> msgListeners; // 消息监听器

	private Map<Integer, List<TelegramProvider>> msgProviders;

	/** Creates a {@code MessageDispatcher} */
	public MessageDispatcher() {
		this.queue = new PriorityQueue<Telegram>();
		this.msgListeners = new HashMap<Integer, List<Telegraph>>();
		this.msgProviders = new HashMap<Integer, List<TelegramProvider>>();
	}

	/**
	 * 注册监听器 <br>
	 * Registers a listener for the specified message code. Messages without an
	 * explicit receiver are broadcasted to all its registered listeners.
	 *
	 * @param listener
	 *            the listener to add
	 * @param msg
	 *            the message code
	 */
	public void addListener(Telegraph listener, int msg) {
		List<Telegraph> listeners = msgListeners.get(msg);
		if (listeners == null) {
			listeners = new ArrayList<Telegraph>(16);
			msgListeners.put(msg, listeners);
		}
		listeners.add(listener);

		// Dispatch messages from registered providers
		List<TelegramProvider> providers = msgProviders.get(msg);
		if (providers != null) {
			for (int i = 0, n = providers.size(); i < n; i++) {
				TelegramProvider provider = providers.get(i);
				Object info = provider.provideMessageInfo(msg, listener);
				if (info != null) {
					Telegraph sender = Telegraph.class.isInstance(provider) ? (Telegraph) provider : null;
					dispatchMessage(0, sender, listener, msg, info, false);
				}
			}
		}
	}

	/**
	 * Registers a listener for a selection of message types. Messages without an
	 * explicit receiver are broadcasted to all its registered listeners.
	 *
	 * @param listener
	 *            the listener to add
	 * @param msgs
	 *            the message codes
	 */
	public void addListeners(Telegraph listener, int... msgs) {
		for (int msg : msgs)
			addListener(listener, msg);
	}

	/**
	 * Registers a provider for the specified message code.
	 *
	 * @param msg
	 *            the message code
	 * @param provider
	 *            the provider to add
	 */
	public void addProvider(TelegramProvider provider, int msg) {
		List<TelegramProvider> providers = msgProviders.get(msg);
		if (providers == null) {
			// Associate an empty unordered array with the message code
			providers = new ArrayList<TelegramProvider>(16);
			msgProviders.put(msg, providers);
		}
		providers.add(provider);
	}

	/**
	 * Registers a provider for a selection of message types.
	 *
	 * @param provider
	 *            the provider to add
	 * @param msgs
	 *            the message codes
	 */
	public void addProviders(TelegramProvider provider, int... msgs) {
		for (int msg : msgs)
			addProvider(provider, msg);
	}

	/**
	 * Unregister the specified listener for the specified message code.
	 *
	 * @param listener
	 *            the listener to remove
	 * @param msg
	 *            the message code
	 */
	public void removeListener(Telegraph listener, int msg) {
		List<Telegraph> listeners = msgListeners.get(msg);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * Unregister the specified listener for the selection of message codes.
	 *
	 * @param listener
	 *            the listener to remove
	 * @param msgs
	 *            the message codes
	 */
	public void removeListener(Telegraph listener, int... msgs) {
		for (int msg : msgs)
			removeListener(listener, msg);
	}

	/**
	 * Unregisters all the listeners for the specified message code.
	 *
	 * @param msg
	 *            the message code
	 */
	public void clearListeners(int msg) {
		msgListeners.remove(msg);
	}

	/**
	 * Unregisters all the listeners for the given message codes.
	 *
	 * @param msgs
	 *            the message codes
	 */
	public void clearListeners(int... msgs) {
		for (int msg : msgs)
			clearListeners(msg);
	}

	/** Removes all the registered listeners for all the message codes. */
	public void clearListeners() {
		msgListeners.clear();
	}

	/**
	 * Unregisters all the providers for the specified message code.
	 *
	 * @param msg
	 *            the message code
	 */
	public void clearProviders(int msg) {
		msgProviders.remove(msg);
	}

	/**
	 * Unregisters all the providers for the given message codes.
	 *
	 * @param msgs
	 *            the message codes
	 */
	public void clearProviders(int... msgs) {
		for (int msg : msgs)
			clearProviders(msg);
	}

	/** Removes all the registered providers for all the message codes. */
	public void clearProviders() {
		msgProviders.clear();
	}

	/**
	 * Removes all the telegrams from the queue and releases them to the internal
	 * pool.
	 */
	public void clearQueue() {
		int size = queue.size();
		for (int i = 0; i < size; i++) {
			POOL.put(queue.remove());
		}
		queue.clear();
	}

	/**
	 * Removes all the telegrams from the queue and the registered listeners for all
	 * the messages.
	 */
	public void clear() {
		clearQueue();
		clearListeners();
		clearProviders();
	}

	/**
	 * Sends an immediate message to all registered listeners, with no extra info.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
	 *
	 * @param msg
	 *            the message code
	 */
	public void dispatchMessage(int msg) {
		dispatchMessage(0, null, null, msg, null, false);
	}

	/**
	 * Sends an immediate message to all registered listeners, with no extra info.
	 * <p>
	 * This is a shortcut method for
	 * {{@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param sender
	 *            the sender of the telegram
	 * @param msg
	 *            the message code
	 */
	public void dispatchMessage(Telegraph sender, int msg) {
		dispatchMessage(0, sender, null, msg, null, false);
	}

	/**
	 * Sends an immediate message to all registered listeners, with no extra info.
	 * <p>
	 * This is a shortcut method for
	 *{@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param sender
	 *            the sender of the telegram
	 * @param msg
	 *            the message code
	 * @param needsReturnReceipt
	 *            whether the return receipt is needed or not
	 * @throws IllegalArgumentException
	 *             if the sender is {@code null} and the return receipt is needed
	 */
	public void dispatchMessage(Telegraph sender, int msg, boolean needsReturnReceipt) {
		dispatchMessage(0, sender, null, msg, null, needsReturnReceipt);
	}

	/**
	 * Sends an immediate message to all registered listeners, with extra info.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 */
	public void dispatchMessage(int msg, Object extraInfo) {
		dispatchMessage(0, null, null, msg, extraInfo, false);
	}

	/**
	 * Sends an immediate message to all registered listeners, with extra info.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param sender
	 *            the sender of the telegram
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 */
	public void dispatchMessage(Telegraph sender, int msg, Object extraInfo) {
		dispatchMessage(0, sender, null, msg, extraInfo, false);
	}

	/**
	 * Sends an immediate message to all registered listeners, with extra info.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param sender
	 *            the sender of the telegram
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 * @param needsReturnReceipt
	 *            whether the return receipt is needed or not
	 * @throws IllegalArgumentException
	 *             if the sender is {@code null} and the return receipt is needed
	 */
	public void dispatchMessage(Telegraph sender, int msg, Object extraInfo, boolean needsReturnReceipt) {
		dispatchMessage(0, sender, null, msg, extraInfo, needsReturnReceipt);
	}

	/**
	 * Sends an immediate message to the specified receiver with no extra info. The
	 * receiver doesn't need to be a register listener for the specified message
	 * code.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param sender
	 *            the sender of the telegram
	 * @param receiver
	 *            the receiver of the telegram; if it's {@code null} the telegram is
	 *            broadcasted to all the receivers registered for the specified
	 *            message code
	 * @param msg
	 *            the message code
	 */
	public void dispatchMessage(Telegraph sender, Telegraph receiver, int msg) {
		dispatchMessage(0, sender, receiver, msg, null, false);
	}

	/**
	 * Sends an immediate message to the specified receiver with no extra info. The
	 * receiver doesn't need to be a register listener for the specified message
	 * code.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param sender
	 *            the sender of the telegram
	 * @param receiver
	 *            the receiver of the telegram; if it's {@code null} the telegram is
	 *            broadcasted to all the receivers registered for the specified
	 *            message code
	 * @param msg
	 *            the message code
	 * @param needsReturnReceipt
	 *            whether the return receipt is needed or not
	 * @throws IllegalArgumentException
	 *             if the sender is {@code null} and the return receipt is needed
	 */
	public void dispatchMessage(Telegraph sender, Telegraph receiver, int msg, boolean needsReturnReceipt) {
		dispatchMessage(0, sender, receiver, msg, null, needsReturnReceipt);
	}

	/**
	 * Sends an immediate message to the specified receiver with extra info. The
	 * receiver doesn't need to be a register listener for the specified message
	 * code.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param sender
	 *            the sender of the telegram
	 * @param receiver
	 *            the receiver of the telegram; if it's {@code null} the telegram is
	 *            broadcasted to all the receivers registered for the specified
	 *            message code
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 */
	public void dispatchMessage(Telegraph sender, Telegraph receiver, int msg, Object extraInfo) {
		dispatchMessage(0, sender, receiver, msg, extraInfo, false);
	}

	/**
	 * Sends an immediate message to the specified receiver with extra info. The
	 * receiver doesn't need to be a register listener for the specified message
	 * code.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param sender
	 *            the sender of the telegram
	 * @param receiver
	 *            the receiver of the telegram; if it's {@code null} the telegram is
	 *            broadcasted to all the receivers registered for the specified
	 *            message code
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 * @param needsReturnReceipt
	 *            whether the return receipt is needed or not
	 * @throws IllegalArgumentException
	 *             if the sender is {@code null} and the return receipt is needed
	 */
	public void dispatchMessage(Telegraph sender, Telegraph receiver, int msg, Object extraInfo,
			boolean needsReturnReceipt) {
		dispatchMessage(0, sender, receiver, msg, extraInfo, needsReturnReceipt);
	}

	/**
	 * Sends a message to all registered listeners, with the specified delay but no
	 * extra info.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param msg
	 *            the message code
	 */
	public void dispatchMessage(int delay, int msg) {
		dispatchMessage(delay, null, null, msg, null, false);
	}

	/**
	 * Sends a message to all registered listeners, with the specified delay but no
	 * extra info.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param sender
	 *            the sender of the telegram
	 * @param msg
	 *            the message code
	 */
	public void dispatchMessage(int delay, Telegraph sender, int msg) {
		dispatchMessage(delay, sender, null, msg, null, false);
	}

	/**
	 * Sends a message to all registered listeners, with the specified delay but no
	 * extra info.
	 * <p>
	 * This is a shortcut method for
	 *{@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param sender
	 *            the sender of the telegram
	 * @param msg
	 *            the message code
	 * @param needsReturnReceipt
	 *            whether the return receipt is needed or not
	 * @throws IllegalArgumentException
	 *             if the sender is {@code null} and the return receipt is needed
	 */
	public void dispatchMessage(int delay, Telegraph sender, int msg, boolean needsReturnReceipt) {
		dispatchMessage(delay, sender, null, msg, null, needsReturnReceipt);
	}

	/**
	 * Sends a message to all registered listeners, with the specified delay and
	 * extra info.
	 * <p>
	 * This is a shortcut method for
	 *{@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 */
	public void dispatchMessage(int delay, int msg, Object extraInfo) {
		dispatchMessage(delay, null, null, msg, extraInfo, false);
	}

	/**
	 * Sends a message to all registered listeners, with the specified delay and
	 * extra info.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param sender
	 *            the sender of the telegram
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 */
	public void dispatchMessage(int delay, Telegraph sender, int msg, Object extraInfo) {
		dispatchMessage(delay, sender, null, msg, extraInfo, false);
	}

	/**
	 * Sends a message to all registered listeners, with the specified delay and
	 * extra info.
	 * <p>
	 * This is a shortcut method for
	 *{@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param sender
	 *            the sender of the telegram
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 * @param needsReturnReceipt
	 *            whether the return receipt is needed or not
	 * @throws IllegalArgumentException
	 *             if the sender is {@code null} and the return receipt is needed
	 */
	public void dispatchMessage(int delay, Telegraph sender, int msg, Object extraInfo, boolean needsReturnReceipt) {
		dispatchMessage(delay, sender, null, msg, extraInfo, needsReturnReceipt);
	}

	/**
	 * Sends a message to the specified receiver, with the specified delay but no
	 * extra info. The receiver doesn't need to be a register listener for the
	 * specified message code.
	 * <p>
	 * This is a shortcut method for
	 *{@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param sender
	 *            the sender of the telegram
	 * @param receiver
	 *            the receiver of the telegram; if it's {@code null} the telegram is
	 *            broadcasted to all the receivers registered for the specified
	 *            message code
	 * @param msg
	 *            the message code
	 */
	public void dispatchMessage(int delay, Telegraph sender, Telegraph receiver, int msg) {
		dispatchMessage(delay, sender, receiver, msg, null, false);
	}

	/**
	 * Sends a message to the specified receiver, with the specified delay but no
	 * extra info. The receiver doesn't need to be a register listener for the
	 * specified message code.
	 * <p>
	 * This is a shortcut method for
	 *{@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param sender
	 *            the sender of the telegram
	 * @param receiver
	 *            the receiver of the telegram; if it's {@code null} the telegram is
	 *            broadcasted to all the receivers registered for the specified
	 *            message code
	 * @param msg
	 *            the message code
	 * @param needsReturnReceipt
	 *            whether the return receipt is needed or not
	 * @throws IllegalArgumentException
	 *             if the sender is {@code null} and the return receipt is needed
	 */
	public void dispatchMessage(int delay, Telegraph sender, Telegraph receiver, int msg, boolean needsReturnReceipt) {
		dispatchMessage(delay, sender, receiver, msg, null, needsReturnReceipt);
	}

	/**
	 * Sends a message to the specified receiver, with the specified delay but no
	 * extra info. The receiver doesn't need to be a register listener for the
	 * specified message code.
	 * <p>
	 * This is a shortcut method for
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     *
	 * @param delay
	 *            the delay in seconds
	 * @param sender
	 *            the sender of the telegram
	 * @param receiver
	 *            the receiver of the telegram; if it's {@code null} the telegram is
	 *            broadcasted to all the receivers registered for the specified
	 *            message code
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 */
	public void dispatchMessage(int delay, Telegraph sender, Telegraph receiver, int msg, Object extraInfo) {
		dispatchMessage(delay, sender, receiver, msg, extraInfo, false);
	}

	/**
	 * 分发消息 <br>
	 * Given a message, a receiver, a sender and any time delay, this method routes
	 * the message to the correct agents (if no delay) or stores in the message
	 * queue to be dispatched at the correct time.
	 *
	 * @param delay
	 *            the delay in seconds
	 * @param sender
	 *            the sender of the telegram
	 * @param receiver
	 *            the receiver of the telegram; if it's {@code null} the telegram is
	 *            broadcasted to all the receivers registered for the specified
	 *            message code
	 * @param msg
	 *            the message code
	 * @param extraInfo
	 *            an optional object
	 * @param needsReturnReceipt
	 *            whether the return receipt is needed or not
	 * @throws IllegalArgumentException
	 *             if the sender is {@code null} and the return receipt is needed
	 */
	public void dispatchMessage(int delay, Telegraph sender, Telegraph receiver, int msg, Object extraInfo,
			boolean needsReturnReceipt) {
		if (sender == null && needsReturnReceipt)
			throw new IllegalArgumentException("Sender cannot be null when a return receipt is needed");

		// Get a telegram from the pool
		Telegram telegram = POOL.get(Telegram.class);
		telegram.sender = sender;
		telegram.receiver = receiver;
		telegram.message = msg;
		telegram.extraInfo = extraInfo;
		telegram.returnReceiptStatus = needsReturnReceipt ? Telegram.RETURN_RECEIPT_NEEDED
				: Telegram.RETURN_RECEIPT_UNNEEDED;

		// If there is no delay, route telegram immediately
		if (delay <= 0) {

			// // TODO: should we set the timestamp here?
			// // telegram.setTimestamp(GdxAI.getTimepiece().getTime());
			//
			// if (debugEnabled) {
			// float currentTime = GdxAI.getTimepiece().getTime();
			// GdxAI.getLogger().info(LOG_TAG, "Instant telegram dispatched at time: " +
			// currentTime + " by " + sender
			// + " for " + receiver + ". Message code is " + msg);
			// }

			// Send the telegram to the recipient
			discharge(telegram);
		} else {

			// Set the timestamp for the delayed telegram
			telegram.setTimestamp(TimeUtil.currentTimeMillis() + delay);

			// Put the telegram in the queue
			boolean added = queue.add(telegram);

			// Return it to the pool if has been rejected
			if (!added) {
				POOL.put(telegram);
				LOGGER.debug("Delayed telegram from " + sender + " for " + receiver
						+ " rejected by the queue. Message code is " + msg);
			}
		}
	}

	/**
	 * 定时检测，执行延迟任务 <br>
	 * Dispatches any delayed telegrams with a timestamp that has expired.
	 * Dispatched telegrams are removed from the queue.
	 * <p>
	 * This method must be called regularly from inside the main game loop to
	 * facilitate the correct and timely dispatch of any delayed messages. Notice
	 * that the message dispatcher internally calls {@link Timepiece#getTime()
	 * GdxAI.getTimepiece().getTime()} to get the current AI time and properly
	 * dispatch delayed messages. This means that
	 * <ul>
	 * <li>if you forget to {@link Timepiece#update(float) update the timepiece} the
	 * delayed messages won't be dispatched.</li>
	 * <li>ideally the timepiece should be updated before the message
	 * dispatcher.</li>
	 * </ul>
	 */
	public void update() {
		long currentTime = TimeUtil.currentTimeMillis();

		// Peek at the queue to see if any telegrams need dispatching.
		// Remove all telegrams from the front of the queue that have gone
		// past their time stamp.
		Telegram telegram;
		while ((telegram = queue.peek()) != null) {

			// Exit loop if the telegram is in the future
			if (telegram.getTimestamp() > currentTime)
				break;

			// if (debugEnabled) {
			// GdxAI.getLogger().info(LOG_TAG, "Queued telegram ready for dispatch: Sent to
			// " + telegram.receiver
			// + ". Message code is " + telegram.message);
			// }

			// Send the telegram to the recipient
			discharge(telegram);

			// Remove it from the queue
			queue.poll();
		}

	}

	/**
	 * Scans the queue and passes pending messages to the given callback in any
	 * particular order.
	 * <p>
	 * Typically this method is used to save (serialize) pending messages and
	 * restore (deserialize and schedule) them back on game loading.
	 *
	 * @param callback
	 *            The callback used to report pending messages individually.
	 **/
	public void scanQueue(PendingMessageCallback callback) {
		float currentTime = TimeUtil.currentTimeMillis();

		Iterator<Telegram> iterator = queue.iterator();
		while (iterator.hasNext()) {
			Telegram telegram = iterator.next();
			callback.report(telegram.getTimestamp() - currentTime, telegram.sender, telegram.receiver, telegram.message,
					telegram.extraInfo, telegram.returnReceiptStatus);
		}
//		int queueSize = queue.size();
//		for (int i = 0; i < queueSize; i++) {
//			Telegram telegram = queue.get(i);
//			callback.report(telegram.getTimestamp() - currentTime, telegram.sender, telegram.receiver, telegram.message,
//					telegram.extraInfo, telegram.returnReceiptStatus);
//		}
	}

	/**
	 * 执行电报<br>
	 * This method is used by
	 * {@link #dispatchMessage(int, Telegraph, int, Object, boolean)}
     * dispatchMessage} for immediate telegrams and {@link #update()}
	 * for delayed telegrams. It first calls the message handling method of the
	 * receiving agents with the specified telegram then returns the telegram to the
	 * pool.
	 *
	 * @param telegram
	 *            the telegram to discharge
	 */
	private void discharge(Telegram telegram) {
		if (telegram.receiver != null) {
			// Dispatch the telegram to the receiver specified by the telegram itself
			if (!telegram.receiver.handleMessage(telegram)) {
				// Telegram could not be handled
				LOGGER.debug("消息{}未处理", telegram.message);
			}
		} else {
			// Dispatch the telegram to all the registered receivers
			int handledCount = 0;
			List<Telegraph> listeners = msgListeners.get(telegram.message);
			if (listeners != null) {
				for (int i = 0; i < listeners.size(); i++) {
					if (listeners.get(i).handleMessage(telegram)) {
						handledCount++;
					}
				}
			}
			// Telegram could not be handled
			if (handledCount == 0) {
				LOGGER.debug("消息{}未处理", telegram.message);
			}
		}

		if (telegram.returnReceiptStatus == Telegram.RETURN_RECEIPT_NEEDED) {
			// Use this telegram to send the return receipt
			telegram.receiver = telegram.sender;
			telegram.sender = this;
			telegram.returnReceiptStatus = Telegram.RETURN_RECEIPT_SENT;
			discharge(telegram);
		} else {
			// Release the telegram to the pool
			POOL.put(telegram);
		}
	}

	/**
	 * Handles the telegram just received. This method always returns {@code false}
	 * since usually the message dispatcher never receives telegrams. Actually, the
	 * message dispatcher implements {@link Telegraph} just because it can send
	 * return receipts.
	 *
	 * @param msg
	 *            The telegram
	 * @return always {@code false}.
	 */
	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	/**
	 * 回调接口 <br>
	 * A {@code PendingMessageCallback} is used by the
	 * {@link MessageDispatcher#scanQueue(PendingMessageCallback) scanQueue} method
	 * of the {@link MessageDispatcher} to report its pending messages individually.
	 *
	 * @author davebaol
	 */
	public interface PendingMessageCallback {

		/**
		 * Reports a pending message.
		 *
		 * @param delay
		 *            The remaining delay in seconds
		 * @param sender
		 *            The message sender
		 * @param receiver
		 *            The message receiver
		 * @param message
		 *            The message code
		 * @param extraInfo
		 *            Any additional information that may accompany the message
		 * @param returnReceiptStatus
		 *            The return receipt status of the message
		 */
		public void report(float delay, Telegraph sender, Telegraph receiver, int message, Object extraInfo,
				int returnReceiptStatus);
	}

}
