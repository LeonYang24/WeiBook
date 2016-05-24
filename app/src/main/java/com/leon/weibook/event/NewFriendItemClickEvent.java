package com.leon.weibook.event;

import com.leon.weibook.model.AddRequest;

/**
 * Created by Leon on 2016/5/17 0017.
 */
public class NewFriendItemClickEvent {

	public AddRequest addRequest;
	public boolean isLongClick;
	public NewFriendItemClickEvent(AddRequest request, boolean isLongClick) {
		addRequest = request;
		this.isLongClick = isLongClick;
	}

}
