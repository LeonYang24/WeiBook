package com.leon.weibook.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 控制软键盘输入显示或隐藏的工具类
 * Created by Leon on 2016/5/19 0019.
 */
public class SoftInputUtils {

	public static void toggleSoftInput(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 显示软键盘
	 * @param context
	 * @param view
	 */
	public static void showSoftInput(Context context, View view) {
		if (null != view) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, 0);
		}
	}

	/**
	 * 隐藏软键盘
	 * @param context
	 * @param view
	 */
	public static void hideSoftInput(Context context, View view) {
		if (null != view) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

}
