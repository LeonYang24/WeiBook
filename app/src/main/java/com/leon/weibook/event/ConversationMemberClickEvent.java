package com.leon.weibook.event;

/**
 * Created by Leon on 2016/5/27 0027.
 */
public class ConversationMemberClickEvent {

	public String memberId;
	public boolean isLongClick;

	public ConversationMemberClickEvent(String memberId, boolean isLongClick) {
		this.memberId = memberId;
		this.isLongClick = isLongClick;
	}

}
