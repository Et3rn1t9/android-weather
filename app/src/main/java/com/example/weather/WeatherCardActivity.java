package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.adapter.WeatherCardAdapter;
import com.example.weather.data.MyCity;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

public class WeatherCardActivity extends AppCompatActivity implements View.OnClickListener {

    String city;
    private String LocationID, locateCity, locateProvince;
    private String selectID, selectCity;
    private CityDbOpenHelper cityDbOpenHelper;
    private List<MyCity> myCityList;
    private MyCity myCity;
    private WeatherCardAdapter weatherCardAdapter;
    private RecyclerView weather_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_card);
        Stetho.initializeWithDefaults(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        Intent intent = getIntent();
        LocationID = intent.getStringExtra("id");
        locateCity = intent.getStringExtra("city");
        locateProvince = intent.getStringExtra("province");
        findViewById(R.id.iv_addCity).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        initData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_addCity) {
            selectAddress();
        } else if (view.getId() == R.id.btn_back) {
            Intent intent = new Intent(WeatherCardActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void initData() {
        myCityList = new ArrayList<>();
        cityDbOpenHelper = new CityDbOpenHelper(this);
        myCityList = cityDbOpenHelper.queryAllFromDb();
        weather_card = findViewById(R.id.weather_card);
        weatherCardAdapter = new WeatherCardAdapter(WeatherCardActivity.this, myCityList);
        weather_card.setAdapter(weatherCardAdapter);
        weather_card.setLayoutManager(new LinearLayoutManager(WeatherCardActivity.this, RecyclerView.VERTICAL, false));
    }


    @SuppressLint("ResourceType")
    private void selectAddress() {
        List<HotCity> hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100")); //code为城市代码
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        hotCities.add(new HotCity("天津", "天津", "101030100"));
        hotCities.add(new HotCity("南京", "江苏", "101190101"));
        hotCities.add(new HotCity("成都", "四川", "101270101"));
        hotCities.add(new HotCity("武汉", "湖北", "101200101"));

        CityPicker.from(WeatherCardActivity.this)
                .enableAnimation(true)
                .setHotCities(hotCities)
                .setAnimationStyle(R.anim.cp_push_bottom_in)
                .setLocatedCity(null)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        selectCity = data.getName();
                        getCityId();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLocate() {
                        //定位接口，需要APP自身实现，这里模拟一下定位
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CityPicker.from(WeatherCardActivity.this).locateComplete(new LocatedCity(locateCity, locateProvince, LocationID), LocateState.SUCCESS);
                            }
                        }, 3000);
                    }
                })
                .show();
    }

    private void getCityId() {
        QWeather.getGeoCityLookup(WeatherCardActivity.this, selectCity, new QWeather.OnResultGeoListener() {
            public static final String TAG = "hefengCity";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Weather Now Error:" + new Gson());
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                if (Code.OK == geoBean.getCode()) {
                    selectID = geoBean.getLocationBean().get(0).getId();
                    Log.i(TAG, selectID);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myCity = new MyCity(selectCity, selectID);
                            long result = cityDbOpenHelper.insertData(myCity);
                            if (result == -1) {
                                Toast.makeText(getApplicationContext(), "该城市已在列表中", Toast.LENGTH_SHORT).show();
                            } else {
                                weatherCardAdapter.addItem(myCity);
                                Toast.makeText(getApplicationContext(), selectCity, Toast.LENGTH_SHORT).show();
                            }
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
}