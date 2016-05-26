package com.leon.weibook.controller;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 天气状态类
 * 用于查询各种天气状况
 * Created by Leon on 2016/5/25 0025.
 */
public class WeatherStatus {

	public static final String DAY1 = "f1";
	public static final String DAY2 = "f2";
	public static final String DAY3 = "f3";
	public static final String DAY4 = "f4";

	JSONObject jsonObject;
	String jsonStr = null;

	public WeatherStatus(String json) {
		jsonStr = json;
		try {
			jsonObject = new JSONObject(json);
			jsonObject = jsonObject.getJSONObject("retData");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断返回的Json是否有效
	 * @return
	 */
	public boolean isJsonValid() {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			String errNum = jsonObject.getString("errNum");
			if (errNum.equals("0")) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取日期 如“2016-5-26”
	 * @param day
	 * @return
	 */
	public String getDate(String day) {
		String result = getWeatherStatueByValue(day, "date");
		return result;
	}

	/**
	 * 获取星期 如“星期一”
	 * @param day
	 * @return
	 */
	public String getWeek(String day) {
		String result = getWeatherStatueByValue(day, "week");
		return result;
	}

	/**
	 * 获取风向和风力
	 * @param day
	 * @return
	 */
	public String getWindStatus(String day) {
		String result = getWeatherStatueByValue(day, "fengxiang") + "  " +
				getWeatherStatueByValue(day, "fengli");
		return result;
	}

	/**
	 * 获取温度
	 * @param day
	 * @return
	 */
	public String getTemperature(String day) {
		String result = null;
		if (DAY1 == day) {
			result = getWeatherStatueByValue(day, "curTemp");
		}else {
			result = getWeatherStatueByValue(day, "lowtemp") + " ~ " +
					getWeatherStatueByValue(day, "hightemp");
		}

		return result;
	}

	/**
	 * 获取天气类型 如“晴”
	 * @param day
	 * @return
	 */
	public String getWeatherType(String day) {
		String result = getWeatherStatueByValue(day, "type");
		return result;
	}

	public String getPM() {
		String result = getWeatherStatueByValue(DAY1, "aqi");
		return result;
	}

	public String getUltraviolet() {
		String result = null;
		try {
			JSONObject jsonday1 = jsonObject.getJSONObject("today");
			JSONArray jsonArray = jsonday1.getJSONArray("index");
			JSONObject jsonIndex = (JSONObject) jsonArray.get(1);
			result = jsonIndex.getString("index");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getWeatherStatueByValue(String whichday, String value) {
		String result = null;
		try {
			JSONArray jsonOtherDay = jsonObject.getJSONArray("forecast");
			switch (whichday) {
				case DAY1 :
					JSONObject jsonday1 = jsonObject.getJSONObject("today");
					result = jsonday1.getString(value);
					break;
				case DAY2 :
					JSONObject jsonday2 = (JSONObject) jsonOtherDay.get(0);
					result = jsonday2.getString(value);
					break;
				case DAY3 :
					JSONObject jsonday3 = (JSONObject) jsonOtherDay.get(1);
					result = jsonday3.getString(value);
					break;
				case DAY4 :
					JSONObject jsonday4 = (JSONObject) jsonOtherDay.get(2);
					result = jsonday4.getString(value);
					break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

}
