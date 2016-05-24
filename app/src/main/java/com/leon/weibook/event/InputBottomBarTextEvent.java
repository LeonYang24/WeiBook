package com.leon.weibook.event;

/**
 * Created by Leon on 2016/5/19 0019.
 */
public class InputBottomBarTextEvent extends InputBottomBarEvent {

	/**
	 * 发送的文本内容
	 */
	public String sendContent;

	public InputBottomBarTextEvent(int action, String content, Object tag) {
		super(action, tag);
		sendContent = content;
	}

}
