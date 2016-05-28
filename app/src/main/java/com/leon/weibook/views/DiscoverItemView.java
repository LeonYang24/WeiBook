package com.leon.weibook.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leon.weibook.R;

/**
 * "发现"页每个Item的自定义View
 * Created by Leon on 2016/5/25 0025.
 */
public class DiscoverItemView extends LinearLayout {
	LayoutInflater inflater;
	LinearLayout item;
	ImageView icon;
	public ImageView iconMsgTip;
	TextView iconName;

	public DiscoverItemView(Context context) {
		super(context);
		init();
	}

	public DiscoverItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DiscoverItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public void init() {
		inflater = LayoutInflater.from(getContext());
		item = (LinearLayout) inflater.inflate(R.layout.common_item_layout, null, false);
		icon = (ImageView) item.findViewById(R.id.iv__item_icon);
		iconName = (TextView) item.findViewById(R.id.tv_item_name);
		iconMsgTip = (ImageView) item.findViewById(R.id.iv_item_msgtips);
		addView(item);
	}

	public void setIcon(int iconId) {
		icon.setBackgroundResource(iconId);
	}

	public void setIconName(String name) {
		iconName.setText(name);
	}

}
