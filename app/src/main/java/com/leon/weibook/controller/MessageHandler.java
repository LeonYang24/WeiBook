package com.leon.weibook.controller;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leon.weibook.R;
import com.leon.weibook.event.ImTypeMessageEvent;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.LogUtils;
import com.leon.weibook.util.NotificationUtils;
import com.leon.weibook.util.ThirdPartUserUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * AVIMTypedMessage 的 handler，socket 过来的 AVIMTypedMessage 都会通过此 handler 与应用交互
 *  需要应用主动调用 AVIMMessageManager.registerMessageHandler 来注册(在ChatManager init()中调用)
 *  当然，自定义的消息也可以通过这种方式来处理
 * Created by Leon on 2016/5/13.
 */
public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

	private Context context;

	public MessageHandler(Context context) {
		this.context = context.getApplicationContext();
	}

	/**
	 * 重载此方法来处理接收消息
	 * @param message
	 * @param conversation
	 * @param client
	 */
	@Override
	public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
		if (null == message || null == message.getMessageId()) {
			LogUtils.d("may be SDK bug, message or message id is null");
			return;
		}

		if (!ConversationHelper.isValidConversation(conversation)) {
			LogUtils.d("receive msg from invalid conversation");
		}

		if (null == ChatManager.getInstance().getSelfId()) {
			LogUtils.d("selfId is null, please call setupManagerWithUserId ");
			client.close(null);
		} else {
			ChatManager.getInstance().getRoomsTable().insertRoom(message.getConversationId());
			if (!message.getFrom().equals(client.getClientId())) {
				if (NotificationUtils.isShowNotification(conversation.getConversationId())) {
					sendNotification(message, conversation);
				}
				ChatManager.getInstance().getRoomsTable().increaseUnreadCount(message.getConversationId());
				sendEvent(message, conversation);
			}
		}

	}

	@Override
	public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
		super.onMessageReceipt(message, conversation, client);
	}

	/**
	 * 因为没有 db，所以暂时先把消息广播出去，由接收方自己处理
	 * 稍后应该加入 db
	 * @param message
	 * @param conversation
	 */
	private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
		ImTypeMessageEvent event = new ImTypeMessageEvent();
		event.message = message;
		event.conversation = conversation;
		EventBus.getDefault().post(event);
	}

	private void sendNotification(AVIMTypedMessage message, AVIMConversation conversation) {
		if (null != conversation && null != message) {
			String notificationContent = message instanceof AVIMTextMessage ?
					((AVIMTextMessage) message).getText() : context.getString(R.string.unspport_message_type);

			String userName = ThirdPartUserUtils.getInstance().getUserName(message.getFrom());
			String title = (TextUtils.isEmpty(userName) ? "" : userName);

			Intent intent = new Intent();
			intent.setAction("com.avoscloud.chat.intent.client_notification");
			intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
			intent.putExtra(Constants.MEMBER_ID, message.getFrom());
			if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
				intent.putExtra(Constants.NOTOFICATION_TAG, Constants.NOTIFICATION_SINGLE_CHAT);
			} else {
				intent.putExtra(Constants.NOTOFICATION_TAG, Constants.NOTIFICATION_SINGLE_CHAT);
			}
			NotificationUtils.showNotification(context, title, notificationContent, null, intent);
		}
	}

}
