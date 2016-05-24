package com.leon.weibook.event;

/**
 * Created by Leon on 2016/5/19 0019.
 */
public class InputBottomBarEvent {

	public static final int INPUTBOTTOMBAR_IMAGE_ACTION = 0;
	public static final int INPUTBOTTOMBAR_CAMERA_ACTION = 1;
	public static final int INPUTBOTTOMBAR_LOCATION_ACTION = 2;
	public static final int INPUTBOTTOMBAR_SEND_TEXT_ACTION = 3;
	public static final int INPUTBOTTOMBAR_SEND_AUDIO_ACTION = 4;

	public int eventAction;
	public Object tag;

	public InputBottomBarEvent(int action, Object tag) {
		eventAction = action;
		this.tag = tag;
	}

}
