package com.leon.weibook.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.avos.avospush.notification.NotificationCompat;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Leon on 2016/5/20 0020.
 */
public class NotificationUtils {

	private static final int REPLY_NOTIFY_ID = 1;

	/**
	 * tag list 用来标记是否应该展示Notification
	 * 比如已经在聊天界面了，实例就不应该再弹出notifacation
	 */
	private static List<String> notificationTagList = new LinkedList<>();

	/**
	 * 添加tag到tag list，在MessageHandler弹出notification前会判断是否与此tag相等
	 * 若相等，则不弹，反之，则弹出
	 * @param tag
	 */
	public static void addTag(String tag) {
		if (!notificationTagList.contains(tag)) {
			notificationTagList.add(tag);
		}
	}

	/**
	 * 移除tag list中的tag
	 * @param tag
	 */
	public static void removeTag(String tag) {
		notificationTagList.remove(tag);
	}

	/**
	 * 判断是否应该弹出notification
	 * 判断标准是该tag是否包含在tag list中
	 * @param tag
	 * @return
	 */
	public static boolean isShowNotification(String tag) {
		return !notificationTagList.contains(tag);
	}

	public static void showNotification(Context context, String title, String content, Intent intent) {
		showNotification(context, title, content, null, intent);
	}

	public static void showNotification(Context context, String title, String content, String sound, Intent intent) {
		intent.setFlags(0);
//    int notificationId = (new Random()).nextInt();
		PendingIntent contentIntent = PendingIntent.getBroadcast(context, REPLY_NOTIFY_ID, intent, 0);
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(context.getApplicationInfo().icon)
						.setContentTitle(title).setAutoCancel(true).setContentIntent(contentIntent)
						.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
						.setContentText(content);
		NotificationManager manager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		if (sound != null && sound.trim().length() > 0) {
			notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + sound);
		}
		manager.notify(REPLY_NOTIFY_ID, notification);
	}

	public static void cancelNotification(Context context) {
		NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancel(REPLY_NOTIFY_ID);
	}

}
