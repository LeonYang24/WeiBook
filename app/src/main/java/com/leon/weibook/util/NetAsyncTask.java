package com.leon.weibook.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Leon on 2016/5/17 0017.
 */
public abstract class NetAsyncTask extends AsyncTask<Void, Void, Void> {

	protected Context ctx;
	ProgressDialog dialog;
	boolean openDialog = true;
	Exception exception;

	protected NetAsyncTask(Context ctx) {
		this.ctx = ctx;
	}

	protected NetAsyncTask(Context ctx, boolean openDialog) {
		this.ctx = ctx;
		this.openDialog = openDialog;
	}

	public NetAsyncTask setOpenDialog(boolean openDialog) {
		this.openDialog = openDialog;
		return this;
	}

	public ProgressDialog getDialog() {
		return dialog;
	}

	/**
	 * Runs on the UI thread before doInBackground}.
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (openDialog) {
			dialog = Utils.showSpinnerDialog((Activity) ctx);
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			doInBack();
		} catch (Exception e) {
			LogUtils.logException(e);
			exception = e;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		if (openDialog) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		onPost(exception);
	}

	protected abstract void doInBack() throws Exception;

	protected abstract void onPost(Exception e);
}
