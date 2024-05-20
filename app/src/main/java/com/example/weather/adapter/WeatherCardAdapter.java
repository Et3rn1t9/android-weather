package com.example.weather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.CityDbOpenHelper;
import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.data.MyCity;
import com.example.weather.view.WeatherView;
import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.Calendar;
import java.util.List;

public class WeatherCardAdapter extends RecyclerView.Adapter<WeatherCardAdapter.ViewHolder> {
    private List<MyCity> myCityList;
    private CityDbOpenHelper cityDbOpenHelper;
    private Context context;
    private Activity activity;

    public WeatherCardAdapter(Activity activity, List<MyCity> myCityList) {
        this.activity = activity;
        this.myCityList = myCityList;
    }



    public void addItem(MyCity city) {
        myCityList.add(city);
        notifyItemInserted(myCityList.size() - 1);
    }
    public void removeItem(int position) {
        myCityList.remove(position);
        notifyItemRemoved(position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        cityDbOpenHelper = new CityDbOpenHelper(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_card, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCity myCity = myCityList.get(position);
        holder.weatherView.setCityName(myCity.getCity_name());
        String id = myCity.getCity_id();
        Log.i("position", position + " " + myCity.getCity_name());
        QWeather.getWeatherNow(context, id, new QWeather.OnResultWeatherNowListener() {
            public static final String TAG = "hefeng";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean).replaceAll("https", "h22ps"));
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();

                    String type = now.getText();
                    String temp = now.getTemp();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.weatherView.setTemperature(temp);
                            if (isDayTime()) {
                                if (type.contains("雨")) {
                                    holder.weatherView.setMyType(WeatherView.Type.raining);
                                } else if (type.contains("雪")) {
                                    holder.weatherView.setMyType(WeatherView.Type.snow);
                                } else {
                                    holder.weatherView.setMyType(WeatherView.Type.sunday);
                                }
                            } else {
                                if (type.contains("雨")) {
                                    holder.weatherView.setMyType(WeatherView.Type.rainingNight);
                                } else if (type.contains("雪")) {
                                    holder.weatherView.setMyType(WeatherView.Type.snowNight);
                                } else {
                                    holder.weatherView.setMyType(WeatherView.Type.night);
                                }
                            }
                            holder.weatherView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("确认删除")
                                            .setMessage("是否删除该城市?")
                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    System.out.println(myCity.getCity_id());
                                                    int position = holder.getAdapterPosition();
                                                    if (position != RecyclerView.NO_POSITION) {
                                                        removeItem(position);
                                                    }
                                                    cityDbOpenHelper.deleteFromDbById(myCity.getCity_id());
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 取消删除，关闭对话框
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create()
                                            .show();

                                    return true;
                                }
                            });
                            holder.weatherView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    intent.putExtra("city", myCity.getCity_name());
                                    activity.startActivity(intent);
                                }
                            });
                        }
                    });
                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherBean.getCode();
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myCityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        WeatherView weatherView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weatherView = itemView.findViewById(R.id.WeatherView);
        }
    }

    public static boolean isDayTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        boolean isDay = currentHour >= 6 && currentHour < 18;
        return isDay;
    }
}