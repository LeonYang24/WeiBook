package com.leon.weibook.event;

/**
 * 用于触发连接改变事件
 * Created by Leon on 2016/5/14 0014.
 */
public class ConnectionChangeEvent {

	public boolean isConnect;

	public ConnectionChangeEvent(boolean isConnect) {
		this.isConnect = isConnect;
	}

}


