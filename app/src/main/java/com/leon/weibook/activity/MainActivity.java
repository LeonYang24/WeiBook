package com.leon.weibook.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.leon.weibook.R;
import com.leon.weibook.fragment.FragmentContact;
import com.leon.weibook.fragment.FragmentConversation;
import com.leon.weibook.fragment.FragmentDiscover;
import com.leon.weibook.fragment.FragmentSetting;
import com.leon.weibook.service.UpdateService;

import java.io.InputStream;
import java.lang.reflect.Method;

import butterknife.BindView;

public class MainActivity extends AVBaseActivity {

	@BindView(R.id.btn_conversation) Button mbtnConversation;
	@BindView(R.id.btn_contact) Button mbtnContact;
	@BindView(R.id.btn_discover) Button mbtnDiscover;
	@BindView(R.id.btn_setting) Button mbtnSetting;

	//Fragment跳转相关逻辑涉及的变量
	public static final int FRAGMENT_N = 4;
	private static final String FRAGMENT_TAG_CONVERSATION = "chat";
	private static final String FRAGMENT_TAG_CONTACT = "contact";
	private static final String FRAGMENT_TAG_DISCOVER = "discover";
	private static final String FRAGMENT_TAG_SETTING = "setting";
	private static final String[] fragmentTags = new String[]{FRAGMENT_TAG_CONVERSATION,
															  FRAGMENT_TAG_CONTACT,
															  FRAGMENT_TAG_DISCOVER,
															  FRAGMENT_TAG_SETTING};
	public static final int[] tabsNormalBackIds = new int[] {R.mipmap.tabbar_conversation_normal,
															 R.mipmap.tabbar_contact_normal,
															 R.mipmap.tabbar_discover_normal,
															 R.mipmap.tabbar_setting_normal};
	public static final int[] tabsSelectBackIds = new int[] {R.mipmap.tabbar_conversation_select,
															 R.mipmap.tabbar_contact_select,
															 R.mipmap.tabbar_discover_select,
															 R.mipmap.tabbar_setting_select};

	Button[] tabs;

	//用于管理Fragment
	private FragmentManager mFragmentManager = null;
	private FragmentConversation fragmentConversation = null;
	private FragmentContact fragmentContact = null;
	private FragmentSetting fragmentSetting = null;
	private FragmentDiscover fragmentDiscover = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

	}

	/**
	 * 初始化按钮数组和调用mbtnConversation按钮的监听事件
	 */
	public void init() {
		tabs = new Button[]{mbtnConversation, mbtnContact, mbtnDiscover, mbtnSetting};
		mbtnConversation.performClick();
	}

	/**
	 * 为每个fragment设置了TAG
	 * 在mainActivity的layout布局文件中对每个button都设置了监听
	 * @param view
	 */
	public void onTabSelect(View view) {
		int id = view.getId();
		mFragmentManager = getFragmentManager();
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		hideFragments(mFragmentManager, transaction);
		setNormalBackgrounds();
		switch (id) {
			case R.id.btn_conversation :
				if (null == fragmentConversation) {
					fragmentConversation = new FragmentConversation();
					transaction.add(R.id.fragment_container, fragmentConversation,
							FRAGMENT_TAG_CONVERSATION);
				}
				transaction.show(fragmentConversation);
				break;
			case R.id.btn_contact :
				if (null == fragmentContact) {
					fragmentContact = new FragmentContact();
					transaction.add(R.id.fragment_container, fragmentContact,
							FRAGMENT_TAG_CONTACT);
				}
				transaction.show(fragmentContact);
				break;
			case R.id.btn_discover :
				if (null == fragmentDiscover) {
					fragmentDiscover = new FragmentDiscover();
					transaction.add(R.id.fragment_container, fragmentDiscover,
							FRAGMENT_TAG_DISCOVER);
				}
				transaction.show(fragmentDiscover);
				break;
			case R.id.btn_setting :
				if (null == fragmentSetting) {
					fragmentSetting = new FragmentSetting();
					transaction.add(R.id.fragment_container, fragmentSetting,
							FRAGMENT_TAG_SETTING);
				}
				transaction.show(fragmentSetting);
				break;
		}
		int pos;
		//检测到点击的view就退出循环
		for (pos = 0; pos < FRAGMENT_N; pos++) {
			if (tabs[pos] == view) {
				break;
			}
		}
		transaction.commit();
		setTopDrawable(tabs[pos], tabsSelectBackIds[pos]);
	}

	/**
	 * 初始化底部tabbar的点击图片为选中状态
	 * 注意：这里之所以设置TopDrawable，是因为在xml中，button的图片属性是drawableTop
	 */
	private void setTopDrawable(Button v, int resId) {
		v.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(resId),
				null, null);
	}

	/**
	 * 初始化底部tabbar的图片为未选中状态
	 */
	private void setNormalBackgrounds() {
		for (int i = 0; i < tabs.length; i++) {
			Button v = tabs[i];
			setTopDrawable(v, tabsNormalBackIds[i]);
		}
	}

	/**
	 * 通过前面onTabSelect（）设置的TAG来隐藏fragment
	 * @param fragmentManager
	 * @param transaction
	 */
	private void hideFragments (FragmentManager fragmentManager, FragmentTransaction transaction) {
		for (int i = 0; i < fragmentTags.length; i++ ) {
			Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags[i]);
			if (fragment != null && fragment.isVisible()) {
				transaction.hide(fragment);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		UpdateService updateService = UpdateService.getInstance(this);
		updateService.checkUpdate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main_action_bar, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuitem_group_chat_start :
				break;
			case R.id.menuitem_add_new_friend :
				jumpToAddNewFriendActivity();
				break;
			case R.id.menuitem_rich_sacn :
				break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && null != menu) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
					toast(e.toString());
				}
			}
		}

		return super.onMenuOpened(featureId, menu);
	}

	private void jumpToAddNewFriendActivity() {
		Intent intent = new Intent(this, ContactAddFriendActivity.class);
		startActivity(intent);
	}

}
