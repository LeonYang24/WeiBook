package com.leon.weibook.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leon.weibook.R;

/**
 * Created by Leon on 2016/5/15 0015.
 */
public class HeaderLayout extends LinearLayout{

	LayoutInflater inflater;
	RelativeLayout header;
	TextView titleView;
	LinearLayout leftContainer, rightContainer;
	Button backBtn;

	public HeaderLayout(Context context) {
		super(context);
		init();
	}

	public HeaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 初始化相关参数，同时将chat_common_base_header该布局文件加入到HeaderLayout中
	 */
	private void init() {
		inflater = LayoutInflater.from(getContext());
		header = (RelativeLayout) inflater.inflate(R.layout.chat_common_base_header, null, false);
		titleView = (TextView) header.findViewById(R.id.titleView);
		leftContainer = (LinearLayout) header.findViewById(R.id.leftContainer);
		rightContainer = (LinearLayout) header.findViewById(R.id.rightContainer);
		backBtn = (Button) header.findViewById(R.id.backBtn);
		addView(header);
	}

	public void setTitle(int titleId) {
		titleView.setText(titleId);
	}

	public void setTitle(String s) {
		titleView.setText(s);
	}

	public void showLeftBackButton(OnClickListener listener) {
		showLeftBackButton(R.string.chat_common_emptyStr, listener);
	}

	public void showLeftBackButton(int backTextId, OnClickListener listener) {
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setText(backTextId);
		if (listener == null) {
			listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					((Activity) getContext()).finish();
				}
			};
		}
		backBtn.setOnClickListener(listener);//如果listener为空，点击则结束activity
	}

	public void showRightImageButton(int rightResId, OnClickListener listener) {
		View imageViewLayout = inflater.inflate(R.layout.chat_common_base_header_right_image_btn,
												null, false);
		ImageButton rightButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
		rightButton.setImageResource(rightResId);
		rightButton.setOnClickListener(listener);
		rightContainer.addView(imageViewLayout);
	}

	public void showRightTextButton(int rightResId, OnClickListener listener) {
		View imageViewLayout = inflater.inflate(R.layout.chat_common_base_header_right_btn,
												null, false);
		Button rightButton = (Button) imageViewLayout.findViewById(R.id.textBtn);
		rightButton.setText(rightResId);
		rightButton.setOnClickListener(listener);
		rightContainer.addView(imageViewLayout);
	}

}
