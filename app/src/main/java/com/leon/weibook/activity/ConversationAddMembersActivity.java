package com.leon.weibook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.leon.weibook.R;
import com.leon.weibook.adapters.MemeberAddAdapter;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.controller.ConversationHelper;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.model.FriendsManager;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.UserCacheUtils;
import com.leon.weibook.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 群聊对话拉人页面
 * Created by Leon on 2016/5/27 0027.
 */
public class ConversationAddMembersActivity extends AVBaseActivity {

	@BindView(R.id.member_add_rv_list) protected RecyclerView recyclerView;

	private LinearLayoutManager layoutManager;
	private MemeberAddAdapter adapter;
	private AVIMConversation conversation;

	public static final int OK = 0;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_add_members_layout);

		String conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
		conversation = AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(conversationId);

		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		adapter = new MemeberAddAdapter();
		recyclerView.setAdapter(adapter);

		setListData();

	}

	private void setListData() {
		FriendsManager.fetchFriends(false, new FindCallback<LeanChatUser>() {
			@Override
			public void done(List<LeanChatUser> list, AVException e) {
				if (filterException(e)) {
					final List<String> userIds = new ArrayList<String>();
					for (LeanChatUser user : list) {
						userIds.add(user.getObjectId());
					}
					userIds.removeAll(conversation.getMembers());
					UserCacheUtils.fetchUsers(userIds, new UserCacheUtils.CacheUserCallback() {
						@Override
						public void done(List<LeanChatUser> userList, Exception e) {
							adapter.setDataList(userList);
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem add = menu.add(0, OK, 0, R.string.common_sure);
		alwaysShowMenuItem(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == OK) {
			addMembers();
		}
		return super.onOptionsItemSelected(item);
	}

	private void addMembers() {
		final List<String> checkedUsers = adapter.getCheckedIds();
		final ProgressDialog dialog = showSpinnerDialog();
		if (checkedUsers.size() == 0) {
			finish();
		} else {
			if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
				List<String> members = new ArrayList<String>();
				members.addAll(checkedUsers);
				members.addAll(conversation.getMembers());
				ChatManager.getInstance().createGroupConversation(members, new AVIMConversationCreatedCallback() {
					@Override
					public void done(final AVIMConversation conversation, AVIMException e) {
						if (filterException(e)) {
							Intent intent = new Intent(ConversationAddMembersActivity.this, ChatRoomActivity.class);
							intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
							startActivity(intent);
							finish();
						}
					}
				});
			} else {
				conversation.addMembers(checkedUsers, new AVIMConversationCallback() {
					@Override
					public void done(AVIMException e) {
						dialog.dismiss();
						if (filterException(e)) {
							Utils.toast(R.string.conversation_inviteSucceed);
							setResult(RESULT_OK);
							finish();
						}
					}
				});
			}
		}
	}

}
