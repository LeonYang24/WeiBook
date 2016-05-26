package com.leon.weibook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.weibook.R;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.service.UpdateService;
import com.leon.weibook.util.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leon on 2016/5/15 0015.
 */
public class FragmentSetting extends BaseFragment {

	private static final int IMAGE_PICK_REQUEST = 1;
	private static final int CROP_REQUEST = 2;

	@BindView(R.id.setting_avatar_view) ImageView avatarView;
	@BindView(R.id.setting_username_view) TextView userNameView;

	ChatManager chatManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		headerLayout.setTitle("设置");
		chatManager = ChatManager.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	/**
	 * 刷新数据，重新获取本机用户姓名和头像
	 */
	private void refresh() {
		LeanChatUser curUser = LeanChatUser.getCurrentUser();
		userNameView.setText(curUser.getUsername());
		ImageLoader.getInstance().displayImage(curUser.getAvatarUrl(), avatarView,
				PhotoUtils.avatarImageOptions);
	}

	@OnClick(R.id.setting_notifysetting_view)
	public void onNotifySettingClick() {

	}

	@OnClick(R.id.setting_logout_btn)
	public void onLogoutClick() {

	}

	/**
	 * 点击检查更新信息
	 */
	@OnClick(R.id.setting_checkupdate_view)
	public void onCheckUpdateClick() {
		UpdateService updateService = UpdateService.getInstance(getActivity());
		updateService.showSureUpdateDialog();
	}

	@OnClick(R.id.setting_avatar_layout)
	public void onAvatarClick() {

	}

}
