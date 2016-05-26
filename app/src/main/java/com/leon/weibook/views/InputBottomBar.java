package com.leon.weibook.views;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leon.weibook.R;
import com.leon.weibook.adapters.ChatEmotionGridAdapter;
import com.leon.weibook.adapters.ChatEmotionPagerAdapter;
import com.leon.weibook.controller.EmotionHelper;
import com.leon.weibook.event.InputBottomBarEvent;
import com.leon.weibook.event.InputBottomBarLocationClickEvent;
import com.leon.weibook.event.InputBottomBarRecordEvent;
import com.leon.weibook.event.InputBottomBarTextEvent;
import com.leon.weibook.util.PathUtils;
import com.leon.weibook.util.SoftInputUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 专门负责输入的底部操作栏，与 activity 解耦
 * 当点击相关按钮时发送 InputBottomBarEvent，需要的 View 可以自己去订阅相关消息
 * Created by Leon on 2016/5/18 0018.
 */
public class InputBottomBar extends LinearLayout{

	/** 加号 Button */
	private View actionBtn;

	/** 表情 Button */
	private View emotionBtn;

	/** 文本输入框 */
	private EmotionEditText contentEditText;

	/** 发送文本的Button */
	private View sendTextBtn;

	/** 切换到语音输入的 Button */
	private View voiceBtn;

	/** 切换到文本输入的 Button */
	private View keyboardBtn;

	/** 底部的layout，包含 emotionLayout 与 actionLayout */
	private View moreLayout;

	/** 表情 layout */
	private View emotionLayout;

	private ViewPager emotionPager;

	/** 录音按钮 */
	private RecordButton recordBtn;

	/** action layout */
	private View actionLayout;
	private View cameraBtn;
	private View locationBtn;
	private View pictureBtn;

	/** 最小间隔时间为 1 秒，避免多次点击 */
	private final int MIN_INTERVAL_SEND_MESSAGE = 1000;

	public InputBottomBar(Context context) {
		super(context);
		initView(context);
	}

	public InputBottomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}


	//TODO 这一坨代码还是太丑了，暂时如此
	private void initView(Context context) {
		View.inflate(context, R.layout.chat_input_bottom_bar_layout, this);
		actionBtn = findViewById(R.id.input_bar_btn_action);
		emotionBtn = findViewById(R.id.input_bar_btn_motion);
		contentEditText = (EmotionEditText) findViewById(R.id.input_bar_et_emotion);
		sendTextBtn = findViewById(R.id.input_bar_btn_send_text);
		voiceBtn = findViewById(R.id.input_bar_btn_voice);
		keyboardBtn = findViewById(R.id.input_bar_btn_keyboard);
		moreLayout = findViewById(R.id.input_bar_layout_more);
		emotionLayout = findViewById(R.id.input_bar_layout_emotion);
		emotionPager = (ViewPager)findViewById(R.id.input_bar_viewpager_emotin);
		recordBtn = (RecordButton) findViewById(R.id.input_bar_btn_record);

		actionLayout = findViewById(R.id.input_bar_layout_action);
		cameraBtn = findViewById(R.id.input_bar_btn_camera);
		locationBtn = findViewById(R.id.input_bar_btn_location);
		pictureBtn = findViewById(R.id.input_bar_btn_picture);

		setEditTextChangeListener();
		initEmotionPager();
		initRecordBtn();

		//点击加号按钮，显示morelayout中的actionlayout，隐藏emotionlayout
		actionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean showActionView =
						(GONE == moreLayout.getVisibility() || GONE == actionLayout.getVisibility());
				moreLayout.setVisibility(showActionView ? VISIBLE : GONE);
				actionLayout.setVisibility(showActionView ? VISIBLE : GONE);
				emotionLayout.setVisibility(View.GONE);
				SoftInputUtils.hideSoftInput(getContext(), contentEditText);
			}
		});

		//点击表情按钮，显示morelayout中的emotionlayout，隐藏actionlayout
		emotionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean showEmotionView =
						(GONE == moreLayout.getVisibility() || GONE == emotionLayout.getVisibility());
				moreLayout.setVisibility(showEmotionView ? VISIBLE : GONE);
				emotionLayout.setVisibility(showEmotionView ? VISIBLE : GONE);
				actionLayout.setVisibility(View.GONE);
				SoftInputUtils.hideSoftInput(getContext(), contentEditText);
			}
		});

		//点击文本编辑框，隐藏morelayout，显示软键盘
		contentEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moreLayout.setVisibility(View.GONE);
				SoftInputUtils.showSoftInput(getContext(), contentEditText);
			}
		});

		//点击键盘按钮，显示文本输入框及相关按钮
		keyboardBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showTextLayout();
			}
		});

		//点击录音按钮，显示录音文本框
		voiceBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAudioLayout();
			}
		});

		//发送信息按钮监听器
		sendTextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = contentEditText.getText().toString();
				if (TextUtils.isEmpty(content)) {
					Toast.makeText(getContext(), R.string.message_is_null, Toast.LENGTH_SHORT).show();
					return;
				}

				contentEditText.setText("");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						sendTextBtn.setEnabled(true);
					}
				}, MIN_INTERVAL_SEND_MESSAGE);

				EventBus.getDefault().post( new InputBottomBarTextEvent(
						InputBottomBarEvent.INPUTBOTTOMBAR_SEND_TEXT_ACTION, content, getTag()));
			}
		});

		pictureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new InputBottomBarEvent(
						InputBottomBarEvent.INPUTBOTTOMBAR_IMAGE_ACTION, getTag()));
			}
		});

		cameraBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new InputBottomBarEvent(
						InputBottomBarEvent.INPUTBOTTOMBAR_CAMERA_ACTION, getTag()));
			}
		});

		locationBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new InputBottomBarLocationClickEvent(
						InputBottomBarEvent.INPUTBOTTOMBAR_LOCATION_ACTION, getTag()));
			}
		});
	}
	/**
	 * 初始化 emotionPager,填满表情页面
	 */
	private void initEmotionPager() {
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < EmotionHelper.emojiGroups.size(); i++) {
			views.add(getEmotionGridView(i));
		}
		ChatEmotionPagerAdapter pagerAdapter = new ChatEmotionPagerAdapter(views);//用gridviews作为参数
		emotionPager.setOffscreenPageLimit(3);
		emotionPager.setAdapter(pagerAdapter);
	}

	/**
	 * 获取第pos页的表情来初始化gridview，并返回该gridview
	 * @param pos
	 * @return
	 */
	private View getEmotionGridView(int pos) {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View emotionView = inflater.inflate(R.layout.chat_emotion_gridview, null, false);
		GridView gridView = (GridView) emotionView.findViewById(R.id.gridview);
		final ChatEmotionGridAdapter chatEmotionGridAdapter = new ChatEmotionGridAdapter(getContext());
		List<String> pageEmotions = EmotionHelper.emojiGroups.get(pos);//获取pos页的数据
		chatEmotionGridAdapter.setDatas(pageEmotions);
		gridView.setAdapter(chatEmotionGridAdapter);//初始化pos页表情
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String emotionText = (String) parent.getAdapter().getItem(position);//获取表情名字
				int start = contentEditText.getSelectionStart();
				StringBuffer sb = new StringBuffer(contentEditText.getText());
				sb.replace(contentEditText.getSelectionStart(), contentEditText.getSelectionEnd(), emotionText);
				contentEditText.setText(sb.toString());//返回的是SpannableString

				CharSequence info = contentEditText.getText();
				if (info instanceof Spannable) {
					Spannable spannable = (Spannable) info;
					//将光标移动到表情后面
					Selection.setSelection(spannable, start + emotionText.length());
				}
			}
		});
		return gridView;
	}

	/**
	 * 初始化录音按钮，并设定保存路径和设置监听器
	 */
	private void initRecordBtn() {
		recordBtn.setSavePath(PathUtils.getRecordPathByCurrentTime(getContext()));
		recordBtn.setRecordEventListener(new RecordButton.RecordEventListener() {
			@Override
			public void onFinishedRecord(final String audioPath, int secs) {
				EventBus.getDefault().post(
						new InputBottomBarRecordEvent(InputBottomBarEvent.INPUTBOTTOMBAR_SEND_AUDIO_ACTION, audioPath, secs, getTag()));
			}

			@Override
			public void onStartRecord() {}
		});
	}

	/**
	 * 展示文本输入框及相关按钮，隐藏不需要的按钮及 layout
	 */
	private void showTextLayout() {
		contentEditText.setVisibility(View.VISIBLE);
		recordBtn.setVisibility(View.GONE);
		voiceBtn.setVisibility(contentEditText.getText().length() > 0 ? GONE : VISIBLE);
		sendTextBtn.setVisibility(contentEditText.getText().length() > 0 ? VISIBLE : GONE);
		keyboardBtn.setVisibility(View.GONE);
		moreLayout.setVisibility(View.GONE);
		contentEditText.requestFocus();
		SoftInputUtils.showSoftInput(getContext(), contentEditText);
	}

	/**
	 * 展示录音相关按钮，隐藏不需要的按钮及 layout
	 */
	private void showAudioLayout() {
		contentEditText.setVisibility(View.GONE);
		recordBtn.setVisibility(View.VISIBLE);
		voiceBtn.setVisibility(GONE);
		keyboardBtn.setVisibility(VISIBLE);
		moreLayout.setVisibility(View.GONE);
		SoftInputUtils.hideSoftInput(getContext(), contentEditText);
	}

	/**
	 * 设置 text change 事件，有文本时展示发送按钮，没有文本时展示切换语音的按钮
	 */
	private void setEditTextChangeListener() {
		contentEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
				boolean showSend = charSequence.length() > 0;
				keyboardBtn.setVisibility(!showSend ? View.VISIBLE : GONE);
				sendTextBtn.setVisibility(showSend ? View.VISIBLE : GONE);
				voiceBtn.setVisibility(View.GONE);
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});
	}

	/**
	 * 隐藏底部的图片、emtion 等 layout
	 */
	public void hideMoreLayout() {
		moreLayout.setVisibility(View.GONE);
	}

}
