package com.leon.weibook.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.leon.weibook.R;

/**
 * Created by Leon on 2016/5/16 0016.
 */
public class CommonHeaderItemHolder extends CommonViewHolder {

	LinearLayout rootLayout;

	public CommonHeaderItemHolder(Context context, ViewGroup root) {
		super(context, root, R.layout.common_header_item_layout);
		rootLayout = (LinearLayout)itemView.findViewById(R.id.common_header_root_view);
	}

	public void setView(View view) {
		rootLayout.removeAllViews();
		if (null != view) {
			rootLayout.addView(view);
		}
	}

	@Override
	public void bindData(Object o) {

	}
}
