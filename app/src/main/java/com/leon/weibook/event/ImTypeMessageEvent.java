package com.leon.weibook.event;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

/**
 * Created by Leon on 2016/5/21 0021.
 */
public class ImTypeMessageEvent {

	public AVIMTypedMessage message;
	public AVIMConversation conversation;

}
