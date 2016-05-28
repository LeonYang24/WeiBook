package com.leon.weibook.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.leon.weibook.R;
import com.leon.weibook.adapters.HeaderListAdapter;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.controller.ConversationHelper;
import com.leon.weibook.controller.RoomsTable;
import com.leon.weibook.event.ConversationMemberClickEvent;
import com.leon.weibook.holder.ConversationDetailItemHolder;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.ConversationManager;
import com.leon.weibook.util.UserCacheUtils;
import com.leon.weibook.util.UserCacheUtils.CacheUserCallback;
import com.leon.weibook.util.Utils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 *
 * Created by Leon on 2016/5/21 0021.
 */
public class ConversationDetailActivity extends AVBaseActivity{
	private static final int ADD_MEMBERS = 0;
	private static final int INTENT_NAME = 1;

	@BindView(R.id.activity_conv_detail_rv_list) RecyclerView recyclerView;

	GridLayoutManager gridLayoutManager;
	HeaderListAdapter<LeanChatUser> listAdapter;

	View nameLayout;
	View quitLayout;

	private AVIMConversation conversation;
	private ConversationType conversationType;
	private ConversationManager conversationManager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_detail);
		init();
	}

	/**
	 * 初始化相关参数
	 */
	private void init() {
		String conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
		conversation = AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(conversationId);

		View footerView = getLayoutInflater().inflate(R.layout.conversation_detail_footer_layout, null);
		nameLayout = footerView.findViewById(R.id.name_layout);
		nameLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoModifyNameActivity();
			}
		});

		quitLayout = footerView.findViewById(R.id.quit_layout);
		quitLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				quitGroup();
			}
		});

		gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return (listAdapter.getItemViewType(position) == HeaderListAdapter.FOOTER_ITEM_TYPE
						? gridLayoutManager.getSpanCount()
						: 1);
			}
		});

		listAdapter = new HeaderListAdapter<>(ConversationDetailItemHolder.class);
		listAdapter.setFooterView(footerView);

		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.setAdapter(listAdapter);

		initData();
		setTitle(R.string.conversation_detail_title);
		setViewByConvType(conversationType);

	}

	/**
	 * 跳转到修改群组名界面
	 */
	private void gotoModifyNameActivity() {
		Intent intent = new Intent(this, UpdateContentActivity.class);
		intent.putExtra(Constants.INTENT_KEY, getString(R.string.conversation_name));
		startActivityForResult(intent, INTENT_NAME);
	}

	/**
	 * 离开群组
	 */
	private void quitGroup() {
		new AlertDialog.Builder(this).setMessage(R.string.conversation_quit_group_tip).
				setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String convid = conversation.getConversationId();
						conversation.quit(new AVIMConversationCallback() {
							@Override
							public void done(AVIMException e) {
								if (filterException(e)) {
									RoomsTable roomsTable = ChatManager.getInstance().getRoomsTable();
									roomsTable.deleteRoom(convid);
									Utils.toast(R.string.conversation_alreadyQuitConv);
									setResult(RESULT_OK);
									finish();
								}
							}
						});
					}
				}).
				setNegativeButton(R.string.chat_common_cancel, null).show();
	}

	private void setViewByConvType(ConversationType conversationType) {
		if (conversationType == ConversationType.Single) {
			nameLayout.setVisibility(View.GONE);
			quitLayout.setVisibility(View.GONE);
		} else {
			nameLayout.setVisibility(View.VISIBLE);
			quitLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem invite = menu.add(0, ADD_MEMBERS, 0, R.string.conversation_detail_invite);
		alwaysShowMenuItem(menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		if (menuId == ADD_MEMBERS) {
			Intent intent = new Intent(this, ConversationAddMembersActivity.class);
			intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
			startActivityForResult(intent, ADD_MEMBERS);
		}
		return super.onOptionsItemSelected(item);
	}

	private void refresh() {
		UserCacheUtils.fetchUsers(conversation.getMembers(), new CacheUserCallback() {
			@Override
			public void done(List<LeanChatUser> userList, Exception e) {
				listAdapter.setDataList(userList);
				listAdapter.notifyDataSetChanged();
			}
		});
	}

	private void initData() {
		conversationManager = ConversationManager.getInstance();
		conversationType = ConversationHelper.typeOfConversation(conversation);
	}

	public void onEvent(ConversationMemberClickEvent clickEvent) {
		if (clickEvent.isLongClick) {
			removeMemeber(clickEvent.memberId);
		} else {
			gotoPersonalActivity(clickEvent.memberId);
		}
	}

	private void gotoPersonalActivity(String memberId) {
		Intent intent = new Intent(this, ContactPersonInfoActivity.class);
		intent.putExtra(Constants.LEANCHAT_USER_ID, memberId);
		startActivity(intent);
	}

	private void removeMemeber(final String memberId) {
		if (conversationType == ConversationType.Single) {
			return;
		}
		boolean isTheOwner = conversation.getCreator().equals(memberId);
		if (!isTheOwner) {
			new AlertDialog.Builder(this).setMessage(R.string.conversation_kickTips)
					.setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final ProgressDialog progress = showSpinnerDialog();
							conversation.kickMembers(Arrays.asList(memberId), new AVIMConversationCallback() {
								@Override
								public void done(AVIMException e) {
									progress.dismiss();
									if (filterException(e)) {
										Utils.toast(R.string.conversation_detail_kickSucceed);
										refresh();
									}
								}
							});
						}
					}).setNegativeButton(R.string.chat_common_cancel, null).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == INTENT_NAME) {
				String newName = data.getStringExtra(Constants.INTENT_VALUE);
				conversationManager.updateName(conversation, newName, new AVIMConversationCallback() {
					@Override
					public void done(AVIMException e) {
						if (filterException(e)) {
							refresh();
						}
					}
				});
			} else if (requestCode == ADD_MEMBERS) {
				refresh();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}

