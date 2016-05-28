package com.leon.weibook.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.leon.weibook.App;
import com.leon.weibook.R;
import com.leon.weibook.adapters.HeaderListAdapter;
import com.leon.weibook.holder.SearchUserItemHolder;
import com.leon.weibook.model.FriendsManager;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.UserCacheUtils;
import com.leon.weibook.views.RefreshableRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * “查找好友”界面
 * Created by Leon on 2016/5/16 0016.
 */
public class ContactAddFriendActivity extends AVBaseActivity{

	@BindView(R.id.search_user_rv_layout)
	protected RefreshableRecyclerView recyclerView;

	@BindView(R.id.searchNameEdit)
	EditText searchNameEdit;

	private HeaderListAdapter<LeanChatUser> adapter;
	private String searchName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_add_friend_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		init();
		//recyclerView.refreshData();
	}

	private void init() {
		setTitle(App.ctx.getString(R.string.activity_add_friend_title));
		adapter = new HeaderListAdapter<>(SearchUserItemHolder.class);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setOnLoadDataListener(new RefreshableRecyclerView.OnLoadDataListener() {
			@Override
			public void onLoad(int skip, int limit, boolean isRefresh) {
				loadMoreFriend(skip, limit, isRefresh);
			}
		});
		recyclerView.setAdapter(adapter);
	}

	/**
	 * 加载更多朋友，该函数只有在OnLoadDataListener被调用后才会调用
	 * @param skip
	 * @param limit
	 * @param isRefresh
	 */
	private void loadMoreFriend(int skip, final int limit, final boolean isRefresh) {

		Log.i("test", "调用 loadMoreFriend");

		AVQuery<LeanChatUser> q = LeanChatUser.getQuery(LeanChatUser.class);
		q.whereContains(LeanChatUser.USERNAME, searchName);
		q.limit(Constants.PAGE_SIZE);
		q.skip(skip);
		LeanChatUser user = LeanChatUser.getCurrentUser();
		List<String> friendIds = new ArrayList<String>(FriendsManager.getFriendIds());
		friendIds.add(user.getObjectId());
		q.whereNotContainedIn(Constants.OBJECT_ID, friendIds);
		q.orderByDescending(Constants.UPDATED_AT);
		q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		q.findInBackground(new FindCallback<LeanChatUser>() {
			@Override
			public void done(List<LeanChatUser> list, AVException e) {
				UserCacheUtils.cacheUsers(list);
				recyclerView.setLoadComplete(list.toArray(), true);

			}
		});
	}

	@OnClick(R.id.search_Friend_Btn)
	public void search(View view) {
		searchName = searchNameEdit.getText().toString();
		recyclerView.refreshData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
