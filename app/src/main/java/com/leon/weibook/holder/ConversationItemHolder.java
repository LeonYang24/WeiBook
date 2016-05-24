package com.leon.weibook.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.leon.weibook.R;
import com.leon.weibook.controller.ConversationHelper;
import com.leon.weibook.event.ConversationItemClickEvent;
import com.leon.weibook.model.ConversationType;
import com.leon.weibook.model.Room;
import com.leon.weibook.util.ConversationManager;
import com.leon.weibook.util.PhotoUtils;
import com.leon.weibook.util.ThirdPartUserUtils;
import com.leon.weibook.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Leon on 2016/5/22 0022.
 */
public class ConversationItemHolder extends CommonViewHolder {

	ImageView avatarView;
	TextView unreadView;
	TextView messageView;
	TextView timeView;
	TextView nameView;
	RelativeLayout avatarLayout;
	LinearLayout contentLayout;

	public ConversationItemHolder(ViewGroup root) {
		super(root.getContext(), root, R.layout.conversation_item);
		initView();
	}

	public void initView() {
		avatarView = (ImageView)itemView.findViewById(R.id.conversation_item_iv_avatar);
		nameView = (TextView)itemView.findViewById(R.id.conversation_item_tv_name);
		timeView = (TextView)itemView.findViewById(R.id.conversation_item_tv_time);
		unreadView = (TextView)itemView.findViewById(R.id.conversation_item_tv_unread);
		messageView = (TextView)itemView.findViewById(R.id.conversation_item_tv_message);
		avatarLayout = (RelativeLayout)itemView.findViewById(R.id.conversation_item_layout_avatar);
		contentLayout = (LinearLayout)itemView.findViewById(R.id.conversation_item_layout_content);
	}

	@Override
	public void bindData(Object o) {
		final Room room = (Room) o;
		AVIMConversation conversation = room.getConversation();
		if (null != conversation) {
			if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
				String userId = ConversationHelper.otherIdOfConversation(conversation);
				String avatar = ThirdPartUserUtils.getInstance().getUserAvatar(userId);
				ImageLoader.getInstance().displayImage(avatar, avatarView, PhotoUtils.avatarImageOptions);
			} else {
				avatarView.setImageBitmap(ConversationManager.getConversationIcon(conversation));
			}
			nameView.setText(ConversationHelper.nameOfConversation(conversation));

			int num = room.getUnreadCount();
			unreadView.setText(num + "");
			unreadView.setVisibility(num > 0 ? View.VISIBLE : View.GONE);

			conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
				@Override
				public void done(AVIMMessage avimMessage, AVIMException e) {
					if (null != avimMessage) {
						Date date = new Date(avimMessage.getTimestamp());
						SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
						timeView.setText(format.format(date));
						messageView.setText(Utils.getMessageeShorthand(getContext(), avimMessage));
					} else {
						timeView.setText("");
						messageView.setText("");
					}
				}
			});
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new ConversationItemClickEvent(room.getConversationId()));
				}
			});
		}
	}

	public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ConversationItemHolder>() {
		@Override
		public ConversationItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
			return new ConversationItemHolder(parent);
		}
	};

}
