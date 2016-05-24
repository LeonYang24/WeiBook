package com.leon.weibook.controller;

import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.util.LogUtils;
import com.leon.weibook.util.ThirdPartUserUtils;

import java.util.List;

/**
 * 对话助手类
 * Created by Leon on 2016/5/13 0013.
 */
public class ConversationHelper {

	/**
	 * 该函数判断对话是否有效
	 * @param conversation
	 * @return 有效则返回true，无效返回false
	 */
	public static boolean isValidConversation(AVIMConversation conversation) {
		if (null == conversation) {
			LogUtils.d("invalid reason : conversation is null");
			return false;
		}

		//getMembers() : 获取conversation当前的参与者
		if (null == conversation.getMembers() || 0 == conversation.getMembers().size()) {
			LogUtils.d("invalid reason : conversation members null or empty");
			return false;
		}

		//getAttribute : 获取当前聊天对话的属性
		Object type = conversation.getAttribute(ConversationType.TYPE_KEY);
		if (type == null) {
			LogUtils.d("invalid reason : type is null");
			return false;
		}

		int typeInt = (Integer) type;
		if (typeInt == ConversationType.Single.getValue()) {
			if (2 != conversation.getMembers().size() ||
				false == conversation.getMembers().contains(ChatManager.getInstance().getSelfId())) {
				LogUtils.d("invalid reason : oneToOne conversation not correct");
				return false;
			}
		} else if (typeInt == ConversationType.Group.getValue()) {

		} else {
			LogUtils.d("invalid reason : typeInt wrong");
			return false;
		}
		return true;
	}

	/**
	 * 判断对话类型
	 * @param conversation
	 * @return
	 */
	public static ConversationType typeOfConversation(AVIMConversation conversation) {
		if (isValidConversation(conversation)) {
			Object typeObject = conversation.getAttribute(ConversationType.TYPE_KEY);
			int typeInt = (Integer)typeObject;
			return ConversationType.fromInt(typeInt);
		} else {
			LogUtils.e("invalid conversation");
			// 因为 Group 不需要取 otherId，检查没那么严格，避免导致崩溃
			return ConversationType.Group;
		}
	}

	/**
	 * 获取单聊对话的另外一个人的 userId
	 * @param conversation
	 * @return 返回另一个人userId，若发生异常，则返回selfId
	 */
	public static String otherIdOfConversation(AVIMConversation conversation) {
		if (isValidConversation(conversation)) {
			if (typeOfConversation(conversation) == ConversationType.Single) {
				List<String> members = conversation.getMembers();
				if (2 == members.size()) {
					if (members.get(0).equals(ChatManager.getInstance().getSelfId())) {
						return members.get(1);
					} else {
						return members.get(0);
					}
				}
			}
		}
		// 尽管异常，返回可以使用的 userId
		return ChatManager.getInstance().getSelfId();
	}

	/**
	 * 获取对话名字
	 * @param conversation
	 * @return
	 */
	public static String nameOfConversation(AVIMConversation conversation) {
		if (isValidConversation(conversation)) {
			if (typeOfConversation(conversation) == ConversationType.Single) {
				String otherId = otherIdOfConversation(conversation);
				String userName = ThirdPartUserUtils.getInstance().getUserName(otherId);
				return (TextUtils.isEmpty(userName) ? "对话" : userName);
			} else {
				//getName : 获取conversation的名字
				return conversation.getName();
			}
		} else {
			return "";
		}
	}

	/**
	 * 获取对话标题
	 * @param conversation
	 * @return
	 */
	public static String titleOfConversation(AVIMConversation conversation) {
		if (isValidConversation(conversation)) {
			if (typeOfConversation(conversation) == ConversationType.Single) {
				return nameOfConversation(conversation);
			} else {
				List<String> members = conversation.getMembers();
				return nameOfConversation(conversation) + " (" + members.size() + ")";
			}
		} else {
			return "";
		}
	}

}
