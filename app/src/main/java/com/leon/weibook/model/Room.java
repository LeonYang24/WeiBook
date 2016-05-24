package com.leon.weibook.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.leon.weibook.util.AVIMConversationCacheUtils;

import java.util.List;

/**
 * 一个room代表一个对话
 * Created by Leon on 2016/5/14 0014.
 */
public class Room {

	private AVIMMessage lastMessage;
	private String conversationId;
	private int unreadCount;

	public AVIMMessage getLastMessage () {
		return lastMessage;
	}

	/**
	 * 获取最后一次更改时间
	 * @return
	 */
	public long getLastModifyTime() {
		if (null != lastMessage) {
			return lastMessage.getTimestamp();//获取消息发送的时间
		}
		AVIMConversation conversation = getConversation();
		if (null != conversation && null != conversation.getUpdatedAt()) {
			return conversation.getUpdatedAt().getTime();//获取Conversation的更新时间
		}

		return 0;
	}

	/**
	 * 通过conversationId获取对话
	 * @return
	 */
	public AVIMConversation getConversation() {
		return AVIMConversationCacheUtils.getCacheConversation(getConversationId());
	}

	/**
	 * 获取conversationId
	 * @return
	 */
	public String getConversationId() {
		return conversationId;
	}

	/**
	 * 设置最后一条信息
	 * @param lastMessage
	 */
	public void setLastMessage(AVIMMessage lastMessage) {
		this.lastMessage = lastMessage;
	}

	/**
	 * 设置conversationId
	 * @param conversationId
	 */
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	/**
	 * 获取未读信息数量
	 * @return
	 */
	public int getUnreadCount() {
		return unreadCount;
	}

	/**
	 * 设置未读信息数量
	 * @param unreadCount
	 */
	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}

	public static abstract class MultiRoomsCallback {
		public abstract void done(List<Room> roomList, AVException exception);
	}

}
