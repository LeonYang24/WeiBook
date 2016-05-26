package com.leon.weibook.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.leon.weibook.R;
import com.leon.weibook.util.LogUtils;

/**
 * 选取地理位置、展现地理位置
 * Created by Leon on 2016/5/21 0021.
 */
public class LocationActivity extends AVBaseActivity implements OnGetGeoCoderResultListener {

	public static final int SEND = 0;
	public static final String TYPE = "type";
	public static final String TYPE_SELECT = "select";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String TYPE_SCAN = "scan";
	public static final String ADDRESS = "address";
	private static BDLocation lastLocation = null;
	private LocationClient locClient;
	private MyLocationListener myListener = new MyLocationListener();
	private MapView mapView;
	private BaiduMap baiduMap;
	private BaiduReceiver receiver;
	private GeoCoder geoCoder = null;//地理编码查询接口
	private BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(
			R.mipmap.chat_location_activity_icon_geo);
	private String intentType;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_location);
		initBaiduMap();
	}

	/**
	 * 初始化百度地图
	 */
	private void initBaiduMap() {
		setTitle(R.string.chat_position);
		mapView = (MapView) findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();

		IntentFilter iFilter = new IntentFilter();
		//key 验证失败广播
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		//网络错误广播
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		//监测SDK错误，只监测key验证和网络错误两个广播
		receiver = new BaiduReceiver();
		registerReceiver(receiver, iFilter);

		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(this);

		Intent intent = getIntent();
		intentType = intent.getStringExtra(TYPE);
		if (intentType.equals(TYPE_SELECT)) {
			// 选择发送位置
			// 开启定位图层
			baiduMap.setMyLocationEnabled(true);
			baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
					MyLocationConfiguration.LocationMode.NORMAL, true, null));
			// 定位初始化
			locClient = new LocationClient(this);
			locClient.registerLocationListener(myListener);
			initLocation();
			locClient.start();
			if (locClient != null && locClient.isStarted()) {
				locClient.requestLocation();
			}
			if (lastLocation != null) {
				// 显示在地图上
				LatLng ll = new LatLng(lastLocation.getLatitude(),
						lastLocation.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);
			}
		} else {
			Bundle b = intent.getExtras();
			LatLng latLng = new LatLng(b.getDouble(LATITUDE), b.getDouble(LONGITUDE));//维度在前，经度在后
			baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
			OverlayOptions ooA = new MarkerOptions().position(latLng).icon(descriptor).zIndex(9);
			baiduMap.addOverlay(ooA);
		}
	}

	/**
	 * 设置定位参数包括：定位模式（高精度定位模式，低功耗定位模式和仅用设备定位模式），返回坐标类型，是否打开GPS，是否返回地址信息、位置语义化信息、POI信息等等。
	 */
	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
		);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		//bd09ll（百度经纬度坐标）、bd09mc（百度摩卡托坐标）
		//gcj02（国测局加密坐标）、wgs84（gps设备获取的坐标）
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
		int span=1000;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		locClient.setLocOption(option);
	}

	@Override
	protected void onDestroy() {
		if (locClient != null && locClient.isStarted()) {
			// 退出时销毁定位
			locClient.stop();
		}
		// 关闭定位图层
		baiduMap.setMyLocationEnabled(false);
		mapView.onDestroy();
		mapView = null;
		// 取消监听 SDK 广播
		unregisterReceiver(receiver);
		super.onDestroy();
		// 回收 bitmap 资源
		descriptor.recycle();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (intentType != null && intentType.equals(TYPE_SELECT)) {
			MenuItem add = menu.add(0, SEND, 0, R.string.chat_location_activity_send);
		}
		alwaysShowMenuItem(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (SEND == id) {
			gotoChatPage();
		}
		return super.onOptionsItemSelected(item);
	}

	private void gotoChatPage() {
		if (lastLocation != null) {
			Intent intent = new Intent();
			intent.putExtra(LATITUDE, lastLocation.getLatitude());// 经度
			intent.putExtra(LONGITUDE, lastLocation.getLongitude());// 维度
			intent.putExtra(ADDRESS, lastLocation.getAddrStr());
			setResult(RESULT_OK, intent);
			this.finish();
		} else {
			showToast(R.string.chat_getGeoInfoFailed);
		}
	}

	/**
	 * 地理编码查询结果回调函数
	 * @param geoCodeResult
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

	}

	/**
	 * 反地理编码查询结果回调函数
	 * @param result
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			toast(getString(R.string.chat_cannotFindResult));
			return;
		}
		LogUtils.d(getString(R.string.chat_reverseGeoCodeResultIs) + result.getAddress());
		lastLocation.setAddrStr(result.getAddress());
	}

	public static void startToSeeLocationDetail(Context ctx, double latitude, double longitude) {
		Intent intent = new Intent(ctx, LocationActivity.class);
		intent.putExtra(LocationActivity.TYPE, LocationActivity.TYPE_SCAN);
		intent.putExtra(LocationActivity.LATITUDE, latitude);
		intent.putExtra(LocationActivity.LONGITUDE, longitude);
		ctx.startActivity(intent);
	}

	public static void startToSelectLocationForResult(Activity from, int requestCode) {
		Intent intent = new Intent(from, LocationActivity.class);
		intent.putExtra(LocationActivity.TYPE, LocationActivity.TYPE_SELECT);
		from.startActivityForResult(intent, requestCode);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mapView == null) {
				return;
			}
			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude()
						&& lastLocation.getLongitude() == location
						.getLongitude()) {
					LogUtils.d(getString(R.string.chat_geoIsSame));// 若两次请求获取到的地理位置坐标是相同的，则不再定位
					locClient.stop();
					return;
				}
			}
			lastLocation = location;

			LogUtils.d("lontitude = " + location.getLongitude() + ",latitude = "
					+ location.getLatitude() + "," + getString(R.string.chat_position) + " = "
					+ lastLocation.getAddrStr());

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			String address = location.getAddrStr();
			if (address != null && !address.equals("")) {
				lastLocation.setAddrStr(address);
			} else {
				// 反Geo搜索
				geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			}
			// 显示在地图上
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			baiduMap.animateMapStatus(u);
			//设置按钮可点击
		}
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				toast(getString(R.string.chat_location_mapKeyErrorTips));
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				toast(getString(R.string.chat_pleaseCheckNetwork));
			}
		}
	}

}
