package com.leon.weibook.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.leon.weibook.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Leon on 2016/5/18 0018.
 */
public class RecordButton extends Button {

	public static final int BACK_RECORDING = R.drawable.chat_voice_bg_pressed;
	public static final int BACK_IDLE = R.drawable.chat_voice_bg;
	public static final int SLIDE_UP_TO_CANCEL = 0;
	public static final int RELEASE_TO_CANCEL = 1;
	private static final int MIN_INTERVAL_TIME = 1000;// 2s

	//按住录音按钮时弹出的显示录音音量大小对话框图标
	private static int[] recordImageIds = {R.mipmap.chat_icon_voice0, R.mipmap.chat_icon_voice1,
			                               R.mipmap.chat_icon_voice2, R.mipmap.chat_icon_voice3,
			                               R.mipmap.chat_icon_voice4, R.mipmap.chat_icon_voice5};

	private TextView textView;
	private String outputPath = null;//
	private RecordEventListener recordEventListener;
	private long startTime;
	private Dialog recordIndicator;//录音时弹出的对话框
	private View view;
	private MediaRecorder recorder;
	private ObtainDecibelThread thread;
	private Handler volumeHandler;
	private ImageView imageView;
	private int status;
	private OnDismissListener onDismiss = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			stopRecording();
		}
	};

	public RecordButton(Context context) {
		super(context);
		init();
	}

	public RecordButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		volumeHandler = new ShowVolumeHandler();
		setBackgroundResource(BACK_IDLE);
	}

	/**
	 * 设置保存路径
	 * @param path
	 */
	public void setSavePath(String path) {
		outputPath = path;
	}

	/**
	 * 设置录音事件监听器
	 * @param listener
	 */
	public void setRecordEventListener(RecordEventListener listener) {
		recordEventListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (null == outputPath) {
			return false;
		}
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN : //按下按钮开始录音
				startRecord();
				break;
			case MotionEvent.ACTION_UP : //释放按钮停止录音
				if (status == RELEASE_TO_CANCEL) {
					cancelRecord();
				} else {
					finishRecord();
				}
				break;
			case MotionEvent.ACTION_MOVE : //触摸后手势上移
				if (event.getY() < 0) {
					status = RELEASE_TO_CANCEL;
				} else {
					status = SLIDE_UP_TO_CANCEL;
				}
				setTextViewByStatus();
				break;
			case MotionEvent.ACTION_CANCEL :
				cancelRecord();
				break;
		}
		return true;
	}

	public int getColor(int id) {
		return getContext().getResources().getColor(id);
	}

	/**
	 * 根据状态来设置上下移动时，对话框显示的文本内容
	 */
	private void setTextViewByStatus() {
		if (status == RELEASE_TO_CANCEL) {
			textView.setTextColor(getColor(R.color.chat_record_btn_red));
			textView.setText(R.string.chat_record_button_releaseToCancel);
		} else if (status == SLIDE_UP_TO_CANCEL) {
			textView.setTextColor(getColor(R.color.chat_common_white));
			textView.setText(R.string.chat_record_button_slideUpToCancel);
		}
	}

	/**
	 * 启动录音，同时设置相关对话框的显示和按钮的变化
	 */
	private void startRecord() {
		initRecordDialog();
		startTime = System.currentTimeMillis();
		setBackgroundResource(BACK_RECORDING);
		startRecording();
		recordIndicator.show();
	}

	/**
	 * 初始化录音显示对话框的相关参数和view
	 */
	private void initRecordDialog() {
		if (null == recordIndicator) {
			recordIndicator = new Dialog(getContext(),
					                     R.style.chat_record_button_toast_dialog_style);
			view = inflate(getContext(), R.layout.chat_record_layout, null);
			imageView = (ImageView) view.findViewById(R.id.imageView);
			textView = (TextView) view.findViewById(R.id.textView);
			recordIndicator.setContentView(view, new LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													ViewGroup.LayoutParams.WRAP_CONTENT));
			recordIndicator.setOnDismissListener(onDismiss);
			LayoutParams lp = recordIndicator.getWindow().getAttributes();
			lp.gravity = Gravity.CENTER;//设置对话框放在中间
		}
	}

	/**
	 * 开始录音
	 */
	private void startRecording() {
		try {
			if (null == recorder) {
				recorder = new MediaRecorder();
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
				recorder.setOutputFile(outputPath);
				recorder.prepare();
			} else {
				recorder.reset();
				recorder.setOutputFile(outputPath);
			}
			recorder.start();
			thread = new ObtainDecibelThread();
			thread.start();
			recordEventListener.onStartRecord();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消录音
	 */
	private void cancelRecord() {
		stopRecording();
		setBackgroundResource(BACK_IDLE);
		recordIndicator.dismiss();
		Toast.makeText(getContext(), getContext().getString(R.string.chat_cancelRecord),
					   Toast.LENGTH_SHORT).show();
		removeFile();
	}

	/**
	 * 删除录音文件
	 */
	private void removeFile() {
		File file = new File(outputPath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 停止录音
	 */
	private void stopRecording() {
		if (null != thread) {
			thread.exit();
			thread = null;
		}
		if (null != recorder) {
			try {
				recorder.stop();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				recorder.release();
				recorder = null;
			}
		}
	}

	private void finishRecord() {
		stopRecording();
		recordIndicator.dismiss();
		setBackgroundResource(BACK_IDLE);

		long intervalTime = System.currentTimeMillis() - startTime;
		if (intervalTime < MIN_INTERVAL_TIME) {
			Toast.makeText(getContext(),
					       getContext().getString(R.string.chat_record_button_pleaseSayMore),
					       Toast.LENGTH_SHORT).show();
			removeFile();
			return;
		}

		int sec = Math.round(intervalTime * 1.0f / 1000);
		if (null != recordEventListener) {
			recordEventListener.onFinishedRecord(outputPath, sec);
		}
	}

	/**
	 * 该线程类用于想handler发出音量分贝值，从而帮助handler显示不同图片
	 */
	private class ObtainDecibelThread extends Thread {
		private volatile boolean running = true;
		public void exit() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(200);//暂停0.2秒
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (null == recorder || !running) {//如果录音停止，则退出线程
					break;
				}
				int x = recorder.getMaxAmplitude();//获取最大振幅,调用setAudioSource()后方可使用
				if (0 != x) {
					int f = (int) (10 * Math.log(x) / Math.log(10));
					int index = (f - 18) / 5;
					if (index < 0) index = 0;
					if (index > 5) index = 5;
					volumeHandler.sendEmptyMessage(index);
				}
			}
		}
	}

	/**
	 *
	 */
	public interface RecordEventListener {
		public void onFinishedRecord(String audioPath, int secs);
		void onStartRecord();
	}

	/**
	 * 接收ObtainDecibelThread线程传过来的值，然后显示不同的音量分贝图
	 */
	class ShowVolumeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			imageView.setImageResource(recordImageIds[msg.what]);
		}
	}

}
