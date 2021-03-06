package com.leon.weibook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.leon.weibook.R;
import com.leon.weibook.event.InputBottomBarLocationClickEvent;
import com.leon.weibook.event.LocationItemClickEvent;
import com.leon.weibook.util.Constants;
import com.leon.weibook.util.NotificationUtils;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Leon on 2016/5/16 0016.
 */
public class ChatRoomActivity extends AVChatActivity{

	public static final int LOCATION_REQUEST = 100;
	public static final int QUIT_GROUP_REQUEST = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		NotificationUtils.cancelNotification(this);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat_ativity_menu, menu);
		alwaysShowMenuItem(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		if (menuId == R.id.people) {
			if (null != conversation) {
				Intent intent = new Intent(ChatRoomActivity.this, ConversationDetailActivity.class);
				intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
				startActivityForResult(intent, QUIT_GROUP_REQUEST);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case LOCATION_REQUEST:
					final double latitude = intent.getDoubleExtra(LocationActivity.LATITUDE, 0);
					final double longitude = intent.getDoubleExtra(LocationActivity.LONGITUDE, 0);
					final String address = intent.getStringExtra(LocationActivity.ADDRESS);
					if (!TextUtils.isEmpty(address)) {
						AVIMLocationMessage locationMsg = new AVIMLocationMessage();
						locationMsg.setLocation(new AVGeoPoint(latitude, longitude));
						locationMsg.setText(address);
						chatFragment.sendMessage(locationMsg);
					} else {
						toast(R.string.chat_cannotGetYourAddressInfo);
					}
					break;
				case QUIT_GROUP_REQUEST:
					finish();
					break;
			}
		}
	}


	@Subscribe
	public void onEvent(InputBottomBarLocationClickEvent event) {
		LocationActivity.startToSelectLocationForResult(this, LOCATION_REQUEST);
	}

	@Subscribe
	public void onEvent(LocationItemClickEvent event) {
		if (null != event && null != event.message && event.message instanceof AVIMLocationMessage) {
			AVIMLocationMessage locationMessage = (AVIMLocationMessage) event.message;
			LocationActivity.startToSeeLocationDetail(this, locationMessage.getLocation().getLatitude(),
					locationMessage.getLocation().getLongitude());
		}
	}
}
