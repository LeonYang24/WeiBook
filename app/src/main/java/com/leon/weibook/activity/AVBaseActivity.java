package com.leon.weibook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leon.weibook.R;
import com.leon.weibook.event.EmptyEvent;
import com.leon.weibook.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * 这是本程序所有Activity所用的父类Activity
 * 封装了常用方法以及默认注册了ButterKnife
 * Created by Leon on 2016/5/13 0013.
 */
public class AVBaseActivity extends AppCompatActivity {

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		ButterKnife.bind(this);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		ButterKnife.bind(this);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		super.setContentView(view, params);
		ButterKnife.bind(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	protected void alwaysShowMenuItem(Menu menu) {
		if (null != menu && menu.size() > 0) {
			MenuItem item = menu.getItem(0);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
			                     MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
	}

	/**
	 * 该函数用于判断是否发生异常，有则显示并返回false，无则返回ture
	 * @param e
	 * @return
	 */
	protected  boolean filterException(Exception e) {
		if (null != e) {
			Log.i("test", "into filterException");
			e.printStackTrace();
			toast(e.getMessage());
			return false;
		} else {
			return true;
		}
	}

	protected void toast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	protected void startActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}

	protected void startActivity(Class<?> cls, String... objs) {
		Intent intent = new Intent(this, cls);
		for (int i = 0; i < objs.length; i++) {
			intent.putExtra(objs[i], objs[++i]);
		}
		startActivity(intent);
	}

	public void startActivity(Class<?> cls, int requestCode) {
		startActivityForResult(new Intent(this, cls), requestCode);
	}

	/**
	 * 必须实现一个以 onEvent开头的、非静态的、public权限以及仅仅只有一个参数的方法
	 * 这里不需要指定是什么Event，所以写了一个空类
	 * @param emptyEvent
	 */
	@Subscribe
	public void onEvent(EmptyEvent emptyEvent) {}

	/**
	 * 该函数
	 * @return
	 */
	protected ProgressDialog showSpinnerDialog() {
		//activity = modifyDialogContext(activity);
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//循环旋转风格，也是默认风格
		dialog.setCancelable(true);//可以取消
		dialog.setMessage(getString(R.string.chat_utils_hardLoading));
		if (!isFinishing()) {//如果activity并非finishing状态，则展示该dialog
			dialog.show();
		}
		return dialog;
	}

}
