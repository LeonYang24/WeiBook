package com.leon.weibook.adapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.leon.weibook.holder.CommonFooterItemHolder;
import com.leon.weibook.holder.CommonHeaderItemHolder;
import com.leon.weibook.holder.CommonViewHolder;
import com.leon.weibook.views.HeaderLayout;

/**
 * Created by Leon on 2016/5/16 0016.
 */
public class HeaderListAdapter<T> extends CommonListAdapter<T> {

	public static final int HEADER_ITEM_TYPE = -1;//头部item类型
	public static final int FOOTER_ITEM_TYPE = -2;//尾部item类型
	public static final int COMMON_ITEM_TYPE = 1;//中间item类型

	private View headerView = null;//头部view
	private View footerView = null;//尾部view

	public HeaderListAdapter(Class<?> vhClass) {
		super(vhClass);
	}

	public void setHeaderView(View view) {
		headerView = view;
	}

	public void setFooterView(View view) {
		footerView = view;
	}

	@Override
	public int getItemCount() {
		int itemCount = super.getItemCount();

		if (null != headerView) {
			++itemCount;
		}
		if (null != footerView) {
			++itemCount;
		}

		return itemCount;
	}

	@Override
	public long getItemId(int position) {
		if (0 == position && position == getItemCount() - 1) {//只有一个
			return -1;
		}
		return super.getItemId(position);
	}

	@Override
	public int getItemViewType(int position) {
		if (null != headerView && 0 == position) {
			return HEADER_ITEM_TYPE;
		}

		if (null != footerView && position == getItemCount() - 1) {
			return FOOTER_ITEM_TYPE;
		}

		return COMMON_ITEM_TYPE;
	}

	/**
	 * 只有position在中间，才会被调用，最后会调用相关ViewHolder的bindData（）函数
	 * @param holder
	 * @param position
	 */
	@Override
	public void onBindViewHolder(CommonViewHolder holder, int position) {
		if (position == 0 && position == getItemCount() - 1) { //如果是头部或尾部，什么都不做
			return;
		}
		super.onBindViewHolder(holder, position);
	}

	@Override
	public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (HEADER_ITEM_TYPE == viewType) {
			CommonHeaderItemHolder itemHolder = new CommonHeaderItemHolder(parent.getContext(), parent);
			itemHolder.setView(headerView);
			return itemHolder;
		}

		if (FOOTER_ITEM_TYPE == viewType) {
			CommonFooterItemHolder itemHolder = new CommonFooterItemHolder(parent.getContext(), parent);
			itemHolder.setView(footerView);
			return itemHolder;
		}
		return super.onCreateViewHolder(parent, viewType);
	}
}
