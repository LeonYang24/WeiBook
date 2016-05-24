package com.leon.weibook.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.leon.weibook.R;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.controller.ConversationHelper;
import com.leon.weibook.fragment.ChatFragment;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.LogUtils;

/**
 * 这是所有聊天界面的基类Activity
 * Created by Leon on 2016/5/18 0018.
 */
public class AVChatActivity extends AVBaseActivity {

	protected ChatFragment chatFragment;
	protected AVIMConversation conversation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);
		initByIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		initByIntent(intent);
	}

	private void initByIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		Log.i("test", "member_id :" + extras.getString(Constants.MEMBER_ID));
		if (null != extras) {
			if (extras.containsKey(Constants.MEMBER_ID)) {
				getConversation(extras.getString(Constants.MEMBER_ID));
			} else if (extras.containsKey(Constants.CONVERSATION_ID)) {
				Log.i("test", "conversation_id :" + extras.getString(Constants.CONVERSATION_ID));
				String conversationId = extras.getString(Constants.CONVERSATION_ID);
				updateConversation(AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(conversationId));
			} else {}
		}
	}

	protected void initActionBar(String title) {
		ActionBar actionBar = getActionBar();
		if (null != actionBar) {
			if (null != title) {
				actionBar.setTitle(title);
			}
			actionBar.setDisplayUseLogoEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(true);
		} else {
			LogUtils.i("action bar is null, so no title, please set an ActionBar style for activity");
		}
	}

	protected void updateConversation(AVIMConversation conversation) {
		if (null != conversation) {
			this.conversation = conversation;
			chatFragment.setConversation(conversation);
			//如果是私聊，则不显示用户名，群聊才显示
			chatFragment.showUserName(ConversationHelper.typeOfConversation(conversation)
										!= ConversationType.Single);
			initActionBar(ConversationHelper.titleOfConversation(conversation));
		}
	}

	/**
	 * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
	 * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
	 */
	private void getConversation(final String memberId) {
		ChatManager.getInstance().createSingleConversation(memberId, new AVIMConversationCreatedCallback() {
			@Override
			public void done(AVIMConversation avimConversation, AVIMException e) {
				if (filterException(e)) {
					ChatManager.getInstance().getRoomsTable().insertRoom(avimConversation.getConversationId());
					updateConversation(avimConversation);
				}
			}
		});
	}


}
