package com.leon.weibook.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leon.weibook.App;
import com.leon.weibook.R;
import com.leon.weibook.adapters.AdapterMultipleItem;
import com.leon.weibook.event.ImTypeMessageEvent;
import com.leon.weibook.event.ImTypeMessageResendEvent;
import com.leon.weibook.event.InputBottomBarEvent;
import com.leon.weibook.event.InputBottomBarRecordEvent;
import com.leon.weibook.event.InputBottomBarTextEvent;
import com.leon.weibook.util.NotificationUtils;
import com.leon.weibook.util.PathUtils;
import com.leon.weibook.util.ProviderPathUtils;
import com.leon.weibook.views.InputBottomBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 将聊天相关的封装到这个Fragment里，只需要通过通过setConversation传入Conversatioin即可
 * Created by Leon on 2016/5/18 0018.
 */
public class ChatFragment extends Fragment {

	private static final int TAKE_CAMERA_REQUEST = 2;
	private static final int GALLERY_REQUEST = 0;
	private static final int GALLERY_KITKAT_REQUEST = 3;

	protected AVIMConversation imConversation;
	protected AdapterMultipleItem itemAdapter;

	protected RecyclerView recyclerView;
	protected SwipeRefreshLayout refreshLayout;
	protected InputBottomBar inputBottomBar;

	protected LinearLayoutManager layoutManager;
	protected String localCameraPath;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chat, container, false);

		localCameraPath = PathUtils.getPicturePathByCurrentTime(App.ctx);//take care

		recyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_rv_chat);
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_chat_srl_pullrefresh);
		inputBottomBar = (InputBottomBar) view.findViewById(R.id.fragment_chat_inputbottombar);

		refreshLayout.setEnabled(false);
		layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);

		itemAdapter = new AdapterMultipleItem();
		itemAdapter.resetRecycledViewPoolSize(recyclerView);
		recyclerView.setAdapter(itemAdapter);

		EventBus.getDefault().register(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		//为refreshlayout设置refresh监听器
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				AVIMMessage message = itemAdapter.getFirstMessage();
				if (null == message) {
					refreshLayout.setRefreshing(false);
				} else {
					//getMessageId()获取消息的全局Id，这个id只有在发送成功或者收到消息时才会有对应的值
					//queryMessages，从messageId向前查询
					imConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20,
							new AVIMMessagesQueryCallback() {
								@Override
								public void done(List<AVIMMessage> list, AVIMException e) {
									refreshLayout.setRefreshing(false);
									if (filterException(e)) {
										if (null != list && list.size() > 0) {
											itemAdapter.addMessageList(list);
											itemAdapter.notifyDataSetChanged();
											//跳转到历史记录的最后一条信息
											layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
										}
									}
								}
							});
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != imConversation) {
			//增加NotificationTag，因为已经在对话界面中，不需要再弹出Notification
			NotificationUtils.addTag(imConversation.getConversationId());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (null != imConversation) {
			//将本对话Tag从NotificationTag中移除，因为已跳出对话界面中，有Notification需要弹出
			NotificationUtils.removeTag(imConversation.getConversationId());
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}


	public void setConversation(AVIMConversation conversation) {
		imConversation = conversation;
		refreshLayout.setEnabled(true);
		inputBottomBar.setTag(imConversation.getConversationId());
		fetchMessages();//一定要加，否则没有历史记录
		NotificationUtils.addTag(conversation.getConversationId());
	}

	public void showUserName(boolean isShow) {
		itemAdapter.showUserName(isShow);
	}

	/**
	 * 拉取消息（历史记录），必须加入conversation后才能拉取消息
	 */
	private void fetchMessages() {
		imConversation.queryMessages(new AVIMMessagesQueryCallback() {
			@Override
			public void done(List<AVIMMessage> list, AVIMException e) {
				if (filterException(e)) {
					itemAdapter.setMessageList(list);
					recyclerView.setAdapter(itemAdapter);
					itemAdapter.notifyDataSetChanged();
					scrollToBottom();
				}
			}
		});
	}

	private void scrollToBottom() {
		layoutManager.scrollToPositionWithOffset(itemAdapter.getItemCount() - 1, 0);
	}

	/**
	 * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
	 * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
	 */
	@Subscribe
	public void onEvent(InputBottomBarTextEvent textEvent) {
		if (null != imConversation && null != textEvent) {
			if (!TextUtils.isEmpty(textEvent.sendContent)
					&& imConversation.getConversationId().equals(textEvent.tag)) {
				sendText(textEvent.sendContent);
			}
		}
	}

	/**
	 * 处理推送过来的消息
	 * 同理，避免无效消息，此处加了 conversation id 判断
	 */
	@Subscribe
	public void onEvent(ImTypeMessageEvent event) {
		if (null != imConversation && null != event &&
				imConversation.getConversationId().equals(event.conversation.getConversationId())) {
			itemAdapter.addMessage(event.message);
			itemAdapter.notifyDataSetChanged();
			scrollToBottom();
		}
	}

	/**
	 * 重新发送已经发送失败的消息
	 */
	@Subscribe
	public void onEvent(ImTypeMessageResendEvent event) {
		if (null != imConversation && null != event &&
				null != event.message &&  imConversation.getConversationId().equals(event.message.getConversationId())) {
			if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == event.message.getMessageStatus()
					&& imConversation.getConversationId().equals(event.message.getConversationId())) {
				imConversation.sendMessage(event.message, new AVIMConversationCallback() {
					@Override
					public void done(AVIMException e) {
						itemAdapter.notifyDataSetChanged();
					}
				});
				itemAdapter.notifyDataSetChanged();
			}
		}
	}

	@Subscribe
	public void onEvent(InputBottomBarEvent event) {
		if (null != imConversation && null != event && imConversation.getConversationId().equals(event.tag)) {
			switch (event.eventAction) {
				case InputBottomBarEvent.INPUTBOTTOMBAR_IMAGE_ACTION:
					selectImageFromLocal();
					break;
				case InputBottomBarEvent.INPUTBOTTOMBAR_CAMERA_ACTION:
					selectImageFromCamera();
					break;
			}
		}
	}

	@Subscribe
	public void onEvent(InputBottomBarRecordEvent recordEvent) {
		if (null != imConversation && null != recordEvent &&
				!TextUtils.isEmpty(recordEvent.audioPath) &&
				imConversation.getConversationId().equals(recordEvent.tag)) {
			sendAudio(recordEvent.audioPath);
		}
	}

	public void selectImageFromLocal() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.chat_activity_select_picture)),
					GALLERY_REQUEST);
		} else {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			startActivityForResult(intent, GALLERY_KITKAT_REQUEST);
		}
	}

	public void selectImageFromCamera() {
		Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		Uri imageUri = Uri.fromFile(new File(localCameraPath));
		takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
		if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, TAKE_CAMERA_REQUEST);
		}
	}


	private void sendText(String content) {
		AVIMTextMessage message = new AVIMTextMessage();
		message.setText(content);
		sendMessage(message);
	}

	private void sendImage(String imagePath) {
		AVIMImageMessage imageMsg = null;
		try {
			imageMsg = new AVIMImageMessage(imagePath);
			sendMessage(imageMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendAudio(String audioPath) {
		try {
			AVIMAudioMessage audioMessage = new AVIMAudioMessage(audioPath);
			sendMessage(audioMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(AVIMTypedMessage message) {
		itemAdapter.addMessage(message);
		itemAdapter.notifyDataSetChanged();
		scrollToBottom();
		imConversation.sendMessage(message, new AVIMConversationCallback() {
			@Override
			public void done(AVIMException e) {
				itemAdapter.notifyDataSetChanged();
			}
		});
	}

	protected void toast(String str) {
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}

	protected boolean filterException(Exception e) {
		if (e != null) {
			e.printStackTrace();
			toast(e.getMessage());
			return false;
		} else {
			return true;
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case GALLERY_REQUEST:
				case GALLERY_KITKAT_REQUEST:
					if (data == null) {
						toast("return intent is null");
						return;
					}
					Uri uri;
					if (requestCode == GALLERY_REQUEST) {
						uri = data.getData();
					} else {
						//for Android 4.4
						uri = data.getData();
						final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
								| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
						getActivity().getContentResolver().takePersistableUriPermission(uri, takeFlags);
					}
					String localSelectPath = ProviderPathUtils.getPath(getActivity(), uri);
					inputBottomBar.hideMoreLayout();
					sendImage(localSelectPath);
					break;
				case TAKE_CAMERA_REQUEST:
					inputBottomBar.hideMoreLayout();
					sendImage(localCameraPath);
					break;
			}
		}
	}
}
