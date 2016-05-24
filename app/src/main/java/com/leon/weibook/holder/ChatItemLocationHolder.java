package com.leon.weibook.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.leon.weibook.R;
import com.leon.weibook.event.LocationItemClickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Leon on 2016/5/19 0019.
 */
public class ChatItemLocationHolder extends ChatItemHolder {

	protected TextView contentView;

	public ChatItemLocationHolder(Context context, ViewGroup root, boolean isLeft) {
		super(context, root, isLeft);
	}

	@Override
	public void initView() {
		super.initView();
		conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_location, null));
		contentView = (TextView)itemView.findViewById(R.id.locationView);
		conventLayout.setBackgroundResource(isLeft ? R.drawable.chat_left_qp : R.drawable.chat_right_qp);
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocationItemClickEvent event = new LocationItemClickEvent();
				event.message = message;
				EventBus.getDefault().post(event);
			}
		});
	}

	@Override
	public void bindData(Object o) {
		super.bindData(o);
		AVIMMessage message = (AVIMMessage)o;
		if (message instanceof AVIMLocationMessage) {
			final AVIMLocationMessage locMsg = (AVIMLocationMessage) message;
			contentView.setText(locMsg.getText());
		}
	}
}