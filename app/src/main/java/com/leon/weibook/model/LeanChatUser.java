package com.leon.weibook.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的AVUser
 * Created by Leon on 2016/5/13 0013.
 */
public class LeanChatUser extends AVUser {

	public static final String USERNAME = "username";
	public static final String AVATAR = "avatar";
	public static final String LOCATION = "location";
	public static final String INSTALLATION = "installation";

	/**
	 * 获取当前用户的ID
	 * 每个用户都有一个唯一的objectId
	 * @return 当前用户不为空则返回objectId，否则返回null
	 */
	public static String getCurrentUserId() {
		LeanChatUser currentUser = getCurrentUser(LeanChatUser.class);
		return (null != currentUser ? currentUser.getObjectId() : null);
	}

	/**
	 * 获取用户的头像路径
	 * @return 如果头像路径存在，则返回路径，否则返回null
	 */
	public String getAvatarUrl() {
		AVFile avatar = getAVFile(AVATAR);
		if (null != avatar) {
			return avatar.getUrl();
		} else {
			return null;
		}
	}

	/**
	 * 保存用户头像
	 * @param path  当前本地头像的路径
	 * @param saveCallback 上传成功或失败时执行的回调函数
	 */
	public void saveAvatar(String path, final SaveCallback saveCallback) {
		final  AVFile file;
		try {
			file = AVFile.withAbsoluteLocalPath(getUsername(), path);//从本地路径构建文件，执行上传
			put(AVATAR, file);//在云端用户表中添加avatar列
			file.saveInBackground(new SaveCallback() {
				@Override
				public void done(AVException e) {
					if (null == e) {//上传成功，则执行传进来的SaveCallback函数
						saveInBackground(saveCallback);
					} else {//上传出现异常，如果传进来的SaveCallback函数不为空则执行
						if (null != saveCallback) {
							saveCallback.done(e);
						}
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前用户
	 * @return The currently logged in AVUser subclass instance.
	 */
	public static LeanChatUser getCurrentUser() {
		return getCurrentUser(LeanChatUser.class);
	}

	/**
	 *更新用户信息（配置）
	 */
	public void updateUserInfo() {
		AVInstallation installation = AVInstallation.getCurrentInstallation();
		if (null != installation) {
			put(INSTALLATION, installation);
			saveInBackground();
		}
	}

	/**
	 * 通过LeanCloud SDK中的线程登录API登录用户
	 * @param name 用户名
	 * @param password 用户密码
	 * @param callback 用户登录完成回调
	 */
	public static void signUpByNameAndPwd(String name, String password, SignUpCallback callback) {
		AVUser user = new AVUser();
		user.setUsername(name);
		user.setPassword(password);
		user.signUpInBackground(callback);
	}

	/**
	 * 删除好友，同时也在服务端将好友ID删除
	 * @param friendId
	 * @param saveCallback
	 */
	public void removeFriend(String friendId, final SaveCallback saveCallback) {
		unfollowInBackground(friendId, new FollowCallback() {
			@Override
			public void done(AVObject avObject, AVException e) {
				if (null != saveCallback) {
					saveCallback.done(e);
				}
			}
		});
	}

	/**
	 * followeeQuery用于创建followee（关注人）查询。请确保传入的userObjectId不为空，否则会抛出
	 * IllegalArgumentException。
	 * 创建followee查询后，您可以使用whereEqualTo("followee", userFollowee)查询特定的followee。
	 * 您也可以使用skip和limit支持分页操作。
	 * @param cachePolicy
	 * @param findCallback
	 */
	public void findFriendsWithCachePolicy(AVQuery.CachePolicy cachePolicy, FindCallback<LeanChatUser>
			findCallback) {
		AVQuery<LeanChatUser> q = null;
		try {
			q = followeeQuery(LeanChatUser.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		q.setCachePolicy(cachePolicy);
		q.setMaxCacheAge(TimeUnit.MINUTES.toMillis(1));//缓存最大值1000
		q.findInBackground(findCallback);
	}

}
