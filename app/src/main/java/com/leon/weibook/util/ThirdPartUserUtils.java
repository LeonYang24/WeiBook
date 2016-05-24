package com.leon.weibook.util;

import java.util.Arrays;
import java.util.List;

/**
 * 第三方用户工具类
 * Created by Leon on 2016/5/14 0014.
 */
public class ThirdPartUserUtils {

	private static ThirdPartUserUtils userUtils;
	private static ThirdPartDataProvider thirdPartDataProvider;

	private ThirdPartUserUtils() {}

	/**
	 * 获取ThirdPartUserUtils类唯一实例
	 * @return
	 */
	public static synchronized ThirdPartUserUtils getInstance() {
		if (null == userUtils) {
			userUtils = new ThirdPartUserUtils();
		}
		return userUtils;
	}

	/**
	 * 设置第三方用于数据源
	 * @param provider
	 */
	public static void setThirdPartUserProvider(ThirdPartDataProvider provider) {
		thirdPartDataProvider= provider;
	}

	/**
	 * 获取第三方数据源
	 * @return
	 */
	public ThirdPartUser getSelf() {
		checkDataProvider();
		return thirdPartDataProvider.getSelf();
	}

	/**
	 * 检测内部第三方数据源变量是否为空
	 */
	private void checkDataProvider() {
		if (null == thirdPartDataProvider) {
			throw new NullPointerException("thirdPartDataProvider is null，" +
					                       "please setThirdPartUserProvider first!");
		}
	}

	public String getUserName(String userId) {
		ThirdPartUser user = getFriend(userId);
		return (null != user ? user.name : "");
	}

	public String getUserAvatar(String userId) {
		ThirdPartUser user = getFriend(userId);
		return (null != user ? user.avatarUrl : "");
	}

	/**
	 * 获取好友
	 * 首先检测缓存中是否有好友存在，否则
	 * @param uesrId
	 * @return
	 */
	private ThirdPartUser getFriend(String uesrId) {
		checkDataProvider();
		if (ThirdPartDataCache.getInstance().hasCachedUser(uesrId)) {
			return ThirdPartDataCache.getInstance().getCacheUser(uesrId);
		} else {
			refreshUserData(Arrays.asList(uesrId));
			return null;
		}
	}

	public void getFriends(int skip, int limit, final FetchUserCallBack callBack) {
		checkDataProvider();
		thirdPartDataProvider.getFriends(skip, limit, new FetchUserCallBack() {
			@Override
			public void done(List<ThirdPartUser> userList, Exception e) {
				if (null == e && null != userList) {
					for (ThirdPartUser user : userList) {
						ThirdPartDataCache.getInstance().cacheUser(user.userId, user);
					}
				}
				callBack.done(userList, e);
			}
		});
	}

	/**
	 *
	 * @param userList
	 */
	public void refreshUserData(List<String> userList) {
		thirdPartDataProvider.getFriends(userList, new FetchUserCallBack() {
			@Override
			public void done(List<ThirdPartUser> userList, Exception e) {
				if (null == e && null != userList) {
					for (ThirdPartUser user : userList) {
						ThirdPartDataCache.getInstance().cacheUser(user.userId, user);
					}
				}
			}
		});
	}

	/**
	 * 第三方用户类
	 */
	public static class ThirdPartUser {
		public String userId;
		public String avatarUrl;
		public String name;

		public ThirdPartUser(String id, String name, String avatar) {
			userId = id;
			this.name = name;
			avatarUrl = avatar;
		}
	}

	public interface ThirdPartDataProvider {
		public ThirdPartUser getSelf();
		public void getFriend(String userId, FetchUserCallBack callBack);
		public void getFriends(List<String> list, FetchUserCallBack callBack);
		public void getFriends(int skip, int limit, FetchUserCallBack callBack);
	}

	public interface FetchUserCallBack {
		public void done(List<ThirdPartUser> userList, Exception e);
	}

}
