package com.leon.weibook.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leon.weibook.controller.ChatManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存对话工具类
 * 可以缓存会话，查找缓存会话
 * Created by Leon on 2016/5/14 0014.
 */
public class AVIMConversationCacheUtils {

	private static Map<String, AVIMConversation> conversationMap;

	static {
		conversationMap = new HashMap<String, AVIMConversation>();
	}

	/**
	 * 获取缓存会话
	 * @param conversationId
	 * @return
	 */
	public static AVIMConversation getCacheConversation(String conversationId) {
		return conversationMap.get(conversationId);
	}

	/**
	 * 缓存会话
	 * @param ids conversation的id
	 * @param callback 缓存成功后的回调
	 */
	public static void cacheConversations(List<String> ids,
										  final CacheConversationCallback callback) {
		//这里是将传入的参数的值在已缓存的map中查找，找不到的就加入新的uncacheIds这个List（未缓存）
		List<String> uncachedIds = new ArrayList<String>();
		for (String id : ids) {
			if (!conversationMap.containsKey(id)) {
				uncachedIds.add(id);
			}
		}

		if (uncachedIds.isEmpty()) {
			if (null != callback) {
				callback.done(null);
				return;
			}
		}

		//查找会话，找到就存入map中
		findConversationsByConversationIds(uncachedIds, new AVIMConversationQueryCallback() {
			@Override
			public void done(List<AVIMConversation> list, AVIMException e) {
				if (null == e) {
					for (AVIMConversation conversation : list) {
						conversationMap.put(conversation.getConversationId(), conversation);
					}
				}
				callback.done(e);
			}
		});
	}

	/**
	 * 通过提供会话Ids来查找会话
	 * @param ids
	 * @param callback
	 */
	public static void findConversationsByConversationIds(List<String> ids,
														  AVIMConversationQueryCallback callback) {
		//AVIMConversationQuery : 用于查询聊天室对象
		AVIMConversationQuery conversationQuery = ChatManager.getInstance().getConversationQuery();
		if (ids.size() > 0 && null != conversationQuery) {
			//增加查询条件，当conversation的属性中对应的字段对应的值包含在指定值中时即可返回
			conversationQuery.whereContainsIn(Constants.OBJECT_ID, ids);
			conversationQuery.setLimit(1000);//设置返回集合的大小上限
			conversationQuery.findInBackground(callback);
		} else if (null != callback) {
			callback.done(new ArrayList<AVIMConversation>(), null);
		}
	}


	public static abstract class CacheConversationCallback {
		public abstract void done(AVException e);
	}

}
