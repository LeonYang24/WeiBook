package com.leon.weibook.util;

import android.graphics.Bitmap;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对话管理类
 * Created by Leon on 2016/5/22 0022.
 */
public class ConversationManager {

	private static ConversationManager conversationManager;

	public ConversationManager() { }

	public static synchronized ConversationManager getInstance() {
		if (null == conversationManager) {
			conversationManager = new ConversationManager();
		}
		return conversationManager;
	}

	/**
	 *
	 * @param callback
	 */
	public void findAndCacheRooms(final Room.MultiRoomsCallback callback) {
		final List<Room> rooms = ChatManager.getInstance().findRecentRooms();
		List<String> conversationIds = new ArrayList<>();
		for (Room room : rooms) {
			conversationIds.add(room.getConversationId());
		}

		if (conversationIds.size() > 0) {
			AVIMConversationCacheUtils.cacheConversations(conversationIds,
					new AVIMConversationCacheUtils.CacheConversationCallback() {
						@Override
						public void done(AVException e) {
							if (null != e) {
								callback.done(rooms, e);
							} else {
								callback.done(rooms, null);
							}
						}
			});
		} else {
			callback.done(rooms, null);
		}
	}

	public void updateName(final AVIMConversation conv, String newName, final AVIMConversationCallback callback) {
		conv.setName(newName);
		conv.updateInfoInBackground(new AVIMConversationCallback() {
			@Override
			public void done(AVIMException e) {
				if (e != null) {
					if (callback != null) {
						callback.done(e);
					}
				} else {
					if (callback != null) {
						callback.done(null);
					}
				}
			}
		});
	}

	public void findGroupConversationsIncludeMe(AVIMConversationQueryCallback callback) {
		AVIMConversationQuery conversationQuery = ChatManager.getInstance().getConversationQuery();
		if (null != conversationQuery) {
			conversationQuery.containsMembers(Arrays.asList(ChatManager.getInstance().getSelfId()));
			conversationQuery.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Group.getValue());
			conversationQuery.orderByDescending(Constants.UPDATED_AT);
			conversationQuery.limit(1000);
			conversationQuery.findInBackground(callback);
		} else if (null != callback) {
			callback.done(new ArrayList<AVIMConversation>(), null);
		}
	}

	public static Bitmap getConversationIcon(AVIMConversation conversation) {
		return ColoredBitmapProvider.getInstance().createColoredBitmapByHashString(conversation.getConversationId());
	}

}

