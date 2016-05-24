package com.leon.weibook.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by Leon on 2016/5/17 0017.
 */
@AVClassName("AddRequest")
public class AddRequest extends AVObject {

	public static final int STATUS_WAIT = 0;
	public static final int STATUS_DONE = 1;

	public static final String FROM_USER = "fromUser";
	public static final String TO_USER = "toUser";
	public static final String STATUS = "status";

	/* 标记接收方是否已读该消息 */
	public static final String IS_READ = "isRead";

	/**
	 * 子类化必须有一个 public 的默认（参数个数为 0）的构造函数
	 */
	public AddRequest() {}

	public LeanChatUser getFromUser() {
		return getAVUser(FROM_USER, LeanChatUser.class);
	}

	public void setFromUser(LeanChatUser fromUser) {
		put(FROM_USER, fromUser);
	}

	public LeanChatUser getToUser() {
		return getAVUser(TO_USER, LeanChatUser.class);
	}

	public void setToUser(LeanChatUser toUser) {
		put(TO_USER, toUser);
	}

	public int getStatus() {
		return getInt(STATUS);
	}

	public void setStatus(int status) {
		put(STATUS, status);
	}

	public boolean isRead() {
		return getBoolean(IS_READ);
	}

	public void setIsRead(boolean isRead) {
		put(IS_READ, isRead);
	}

}
