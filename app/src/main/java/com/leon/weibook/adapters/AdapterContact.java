package com.leon.weibook.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.leon.weibook.R;
import com.leon.weibook.event.ContactItemClickEvent;
import com.leon.weibook.event.ContactItemLongClickEvent;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Leon on 2016/5/18 0018.
 */
public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ViewHolder> {

	/**
	 * 在有序 memberList 中 MemberItem.sortContent 第一次出现时的字母与位置的 map
	 */
	private Map<Character, Integer> indexMap = new HashMap<Character, Integer>();

	/**
	 * 简体中文的 Collator
	 */
	Collator cmp = Collator.getInstance(Locale.SIMPLIFIED_CHINESE);

	protected List<ContactItem> dataList = new ArrayList<ContactItem>();

	public class SortChineseName implements Comparator<ContactItem> {
		@Override
		public int compare(ContactItem str1, ContactItem str2) {
			if (null == str1) {
				return -1;
			}
			if (null == str2) {
				return 1;
			}
			if (cmp.compare(str1.sortContent, str2.sortContent) > 0) {
				return 1;
			}else if (cmp.compare(str1.sortContent, str2.sortContent) < 0) {
				return -1;
			}
			return 0;
		}
	}

	/**
	 * 获取索引 Map
	 */
	public Map<Character, Integer> getIndexMap() {
		return indexMap;
	}

	/**
	 * 更新索引 Map
	 */
	private Map<Character, Integer> updateIndex(List<ContactItem> list) {
		Character lastCharcter = '#';
		Map<Character, Integer> map = new HashMap<>();
		for (int i = 0; i < list.size(); i++) {
			Character curChar = Character.toLowerCase(list.get(i).sortContent.charAt(0));
			if (!lastCharcter.equals(curChar)) {
				map.put(curChar, i);
			}
			lastCharcter = curChar;
		}
		return map;
	}

	/**
	 * 设置成员列表，然后更新索引
	 * 此处会对数据以 空格、数字、字母（汉字转化为拼音后的字母） 的顺序进行重新排列
	 */
	public void setUserList(List<LeanChatUser> list) {
		List<ContactItem> contactList = new ArrayList<>();
		if (null != list) {
			for (LeanChatUser user : list) {
				ContactItem item = new ContactItem();
				item.user = user;
				item.sortContent = PinyinHelper.convertToPinyinString(user.getUsername(), "",
						PinyinFormat.WITHOUT_TONE);
				contactList.add(item);
			}
		}
		Collections.sort(contactList, new SortChineseName());
		indexMap = updateIndex(contactList);
		updateInitialsVisible(contactList);
		dataList.clear();
		dataList.addAll(contactList);
		//super.setDataList(contactList);
	}

	/**
	 * 必须要排完序后，否则没意义
	 * @param list
	 */
	private void updateInitialsVisible(List<ContactItem> list) {
		if (null != list && list.size() > 0) {
			char lastInitial = ' ';
			for (ContactItem item : list) {
				if (!TextUtils.isEmpty(item.sortContent)) {
					item.initialVisible = (lastInitial != item.sortContent.charAt(0));
					lastInitial = item.sortContent.charAt(0);
				} else {
					item.initialVisible = true;
					lastInitial = ' ';
				}
			}
		}
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}

	private ContactItem contactItem;

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Log.i("test", "onBindViewHolder");
		contactItem = dataList.get(position);
		holder.alpha.setVisibility(contactItem.initialVisible ? View.VISIBLE : View.GONE);
		holder.alpha.setText(String.valueOf(Character.toUpperCase(contactItem.sortContent.charAt(0))));
		ImageLoader.getInstance().displayImage(contactItem.user.getAvatarUrl(),
				holder.avatarView, PhotoUtils.avatarImageOptions);
		holder.nameView.setText(contactItem.user.getUsername());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Log.i("test", "onCreateViewHolder");
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_user_item,
																     parent, false);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		TextView alpha;
		TextView nameView;
		ImageView avatarView;

		public ViewHolder(View view) {
			super(view);
			alpha = (TextView)itemView.findViewById(R.id.alpha);
			nameView = (TextView)itemView.findViewById(R.id.tv_friend_name);
			avatarView = (ImageView)itemView.findViewById(R.id.img_friend_avatar);

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new ContactItemClickEvent(contactItem.user.getObjectId()));
				}
			});

			itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					EventBus.getDefault().post(new ContactItemLongClickEvent(contactItem.user.getObjectId()));
					return true;
				}
			});
		}
	}

	public static class ContactItem {
		public LeanChatUser user;
		public String sortContent;
		public boolean initialVisible;
	}

}
