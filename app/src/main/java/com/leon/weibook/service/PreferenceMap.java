package com.leon.weibook.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avos.avoscloud.AVGeoPoint;
import com.leon.weibook.App;
import com.leon.weibook.R;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.LogUtils;

/**
 * Created by Leon on 2016/5/17 0017.
 */
public class PreferenceMap {

	public static final String ADD_REQUEST_N = "addRequestN";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String NOTIFY_WHEN_NEWS = "notifyWhenNews";
	public static final String VOICE_NOTIFY = "voiceNotify";
	public static final String VIBRATE_NOTIFY = "vibrateNotify";
	public static final String NEARBY_ORDER = "nearbyOrder";
	//int addRequestN;
	//String latitude;
	//String longitude;
	public static PreferenceMap currentUserPreferenceMap;
	Context cxt;
	SharedPreferences pref;
	SharedPreferences.Editor editor;

	public PreferenceMap(Context cxt) {
		this.cxt = cxt.getApplicationContext();
		pref = PreferenceManager.getDefaultSharedPreferences(cxt);
		editor = pref.edit();
		LogUtils.d("PreferenceMap init no specific user");
	}

	public PreferenceMap(Context cxt, String prefName) {
		this.cxt = cxt.getApplicationContext();
		pref = cxt.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		editor = pref.edit();
	}

	public static PreferenceMap getCurUserPrefDao(Context ctx) {
		if (currentUserPreferenceMap == null) {
			currentUserPreferenceMap = new PreferenceMap(ctx.getApplicationContext(), LeanChatUser.getCurrentUserId());
		}
		return currentUserPreferenceMap;
	}

	public static PreferenceMap getMyPrefDao(Context ctx) {
		LeanChatUser user = LeanChatUser.getCurrentUser();
		if (user == null) {
			return new PreferenceMap(ctx, "default_pref");
		}
		return new PreferenceMap(ctx, user.getObjectId());
	}

	public int getAddRequestN() {
		return pref.getInt(ADD_REQUEST_N, 0);
	}

	public void setAddRequestN(int addRequestN) {
		editor.putInt(ADD_REQUEST_N, addRequestN).commit();
	}

	private String getLatitude() {
		return pref.getString(LATITUDE, null);
	}

	private void setLatitude(String latitude) {
		editor.putString(LATITUDE, latitude).commit();
	}

	private String getLongitude() {
		return pref.getString(LONGITUDE, null);
	}

	private void setLongitude(String longitude) {
		editor.putString(LONGITUDE, longitude).commit();
	}

	public AVGeoPoint getLocation() {
		String latitudeStr = getLatitude();
		String longitudeStr = getLongitude();
		if (latitudeStr == null || longitudeStr == null) {
			return null;
		}
		double latitude = Double.parseDouble(latitudeStr);
		double longitude = Double.parseDouble(longitudeStr);
		return new AVGeoPoint(latitude, longitude);
	}

	public void setLocation(AVGeoPoint location) {
		setLatitude(location.getLatitude() + "");
		setLongitude(location.getLongitude() + "");
	}

	public boolean isNotifyWhenNews() {
		return pref.getBoolean(NOTIFY_WHEN_NEWS,
				App.ctx.getResources().getBoolean(R.bool.defaultNotifyWhenNews));
	}

	public void setNotifyWhenNews(boolean notifyWhenNews) {
		editor.putBoolean(NOTIFY_WHEN_NEWS, notifyWhenNews).commit();
	}

	boolean getBooleanByResId(int resId) {
		return App.ctx.getResources().getBoolean(resId);
	}

	public boolean isVoiceNotify() {
		return pref.getBoolean(VOICE_NOTIFY,
				getBooleanByResId(R.bool.defaultVoiceNotify));
	}

	public void setVoiceNotify(boolean voiceNotify) {
		editor.putBoolean(VOICE_NOTIFY, voiceNotify).commit();
	}

	public boolean isVibrateNotify() {
		return pref.getBoolean(VIBRATE_NOTIFY,
				getBooleanByResId(R.bool.defaultVibrateNotify));
	}

	public void setVibrateNotify(boolean vibrateNotify) {
		editor.putBoolean(VIBRATE_NOTIFY, vibrateNotify);
	}


	public int getNearbyOrder() {
		return pref.getInt(NEARBY_ORDER, Constants.ORDER_UPDATED_AT);
	}

	public void setNearbyOrder(int nearbyOrder) {
		editor.putInt(NEARBY_ORDER, nearbyOrder).commit();
	}

}
