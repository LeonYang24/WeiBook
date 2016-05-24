package com.leon.weibook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.leon.weibook.R;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.PhotoUtils;

/**
 * Created by Leon on 2016/5/19 0019.
 */
public class ImageBrowserActivity extends Activity {

	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_image_brower_layout);
		imageView = (ImageView) findViewById(R.id.imageView);
		Intent intent = getIntent();
		String path = intent.getStringExtra(Constants.IMAGE_LOCAL_PATH);
		String url = intent.getStringExtra(Constants.IMAGE_URL);
		PhotoUtils.displayImageCacheElseNetwork(imageView, path, url);
	}
}
