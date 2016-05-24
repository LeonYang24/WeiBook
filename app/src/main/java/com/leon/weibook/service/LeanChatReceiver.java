package com.leon.weibook.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.leon.weibook.R;
import com.leon.weibook.event.InvitationEvent;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.LogUtils;
import com.leon.weibook.util.NotificationUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leon on 2016/5/23 0023.
 */
public class LeanChatReceiver extends BroadcastReceiver {

	public final static String AVOS_DATA = "com.avoscloud.Data";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action)) {
			if (action.equals(context.getString(R.string.invitation_action))) {
				String avosData = intent.getStringExtra(AVOS_DATA);
				if (!TextUtils.isEmpty(avosData)) {
					try {
						JSONObject json = new JSONObject(avosData);
						if (null != json) {
							String alertStr = json.getString(PushManager.AVOS_ALERT);

							Intent notificationIntent = new Intent(context, NotificationBroadcastReceiver.class);
							notificationIntent.putExtra(Constants.NOTOFICATION_TAG, Constants.NOTIFICATION_SYSTEM);
							NotificationUtils.showNotification(context, "LeanChat", alertStr, notificationIntent);
						}
					} catch (JSONException e) {
						LogUtils.logException(e);
					}
				}
			}
		}
		EventBus.getDefault().post(new InvitationEvent());
	}
}
