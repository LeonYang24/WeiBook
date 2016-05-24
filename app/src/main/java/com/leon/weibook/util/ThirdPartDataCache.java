package com.leon.weibook.util;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方用户数据缓存
 * Created by Leon on 2016/5/14 0014.
 */
public class ThirdPartDataCache {

	private Map<String, ThirdPartUserUtils.ThirdPartUser> userMap;

	public static ThirdPartDataCache thirdPartDataCache;

	private ThirdPartDataCache() {
		userMap = new HashMap<>();
	}

	/**
	 * 获得该类唯一实例
	 * @return
	 */
	public static ThirdPartDataCache getInstance() {
		if (null == thirdPartDataCache) {
			thirdPartDataCache = new ThirdPartDataCache();
		}
		return thirdPartDataCache;
	}

	/**
	 * 获取缓存用户
	 * @param objectId 通过该参数来查找缓存map里的用户
	 * @return 返回用户
	 */
	public ThirdPartUserUtils.ThirdPartUser getCacheUser(String objectId) {
		return userMap.get(objectId);
	}

	/**
	 * 判断缓存中是否存在相应用户
	 * @param id
	 * @return
	 */
	public boolean hasCachedUser(String id) {
		return userMap.containsKey(id);
	}

	/**
	 * 将用户存入缓存中
	 * @param id 用户id
	 * @param user 用户
	 */
	public void cacheUser(String id, ThirdPartUserUtils.ThirdPartUser user) {
		if (!TextUtils.isEmpty(id) && null != user) {
			userMap.put(id, user);
		}
	}

}
