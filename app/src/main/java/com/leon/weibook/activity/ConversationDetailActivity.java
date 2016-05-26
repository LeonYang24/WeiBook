package com.leon.weibook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.leon.weibook.R;

/**
 * Created by Leon on 2016/5/21 0021.
 */
public class ConversationDetailActivity extends AVBaseActivity{
	private static final int ADD_MEMBERS = 0;
	private static final int INTENT_NAME = 1;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_detail);


	}
}
