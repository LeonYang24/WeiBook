package com.leon.weibook.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.leon.weibook.R;
import com.leon.weibook.controller.WeatherStatus;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * "天气"界面
 * Created by Leon on 2016/5/24 0024.
 */
public class WeatherActivity extends AVBaseActivity {

	public static final int DAY1 = 1;
	public static final int DAY2 = 2;
	public static final int DAY3 = 3;
	public static final int DAY4 = 4;

	@BindView(R.id.srl_weather)
	SwipeRefreshLayout swipeRefreshLayout;
	/** 位置 */
	@BindView(R.id.btn_weather_location) Button btn_location;
	/** 天气图标 */
	@BindView(R.id.iv_weather_day1_pic) ImageView icon_day1;
	@BindView(R.id.iv_weather_day2_pic) ImageView icon_day2;
	@BindView(R.id.iv_weather_day3_pic) ImageView icon_day3;
	@BindView(R.id.iv_weather_day4_pic) ImageView icon_day4;
	/** 天气状况概述 */
	@BindView(R.id.tv_weather_day1_weather) TextView weatherType_day1;
	@BindView(R.id.tv_weather_day2_weather) TextView weatherType_day2;
	@BindView(R.id.tv_weather_day3_weather) TextView weatherType_day3;
	@BindView(R.id.tv_weather_day4_weather) TextView weatherType_day4;
	/** 温度 */
	@BindView(R.id.tv_weather_day1_temperature) TextView temperature_day1;
	@BindView(R.id.tv_weather_day2_temperature) TextView temperature_day2;
	@BindView(R.id.tv_weather_day3_temperature) TextView temperature_day3;
	@BindView(R.id.tv_weather_day4_temperature) TextView temperature_day4;
	/** 风向及风力 */
	@BindView(R.id.tv_weather_day1_wind) TextView wind_day1;
	@BindView(R.id.tv_weather_day2_wind) TextView wind_day2;
	@BindView(R.id.tv_weather_day3_wind) TextView wind_day3;
	@BindView(R.id.tv_weather_day4_wind) TextView wind_day4;
	/** 紫外线 */
	@BindView(R.id.tv_weather_day1_ultraviolet) TextView ultraviolet_day1;
	/** PM2.5 */
	@BindView(R.id.tv_weather_day1_pm) TextView pmStatus_day1;
	/** 日期 */
	@BindView(R.id.tv_weather_day_1_date) TextView date_day1;
	@BindView(R.id.tv_weather_day_2_date) TextView date_day2;
	@BindView(R.id.tv_weather_day_3_date) TextView date_day3;
	@BindView(R.id.tv_weather_day_4_date) TextView date_day4;
	/** 星期 */
	@BindView(R.id.tv_weather_day1_week) TextView week_day1;
	@BindView(R.id.tv_weather_day2_week) TextView week_day2;
	@BindView(R.id.tv_weather_day3_week) TextView week_day3;
	@BindView(R.id.tv_weather_day4_week) TextView week_day4;

	private static final String HTTPURL = "http://apis.baidu.com/apistore/weatherservice/recentweathers";
	private Parameters parameters = null;

	Class<R.mipmap> cls = R.mipmap.class;
	Calendar calendar = Calendar.getInstance();

	SimpleDateFormat format = new SimpleDateFormat("HH");

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		init();

		updateWeatherInfo(btn_location.getText().toString());
	}

	/**
	 * 初始化监听器
	 */
	private void init() {
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						updateWeatherInfo(btn_location.getText().toString());
					}
				}, 1000);
			}
		});
	}

	@OnClick(R.id.btn_weather_location)
	public void setLocationAndUpdate() {
		final EditText et = new EditText(this);
		new AlertDialog.Builder(this).
				setTitle(getString(R.string.activity_weather_alertdialog_title)).
				setView(et).
				setPositiveButton(getString(R.string.activity_weather_alertdialog_confirm),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = et.getText().toString().trim();
						if (name.equals("")) {
							toast(getString(R.string.activity_weather_alertdialog_empty));
						} else {
							updateWeatherInfo(name);
						}
					}
				}).
				setNegativeButton(getString(R.string.activity_weather_alertdialog_cancel), null).
				show();
	}

	/**
	 * 根据地理位置更新天气数据
	 * @param cityName
	 */
	private void updateWeatherInfo(final String cityName) {
		parameters = new Parameters();
		parameters.put("cityname", cityName);

		ApiStoreSDK.execute(HTTPURL, ApiStoreSDK.GET, parameters, new ApiCallBack() {
			@Override
			public void onSuccess(int i, String responseString) {
				WeatherStatus weatherStatus = new WeatherStatus(responseString);

				if (weatherStatus.isJsonValid()) {
					date_day1.setText(weatherStatus.getDate(WeatherStatus.DAY1));
					date_day2.setText(weatherStatus.getDate(WeatherStatus.DAY2));
					date_day3.setText(weatherStatus.getDate(WeatherStatus.DAY3));
					date_day4.setText(weatherStatus.getDate(WeatherStatus.DAY4));

					week_day1.setText(weatherStatus.getWeek(WeatherStatus.DAY1));
					week_day2.setText(weatherStatus.getWeek(WeatherStatus.DAY2));
					week_day3.setText(weatherStatus.getWeek(WeatherStatus.DAY3));
					week_day4.setText(weatherStatus.getWeek(WeatherStatus.DAY4));

					wind_day1.setText(weatherStatus.getWindStatus(WeatherStatus.DAY1));
					wind_day2.setText(weatherStatus.getWindStatus(WeatherStatus.DAY2));
					wind_day3.setText(weatherStatus.getWindStatus(WeatherStatus.DAY3));
					wind_day4.setText(weatherStatus.getWindStatus(WeatherStatus.DAY4));

					weatherType_day1.setText(weatherStatus.getWeatherType(WeatherStatus.DAY1));
					weatherType_day2.setText(weatherStatus.getWeatherType(WeatherStatus.DAY2));
					weatherType_day3.setText(weatherStatus.getWeatherType(WeatherStatus.DAY3));
					weatherType_day4.setText(weatherStatus.getWeatherType(WeatherStatus.DAY4));

					temperature_day1.setText(weatherStatus.getTemperature(WeatherStatus.DAY1));
					temperature_day2.setText(weatherStatus.getTemperature(WeatherStatus.DAY2));
					temperature_day3.setText(weatherStatus.getTemperature(WeatherStatus.DAY3));
					temperature_day4.setText(weatherStatus.getTemperature(WeatherStatus.DAY4));

					pmStatus_day1.setText(weatherStatus.getPM());

					ultraviolet_day1.setText(weatherStatus.getUltraviolet());

					icon_day1.setBackgroundResource(getWeatherTypeIcon(weatherStatus,
							WeatherStatus.DAY1));
					icon_day2.setBackgroundResource(getWeatherTypeIcon(weatherStatus,
							WeatherStatus.DAY2));
					icon_day3.setBackgroundResource(getWeatherTypeIcon(weatherStatus,
							WeatherStatus.DAY3));
					icon_day4.setBackgroundResource(getWeatherTypeIcon(weatherStatus,
							WeatherStatus.DAY4));

					btn_location.setText(cityName);
					swipeRefreshLayout.setRefreshing(false);
					toast(getString(R.string.activity_weather_refresh_success));
				} else {
					swipeRefreshLayout.setRefreshing(false);
					toast(getString(R.string.activity_weather_reccode_error));
				}
			}

		});
	}

	/**
	 * 获取天气类型图标
	 * 这里用了JPinYin包来转换类型文字，如“晴”转为“qing”，然后搜索resource中的png图片资源
	 * @param status
	 * @param day
	 * @return
	 */
	public int getWeatherTypeIcon(WeatherStatus status, String day) {
		String type = status.getWeatherType(day);
		String pinyin = null;

		String hour = format.format(new Date());
		int id = 0;
		if (Integer.parseInt(hour) > 20 && day.equals(WeatherStatus.DAY1)) {
			pinyin = PinyinHelper.convertToPinyinString(type, "", PinyinFormat.WITHOUT_TONE) + "_night";
		} else {
			pinyin = PinyinHelper.convertToPinyinString(type, "", PinyinFormat.WITHOUT_TONE);
		}

		try {
			id = cls.getDeclaredField(pinyin).getInt(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

}
