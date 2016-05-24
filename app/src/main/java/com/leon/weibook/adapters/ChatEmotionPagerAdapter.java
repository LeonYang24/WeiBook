package com.leon.weibook.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天表情显示页面的适配器
 * Created by Leon on 2016/5/18 0018.
 */
public class ChatEmotionPagerAdapter extends PagerAdapter {

	private List<View> views = new ArrayList<View>();

	public ChatEmotionPagerAdapter(List<View> views) {
		this.views = views;
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position));
		return views.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}

}
