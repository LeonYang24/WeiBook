package com.leon.weibook.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.leon.weibook.R;
import com.leon.weibook.activity.ChatRoomActivity;
import com.leon.weibook.adapters.ConversationListAdapter;
import com.leon.weibook.controller.ConversationHelper;
import com.leon.weibook.event.ConnectionChangeEvent;
import com.leon.weibook.event.ConversationItemClickEvent;
import com.leon.weibook.event.ImTypeMessageEvent;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.model.Room;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.ConversationManager;
import com.leon.weibook.util.UserCacheUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 最近会话列表
 * Created by Leon on 2016/5/15 0015.
 */
public class FragmentConversation extends BaseFragment {

	@BindView(R.id.im_client_state_view) View imClientStateView;
	@BindView(R.id.fragment_conversation_srl_pullrefresh) SwipeRefreshLayout refreshLayout;
	@BindView(R.id.fragment_conversation_srl_view) RecyclerView recyclerView;

	protected ConversationListAdapter<Room> itemAdapter;
	protected LinearLayoutManager layoutManager;

	private boolean hidden;
	private ConversationManager conversationManager;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_conversation, container, false);
		ButterKnife.bind(this, view);
		conversationManager = ConversationManager.getInstance();
		refreshLayout.setEnabled(false);
		layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);
		itemAdapter = new ConversationListAdapter<Room>();
		recyclerView.setAdapter(itemAdapter);
		EventBus.getDefault().register(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateConversationList();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			updateConversationList();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			updateConversationList();
		}
	}

	/**
	 * 对话Item点击事件触发
	 * @param event
	 */
	@Subscribe
	public void onEvent(ConversationItemClickEvent event) {
		Log.i("test", "event.conversationId = " + event.conversationId);
		Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
		intent.putExtra(Constants.CONVERSATION_ID, event.conversationId);
		startActivity(intent);
	}

	@Subscribe
	public void onEvent(ImTypeMessageEvent event) {
		updateConversationList();
	}

	/**
	 * 更新会话列表
	 */
	private void updateConversationList() {
		conversationManager.findAndCacheRooms(new Room.MultiRoomsCallback() {
			@Override
			public void done(List<Room> roomList, AVException exception) {
				if (filterException(exception)) {

					updateLastMessage(roomList);
					cacheRelatedUsers(roomList);

					List<Room> sortedRooms = sortRooms(roomList);
					itemAdapter.setDataList(sortedRooms);
					itemAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void updateLastMessage(final List<Room> roomList) {
		for (final Room room : roomList) {
			AVIMConversation conversation = room.getConversation();
			if (null != conversation) {
				conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
					@Override
					public void done(AVIMMessage avimMessage, AVIMException e) {
						if (filterException(e) && null != avimMessage) {
							room.setLastMessage(avimMessage);
							int index = roomList.indexOf(room);
							itemAdapter.notifyItemChanged(index);
						}
					}
				});
			}
		}
	}

	private void cacheRelatedUsers(List<Room> rooms) {
		List<String> needCacheUsers = new ArrayList<String>();
		for(Room room : rooms) {
			AVIMConversation conversation = room.getConversation();
			if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
				needCacheUsers.add(ConversationHelper.otherIdOfConversation(conversation));
			}
		}
		UserCacheUtils.fetchUsers(needCacheUsers, new UserCacheUtils.CacheUserCallback() {
			@Override
			public void done(List<LeanChatUser> userList, Exception e) {
				itemAdapter.notifyDataSetChanged();
			}
		});
	}

	private List<Room> sortRooms(final List<Room> roomList) {
		List<Room> sortedList = new ArrayList<Room>();
		if (null != roomList) {
			sortedList.addAll(roomList);
			Collections.sort(sortedList, new Comparator<Room>() {
				@Override
				public int compare(Room lhs, Room rhs) {
					long value = lhs.getLastModifyTime() - rhs.getLastModifyTime();
					if (value > 0) {
						return -1;
					} else if (value < 0) {
						return 1;
					} else {
						return 0;
					}
				}
			});
		}
		return sortedList;
	}

	@Subscribe
	public void onEvent(ConnectionChangeEvent event) {
		imClientStateView.setVisibility(event.isConnect ? View.GONE : View.VISIBLE);
	}

}
