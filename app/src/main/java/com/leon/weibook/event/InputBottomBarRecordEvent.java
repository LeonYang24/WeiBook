package com.leon.weibook.event;

/**
 * Created by Leon on 2016/5/19 0019.
 */
public class InputBottomBarRecordEvent extends InputBottomBarEvent {

	/**
	 * 录音本地路径
	 */
	public String audioPath;

	/**
	 * 录音长度
	 */
	public int audioDuration;

	public InputBottomBarRecordEvent(int action, String path, int duration, Object tag) {
		super(action, tag);
		audioDuration = duration;
		audioPath = path;
	}

}
