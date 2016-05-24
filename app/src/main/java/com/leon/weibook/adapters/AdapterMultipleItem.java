package com.leon.weibook.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.event.ChatItemImageHolder;
import com.leon.weibook.holder.ChatItemAudioHolder;
import com.leon.weibook.holder.ChatItemHolder;
import com.leon.weibook.holder.ChatItemLocationHolder;
import com.leon.weibook.holder.ChatItemTextHolder;
import com.leon.weibook.holder.CommonViewHolder;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Leon on 2016/5/19 0019.
 */
public class AdapterMultipleItem extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int ITEM_LEFT = 100;
	private final int ITEM_LEFT_TEXT = 101;
	private final int ITEM_LEFT_IMAGE = 102;
	private final int ITEM_LEFT_AUDIO = 103;
	private final int ITEM_LEFT_LOCATION = 104;

	private final int ITEM_RIGHT = 200;
	private final int ITEM_RIGHT_TEXT = 201;
	private final int ITEM_RIGHT_IMAGE = 202;
	private final int ITEM_RIGHT_AUDIO = 203;
	private final int ITEM_RIGHT_LOCATION = 204;

	//时间间隔最小为10分钟
	private final static long TIME_INTERVAL  = 1000 * 60 * 3;
	private boolean isShowUserName = true;

	private List<AVIMMessage> messageList = new ArrayList<AVIMMessage>();
	private static PrettyTime prettyTime = new PrettyTime();

	public AdapterMultipleItem() {}

	/**
	 * 设置messageList的值
	 * @param messages
	 */
	public void setMessageList(List<AVIMMessage> messages) {
		messageList.clear();
		if (null != messages) {
			messageList.addAll(messages);
		}
	}

	/**
	 * 在messageList中最开头处加入messages
	 * @param messages
	 */
	public void addMessageList(List<AVIMMessage> messages) {
		messageList.addAll(0, messages);//位置需注意
	}

	/**
	 * 在messageList中添加一条message
	 * @param message
	 */
	public void addMessage(AVIMMessage message) {
		messageList.addAll(Arrays.asList(message));
	}

	/**
	 * 从messageList中获取第一条信息
	 * @return
	 */
	public AVIMMessage getFirstMessage() {
		if (null != messageList && messageList.size() > 0) {
			return messageList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public int getItemCount() {
		return messageList.size();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case ITEM_LEFT_TEXT:
				return new ChatItemTextHolder(parent.getContext(), parent, true);
			case ITEM_LEFT_IMAGE:
				return new ChatItemImageHolder(parent.getContext(), parent, true);
			case ITEM_LEFT_AUDIO:
				return new ChatItemAudioHolder(parent.getContext(), parent, true);
			case ITEM_LEFT_LOCATION:
				return new ChatItemLocationHolder(parent.getContext(), parent, true);
			case ITEM_RIGHT_TEXT:
				return new ChatItemTextHolder(parent.getContext(), parent, false);
			case ITEM_RIGHT_IMAGE:
				return new ChatItemImageHolder(parent.getContext(), parent, false);
			case ITEM_RIGHT_AUDIO:
				return new ChatItemAudioHolder(parent.getContext(), parent, false);
			case ITEM_RIGHT_LOCATION:
				return new ChatItemLocationHolder(parent.getContext(), parent, false);
			default:
				//TODO 此处还要判断左右
				return new ChatItemTextHolder(parent.getContext(), parent, true);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		((CommonViewHolder)holder).bindData(messageList.get(position));
		if (holder instanceof ChatItemHolder) {
			((ChatItemHolder)holder).showTimeView(shouldShowTime(position));
			((ChatItemHolder)holder).showUserName(isShowUserName);
		}
	}

	@Override
	public int getItemViewType(int position) {
		//TODO 如果是自定义的数据类型该如何
		AVIMMessage message = messageList.get(position);
		if (null != message && message instanceof AVIMTypedMessage) {
			AVIMTypedMessage typedMessage = (AVIMTypedMessage) message;
			boolean isMe = fromMe(typedMessage);
			if (typedMessage.getMessageType() == AVIMReservedMessageType.TextMessageType.getType()) {
				return isMe ? ITEM_RIGHT_TEXT : ITEM_LEFT_TEXT;
			} else if (typedMessage.getMessageType() == AVIMReservedMessageType.AudioMessageType.getType()) {
				return isMe ? ITEM_RIGHT_AUDIO : ITEM_LEFT_AUDIO;
			} else if (typedMessage.getMessageType() == AVIMReservedMessageType.ImageMessageType.getType()) {
				return isMe ? ITEM_RIGHT_IMAGE : ITEM_LEFT_IMAGE;
			} else if (typedMessage.getMessageType() == AVIMReservedMessageType.LocationMessageType.getType()) {
				return isMe ? ITEM_RIGHT_LOCATION : ITEM_LEFT_LOCATION;
			} else {
				return isMe ? ITEM_RIGHT : ITEM_LEFT;
			}
		}
		return 8888;
	}

	/**
	 * 判断是否显示时间，若position为0则显示，否则判断最近一条信息发送时间是否超过10分钟，超过则显示
	 * @param position
	 * @return
	 */
	private boolean shouldShowTime(int position) {
		if (position == 0) {
			return true;
		}
		long lastTime = messageList.get(position - 1).getTimestamp();
		long curTime = messageList.get(position).getTimestamp();
		return curTime - lastTime > TIME_INTERVAL;//超过10分钟，则显示时间
	}

	/**
	 * 决定是否显示用户名
	 * @param isShow
	 */
	public void showUserName(boolean isShow) {
		isShowUserName = isShow;
	}


	/**
	 * 因为 RecyclerView 中的 item 缓存默认最大为 5，造成会重复的 create item 而卡顿
	 * 所以这里根据不同的类型设置不同的缓存值，经验值，不同 app 可以根据自己的场景进行更改
	 */
	public void resetRecycledViewPoolSize(RecyclerView recyclerView) {
		recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_LEFT_TEXT, 25);
		recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_LEFT_IMAGE, 10);
		recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_LEFT_AUDIO, 15);
		recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_LEFT_LOCATION, 10);

		recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_RIGHT_TEXT, 25);
		recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_RIGHT_IMAGE, 10);
		recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_RIGHT_AUDIO, 15);
		recyclerView.getRecycledViewPool().setMaxRecycledViews(ITEM_RIGHT_LOCATION, 10);
	}

	/**
	 * 判断消息的发送者是本机用户还是其他用户
	 * @param msg
	 * @return
	 */
	private boolean fromMe(AVIMTypedMessage msg) {
		ChatManager chatManager = ChatManager.getInstance();
		String selfId = chatManager.getSelfId();
		return msg.getFrom() != null && msg.getFrom().equals(selfId);
	}

}