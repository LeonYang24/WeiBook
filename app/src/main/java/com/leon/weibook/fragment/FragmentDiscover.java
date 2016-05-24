package com.leon.weibook.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leon.weibook.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Leon on 2016/5/15 0015.
 */
public class FragmentDiscover extends BaseFragment {

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_discover, container, false);

		EventBus.getDefault().register(this);
		return view;
	}
}
