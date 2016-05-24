package com.leon.weibook.event;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * Created by Leon on 2016/5/14 0014.
 */
public class ConversationChangeEvent {

	private AVIMConversation conv;

	public ConversationChangeEvent(AVIMConversation conv) {
		this.conv = conv;
	}

	public AVIMConversation getConv() {
		return conv;
	}

}
