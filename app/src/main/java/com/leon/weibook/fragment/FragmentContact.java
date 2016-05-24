package com.leon.weibook.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.leon.weibook.App;
import com.leon.weibook.R;
import com.leon.weibook.activity.ChatRoomActivity;
import com.leon.weibook.activity.ContactAddFriendActivity;
import com.leon.weibook.activity.ContactNewFriendActivity;
import com.leon.weibook.activity.ConversationGroupListActivity;
import com.leon.weibook.adapters.AdapterContact;
import com.leon.weibook.event.ContactItemClickEvent;
import com.leon.weibook.event.ContactItemLongClickEvent;
import com.leon.weibook.event.ContactRefreshEvent;
import com.leon.weibook.event.InvitationEvent;
import com.leon.weibook.event.MemberLetterEvent;
import com.leon.weibook.model.AddRequestManager;
import com.leon.weibook.model.FriendsManager;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leon on 2016/5/15 0015.
 */
public class FragmentContact extends BaseFragment {

	@BindView(R.id.activity_square_members_srl_list)
	protected SwipeRefreshLayout refreshLayout;

	@BindView(R.id.activity_square_members_rv_list)
	protected RecyclerView recyclerView;

	@BindView(R.id.iv_msg_tips)
	protected ImageView msgTipsView;

	@OnClick(R.id.layout_new)
	public void newFriend() {
		Intent intent = new Intent(ctx, ContactNewFriendActivity.class);
				ctx.startActivity(intent);
	}

	@OnClick(R.id.layout_group)
	public void group() {
		Intent intent = new Intent(ctx, ConversationGroupListActivity.class);
				ctx.startActivity(intent);
	}

	private AdapterContact itemAdapter;
	LinearLayoutManager layoutManager;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initHeader();
		refresh();
		EventBus.getDefault().register(this);
		getMembers(false);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contact, container, false);
		//headerView = inflater.inflate(R.layout.contact_fragment_header_layout, container, false);
		ButterKnife.bind(this, view);

		layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);

		itemAdapter = new AdapterContact();
		recyclerView.setAdapter(itemAdapter);

		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getMembers(false);
			}
		});

		return view;
	}

	private void initHeader() {
		headerLayout.setTitle(App.ctx.getString(R.string.contact));
		headerLayout.showRightImageButton(R.drawable.base_action_bar_add_bg_selector,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ctx, ContactAddFriendActivity.class);
						ctx.startActivity(intent);
					}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateNewRequestBadge();
	}

	/**
	 * 获取好友并设置在item上
	 * @param isforce true则是NETWORK_ELSE_CACHE，先网络查询，再缓存查询
	 *                false则是CACHE_ELSE_NETWORK，先缓存查询，再网络查询
	 */
	private void getMembers(final boolean isforce) {
		FriendsManager.fetchFriends(isforce, new FindCallback<LeanChatUser>() {
			@Override
			public void done(List<LeanChatUser> list, AVException e) {
				Log.i("test", "开始获取好友");
				for(LeanChatUser user : list) {
					Log.i("test", user.getUsername());
				}
				refreshLayout.setRefreshing(false);
				itemAdapter.setUserList(list);
				itemAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 更新新好友添加的标记
	 * 有未读信息则显示标记，否则不现实
	 */
	private void updateNewRequestBadge() {
		msgTipsView.setVisibility(
				AddRequestManager.getInstance().hasUnreadRequests() ? View.VISIBLE : View.GONE);
	}

	/**
	 * 用于刷新
	 * 如果有未读好友添加新奇，则更新标记
	 */
	private void refresh() {
		AddRequestManager.getInstance().countUnreadRequests(new CountCallback() {
			@Override
			public void done(int i, AVException e) {
				updateNewRequestBadge();
			}
		});
	}

	/**
	 * 用于弹出删除对话框
	 * @param memberId
	 */
	public void showDeleteDialog(final String memberId) {
		new AlertDialog.Builder(ctx).setMessage(R.string.contact_deleteContact)
				.setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final ProgressDialog dialog1 = showSpinnerDialog();
						LeanChatUser.getCurrentUser().removeFriend(memberId, new SaveCallback() {
							@Override
							public void done(AVException e) {
								dialog1.dismiss();
								if (filterException(e)) {
									getMembers(true);
								}
							}
						});
					}
				}).setNegativeButton(R.string.chat_common_cancel, null).show();
	}

	/**
	 * 从ContactNewFriendActivity post过来的event
	 * @param event
	 */
	@Subscribe
	public void onEvent(ContactRefreshEvent event) {
		getMembers(true);
	}

	@Subscribe
	public void onEvent(InvitationEvent event) {
		AddRequestManager.getInstance().unreadRequestsIncrement();
		updateNewRequestBadge();
	}

	@Subscribe
	public void onEvent(ContactItemClickEvent event) {
		Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
		intent.putExtra(Constants.MEMBER_ID, event.memberId);
		startActivity(intent);
	}

	@Subscribe
	public void onEvent(ContactItemLongClickEvent event) {
		showDeleteDialog(event.memberId);
	}

	/**
	 * 处理 LetterView 发送过来的 MemberLetterEvent
	 * 会通过 MembersAdapter 获取应该要跳转到的位置，然后跳转
	 */
	@Subscribe
	public void onEvent(MemberLetterEvent event) {
		Character targetChar = Character.toLowerCase(event.letter);
		if (itemAdapter.getIndexMap().containsKey(targetChar)) {
			int index = itemAdapter.getIndexMap().get(targetChar);
			if (index > 0 && index < itemAdapter.getItemCount()) {
				layoutManager.scrollToPositionWithOffset(index, 0);
			}
		}
	}
}
