package com.leon.weibook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.leon.weibook.R;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Utils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Leon on 2016/5/13 0013.
 */
public class UserLoginActivity extends AVBaseActivity {

	@BindView(R.id.activity_login_et_username) EditText userNameView;
	@BindView(R.id.activity_login_et_password) EditText passwordView;
	//@BindView(R.id.activity_login_cb_savepwd) CheckBox savePwdCheckBox;
	//@BindView(R.id.activity_login_cb_autologin) CheckBox autoLoginCheckBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlogin);
	}

	@OnClick(R.id.activity_login_tv_register)
	public void register() {
		Intent userRegister = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
		startActivity(userRegister);
		UserLoginActivity.this.finish();

	}

	@OnClick(R.id.activity_login_btn_login)
	public void login() {
		Log.i("test", "login");
		final String name = userNameView.getText().toString().trim();
		final String password = passwordView.getText().toString().trim();

		if (TextUtils.isEmpty(name)) {
			Utils.toast(R.string.username_cannot_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			Utils.toast(R.string.password_can_not_null);
			return;
		}

		final ProgressDialog dialog = showSpinnerDialog();
		LeanChatUser.logInInBackground(name, password, new LogInCallback<LeanChatUser>() {
			@Override
			public void done(LeanChatUser avUser, AVException e) {
				dialog.dismiss();
				if (filterException(e)) {
					imLogin();
				}
			}
		}, LeanChatUser.class);
	}

	/**
	 * 因为 leancloud 实时通讯与账户体系是完全解耦的，所以此处需要先 LeanchatUser.logInInBackground
	 * 如果验证账号密码成功，然后再 openClient 进行实时通讯
	 * Attention：使用实时通讯模块，务必在AndroidManifest.xml文件中做server声明，否则回调不会调用，
	 * 主线程会一直卡住，出现“Failed to set EGL_SWAP_BEHAVIOR on surface”错误
	 */
	public void imLogin() {
		ChatManager.getInstance().openClient(this, LeanChatUser.getCurrentUserId(), new AVIMClientCallback() {
			@Override
			public void done(AVIMClient avimClient, AVIMException e) {
				if (filterException(e)) {
					Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}

}
