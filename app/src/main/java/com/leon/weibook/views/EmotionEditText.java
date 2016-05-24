package com.leon.weibook.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.leon.weibook.controller.EmotionHelper;

/**
 * 这是用于处理表情输入的EditText
 * Created by Leon on 2016/5/18 0018.
 */
public class EmotionEditText extends EditText {

	public EmotionEditText(Context context) {
		super(context);
	}

	public EmotionEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmotionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * 重载setText，检测EditText的内容是文字还是表情，并进行适当转换。
	 * @param text
	 * @param type
	 */
	@Override
	public void setText(CharSequence text, BufferType type) {
		if (!TextUtils.isEmpty(text)) {
			super.setText(EmotionHelper.replace(getContext(), text.toString()), type);
		} else {
			super.setText(text, type);
		}
 	}
}
