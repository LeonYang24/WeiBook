package com.leon.weibook.controller;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.model.Room;
import com.leon.weibook.util.LogUtils;
import com.leon.weibook.util.ThirdPartUserUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类来负责处理接收消息、聊天服务连接状态管理、查找对话、获取最近对话列表最后一条消息
 * Created by Leon on 2016/5/13 0013.
 */
public class ChatManager {

	private static ChatManager chatManager;

	private volatile AVIMClient imClient;
	private volatile String selfId;

	private RoomsTable roomsTable;

	private ChatManager() {}

	/**
	 * 单例模式
	 * @return 唯一的一个实例对象
	 */
	public static synchronized ChatManager getInstance() {
		if (chatManager == null) {
			chatManager = new ChatManager();
		}
		return chatManager;
	}

	/**
	 * 请在应用一启动(Application onCreate)的时候就调用，因为SDK一启动，就会去连接聊天服务器
	 * 这里包含了一些需要设置的 handler
	 * @param context
	 */
	public void init(Context context) {
		//注册处理特定消息（继承自AVIMMessage的任意类）AVIMTypedMessage类的指定handler
		AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new MessageHandler(context));

		// 与网络相关的 handler 设置AVIMClient的事件处理单元， 包括Client断开链接和重连成功事件
		AVIMClient.setClientEventHandler(LeanChatClientEventHandler.getInstance());

		// 和 Conversation 相关的事件的 handler
		AVIMMessageManager.setConversationEventHandler(ConversationEventHandler.getInstance());
	}

	/**
	 * 获取自身Id
	 * @return
	 */
	public String getSelfId() {
		return selfId;
	}

	public RoomsTable getRoomsTable() {
		return roomsTable;
	}

	/**
	 * 是否登录
	 * @return 已经登录则返回true，否则返回false
	 */
	public boolean isLogin() {
		return null != imClient;
	}

	/**
	 * 连接聊天服务器，用 userId 登录，在进入MainActivity 前调用
	 * @param callback AVException 常发生于网络错误、签名错误
	 */
	public void openClient(Context context, String userId, final AVIMClientCallback callback) {
		this.selfId = userId;
		roomsTable = RoomsTable.getInstanceByUserId(context, userId);

		Log.i("test", "userid = " + selfId);

		imClient = AVIMClient.getInstance(this.selfId);
		imClient.open(new AVIMClientCallback() {
			@Override
			public void done(AVIMClient avimClient, AVIMException e) {
				if (e != null) {
					Log.i("test", "e!=null");
					LeanChatClientEventHandler.getInstance().setConnectAndNotify(false);
				} else {
					Log.i("test", "e=null");
					LeanChatClientEventHandler.getInstance().setConnectAndNotify(true);
				}
				if (callback != null) {
					callback.done(avimClient, e);
				}
			}
		});
	}

	/**
	 * 用户注销的时候调用，close 之后消息不会推送过来，也不可以进行发消息等操作
	 *
	 * @param callback AVException 常见于网络错误
	 */
	public void closeWithCallback(final AVIMClientCallback callback) {
		imClient.close(new AVIMClientCallback() {

			@Override
			public void done(AVIMClient avimClient, AVIMException e) {
				if (e != null) {
					LogUtils.logException(e);
				}
				if (callback != null) {
					callback.done(avimClient, e);
				}
			}
		});
		imClient = null;
		selfId = null;
	}

	public void createGroupConversation(List<String> memberIds, AVIMConversationCreatedCallback callback) {
		Map<String, Object> attrs = new HashMap<>();
		attrs.put(ConversationType.TYPE_KEY, ConversationType.Group.getValue());
		attrs.put("name", getConversationName(memberIds));
		imClient.createConversation(memberIds, "", attrs, false, true, callback);
	}

	public void createSingleConversation(String memberId, AVIMConversationCreatedCallback callback) {
		Map<String, Object> attrs = new HashMap<>();
		attrs.put(ConversationType.TYPE_KEY, ConversationType.Single.getValue());
		imClient.createConversation(Arrays.asList(memberId), "", attrs, false, true, callback);
	}

	/**
	 * 获取 AVIMConversationQuery，用来查询对话
	 * @return
	 */
	public AVIMConversationQuery getConversationQuery() {
		return imClient.getQuery();
	}

	//ChatUser
	public List<Room> findRecentRooms() {
		return ChatManager.getInstance().getRoomsTable().selectRooms();
	}

	private String getConversationName(List<String> userIds) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < userIds.size(); i++) {
			String id = userIds.get(i);
			if (i != 0) {
				sb.append(",");
			}
			sb.append(ThirdPartUserUtils.getInstance().getUserName(id));
		}
		if (sb.length() > 50) {
			return sb.subSequence(0, 50).toString();
		} else {
			return sb.toString();
		}
	}

}
