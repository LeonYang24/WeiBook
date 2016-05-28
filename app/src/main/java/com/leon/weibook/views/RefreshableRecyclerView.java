package com.leon.weibook.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.leon.weibook.adapters.HeaderListAdapter;
import com.leon.weibook.util.Utils;

import java.util.Arrays;

/**
 * 自定义的一个RecyclerView
 *
 * 1、支持下拉刷新（下拉刷新需要配合 SwipeRefreshLayout 使用，需要在初始化 RefreshableRecyclerView后
 *                 调用 setRelationSwipeLayout 来设置关联）
 *
 * Created by Leon on 2016/5/16 0016.
 */
public class RefreshableRecyclerView extends RecyclerView {

	private final int DEFAULT_PAGE_NUM = 5;
	public static int STATUS_NORMAL = 0;
	public static int STATUS_LAOD_MORE = 2;

	public final double VISIBLE_SCALE = 0.75;

	private int pageNum = DEFAULT_PAGE_NUM;
	private int loadStatus = STATUS_NORMAL;
	public boolean enableLoadMore = true;

	private SwipeRefreshLayout swipeRefreshLayout;
	private LoadMoreFooterView loadMoreFooterView;
	private OnLoadDataListener onLoadDataListener;

	public RefreshableRecyclerView(Context context) {
		super(context);
		initView();
	}

	public RefreshableRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public RefreshableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	/**
	 *
	 */
	private void initView() {
		loadMoreFooterView = new LoadMoreFooterView(getContext());
		loadMoreFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (enableLoadMore && STATUS_LAOD_MORE != getLoadStatus()) {
					startLoad();
				}
			}
		});

		/**
		 * recyclerView 滚动时的监听器
		 */
		addOnScrollListener(new OnScrollListener() {
			@Override
			/**
			 * @param recyclerView The RecyclerView which scrolled.
			 * @param dx The amount of horizontal scroll.
			 * @param dy The amount of vertical scroll.
			 */
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (enableLoadMore && STATUS_LAOD_MORE != getLoadStatus()) {
					LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
					int totalItemCount = layoutManager.getItemCount();
					int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
					if (lastVisibleItem == totalItemCount - 1) {
						View view = layoutManager.findViewByPosition(lastVisibleItem);
						Rect rect = new Rect();
						//getGlobalVisibleRect方法的作用是获取视图在屏幕坐标系中的偏移量
						view.getGlobalVisibleRect(rect);
						if (rect.height() / view.getHeight() > VISIBLE_SCALE) {//上拉大于一定比例就加载
							startLoad();
						}
					}
				}
			}
		});
	}

	/**
	 * 设置关联的 SwipeRefreshLayout，下拉刷新时使用
	 * @param relationSwipeLayout
	 */
	public void setRelationSwipeLayout(SwipeRefreshLayout relationSwipeLayout) {
		swipeRefreshLayout = relationSwipeLayout;
		if (null != swipeRefreshLayout) {
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					refreshData();
				}
			});
		} else {
			throw new IllegalArgumentException("SwipeRefreshLayout can not be null");
		}
	}

	/**
	 * 用于设置适配器，需要配合 HeaderListAdapter使用
	 * 这里会为界面添加一个 loadMoreFooterView（“查看更多”）
	 * @param adapter
	 */
	@Override
	public void setAdapter(Adapter adapter) {
		super.setAdapter(adapter);
		if (null != adapter) {
			if (adapter instanceof HeaderListAdapter) {
				((HeaderListAdapter)adapter).setFooterView(loadMoreFooterView);
			} else {
				throw new IllegalArgumentException("adapter should be HeaderListAdapter");
			}
		} else {
			throw new IllegalArgumentException("adapter can not be null");
		}
	}

	/**
	 * 获取适配器adapter
	 * @return
	 */
	@Override
	public HeaderListAdapter getAdapter() {
		return (HeaderListAdapter)super.getAdapter();
	}

	/**
	 * 设置加载页的大小，默认为 DEFAULT_PAGE_NUM
	 * @param pageNum
	 */
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * 设置数据加载时的监听器，注意是加载时调用，并非完成后调用
	 * @param loadDataListener
	 */
	public void setOnLoadDataListener(OnLoadDataListener loadDataListener) {
		onLoadDataListener = loadDataListener;
	}

	/**
	 * 刷新数据
	 * TODO 调用该函数之前，需先保证setOnLoadDataListener得到了调用
	 */
	public void refreshData() {
		if (null != onLoadDataListener) {
			onLoadDataListener.onLoad(0, pageNum, true);
		} else {
			Utils.toast("Have to call the method setOnLoadDataListener");
		}
	}

	/**
	 * 开始加载
	 */
	private void startLoad() {
		if (STATUS_LAOD_MORE != getLoadStatus()) {
			HeaderListAdapter adapter = getAdapter();
			if (null != onLoadDataListener && null != adapter) {
				setLoadStatus(STATUS_LAOD_MORE);
				onLoadDataListener.onLoad(adapter.getDataList().size(), pageNum, false);
			} else {
				setLoadStatus(STATUS_NORMAL);
			}
		}
	}

	/**
	 * 设置是否可用上滑加载
	 * @param enable
	 */
	public void setEnableLoadMore(boolean enable) {
		enableLoadMore = enable;
	}

	/**
	 * 设置加载状态 通过状态来决定是否显示 loadMoreFooterView
	 * @param status
	 */
	private void setLoadStatus(int status) {
		loadStatus = status;
		loadMoreFooterView.onLoadStatusChanged(status);
	}

	/**
	 * 设置刷新完毕
	 */
	public void setLoadComplete() {
		setLoadStatus(STATUS_NORMAL);
		if (null != swipeRefreshLayout) {
			swipeRefreshLayout.setRefreshing(false);
		}
	}

	/**
	 * 获取加载状态
	 * @return
	 */
	public int getLoadStatus() {
		return loadStatus;
	}

	/**
	 * 设置刷新后的数据
	 * @param datas 用于显示的数据
	 * @param isRefresh 如果 isRefresh 为 true，则清空所有数据，设置为 datas
	 *                  如果 isRefresh 为 false，则把 datas 叠加到现有数据中
	 */
	public void setLoadComplete(Object[] datas, boolean isRefresh) {
		setLoadStatus(STATUS_NORMAL);
		HeaderListAdapter adapter = getAdapter();//获取本View的adapter
		if (null != adapter) {
			if (isRefresh) {
				adapter.setDataList(Arrays.asList(datas));
				adapter.notifyDataSetChanged();
				if (null != swipeRefreshLayout) {
					swipeRefreshLayout.setRefreshing(false);
				}
			} else {
				adapter.addDataList(Arrays.asList(datas));
				adapter.notifyDataSetChanged();
			}
		}

	}

	/**
	 * 加载数据监听器接口
	 */
	public interface OnLoadDataListener {
		/**
		 * 加载时调用的函数
		 * @param skip 设置跳过多少数据 默认是0
		 * @param limit 设置数据获取数量
		 * @param isRefresh 是否刷新
		 */
		public void onLoad(int skip, int limit, boolean isRefresh);
	}
}
