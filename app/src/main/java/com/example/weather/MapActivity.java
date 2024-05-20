package com.example.weather;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.weather.data.MyCity;
import com.example.weather.util.ChangeIcon;
import com.example.weather.util.ToastUtils;
import com.google.gson.Gson;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.List;


/**
 * 拖拽定位位置,DragLocation and drop
 */
public class MapActivity extends Activity implements
        AMap.OnMarkerClickListener,
        AMap.OnMapLoadedListener,
        LocationSource,
        AMapLocationListener,
        GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnCameraChangeListener,
        View.OnClickListener {

    private MapView mMapView;
    private AMap mAMap;
    private Marker mGPSMarker;             //定位位置显示
    private AMapLocation location;
    private LocationSource.OnLocationChangedListener mListener;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //你编码对象
    private GeocodeSearch geocoderSearch;

    private String custAddr;
    private Double custLon;
    private Double custLat;
    private String actualAddr;
    private Double actualLon;
    private Double actualLat;
    private ImageView img_back;
    private String city;
    private MarkerOptions markOptions;
    private LatLng latLng;
    private String addressName;
    private AutoTransition autoTransition;//过渡动画
    private Animation bigShowAnim;//放大显示
    private Animation smallHideAnim;//缩小隐藏
    private int width;//屏幕宽度
    //顶部搜索布局的状态
    ImageView ivSearch;
    EditText edSearch;
    ImageView ivClose, ivWeather;
    RelativeLayout laySearch, weatherLayout;//搜索布局
    TextView tvCity, tvTemperature, tvWeatherState, tvAir, tvWind, tv_Vis, tv_humidity, tv_pressure;

    public MapActivity() {
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        try {
            initMap(savedInstanceState);
        } catch (AMapException e) {
            throw new RuntimeException(e);
        }
        //获取屏幕宽高
        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;  //获取屏幕的宽度 像素

        laySearch = findViewById(R.id.lay_search);
        weatherLayout = findViewById(R.id.weather_layout);

        edSearch = findViewById(R.id.ed_search);

        tvTemperature = findViewById(R.id.tv_temperature);
        tvWeatherState = findViewById(R.id.tv_weather_state);
        tvCity = findViewById(R.id.tv_city);
        tv_pressure = findViewById(R.id.tv_pressure);
        tv_humidity = findViewById(R.id.tv_humidity);
        tvWind = findViewById(R.id.tv_wind);
        tv_Vis = findViewById(R.id.tv_Vis);
        tvAir = findViewById(R.id.tv_air);

        ivWeather = findViewById(R.id.iv_weather);
        ivSearch = findViewById(R.id.iv_search);
        ivClose = findViewById(R.id.iv_close);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.iv_search).setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.ed_search).setOnClickListener(this);
        initEdit();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        } else if (view.getId() == R.id.iv_search) {
            initExpand();
            Log.d("searchbtn", "onClick: ");
        } else if (view.getId() == R.id.iv_close) {
            initClose();
            weatherLayout.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.ed_search) {
            weatherLayout.setVisibility(View.GONE);
        }
    }

    private void initEdit() {
        //软键盘回车变搜索按钮
        edSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String input = edSearch.getText().toString();
                    if (!TextUtils.isEmpty(input)) {
                        city = input;
                        QWeather.getGeoCityLookup(MapActivity.this, city, new QWeather.OnResultGeoListener() {
                            public static final String TAG = "hefengCity";

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG, "onError: ", e);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showToast(MapActivity.this, "请输入正确的地名");
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(GeoBean geoBean) {
                                if (Code.OK == geoBean.getCode()) {
                                    double lat = Double.parseDouble(geoBean.getLocationBean().get(0).getLat());
                                    double lon = Double.parseDouble(geoBean.getLocationBean().get(0).getLon());
                                    city = geoBean.getLocationBean().get(0).getName();
                                    latLng = new LatLng(lat, lon);
                                    updateMapCenter(latLng);
                                }
                            }
                        });
                        weatherLayout.setVisibility(View.VISIBLE);
                        //关闭软键盘
                        closeKeybord(MapActivity.this);

                    }
                    return true;
                }
                return false;
            }
        });
    }


    private void initMap(Bundle savedInstanceState) throws AMapException {
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        geocoderSearch = new GeocodeSearch(this);
        mAMap = mMapView.getMap();
        // 设置定位监听
        mAMap.setOnMapLoadedListener(this);
        mAMap.setOnMarkerClickListener(this);
//        mAMap.setOnMapClickListener(this);

        mAMap.setLocationSource(this);
        //设置地图拖动监听
        mAMap.setOnCameraChangeListener(this);

        geocoderSearch.setOnGeocodeSearchListener(this);

        //导航箭头定位的小图标，这里自定义一张图片
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);

        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(30, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
        myLocationStyle.anchor(0.5f, 0.7f);
        mAMap.setMyLocationStyle(myLocationStyle);

        mAMap.moveCamera(CameraUpdateFactory.zoomTo(12)); //缩放比例

        //设置amap的属性
        UiSettings settings = mAMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        settings.setZoomControlsEnabled(false);
//        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        location = aMapLocation;
        city = location.getDistrict();
        if (mListener != null && location != null) {
            if (location != null && location.getErrorCode() == 0) {
                mListener.onLocationChanged(location);// 显示系统箭头

                LatLng la = new LatLng(location.getLatitude(), location.getLongitude());

                setMarket(la, location.getAddress());
                this.actualAddr = location.getAddress();
                this.actualLon = location.getLongitude();
                this.actualLat = location.getLatitude();
                mLocationClient.stopLocation();

            }
        } else {
            ToastUtils.showToast(this, "定位失败");
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //设置定位回调监听
        mLocationClient.setLocationListener(this);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000 * 10);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }


    @Override
    public void onMapLoaded() {

    }

    /**
     * 改变地图中心位置
     *
     * @param latLng 位置
     */
    private void updateMapCenter(LatLng latLng) {
        // CameraPosition 第一个参数： 目标位置的屏幕中心点经纬度坐标。
        // CameraPosition 第二个参数： 目标可视区域的缩放级别
        // CameraPosition 第三个参数： 目标可视区域的倾斜度，以角度为单位。
        // CameraPosition 第四个参数： 可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度
        CameraPosition cameraPosition = new CameraPosition(latLng, 16, 30, 0);
        //位置变更
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        //改变位置
        mAMap.moveCamera(cameraUpdate);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();

        } else {
            marker.showInfoWindow();
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        // aMapEx.onRegister();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁定位
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mMapView.onDestroy();
    }

    private void setMarket(LatLng latLng, String content) {
        if (mGPSMarker != null) {
            mGPSMarker.remove();
        }
        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = (wm.getDefaultDisplay().getWidth()) / 2;
        int height = ((wm.getDefaultDisplay().getHeight()) / 2) - 80;
        markOptions = new MarkerOptions();
        markOptions.draggable(true);//设置Marker可拖动
        //设置一个角标
        mGPSMarker = mAMap.addMarker(markOptions);
        //设置marker在屏幕的像素坐标
        mGPSMarker.setPosition(latLng);
        mGPSMarker.setTitle("当前地址");
        mGPSMarker.setSnippet(content);
        //设置像素坐标
        mGPSMarker.setPositionByPixels(width, height);
        if (!TextUtils.isEmpty(content)) {
            mGPSMarker.showInfoWindow();
        }
        mMapView.invalidate();
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        latLng = cameraPosition.target;
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        Log.i("latitude", latitude + "");
        Log.i("longitude", longitude + "");
        getAddress(latLng);
        String str = longitude + "," + latitude;
        QWeather.getGeoCityLookup(MapActivity.this, str, new QWeather.OnResultGeoListener() {
            public static final String TAG = "hefengCity";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                if (Code.OK == geoBean.getCode()) {
                    city = geoBean.getLocationBean().get(0).getName();
                    String id = geoBean.getLocationBean().get(0).getId();
                    getNowTemp(id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCity.setText(city);
                        }
                    });
                } else {
                    //在此查看返回数据失败的原因
                    Code code = geoBean.getCode();
                    System.out.println("失败代码: " + code);
                }
            }
        });
    }

    /**
     * 根据经纬度得到地址
     */
    public void getAddress(final LatLng latLng) {
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress();       // 逆转地里编码不是每次都可以得到对应地图上的opi
                Log.i("逆地理编码回调  得到的地址：", addressName);
                setMarket(latLng, addressName);
            }
        }
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }

    // dp 转成 px
    private int dip2px(float dpVale) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }

    // px 转成 dp
    private int px2dip(float pxValue) {
        final float scale = (MapActivity.this).getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 展开
     */
    public void initExpand() {
        edSearch.setVisibility(View.VISIBLE);//显示输入框
        ivClose.setVisibility(View.VISIBLE);//显示关闭按钮
        LinearLayout.LayoutParams LayoutParams = (LinearLayout.LayoutParams) laySearch.getLayoutParams();
        LayoutParams.width = dip2px(px2dip(width) - 100);//设置展开的宽度
        LayoutParams.setMargins(dip2px(0), dip2px(0), dip2px(0), dip2px(0));
        laySearch.setPadding(14, 0, 14, 0);
        laySearch.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(laySearch);
        weatherLayout.setVisibility(View.GONE);
    }

    /**
     * 收缩
     */
    private void initClose() {
        edSearch.setVisibility(View.GONE);
        edSearch.setText("");
        ivClose.setVisibility(View.GONE);

        LinearLayout.LayoutParams LayoutParams = (LinearLayout.LayoutParams) laySearch.getLayoutParams();
        LayoutParams.width = dip2px(48);
        LayoutParams.height = dip2px(48);
        LayoutParams.setMargins(dip2px(0), dip2px(0), dip2px(0), dip2px(0));
        laySearch.setLayoutParams(LayoutParams);

        //隐藏键盘
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);

        //开始动画
        beginDelayedTransition(laySearch);
    }

    //过渡动画
    private void beginDelayedTransition(ViewGroup view) {
        autoTransition = new AutoTransition();
        autoTransition.setDuration(500);
        TransitionManager.beginDelayedTransition(view, autoTransition);
    }

    public static void closeKeybord(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }


    public void getNowTemp(String id) {
        String TAG = "hefeng";
        QWeather.getWeatherNow(this, id, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    String temp = now.getTemp();
                    String text = now.getText();
                    String vis = now.getVis() + "公里";
                    String pressure = now.getPressure() + "hPa";
                    String humidity = now.getHumidity() + "%";
                    String wind = now.getWindDir() + now.getWindScale() + "级";
                    int icon = Integer.parseInt(now.getIcon());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTemperature.setText(temp + "°");
                            tvWeatherState.setText(text);
                            ChangeIcon.changeIcon(ivWeather, icon);
                            tv_Vis.setText(vis);
                            tv_pressure.setText(pressure);
                            tv_humidity.setText(humidity);
                            tvWind.setText(wind);
                        }
                    });
                }
            }

        });
        QWeather.getAirNow(this, id, Lang.ZH_HANS, new QWeather.OnResultAirNowListener() {

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                String air = "AQI " + airNowBean.getNow().getCategory();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvAir.setText(air);
                    }
                });
            }
        });
    }
}