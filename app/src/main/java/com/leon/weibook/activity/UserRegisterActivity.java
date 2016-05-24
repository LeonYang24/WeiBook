package com.leon.weibook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.leon.weibook.App;
import com.leon.weibook.R;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Utils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Leon on 2016/5/15 0015.
 */
public class UserRegisterActivity extends AVBaseActivity {

	@BindView(R.id.activity_register_et_username) EditText usernameEdit;
	@BindView(R.id.activity_register_et_pwd) EditText passwordEdit;
	@BindView(R.id.activity_register_et_ensurepwd) EditText ensurePwdEdit;
	@BindView(R.id.activity_register_btn_register) Button registerButton;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userregister);
		setTitle(App.ctx.getString(R.string.register));
	}

	@OnClick(R.id.activity_register_btn_register)
	public void register() {
		final String name = usernameEdit.getText().toString();
		final String password = passwordEdit.getText().toString();
		String againPassword = ensurePwdEdit.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Utils.toast(R.string.username_cannot_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			Utils.toast(R.string.password_can_not_null);
			return;
		}
		if (!againPassword.equals(password)) {
			Utils.toast(R.string.password_not_consistent);
			return;
		}

		LeanChatUser.signUpByNameAndPwd(name, password, new SignUpCallback() {
			@Override
			public void done(AVException e) {
				if (e != null) {
					Utils.toast(App.ctx.getString(R.string.registerFailed) + e.getMessage());
				} else {
					Utils.toast(R.string.registerSucceed);
					imLogin();
				}
			}
		});
	}

	private void imLogin() {
		ChatManager.getInstance().openClient(this, LeanChatUser.getCurrentUserId(), new AVIMClientCallback() {
			@Override
			public void done(AVIMClient avimClient, AVIMException e) {
				if (filterException(e)) {
					Intent intent = new Intent(UserRegisterActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}

}
