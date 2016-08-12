package htf.scme.org.Activity;

import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;

import com.baidu.mapapi.map.MyLocationData;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;


import java.util.ArrayList;
import java.util.List;

import htf.scme.org.bgims.PlaceListAdapter;
import htf.scme.org.bgims.R;
import htf.scme.org.bgims.Tool;
import io.rong.message.LocationMessage;

public class MapLocationActivity extends AppCompatActivity {

  MapView mMapView;
  BaiduMap baiduMap;
  private TextView text_go, toolbar_tetle;
  private RelativeLayout btn_go;
  //当前经纬度

  private double jingdu;
  private double weidu;
  LatLng mLoactionLatLng;
  private String adderss = "";
  // 定位相关声明
  LocationClient locationClient;
  LocationMessage mMsg;
  //自定义图标
  boolean isFirstLoc = true;// 是否首次定位
  // MapView中央对于的屏幕坐标
  Point mCenterPoint = null;

  // 地理编码
  GeoCoder mGeoCoder = null;


  // 位置列表
  ListView mListView;
  // PlaceListAdapter mAdapter;
  List<PoiInfo> mInfoList;
  PoiInfo mCurentInfo;

  ListView Maplistview;
  PlaceListAdapter customListAdpter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_location);
    initview();
    getimgxy();
  }

  private void initview() {

    text_go = (TextView) findViewById(R.id.toolbar_edit);
    btn_go = (RelativeLayout) findViewById(R.id.btn_go);
    toolbar_tetle = (TextView) findViewById(R.id.toolbar_tetle);
    Maplistview = (ListView) findViewById(R.id.mymapListView);
    text_go.setText("发送");
    toolbar_tetle.setText("地理位置");
    btn_go.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Uri uri = Uri.parse("http://api.map.baidu.com/staticimage?width=300&height=200&center="+jingdu + "," + weidu + "&zoom=17&markers=" + jingdu + "," + weidu + "&markerStyles=m,A");
        LocationMessage locationMessage = LocationMessage.obtain(weidu, jingdu, adderss, uri);
        //如果地图地位成功，那么调用
        Tool.mLastLocationCallback.onSuccess(locationMessage);
        //如果地图地位失败，那么调用
        Tool.mLastLocationCallback.onFailure("定位失败!");
        finish();
      }
    });
    mMapView = (MapView) findViewById(R.id.bmapView);
    mMapView.showZoomControls(false);
    baiduMap = mMapView.getMap();
    MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
    baiduMap.setMapStatus(msu);
    //开启定位图层
    baiduMap.setMyLocationEnabled(true);
    baiduMap.setOnMapTouchListener(touchListener);


    try {
      if (getIntent().hasExtra("location")) {
        mMsg = getIntent().getParcelableExtra("location");
      }
      if (mMsg != null) {
        btn_go.setVisibility(View.INVISIBLE);
        text_go.setText("");
        Maplistview.setVisibility(View.GONE);
        locationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类
        MyLocationData locData = new MyLocationData.Builder()

          // 此处设置开发者获取到的方向信息，顺时针0-360
          .direction(100).latitude(mMsg.getLat())
          .longitude(mMsg.getLng()).build();
        baiduMap.setMyLocationData(locData);    //设置定位数据
        mLoactionLatLng = new LatLng(mMsg.getLat(),
          mMsg.getLng());
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(mLoactionLatLng, 16);    //设置地图中心点以及缩放级别
        baiduMap.animateMapStatus(u);
      } else {
        locationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类
        locationClient.registerLocationListener(myListener); // 注册监听函数
        this.setLocationOption();    //设置定位参数
        locationClient.start(); // 开始定位
      }
    } catch (Exception e) {

    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    mMapView.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
    //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
    mMapView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
    mMapView.onPause();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      locationClient.stop();

      Log.d("stop", "定位关闭");
      finish();
    }

    return false;

  }

  /**
   * 设置定位参数
   */
  private void setLocationOption() {
    LocationClientOption option = new LocationClientOption();
    option.setOpenGps(true); // 打开GPS
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
    option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
    option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
    option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
    option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
    locationClient.setLocOption(option);
  }

  public BDLocationListener myListener = new BDLocationListener() {
    @Override
    public void onReceiveLocation(BDLocation location) {
      // map view 销毁后不在处理新接收的位置
      if (location == null || mMapView == null)
        return;
      weidu = location.getLatitude();
      jingdu = location.getLongitude();
      adderss = location.getAddrStr();
      MyLocationData locData = new MyLocationData.Builder()
        /*.accuracy(location.getRadius())*/
        // 此处设置开发者获取到的方向信息，顺时针0-360
        .direction(100).latitude(location.getLatitude())
        .longitude(location.getLongitude()).build();
      baiduMap.setMyLocationData(locData);    //设置定位数据
      if (isFirstLoc) {
        isFirstLoc = false;

        mLoactionLatLng = new LatLng(location.getLatitude(),
          location.getLongitude());
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(mLoactionLatLng, 20);//设置地图中心点以及缩放级别
        baiduMap.animateMapStatus(u);
      }
      // 获取当前MapView中心屏幕坐标对应的地理坐标
      LatLng currentLatLng;
      currentLatLng = baiduMap.getProjection().fromScreenLocation(
        mCenterPoint);
      System.out.println("----" + mCenterPoint.x);
      System.out.println("----" + currentLatLng.latitude);
      // 发起反地理编码检索
      mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
        .location(currentLatLng));
    }
  };

  /**
   * 初始化地图物理坐标
   */
  private void getimgxy() {
    // 初始化POI信息列表
    mInfoList = new ArrayList<PoiInfo>();
    mCenterPoint = baiduMap.getMapStatus().targetScreen;
    mLoactionLatLng = baiduMap.getMapStatus().target;
// 地理编码
    mGeoCoder = GeoCoder.newInstance();
    mGeoCoder.setOnGetGeoCodeResultListener(GeoListener);
    customListAdpter = new PlaceListAdapter(getLayoutInflater(), mInfoList);
    Maplistview.setAdapter(customListAdpter);
    Maplistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        customListAdpter.clearSelection(i);
        customListAdpter.notifyDataSetChanged();
        locationClient.stop();
        baiduMap.clear();
        PoiInfo info = (PoiInfo) customListAdpter.getItem(i);
        LatLng la = info.location;
        weidu = la.latitude;
        jingdu = la.longitude;
        adderss = info.address;

        MyLocationData locData = new MyLocationData.Builder()
        /*.accuracy(location.getRadius())*/
          // 此处设置开发者获取到的方向信息，顺时针0-360
          .direction(100).latitude(la.latitude)
          .longitude(la.longitude).build();
        baiduMap.setMyLocationData(locData);
        //设置定位数据
        mLoactionLatLng = new LatLng(la.latitude,
          la.longitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(mLoactionLatLng, 20);    //设置地图中心点以及缩放级别
        baiduMap.animateMapStatus(u);
      }
    });

  }

  // 地理编码监听器
  OnGetGeoCoderResultListener GeoListener = new OnGetGeoCoderResultListener() {
    public void onGetGeoCodeResult(GeoCodeResult result) {
      if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
        // 没有检索到结果
      }
      // 获取地理编码结果
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
      if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
        // 没有找到检索结果
      }
      // 获取反向地理编码结果
      else {
        // 当前位置信息
        mCurentInfo = new PoiInfo();
        mCurentInfo.address = result.getAddress();
        mCurentInfo.location = result.getLocation();
        mCurentInfo.name = "[位置]";
        mInfoList.clear();
        mInfoList.add(mCurentInfo);

        // 将周边信息加入表
        if (result.getPoiList() != null) {
          mInfoList.addAll(result.getPoiList());
        }
        // 通知适配数据已改变
        customListAdpter.notifyDataSetChanged();
       /* mLoadBar.setVisibility(View.GONE);*/

      }
    }
  };


  // 地图触摸事件监听器
  BaiduMap.OnMapTouchListener touchListener = new BaiduMap.OnMapTouchListener() {
    @Override
    public void onTouch(MotionEvent event) {
      // TODO Auto-generated method stub
      if (event.getAction() == MotionEvent.ACTION_UP) {

        if (mCenterPoint == null) {
          return;
        }

        // 获取当前MapView中心屏幕坐标对应的地理坐标
        LatLng currentLatLng;
        currentLatLng = baiduMap.getProjection().fromScreenLocation(
          mCenterPoint);
        System.out.println("----" + mCenterPoint.x);
        System.out.println("----" + currentLatLng.latitude);
        // 发起反地理编码检索
        mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
          .location(currentLatLng));
        //mLoadBar.setVisibility(View.VISIBLE);

      }
    }
  };


}