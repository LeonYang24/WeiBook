package com.leon.weibook.model;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.SaveCallback;
import com.leon.weibook.App;
import com.leon.weibook.R;
import com.leon.weibook.service.PushManager;
import com.leon.weibook.util.LogUtils;
import com.leon.weibook.util.SimpleNetTask;
import com.leon.weibook.util.Utils;

import java.util.List;

/**
 * 好友请求管理类
 * Created by Leon on 2016/5/17 0017.
 */
public class AddRequestManager {

	/* 用户端未读的邀请消息数量 */
	private int unreadAddRequestsCount = 0;

	private static AddRequestManager addRequestManager;

	public AddRequestManager() {}

	public static synchronized AddRequestManager getInstance() {
		if (null == addRequestManager) {
			addRequestManager = new AddRequestManager();
		}
		return addRequestManager;
	}

	/**
	 * 是否有未读的信息
	 * @return
	 */
	public boolean hasUnreadRequests() {
		return  unreadAddRequestsCount > 0;
	}

	/**
	 * 推送过来时自增
	 */
	public void unreadRequestsIncrement() {
		++unreadAddRequestsCount;
	}

	/**
	 * 从server获取未读消息的数量
	 * @param countCallback
	 */
	public void countUnreadRequests(final CountCallback countCallback) {
		AVQuery<AddRequest> addRequsetAVQuery = AVObject.getQuery(AddRequest.class);
		addRequsetAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		addRequsetAVQuery.whereEqualTo(AddRequest.TO_USER, LeanChatUser.getCurrentUser());
		addRequsetAVQuery.whereEqualTo(AddRequest.IS_READ, false);
		addRequsetAVQuery.countInBackground(new CountCallback() {
			@Override
			public void done(int i, AVException e) {
				if (null != countCallback) {
					unreadAddRequestsCount = i;
					countCallback.done(i, e);
				}
			}
		});
	}

	/**
	 * 标记信息为已读，标记完后会刷新未读消息数量
	 * @param addRequestList
	 */
	public void markAddRequestsRead(List<AddRequest> addRequestList) {
		if (null != addRequestList) {
			for (AddRequest request : addRequestList) {
				request.put(AddRequest.IS_READ, true);
			}
			AVObject.saveAllInBackground(addRequestList, new SaveCallback() {
				@Override
				public void done(AVException e) {
					if (null == e) {
						countUnreadRequests(null);
					}
				}
			});
		}
	}

	/**
	 * 查找好友请求
	 * @param skip
	 * @param limit
	 * @param findCallback
	 */
	public void findAddRequests(int skip, int limit, FindCallback findCallback) {
		LeanChatUser user = LeanChatUser.getCurrentUser();
		AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
		q.include(AddRequest.FROM_USER);
		q.skip(skip);
		q.limit(limit);
		q.whereEqualTo(AddRequest.TO_USER, user);
		q.orderByDescending(AVObject.CREATED_AT);
		q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		q.findInBackground(findCallback);
	}

	/**
	 * 同意好友请求
	 * @param addRequest
	 * @param saveCallback
	 */
	public void agreeAddRequest(final AddRequest addRequest, final SaveCallback saveCallback) {
		addFriend(addRequest.getFromUser().getObjectId(), new SaveCallback() {
			@Override
			public void done(AVException e) {
				if (null != e) {
					if (e.getCode() == AVException.DUPLICATE_VALUE) {
						addRequest.setStatus(AddRequest.STATUS_DONE);
						addRequest.saveInBackground(saveCallback);
					} else{
						saveCallback.done(e);
					}
				} else {
					addRequest.setStatus(AddRequest.STATUS_DONE);
					addRequest.saveInBackground(saveCallback);
				}
			}
		});
	}

	public static void addFriend(String friendId, final SaveCallback saveCallback) {
		LeanChatUser user = LeanChatUser.getCurrentUser();
		//Follow the user specified by userObjectId. This will create a follow relation
		//between this user and the user specified by the userObjectId.
		user.followInBackground(friendId, new FollowCallback() {
			@Override
			public void done(AVObject avObject, AVException e) {
				if (null !=saveCallback) {
					saveCallback.done(e);
				}
			}

		});
	}

	private void createAddRequest(LeanChatUser toUser) throws Exception{
		LeanChatUser curUser= LeanChatUser.getCurrentUser();
		AVQuery<AddRequest> q = AVQuery.getQuery(AddRequest.class);
		q.whereEqualTo(AddRequest.FROM_USER, curUser);
		q.whereEqualTo(AddRequest.TO_USER, toUser);
		q.whereEqualTo(AddRequest.STATUS, AddRequest.STATUS_WAIT);

		int count = 0;
		try {
			count = q.count();
		} catch (AVException e) {
			LogUtils.logException(e);
			if (e.getCode() == AVException.OBJECT_NOT_FOUND) {
				count = 0;
			} else{
				throw e;
			}
		}

		if (count > 0) {//这里count大于0，说明之前已经发送过请求，但对方仍未添加
			throw new IllegalStateException(App.ctx.getString(R.string.contact_alreadyCreateAddRequest));
		} else {//发送请求（其实是在云端的AddRequest表添加数据）
			AddRequest add = new AddRequest();
			add.setFromUser(curUser);
			add.setToUser(toUser);
			add.setStatus(AddRequest.STATUS_WAIT);
			add.setIsRead(false);
			add.save();
		}
	}

	public void createAddRequestInBackground(Context ctx, final LeanChatUser user) {
		new SimpleNetTask(ctx) {
			@Override
			protected void doInBack() throws Exception {
				createAddRequest(user);
			}

			@Override
			protected void onSucceed() {
				PushManager.getInstance().pushMessage(user.getObjectId(),
						                              ctx.getString(R.string.push_add_request),
													  ctx.getString(R.string.invitation_action));
				Utils.toast(R.string.contact_sendRequestSucceed);
			}
		}.execute();
	}

}
