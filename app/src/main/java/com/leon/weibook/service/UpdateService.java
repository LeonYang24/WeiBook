package com.leon.weibook.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.leon.weibook.R;
import com.leon.weibook.model.UpdateInfo;
import com.leon.weibook.util.LogUtils;
import com.leon.weibook.util.NetAsyncTask;
import com.leon.weibook.util.Utils;

import java.util.List;

/**
 * 这是“检查更新”服务类
 * Created by Leon on 2016/5/25 0025.
 */
public class UpdateService {
	private static final String LAST_VERSION = "lastVersion";
	private static final String PROMPTED_UPDATE = "promtedUpdate";

	static UpdateService updateService;
	Activity activity;
	SharedPreferences pref;
	SharedPreferences.Editor editor;

	private UpdateService(Activity activity) {
		this.activity = activity;
		pref = PreferenceManager.getDefaultSharedPreferences(activity);
		editor = pref.edit();
	}

	public static UpdateService getInstance (Activity ctx) {
		if (null == updateService) {
			updateService = new UpdateService(ctx);
		}
		return updateService;
	}

	/**
	 * 获取当前应用程序版本号
	 * @param ctx
	 * @return
	 */
	public static int getVersionCode(Context ctx) {
		int versionCode = 0;
		try {
			versionCode = ctx.getPackageManager().
					getPackageInfo(ctx.getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			LogUtils.logException(e);
		}
		return versionCode;
	}

	/**
	 * 获取版本名
	 * @param ctx
	 * @return
	 */
	public static String getVersitonName(Context ctx) {
		String versionName = null;
		try {
			versionName = ctx.getPackageManager().
					getPackageInfo(ctx.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			LogUtils.logException(e);
		}
		return versionName;
	}

	/**
	 * 创建更新信息
	 * @throws AVException
	 */
	public static void createUpdateInfo() throws AVException{
		UpdateInfo updateInfo = new UpdateInfo();
		updateInfo.setVersion(1);
		updateInfo.setApkUrl("https://leancloud.cn");
		updateInfo.setDesc("desc");
		updateInfo.save();
	}

	/**
	 * 在浏览器中打开地址
	 * @param url
	 */
	public void openUrlInBrowser(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		activity.startActivity(intent);
	}

	public void checkUpdate() {
		new UpdateTask(activity, false, AVQuery.CachePolicy.NETWORK_ELSE_CACHE) {
			@Override
			public void done(final UpdateInfo info, Exception e) {
				if (e == null) {
					int ver = info.getVersion();
					int curVer = UpdateService.getVersionCode(ctx);
					//Logger.d("info url=" + info.getApkUrl());
					if (curVer < ver) {
						if (isPromptedUpdate() == false) {
							setPromptedUpdate(true);
							AlertDialog.Builder builder = new AlertDialog.Builder(UpdateService.this.activity);
							builder.setTitle(R.string.update_service_haveNewVersion)
									.setMessage(info.getDesc())
									.setPositiveButton(R.string.update_service_installNewVersion, new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											openUrlInBrowser(info.getApkUrl());
										}
									}).setNegativeButton(R.string.update_service_laterNotify, null).show();
						}
					} else {
						if (curVer == ver) {
							int lastVer = getLastVersion();
							boolean firstRunThisVersion = lastVer < curVer;
							if (firstRunThisVersion) {
								setPromptedUpdate(false);
								setLastVersion(curVer);
								String msg = info.getDesc();
								String title = UpdateService.this.activity.getString(R.string.update_service_updateLog);
								Utils.showInfoDialog(UpdateService.this.activity, msg, title);
							}
						}
					}
				}
			}
		}.execute();
	}

	private int getLastVersion() {
		return pref.getInt(LAST_VERSION, 0);
	}

	private void setLastVersion(int lastVersion) {
		editor.putInt(LAST_VERSION, lastVersion).commit();
	}

	private boolean isPromptedUpdate() {
		return pref.getBoolean(PROMPTED_UPDATE, false);
	}

	private void setPromptedUpdate(boolean promptedUpdate) {
		editor.putBoolean(PROMPTED_UPDATE, promptedUpdate).commit();
	}

	public void showSureUpdateDialog() {
		new UpdateTask(activity, true, AVQuery.CachePolicy.NETWORK_ELSE_CACHE) {
			@Override
			public void done(final UpdateInfo info, Exception e) {
				if (e == null) {
					if (info.getVersion() > getVersionCode(ctx)) {
						AlertDialog.Builder builder = Utils.getBaseDialogBuilder(activity);
						builder.setTitle(R.string.update_service_sureToUpdate)
								.setMessage(info.getDesc())
								.setPositiveButton(R.string.chat_utils_right, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										openUrlInBrowser(info.getApkUrl());
									}
								}).setNegativeButton(R.string.chat_common_cancel, null).show();
					} else {
						Utils.toast(ctx, R.string.update_service_versionIsAlreadyNew);
					}
				} else {
					Log.i("test", "sho...e = " + e.getMessage());
					Utils.toast(e.getMessage());
				}
			}
		}.execute();
	}

	abstract static class UpdateTask extends NetAsyncTask {
		UpdateInfo info;
		AVQuery.CachePolicy policy;

		protected UpdateTask(Context ctx, boolean openDialog, AVQuery.CachePolicy policy) {
			super(ctx, openDialog);
			this.policy = policy;
		}

		@Override
		protected void doInBack() throws Exception {
			info = getNewestUpdateInfo();
		}

		private UpdateInfo getNewestUpdateInfo() throws AVException {
			AVQuery<UpdateInfo> query = AVObject.getQuery(UpdateInfo.class);
			query.setLimit(1);
			query.orderByDescending(UpdateInfo.VERSION);
			if (policy != null) {
				query.setCachePolicy(policy);
			}
			List<UpdateInfo> updateInfos = query.find();
			if (updateInfos.size() > 0) {
				return updateInfos.get(0);
			}
			return null;
		}

		@Override
		protected void onPost(Exception e) {
			if (e != null) {
				done(null, e);
			} else {
				if (info != null) {
					done(info, null);
				} else {
					done(null, new IllegalStateException("info is null"));
				}
			}
		}

		public abstract void done(UpdateInfo info, Exception e);
	}

}
