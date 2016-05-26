package com.leon.weibook;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.mapapi.SDKInitializer;
import com.leon.weibook.controller.ChatManager;
import com.leon.weibook.model.AddRequest;
import com.leon.weibook.model.LeanChatUser;
import com.leon.weibook.model.UpdateInfo;
import com.leon.weibook.service.PushManager;
import com.leon.weibook.util.LeanChatUserProvider;
import com.leon.weibook.util.ThirdPartUserUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 用来初始化各种SDK
 * Created by Leon on 2016/5/13 0013.
 */
public class App extends Application {
	public static boolean debug = true;
	public static App ctx;

	@Override
	public void onCreate() {
		super.onCreate();
		ctx = this;

		String appId = "6UfWCOpoIVKCFLp3WXwnVqwY-gzGzoHsz";
		String appKey = "AVsKUSAHQzwgd7VJDBlQncEH";

		/**
		 * 当用户子类化 AVUser 后，如果希望以后查询 AVUser 所得到的对象会自动转化为用户子类化的对象，
		 * 则需要在调用 AVOSCloud.initialize() 之前添加alwaysUseSubUserClass
		 * 通过设置此方法，所有关联对象中的AVUser对象都会被强转成注册的AVUser子类对象
		 */
		LeanChatUser.alwaysUseSubUserClass(LeanChatUser.class);

		/** 注册子类化类 */
		AVObject.registerSubclass(AddRequest.class);
		AVObject.registerSubclass(UpdateInfo.class);

		/** LeanCloud云服务初始化 */
		AVOSCloud.initialize(this, appId, appKey);

		/** 百度APIStore Key初始化 */
		ApiStoreSDK.init(this, "3d6e00155c32618d0975467a20c82007");

		PushManager.getInstance().init(ctx);

		initImageLoader(ctx);
		initBaiduMap();

		ThirdPartUserUtils.setThirdPartUserProvider(new LeanChatUserProvider());

		ChatManager.getInstance().init(this);


	}

	/**
	 * 要使用ImageLoader必须先初始化，否则会抛出异常
	 * “java.lang.IllegalStateException: ImageLoader must be init with configuration before using”
	 */
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				//.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 百度地图SDK初始化
	 */
	private void initBaiduMap() {
		SDKInitializer.initialize(this);
	}

}
