package com.leon.weibook.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leon.weibook.R;
import com.leon.weibook.activity.WeatherActivity;
import com.leon.weibook.views.DiscoverItemView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * "发现"页
 * Created by Leon on 2016/5/15 0015.
 */
public class FragmentDiscover extends BaseFragment {

	@BindView(R.id.item_music) DiscoverItemView itemMusic;
	@BindView(R.id.item_weather) DiscoverItemView itemWeather;

	@OnClick(R.id.item_music)
	public void openMusic() {

	}

	@OnClick(R.id.item_weather)
	public void openWeather() {
		Intent intent = new Intent(getActivity(), WeatherActivity.class);
		startActivity(intent);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		headerLayout.setTitle(getString(R.string.fragment_discover_title_name));
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_discover, container, false);
		ButterKnife.bind(this, view);

		init();
		return view;
	}

	private void init() {
		itemMusic.setIcon(R.mipmap.discover_music);
		itemMusic.setIconName(getString(R.string.fragment_discover_music_name));

		itemWeather.setIcon(R.mipmap.discover_weather);
		itemWeather.setIconName(getString(R.string.fragment_discover_weather_name));
	}

}
