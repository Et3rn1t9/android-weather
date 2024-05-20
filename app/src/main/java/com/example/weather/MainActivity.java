package com.example.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.MapsInitializer;
import com.example.weather.adapter.DaysAdapter;
import com.example.weather.adapter.HourlsAdapter;
import com.example.weather.adapter.RainAdapter;
import com.example.weather.data.DaysData;
import com.example.weather.data.HoursData;
import com.example.weather.data.MyCity;
import com.example.weather.data.RainData;
import com.example.weather.util.QuoteUtil;
import com.example.weather.util.SpfUtil;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.grid.GridMinutelyBean;
import com.qweather.sdk.bean.warning.WarningBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AMapLocationListener, View.OnClickListener {
    private TextView tv_City;
    private String locateCity, locateProvince;
    private double lon, lat;
    private String LocationID;
    private PopupWindow mPopupWindow;
    private CityDbOpenHelper cityDbOpenHelper;

    TextView tvPrecMore, tvNotify;
    RecyclerView fore_rain, hoursWeather, daysWeather;
    private List<RainData> rainDataList = new ArrayList<>();//分钟级降水数据列表
    private List<HoursData> hoursDataList = new ArrayList<>();
    private List<DaysData> daysDataList = new ArrayList<>();
    private RainAdapter rainAdapter;//分钟级降水适配器
    private HourlsAdapter hourlsAdapter;
    private DaysAdapter daysAdapter;
    private boolean state = false;//分钟级降水数据收缩  false 收缩  true 展开
    private int wallpaper;
    private String imagePath;

    public static final String KEY_WALLPAPER = "wallpaper_id";
    public static final String KEY_IMAGEPATH = "image_path";
    public static final String WALLPAPER_CHOOSE = "sourceofwallpaper";
    public static final int SOUCREOFLOCAL = 0;
    public static final int SOUCREOFSYSTEM = 1;


    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("onCreate", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat();
        String formattedTime = sdf.format(date).substring(11);
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);

        tv_City = findViewById(R.id.tv_city);
        fore_rain = findViewById(R.id.fore_rain);
        tvPrecMore = findViewById(R.id.tv_precMore);
        TextView tv_lastTime = findViewById(R.id.tv_lastTime);
        tvNotify = findViewById(R.id.tv_notify);
        hoursWeather = findViewById(R.id.hoursWeather);
        daysWeather = findViewById(R.id.daysWeather);
        tv_lastTime.setText(tv_lastTime.getText() + formattedTime);
        cityDbOpenHelper = new CityDbOpenHelper(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 若没有权限，则请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            initLocation();
        }

        //添加：设置图片背景
        changeBackGround();

        mPopupWindow = new PopupWindow(MainActivity.this);
        findViewById(R.id.btn_map).setOnClickListener(this);
        findViewById(R.id.tv_precMore).setOnClickListener(this);
        findViewById(R.id.tv_moreDays).setOnClickListener(this);
        findViewById(R.id.iv_add).setOnClickListener(this);
        //和风sdk key
        HeConfig.init("", "");
        HeConfig.switchToBizService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_map) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_precMore) {
            Log.d("hhz", "onClick: " + state);
            if (state) {//收缩
                fore_rain.setVisibility(View.GONE);
                state = false;
                tvPrecMore.setText("查看详情");
            } else {//展开
//                AnimationUtil.collapse(fore_rain, findViewById(R.id.tv_precMore));
                fore_rain.setVisibility(View.VISIBLE);
                state = true;
                tvPrecMore.setText("收起详情");
            }
        } else if (view.getId() == R.id.tv_moreDays) {
            Intent intent = new Intent(MainActivity.this, DaysMoreActivity.class);
            intent.putExtra("id", LocationID);
            startActivity(intent);
        } else if (view.getId() == R.id.iv_add) {
            showAddWindow(view);
        }

    }

    @SuppressLint("InflateParams")
    private void showAddWindow(View view) {
        mPopupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.mpopupwindow, null, false));// 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));// 设置pop透明效果
        mPopupWindow.setAnimationStyle(R.anim.cp_push_bottom_in);
        mPopupWindow.setFocusable(true);    // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setTouchable(true);    // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setOutsideTouchable(true);// 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.showAsDropDown(view, -100, 0);
        TextView changeCity = mPopupWindow.getContentView().findViewById(R.id.tv_change_city);//切换城市
        TextView wallpaper = mPopupWindow.getContentView().findViewById(R.id.tv_wallpaper);//壁纸管理
        changeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeatherCardActivity.class);
                intent.putExtra("id", LocationID);
                intent.putExtra("city", locateCity);
                intent.putExtra("province", locateProvince);
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });
        //添加：“每日一图”功能在此实现
        wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChangeWallpaper.class);   //调用活动“ChangeWallpaper”
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });
    }

    private void initLocation() {
        AMapLocationClient mLocationClient;
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
            mLocationClient.setLocationListener(this);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                locateCity = aMapLocation.getDistrict();
                locateProvince = aMapLocation.getProvince();
                tv_City.setText(locateCity);
                Log.d("hefeng", "locateCity");
                getCityId(locateCity);
            }
        }
    }

    /**
     * 获取城市id
     */
    private void getCityId(String city) {
        QWeather.getGeoCityLookup(MainActivity.this, city, new QWeather.OnResultGeoListener() {
            public static final String TAG = "hefengCity";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Weather Now Error:" + new Gson());
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                if (Code.OK == geoBean.getCode()) {
                    LocationID = geoBean.getLocationBean().get(0).getId();
                    lon = Double.parseDouble(geoBean.getLocationBean().get(0).getLon());
                    lat = Double.parseDouble(geoBean.getLocationBean().get(0).getLat());
                    MyCity myCity1 = new MyCity(locateCity, LocationID);
                    cityDbOpenHelper.insertData(myCity1);
                    Log.i(TAG, LocationID);
                    getNowTemp(LocationID);
                    getRain(lon, lat);
                    getTodayTemp(LocationID);
                    getHoursTemp(LocationID);
                    getDays(LocationID);
                    getWarning(LocationID);
                } else {
                    //在此查看返回数据失败的原因
                    Code code = geoBean.getCode();
                    System.out.println("失败代码: " + code);
                }
            }
        });
    }

    public void getNowTemp(String id) {
        String TAG = "hefeng";
        QWeather.getWeatherNow(MainActivity.this, id, new QWeather.OnResultWeatherNowListener() {

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Weather Now Error:" + new Gson());
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean).replaceAll("https", "h22ps"));
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    String temp = now.getTemp();
                    String text = now.getText();
                    TextView tv_temp = findViewById(R.id.tv_temperature);
                    TextView tv_text = findViewById(R.id.tv_Weather);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_temp.setText(temp);
                            tv_text.setText(text);
                        }
                    });


                }
            }

        });
    }

    public void getTodayTemp(String id) {
        QWeather.getWeather3D(MainActivity.this, id, new QWeather.OnResultWeatherDailyListener() {
            public static final String TAG = "hefeng";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Rain Today Error:" + new Gson());
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                String maxTemp = weatherDailyBean.getDaily().get(0).getTempMax();
                String minTemp = weatherDailyBean.getDaily().get(0).getTempMin();
                TextView tv_maxTemp = findViewById(R.id.tv_maxTemp);
                TextView tv_minTemp = findViewById(R.id.tv_minTemp);
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        tv_minTemp.setText(minTemp + " ~ ");
                        tv_maxTemp.setText(maxTemp + "℃");
                    }
                });
            }
        });
    }

    public void getHoursTemp(String id) {
        QWeather.getWeather24Hourly(MainActivity.this, id, new QWeather.OnResultWeatherHourlyListener() {
            public static final String TAG = "hefeng";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Rain Now Error:" + new Gson());
            }

            @Override
            public void onSuccess(WeatherHourlyBean weatherHourlyBean) {
                Log.i(TAG, "getRain onSuccess: " + new Gson().toJson(weatherHourlyBean).replaceAll("https", "h22ps"));
                if (Code.OK == weatherHourlyBean.getCode()) {
                    List<WeatherHourlyBean.HourlyBean> weatherHourly = weatherHourlyBean.getHourly();
                    for (int i = 0; i < weatherHourly.size(); i++) {
                        String time = weatherHourly.get(i).getFxTime().substring(11, 16);
                        HoursData hoursData = new HoursData(time, weatherHourly.get(i).getTemp(), weatherHourly.get(i).getIcon());
                        hoursDataList.add(hoursData);
                    }
                    Log.d(TAG, "onSuccess: " + hoursDataList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hourlsAdapter = new HourlsAdapter(hoursDataList);
                            hoursWeather.setAdapter(hourlsAdapter);
                            hoursWeather.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
                        }
                    });

                }
            }
        });
    }

    public void getRain(double lon, double lat) {
        QWeather.getMinutely(MainActivity.this, lon, lat, new QWeather.OnResultMinutelyListener() {

            public static final String TAG = "hefeng";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Rain Now Error:" + new Gson());
            }

            @Override
            public void onSuccess(GridMinutelyBean gridMinutelyBean) {
                Log.i(TAG, "getRain onSuccess: " + new Gson().toJson(gridMinutelyBean).replaceAll("https", "h22ps"));

                if (Code.OK == gridMinutelyBean.getCode()) {
                    String summary = gridMinutelyBean.getSummary();
                    TextView tv_precipitation = findViewById(R.id.tv_precipitation);
                    List<GridMinutelyBean.Minutely> minutelyList = gridMinutelyBean.getMinutelyList();
                    for (int i = 0; i < minutelyList.size(); i++) {
                        String time = minutelyList.get(i).getFxTime().substring(11, 16);
                        RainData rainData = new RainData(time, minutelyList.get(i).getPrecip(), minutelyList.get(i).getType());
                        rainDataList.add(rainData);
                    }
                    Log.d(TAG, "onSuccess: " + rainDataList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_precipitation.setText(summary);
                            rainAdapter = new RainAdapter(rainDataList);
                            fore_rain.setAdapter(rainAdapter);
                            fore_rain.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
                        }
                    });

                } else {
                    //在此查看返回数据失败的原因
                    Log.i(TAG, "failed code: " + gridMinutelyBean.getCode());
                }
            }
        });
    }

    public void getDays(String LocationID) {
        QWeather.getWeather7D(MainActivity.this, LocationID, new QWeather.OnResultWeatherDailyListener() {

            private static final String TAG = "hefeng";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError", e);
                System.out.println("Rain Days Error:" + new Gson());
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                Log.i(TAG, "onSuccess" + new Gson().toJson(weatherDailyBean).replaceAll("https", "h22ps"));

                if (Code.OK == weatherDailyBean.getCode()) {
                    List<WeatherDailyBean.DailyBean> dailyBeanList = weatherDailyBean.getDaily();
                    for (int i = 0; i < dailyBeanList.size(); i++) {
                        String date = dailyBeanList.get(i).getFxDate();
                        String icon = dailyBeanList.get(i).getIconDay();
                        String max = dailyBeanList.get(i).getTempMax();
                        String min = dailyBeanList.get(i).getTempMin();
                        DaysData daysData = new DaysData(date, icon, max, min);
                        daysDataList.add(daysData);
                    }
                    Log.d(TAG, "onSuccess: " + daysDataList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            daysAdapter = new DaysAdapter(daysDataList);
                            daysWeather.setAdapter(daysAdapter);
                            daysWeather.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
                        }
                    });
                } else {
                    Log.i(TAG, "failed code: " + weatherDailyBean.getCode());
                }
            }
        });
    }

    public void getWarning(String id) {
        String TAG = "hefeng";
        QWeather.getWarning(this, id, new QWeather.OnResultWarningListener() {

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(WarningBean warningBean) {
                Log.i(TAG, "onSuccess: " + warningBean.getWarningList());
                tvNotify.setSelected(true);
                if (warningBean.getWarningList() != null && warningBean.getWarningList().size() != 0) {
                    String notify = warningBean.getWarningList().get(0).getText();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvNotify.setText(notify);
                        }
                    });
                } else {
                    QuoteUtil.getQuote(new QuoteUtil.QuoteCallback() {
                        @Override
                        public void onSuccess(String quote) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvNotify.setText(quote);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getStringExtra("city") != null) {
            hourlsAdapter.clearData();
            rainAdapter.clearData();
            daysAdapter.clearData();
            String city = intent.getStringExtra("city");
            Log.i("onNewIntent", "city:" + city);
            tv_City.setText(city);
            getCityId(city);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeBackGround();
    }

    public void changeBackGround() {
        LinearLayout layout = findViewById(R.id.main);
        int sourceofwallpaper = SpfUtil.getIntWithDefault(this, WALLPAPER_CHOOSE, SOUCREOFSYSTEM);
//        imagePath = getIntent().getStringExtra("imagePath");
        if (sourceofwallpaper == 0) {
            imagePath = SpfUtil.getString(this,KEY_IMAGEPATH);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Drawable drawable = new BitmapDrawable(bitmap);
            layout.setBackground(drawable);
        } else {
            wallpaper = SpfUtil.getIntWithDefault(this, KEY_WALLPAPER, R.drawable.wallpaper0);
            layout.setBackgroundResource(wallpaper);
        }
    }
}
