package com.leon.weibook.controller;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.leon.weibook.event.ConversationChangeEvent;
import com.leon.weibook.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 和Conversation相关的事件的handler
 * 已在ChatManager init（）中AVIMMessageManager.setConversationEventHandler调用
 * Created by Leon on 2016/5/14 0014.
 */
public class ConversationEventHandler extends AVIMConversationEventHandler {

	private static ConversationEventHandler eventHandler;

	private ConversationEventHandler() {};

	public static synchronized ConversationEventHandler getInstance() {
		if (null == eventHandler) {
			eventHandler = new ConversationEventHandler();
		}
		return eventHandler;
	}

	/**
	 * 用于刷新对话相关变化
	 * @param conversation
	 */
	private void refreshCacheAndNotify(AVIMConversation conversation) {
		ConversationChangeEvent conversationChangeEvent = new ConversationChangeEvent(conversation);
		EventBus.getDefault().post(conversationChangeEvent);
	}

	/**
	 * 实现本方法来处理当前用户被邀请到某个聊天对话事件
	 * @param avimClient
	 * @param avimConversation
	 * @param s
	 */
	@Override
	public void onInvited(AVIMClient avimClient, AVIMConversation avimConversation, String s) {
		LogUtils.i("onInvited");
		refreshCacheAndNotify(avimConversation);
	}

	/**
	 *实现本方法以处理聊天对话中的参与者加入事件
	 * @param avimClient
	 * @param avimConversation
	 * @param list
	 * @param s
	 */
	@Override
	public void onMemberJoined(AVIMClient avimClient, AVIMConversation avimConversation, List<String> list, String s) {
		LogUtils.i("onMemberJoined");
		refreshCacheAndNotify(avimConversation);
	}

	/**
	 *实现本方法以处理聊天对话中的参与者离开事件
	 * @param avimClient
	 * @param avimConversation
	 * @param list
	 * @param s
	 */
	@Override
	public void onMemberLeft(AVIMClient avimClient, AVIMConversation avimConversation, List<String> list, String s) {
		LogUtils.i("onMemberLeft");
		refreshCacheAndNotify(avimConversation);
	}

	/**
	 * 实现本方法来处理当前用户被踢出某个聊天对话事件
	 * @param avimClient
	 * @param avimConversation
	 * @param s
	 */
	@Override
	public void onKicked(AVIMClient avimClient, AVIMConversation avimConversation, String s) {
		LogUtils.i("onKicked");
		refreshCacheAndNotify(avimConversation);
	}
}
