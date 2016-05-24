package com.leon.weibook.holder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.weibook.R;
import com.leon.weibook.event.NewFriendItemClickEvent;
import com.leon.weibook.model.AddRequest;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Leon on 2016/5/17 0017.
 */
public class NewFriendItemHolder extends CommonViewHolder<AddRequest> {

	TextView nameView;
	ImageView avatarView;
	Button addBtn;
	View agreedView;
	private AddRequest addRequest;

	public NewFriendItemHolder(Context context, ViewGroup root) {
		super(context, root, R.layout.contact_add_friend_item);
		Log.i("test", "into NewFriendItemHolder --NewFriendItemHolder");
		nameView = (TextView)itemView.findViewById(R.id.name);
		avatarView = (ImageView)itemView.findViewById(R.id.avatar);
		addBtn = (Button)itemView.findViewById(R.id.add);
		agreedView = itemView.findViewById(R.id.agreedView);

		itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				EventBus.getDefault().post(new NewFriendItemClickEvent(addRequest, true));
				return true;
			}
		});

		addBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EventBus.getDefault().post(new NewFriendItemClickEvent(addRequest, false));
			}
		});
	}

	@Override
	public void bindData(final AddRequest addRequest) {

		Log.i("test", "into bindData --NewFriendItemHolder");
		this.addRequest = addRequest;
		LeanChatUser from = addRequest.getFromUser();
		ImageLoader.getInstance().displayImage(from.getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);
		if (from != null) {
			nameView.setText(from.getUsername());
		}
		int status = addRequest.getStatus();
		if (status == AddRequest.STATUS_WAIT) {
			addBtn.setVisibility(View.VISIBLE);
			agreedView.setVisibility(View.GONE);
		} else if (status == AddRequest.STATUS_DONE) {
			addBtn.setVisibility(View.GONE);
			agreedView.setVisibility(View.VISIBLE);
		}
	}

	public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<NewFriendItemHolder>() {
		@Override
		public NewFriendItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
			return new NewFriendItemHolder(parent.getContext(), parent);
		}
	};
}
