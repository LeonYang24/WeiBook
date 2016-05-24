package com.leon.weibook.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.weibook.R;
import com.leon.weibook.activity.ContactPersonInfoActivity;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Leon on 2016/5/16 0016.
 */
public class SearchUserItemHolder extends CommonViewHolder<LeanChatUser> {

	private TextView nameView;
	private ImageView avatarView;
	private LeanChatUser leanchatUser;

	public SearchUserItemHolder(Context context, ViewGroup root) {
		super(context, root, R.layout.search_user_item_layout);

		nameView = (TextView)itemView.findViewById(R.id.search_user_item_tv_name);
		avatarView = (ImageView)itemView.findViewById(R.id.search_user_item_im_avatar);

		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), ContactPersonInfoActivity.class);
				intent.putExtra(Constants.LEANCHAT_USER_ID, leanchatUser.getObjectId());
				getContext().startActivity(intent);
			}
		});
	}

	@Override
	public void bindData(final LeanChatUser leanchatUser) {
		this.leanchatUser = leanchatUser;
		ImageLoader.getInstance().displayImage(leanchatUser.getAvatarUrl(),
				                               avatarView,
											   PhotoUtils.avatarImageOptions);
		nameView.setText(leanchatUser.getUsername());
	}

	public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<SearchUserItemHolder>() {
		@Override
		public SearchUserItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
			return new SearchUserItemHolder(parent.getContext(), parent);
		}
	};

}
