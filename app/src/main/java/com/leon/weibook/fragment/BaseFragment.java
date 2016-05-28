package com.leon.weibook.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.leon.weibook.R;
import com.leon.weibook.views.HeaderLayout;

/**
 * 这是所有底部tab页面的Fragment的基类
 *
 * 1、主要创建了一个HeaderLayout用于继承使用（标题）
 * 2、写了一个toast，用于短暂显示Toast信息
 * 3、写了一个判断Exception函数
 *
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

	/**
	 * 显示传入的字符串
	 * @param str
	 */
	protected void toast(String str) {
		Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示传入的资源ID的信息
	 * @param resId
	 */
	protected void toast(int resId) {
		Toast.makeText(this.getActivity(), resId, Toast.LENGTH_SHORT);
	}

	/**
	 * 该函数用于判断是否发生异常，有则显示并返回false，无则返回ture
	 * @param e
	 * @return
	 */
	protected boolean filterException(Exception e) {
		if (e != null) {
			toast(e.getMessage());
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 显示一个加载画面 （与AVBaseActivity里的一致）
	 * @return
	 */
	protected ProgressDialog showSpinnerDialog() {
		//activity = modifyDialogContext(activity);
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(true);
		dialog.setMessage(getString(R.string.hard_to_loading));
		if (!getActivity().isFinishing()) {//只要Activity不结束，就一直显示（除非调用dismiss）
			dialog.show();
		}
		return dialog;
	}

}
