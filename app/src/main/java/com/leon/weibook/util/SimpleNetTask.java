package com.leon.weibook.util;

import android.content.Context;

/**
 * Created by Leon on 2016/5/17 0017.
 */
public abstract class SimpleNetTask extends NetAsyncTask {

	protected SimpleNetTask(Context cxt) {
		super(cxt);
	}

	protected SimpleNetTask(Context cxt, boolean openDialog) {
		super(cxt, openDialog);
	}

	@Override
	protected void onPost(Exception e) {
		if (null != e) {
			e.printStackTrace();
			Utils.toast(e.getMessage());
		} else {
			onSucceed();
		}
	}

	@Override
	protected abstract void doInBack() throws Exception;

	protected abstract void onSucceed();
}
