package com.leon.weibook.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.leon.weibook.util.UserCacheUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2016/5/16 0016.
 */
public class FriendsManager {

	private static volatile List<String> friendIds = new ArrayList<String>();

	public static List<String> getFriendIds() {
		return friendIds;
	}

	/**
	 * 加入朋友id
	 * Attention：该函数会将之前的朋友id全部clear
	 * @param friendList
	 */
	public static void setFriendIds(List<String> friendList) {
		friendIds.clear();
		if (null != friendList) {
			friendIds.addAll(friendList);
		}
	}

	/**
	 * 获取朋友Id
	 * @param isForce
	 * @param findCallback
	 */
	public static void fetchFriends(boolean isForce,
									final FindCallback<LeanChatUser> findCallback) {
		AVQuery.CachePolicy policy = (isForce ? AVQuery.CachePolicy.NETWORK_ELSE_CACHE
				                              : AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
		LeanChatUser.getCurrentUser().findFriendsWithCachePolicy(policy,
										                         new FindCallback<LeanChatUser>() {
			@Override
			public void done(List<LeanChatUser> list, AVException e) {
				if (null != e) { //发生异常错误
					findCallback.done(list, e);
				} else {
					final List<String> userIds = new ArrayList<String>();
					for (LeanChatUser user : list) {
						userIds.add(user.getObjectId());
					}
					UserCacheUtils.fetchUsers(userIds, new UserCacheUtils.CacheUserCallback() {
						@Override
						public void done(List<LeanChatUser> userList, Exception e) {
							setFriendIds(userIds);
							findCallback.done(userList, null);
						}
					});
				}
			}
		});
	}

}
