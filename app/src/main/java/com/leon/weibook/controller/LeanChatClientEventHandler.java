package com.leon.weibook.controller;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.leon.weibook.event.ConnectionChangeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 该类用于Client的网络状态响应
 * Created by Leon on 2016/5/14 0014.
 */
public class LeanChatClientEventHandler extends AVIMClientEventHandler {

	private static LeanChatClientEventHandler eventHandler;

	private volatile boolean connect = false;

	private LeanChatClientEventHandler() {}

	/**
	 * 单例模式
	 * @return 返回该类的唯一实例
	 */
	public static synchronized LeanChatClientEventHandler getInstance() {
		if (null == eventHandler) {
			eventHandler = new LeanChatClientEventHandler();
		}
		return eventHandler;
	}

	/**
	 * 是否连上聊天服务
	 * @return
	 */
	public boolean isConnect() {
		return connect;
	}

	public void setConnectAndNotify(boolean isConnect) {
		connect = isConnect;
		EventBus.getDefault().post(new ConnectionChangeEvent(connect));
	}

	/**
	 * 指网络连接断开事件发生，此时聊天服务不可用
	 * 同时触发ConnectionChangeEvent
	 * @param avimClient
	 */
	@Override
	public void onConnectionPaused(AVIMClient avimClient) {
		setConnectAndNotify(false);
	}

	/**
	 * 指网络连接恢复正常，此时聊天服务变得可用
	 * 同时触发ConnectionChangeEvent
	 * @param avimClient
	 */
	@Override
	public void onConnectionResume(AVIMClient avimClient) {
		setConnectAndNotify(true);
	}

	@Override
	public void onClientOffline(AVIMClient avimClient, int i) {

	}
}
