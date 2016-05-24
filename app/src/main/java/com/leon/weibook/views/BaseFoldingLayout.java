package com.leon.weibook.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Leon on 2016/5/23 0023.
 */
public class BaseFoldingLayout extends ViewGroup {

	public static enum Orientation {
		VERTICAL, HORIZONTAL
	}

	public BaseFoldingLayout(Context context) {
		super(context);
	}

	public BaseFoldingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public BaseFoldingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public void init(Context context, AttributeSet attrs) {

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}
}
