package com.leon.weibook.service;

import android.content.Context;
import android.text.TextUtils;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.PushService;
import com.leon.weibook.activity.EntrySplashActivity;
import com.leon.weibook.model.LeanChatUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leon on 2016/5/17 0017.
 */
public class PushManager {

	public final static String AVOS_ALERT = "alert";

	private final static String AVOS_PUSH_ACTION = "action";
	public static final String INSTALLATION_CHANNELS = "channels";
	private static PushManager pushManager;
	private Context context;

	public synchronized static PushManager getInstance() {
		if (pushManager == null) {
			pushManager = new PushManager();
		}
		return pushManager;
	}

	public void init(Context context) {
		this.context = context;
		//启动推送服务
		// 设置默认打开的 Activity
		PushService.setDefaultPushCallback(context, EntrySplashActivity.class);
		subscribeCurrentUserChannel();
	}

	private void subscribeCurrentUserChannel() {
		String currentUserId = LeanChatUser.getCurrentUserId();
		if (!TextUtils.isEmpty(currentUserId)) {
			// 订阅频道，当该频道消息到来的时候，打开对应的 Activity
			// 参数依次为：当前的 context、频道名称、回调对象的类
			PushService.subscribe(context, currentUserId, EntrySplashActivity.class);
		}
	}

	public void unsubscribeCurrentUserChannel() {
		String currentUserId = LeanChatUser.getCurrentUserId();
		if (!TextUtils.isEmpty(currentUserId)) {
			//退订频道
			PushService.unsubscribe(context, currentUserId);
		}
	}

	public void pushMessage(String userId, String message, String action) {
		AVQuery query = AVInstallation.getQuery();
		query.whereEqualTo(INSTALLATION_CHANNELS, userId);
		AVPush push = new AVPush();
		push.setQuery(query);

		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(AVOS_ALERT, message);
		dataMap.put(AVOS_PUSH_ACTION, action);
		push.setData(dataMap);
		push.sendInBackground();
	}

}
