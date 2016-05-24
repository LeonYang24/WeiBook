package com.leon.weibook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leon.weibook.R;
import com.leon.weibook.model.AddRequest;
import com.leon.weibook.model.AddRequestManager;
import com.leon.weibook.model.FriendsManager;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.PhotoUtils;
import com.leon.weibook.util.UserCacheUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户详情页，从对话详情页面和发现页面跳转过来
 * Created by Leon on 2016/5/16 0016.
 */
public class ContactPersonInfoActivity extends AVBaseActivity{

	@BindView(R.id.all_layout) LinearLayout allLayout;
	@BindView(R.id.avatar_view) ImageView avatarView;
	@BindView(R.id.avatar_arrow) ImageView avatarArrowView;
	@BindView(R.id.username_view) TextView usernameView;
	@BindView(R.id.sexView) TextView genderView;
	@BindView(R.id.head_layout) RelativeLayout avatarLayout;
	@BindView(R.id.sex_layout) RelativeLayout genderLayout;
	@BindView(R.id.chatBtn) Button chatBtn;
	@BindView(R.id.addFriendBtn) Button addFriendBtn;
	public static final String USER_ID = "userId";

	String userId = "";
	LeanChatUser user;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_person_info_activity);

		init();
		initView();
	}

	/**
	 * 初始化变量
	 */
	public void init() {
		userId = getIntent().getStringExtra(Constants.LEANCHAT_USER_ID);
		Log.i("test", userId);
		user = UserCacheUtils.getCachedUser(userId);
	}

	private void initView() {
		LeanChatUser curUser = LeanChatUser.getCurrentUser().getCurrentUser();
		if (curUser.equals(user)) { //如果搜索到的是自己
			setTitle(R.string.contact_personalInfo);
			avatarArrowView.setVisibility(View.VISIBLE);
			chatBtn.setVisibility(View.GONE);
			addFriendBtn.setVisibility(View.GONE);
		} else {
			setTitle(R.string.contact_detailInfo);
			avatarArrowView.setVisibility(View.INVISIBLE);
			List<String> cacheFriends = FriendsManager.getFriendIds();
			boolean isFriend = cacheFriends.contains(user.getObjectId());
			if (isFriend) {
				chatBtn.setVisibility(View.VISIBLE);
			} else {
				chatBtn.setVisibility(View.GONE);
				addFriendBtn.setVisibility(View.VISIBLE);
			}
		}
		updateView(user);
	}

	/**
	 * 更新视图
	 * @param user
	 */
	private void updateView(LeanChatUser user) {
		ImageLoader.getInstance().displayImage(user.getAvatarUrl(),
											   avatarView,
											   PhotoUtils.avatarImageOptions);
		usernameView.setText(user.getUsername());
	}

	@OnClick(R.id.chatBtn)
	public void startChat() {
		Intent intent = new Intent(ContactPersonInfoActivity.this, ChatRoomActivity.class);
		intent.putExtra(Constants.MEMBER_ID, userId);
		startActivity(intent);
		finish();
	}

	@OnClick(R.id.addFriendBtn)
	public void addFriend() {
		AddRequestManager.getInstance().createAddRequestInBackground(this, user);
	}

}
