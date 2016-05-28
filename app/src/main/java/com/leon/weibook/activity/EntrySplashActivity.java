package com.leon.weibook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.leon.weibook.R;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.model.LeanChatUser;

/**
 * 这是应用程序开启界面
 *
 *
 *
 * Created by Leon on 2016/5/13 0013.
 */
public class EntrySplashActivity extends AVBaseActivity {

	public static final int SPLASH_DURATION = 1000;//设置间隔为1秒

	private static final int GO_MAIN_MSG = 1;//跳转到主界面
	private static final int GO_LOGIN_MSG = 2;//跳转到登录界面

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GO_MAIN_MSG:
					imLogin();
					break;
				case GO_LOGIN_MSG:
					Intent intent = new Intent(EntrySplashActivity.this, UserLoginActivity.class);
					EntrySplashActivity.this.startActivity(intent);
					finish();
					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entrysplash);

		if (null != LeanChatUser.getCurrentUser()) {
			LeanChatUser.getCurrentUser().updateUserInfo();
			handler.sendEmptyMessageDelayed(GO_MAIN_MSG, SPLASH_DURATION);
		} else {
			handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, SPLASH_DURATION);
		}
	}

	public void imLogin() {
		ChatManager.getInstance().openClient(this, LeanChatUser.getCurrentUserId(), new AVIMClientCallback() {
			@Override
			public void done(AVIMClient avimClient, AVIMException e) {
				if (filterException(e)) {
					Intent intent = new Intent(EntrySplashActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}

}
