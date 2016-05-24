package com.leon.weibook.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leon.weibook.event.MemberLetterEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Leon on 2016/5/15 0015.
 */
public class LetterView extends LinearLayout {

	public LetterView(Context context) {
		super(context);
		setOrientation(VERTICAL);
		updateLetters();
	}

	public LetterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		updateLetters();
	}

	private void updateLetters() {
		setLetters(getSortLetters());
	}

	/**
	 * 快速设置滑动的字母集合
	 * @param letters
	 */
	public void setLetters(List<Character> letters) {
		removeAllViews();//Call this method to remove all child views from the ViewGroup
		for (Character content : letters) {
			TextView view = new TextView(getContext());
			view.setText(content.toString());
			addView(view);
		}

		/**
		 * 这里的监听器可以检测手指触摸到A-Z的哪一个字母（TextView），然后发布MemberLetterEvent
		 * 等待EventBus做出反馈
		 */
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = Math.round(event.getX());
				int y = Math.round(event.getY());
				for (int i = 0; i < getChildCount(); i++) {
					TextView child = (TextView)getChildAt(i);
					if (y > child.getTop() && y < child.getBottom()) {
						MemberLetterEvent letterEvent = new MemberLetterEvent();
						letterEvent.letter = child.getText().toString().charAt(0);
						EventBus.getDefault().post(letterEvent);
					}
				}
				return true;
			}
		});
	}

	/**
	 * 默认的只包含 A-Z 的字母
	 */
	private List<Character> getSortLetters() {
		List<Character> letterList = new ArrayList<Character>();
		for (char c = 'A'; c <= 'Z'; c++) {
			letterList.add(c);
		}
		return letterList;
	}

}
