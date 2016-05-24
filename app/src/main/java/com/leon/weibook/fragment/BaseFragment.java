package com.leon.weibook.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.leon.weibook.R;
import com.leon.weibook.views.HeaderLayout;

/**
 * Created by Leon on 2016/5/15 0015.
 */
public class BaseFragment extends Fragment {

	protected HeaderLayout headerLayout;
	protected Context ctx;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ctx = getActivity();
		headerLayout = (HeaderLayout) getView().findViewById(R.id.headerLayout);
	}

	protected void toast(String str) {
		Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT);
	}

	protected boolean filterException(Exception e) {
		if (e != null) {
			toast(e.getMessage());
			return false;
		} else {
			return true;
		}
	}

	protected ProgressDialog showSpinnerDialog() {
		//activity = modifyDialogContext(activity);
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(true);
		dialog.setMessage(getString(R.string.chat_utils_hardLoading));
		if (!getActivity().isFinishing()) {
			dialog.show();
		}
		return dialog;
	}

}
