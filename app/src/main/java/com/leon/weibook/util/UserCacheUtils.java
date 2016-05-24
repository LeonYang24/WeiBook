package com.leon.weibook.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.leon.weibook.model.LeanChatUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 该类是用户缓存工具类
 * 用户进行缓存、从缓存中提取用户等操作可通过该类完成
 * Created by Leon on 2016/5/16 0016.
 */
public class UserCacheUtils {

	private static Map<String, LeanChatUser> userMap;

	/**
	 * 静态块，用于初始化
	 */
	static {
		userMap = new HashMap<String, LeanChatUser>();
	}

	/**
	 * 获取已经缓存的用户
	 * @param objectId 用户objectId
	 * @return 有对应缓存用户则返回，否则返回null
	 */
	public static LeanChatUser getCachedUser(String objectId) {
		return userMap.get(objectId);
	}

	/**
	 * 判断缓存中是否存在用户
	 * @param objectId 用户objectId
	 * @return 存在则返回true，否则返回false
	 */
	public static boolean hasCachedUser(String objectId) {
		return userMap.containsKey(objectId);
	}

	/**
	 * 缓存用户
	 * @param user 要加入缓存的用户
	 */
	public static void cacheUser(LeanChatUser user) {
		if (null != user && !TextUtils.isEmpty(user.getObjectId())) {
			userMap.put(user.getObjectId(), user);
		}
	}

	/**
	 * 缓存用户list
	 * @param userList 要加入缓存的用户list
	 */
	public static void cacheUsers(List<LeanChatUser> userList) {
		if (null != userList) {
			for (LeanChatUser user : userList) {
				cacheUser(user);
			}
		}
	}

	/**
	 * 获取用户（缓存中没有的用户）
	 * @param ids
	 * @param cacheUserCallback
	 */
	public static void fetchUsers(final List<String> ids, final CacheUserCallback cacheUserCallback) {
		Set<String> uncachedIds = new HashSet<String>();
		for (String id : ids) {
			if (!userMap.containsKey(id)) { //如果userMap中不存在，就加入set中
				uncachedIds.add(id);
			}
		}

		if (uncachedIds.isEmpty()) {
			if (null != cacheUserCallback) {
				cacheUserCallback.done(getUsersFromCache(ids), null);
			}
		}

		AVQuery<LeanChatUser> q = LeanChatUser.getQuery(LeanChatUser.class);
		q.whereContainedIn(Constants.OBJECT_ID, uncachedIds);
		q.setLimit(1000);//设置结果返回数量，同一时间最大只能返回1000
		q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		q.findInBackground(new FindCallback<LeanChatUser>() {
			@Override
			public void done(List<LeanChatUser> list, AVException e) {
				if (null == e) {
					for (LeanChatUser user : list) {
						userMap.put(user.getObjectId(), user);
					}
				}
				if (null != cacheUserCallback) {
					cacheUserCallback.done(getUsersFromCache(ids), e);
				}
			}
		});
	}

	/**
	 * 用于从缓存中提取参数list中的用户
	 * @param ids
	 * @return
	 */
	public static List<LeanChatUser> getUsersFromCache(List<String> ids) {
		List<LeanChatUser> userList = new ArrayList<LeanChatUser>();
		for (String id : ids) {
			if (userMap.containsKey(id)) {
				userList.add(userMap.get(id));
			}
		}
		return userList;
	}

	public static abstract class CacheUserCallback {
		public abstract void done(List<LeanChatUser> userList, Exception e);
	}

}
