package com.leon.weibook.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.leon.weibook.model.FriendsManager;
import com.leon.weibook.model.LeanChatUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Leon on 2016/5/21 0021.
 */
public class LeanChatUserProvider implements ThirdPartUserUtils.ThirdPartDataProvider  {

	@Override
	public ThirdPartUserUtils.ThirdPartUser getSelf() {
		return new ThirdPartUserUtils.ThirdPartUser("daweibayu", "daweibayu",
				"http://ac-x3o016bx.clouddn.com/CsaX0GuXL7gXWBkaBFXfBWZPlcanClEESzHxSq2T.jpg");
	}

	@Override
	public void getFriend(String userId, final ThirdPartUserUtils.FetchUserCallBack callBack) {
		if (UserCacheUtils.hasCachedUser(userId)) {
			callBack.done(Arrays.asList(getThirdPartUser(UserCacheUtils.getCachedUser(userId))), null);
		} else {
			UserCacheUtils.fetchUsers(Arrays.asList(userId), new UserCacheUtils.CacheUserCallback() {
				@Override
				public void done(List<LeanChatUser> userList, Exception e) {
					callBack.done(getThirdPartUsers(userList), e);
				}
			});
		}
	}

	@Override
	public void getFriends(List<String> list, final ThirdPartUserUtils.FetchUserCallBack callBack) {
		UserCacheUtils.fetchUsers(list, new UserCacheUtils.CacheUserCallback() {
			@Override
			public void done(List<LeanChatUser> userList, Exception e) {
				callBack.done(getThirdPartUsers(userList), e);
			}
		});
	}

	@Override
	public void getFriends(int skip, int limit, final ThirdPartUserUtils.FetchUserCallBack callBack) {
		FriendsManager.fetchFriends(false, new FindCallback<LeanChatUser>() {
			@Override
			public void done(List<LeanChatUser> list, AVException e) {
				callBack.done(getThirdPartUsers(list), e);
			}
		});
	}

	private static ThirdPartUserUtils.ThirdPartUser getThirdPartUser(LeanChatUser leanchatUser) {
		return new ThirdPartUserUtils.ThirdPartUser(leanchatUser.getObjectId(), leanchatUser.getUsername(), leanchatUser.getAvatarUrl());
	}

	public static List<ThirdPartUserUtils.ThirdPartUser> getThirdPartUsers(List<LeanChatUser> leanchatUsers) {
		List<ThirdPartUserUtils.ThirdPartUser> thirdPartUsers = new ArrayList<>();
		for (LeanChatUser user : leanchatUsers) {
			thirdPartUsers.add(getThirdPartUser(user));
		}
		return thirdPartUsers;
	}

}
