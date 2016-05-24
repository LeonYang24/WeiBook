package com.leon.weibook.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by Leon on 2016/5/19 0019.
 */
public class PathUtils {

	/**
	 * 检查文件是否存在，不存在则创建文件
	 * @param file
	 * @return
	 */
	private static File checkAndMkdirs(File file) {
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 判断外部设备是否可以写入
	 * @return
	 */
	private static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/**
	 * 有 sdcard 的时候，三星S7Edge是 /storage/emulated/0/Android/data/com.leon.weibook/cache
	 * 无 sdcard 的时候，未考证
	 * @param context
	 * @return
	 */
	private static File getAvailableCacheDir(Context context) {
		if (isExternalStorageWritable()) {
			return context.getExternalCacheDir();
		} else {
			return context.getCacheDir();
		}
	}

	/**
	 * 获取文件的绝对路径
	 * @param context
	 * @param id
	 * @return
	 */
	public static String getChatFilePath(Context context, String id) {
		return (TextUtils.isEmpty(id) ? null
				  : new File(getAvailableCacheDir(context), id).getAbsolutePath());
	}

	/**
	 * 获取保存录音的路径（以“record_时间”命名）
	 * @param context
	 * @return
	 */
	public static String getRecordPathByCurrentTime(Context context) {
		return new File(getAvailableCacheDir(context),
				        "record_" + System.currentTimeMillis()).getAbsolutePath();
	}

	/**
	 * 获取保存拍照的地址（以“picture_时间”命名）
	 * @param context
	 * @return
	 */
	public static String getPicturePathByCurrentTime(Context context) {
		return new File(getAvailableCacheDir(context),
				        "picture_" + System.currentTimeMillis()).getAbsolutePath();
	}

}
