package com.leon.weibook.activity;

import android.app.Activity;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * 这是本程序所有Activity所用的父类Activity
 *
 * 1、封装了常用方法setContentView（默认注册了ButterKnife）
 * 2、注册了EventBus（注意，每个继承该Acvitiy的类都要构造一个onEvent字样的函数）
 *
 * Created by Leon on 2016/5/13 0013.
 */
public class AVBaseActivity extends Activity {

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

	/**
	 *
	 * @param menu
	 */
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
			e.printStackTrace();
			toast(e.getMessage());
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 显示传入的字符串
	 * @param str
	 */
	protected void toast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示传入的资源ID的信息
	 * @param resId
	 */
	protected void toast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 直接传入要跳转到的activity类即可
	 * @param cls
	 */
	protected void startActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}

	/**
	 * 传入需要跳转到的activity类 以及 要传递的信息
	 * @param cls
	 * @param objs
	 */
	protected void startActivity(Class<?> cls, String... objs) {
		Intent intent = new Intent(this, cls);
		for (int i = 0; i < objs.length; i++) {
			intent.putExtra(objs[i], objs[++i]);
		}
		startActivity(intent);
	}

	/**
	 * 传入需要跳转到的activity类 以及 请求码 （可以收到返回值）
	 * @param cls
	 * @param requestCode
	 */
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
	 * 显示一个加载画面 （与BaseFragment里的一致）
	 * @return
	 */
	protected ProgressDialog showSpinnerDialog() {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//循环旋转风格，也是默认风格
		dialog.setCancelable(true);//可以取消
		dialog.setMessage(getString(R.string.hard_to_loading));
		if (!isFinishing()) {//如果activity并非finishing状态，则展示该dialog
			dialog.show();
		}
		return dialog;
	}

}
