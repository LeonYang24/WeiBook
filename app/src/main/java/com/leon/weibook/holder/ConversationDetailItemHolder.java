package com.leon.weibook.holder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.weibook.R;
import com.leon.weibook.event.ConversationMemberClickEvent;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Leon on 2016/5/27 0027.
 */
public class ConversationDetailItemHolder extends CommonViewHolder<LeanChatUser> {

	ImageView avatarView;
	TextView nameView;
	LeanChatUser leanChatUser;

	public ConversationDetailItemHolder(Context context, ViewGroup root) {
		super(context, root, R.layout.conversation_member_item);
		avatarView = (ImageView) itemView.findViewById(R.id.member_item_avatar);
		nameView = (TextView) itemView.findViewById(R.id.member_item_username);

		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new ConversationMemberClickEvent(leanChatUser.getObjectId(), false));
			}
		});

		itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				EventBus.getDefault().post(new ConversationMemberClickEvent(leanChatUser.getObjectId(), true));
				return true;
			}
		});
	}


	@Override
	public void bindData(LeanChatUser user) {
		leanChatUser = user;
		ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);
		nameView.setText(user.getUsername());
	}

	public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ConversationDetailItemHolder>() {
		@Override
		public ConversationDetailItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
			return new ConversationDetailItemHolder(parent.getContext(), parent);
		}
	};

}
