package com.leon.weibook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.leon.weibook.R;

/**
 * Created by Leon on 2016/5/13 0013.
 */
public class EntrySplashActivity extends AVBaseActivity {

	public static final int SPLASH_DURATION = 1000;//设置间隔为2秒

	private static final int GO_MAIN_MSG = 1;
	private static final int GO_LOGIN_MSG = 2;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
//				case GO_MAIN_MSG:
//					imLogin();
//					break;
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

		handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, SPLASH_DURATION);
	}


}
