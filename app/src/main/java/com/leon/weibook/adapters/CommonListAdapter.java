package com.leon.weibook.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.leon.weibook.holder.CommonViewHolder;
import com.leon.weibook.holder.CommonViewHolder.ViewHolderCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 该类是所有ListAdapter的父类
 *
 * Created by Leon on 2016/5/16 0016.
 */
public class CommonListAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

	private static HashMap<String, ViewHolderCreator> creatorHashMap = new HashMap<>();

	private Class<?> vhClass;

	protected List<T> dataList = new ArrayList<T>();

	public CommonListAdapter() {}

	public CommonListAdapter(Class<?> vhClass) {
		this.vhClass = vhClass;
	}

	/**
	 * 获取数据列表
	 * @return
	 */
	public List<T> getDataList() {
		return dataList;
	}

	/**
	 * 设置数据列表
	 * TODO 会先清除原先数据
	 * @param datas
	 */
	public void setDataList(List<T> datas) {
		dataList.clear();
		if (null != datas) {
			dataList.addAll(datas);
		}
	}

	/**
	 * 增加数据
	 * 不清除数据，默认在最后增加
	 * @param datas
	 */
	public void addDataList(List<T> datas) {
		dataList.addAll(datas);
	}

	/**
	 * Returns the total number of items in the data set hold by the adapter.
	 * @return The total number of items in this adapter.
	 * TODO 注意，返回值必须大于0，RecyclerView才会去刷新ViewHolder并bindViewHolder
	 */
	@Override
	public int getItemCount() {
		return dataList.size();
	}

	/**
	 * Called by RecyclerView to display the data at the specified position.
	 * @param holder
	 * @param position
	 */
	@Override
	public void onBindViewHolder(CommonViewHolder holder, int position) {
		if (position >= 0 && position < dataList.size()) {
			holder.bindData(dataList.get(position));
		}
	}

	/**
	 * Called when RecyclerView needs a new ViewHolder of the given type to represent
	 * an item.
	 * @param parent
	 * @param viewType
	 * @return
	 */
	@Override
	public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (null == vhClass) {
			try {
				throw new IllegalArgumentException("Please use CommonListAdapter(Class<?> vhClass)");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ViewHolderCreator<?> creator = null;
		if (creatorHashMap.containsKey(vhClass.getName())) {
			creator = creatorHashMap.get(vhClass.getName());
		} else {
			try {
				creator = (ViewHolderCreator)vhClass.getField("HOLDER_CREATOR").get(null);
				creatorHashMap.put(vhClass.getName(), creator);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}

		if (null != creator) {
			return creator.createByViewGroupAndType(parent, viewType);
		} else {
			throw new IllegalArgumentException(vhClass.getName() +
					                           " HOLDER_CREATOR should be instantiated");
		}
	}


}
