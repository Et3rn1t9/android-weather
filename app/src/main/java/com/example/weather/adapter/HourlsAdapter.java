package com.example.weather.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.data.HoursData;
import com.example.weather.util.ChangeIcon;

import java.util.List;

public class HourlsAdapter extends RecyclerView.Adapter<HourlsAdapter.ViewHolder> {

    private final List<HoursData> hoursDataList;

    public HourlsAdapter(List<HoursData> hoursDataList) {
        this.hoursDataList = hoursDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hours, parent, false);
        return new HourlsAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HoursData hoursData = hoursDataList.get(position);
        String time = hoursData.getTime();
        String timeInfo = showTimeInfo(time);
        time = timeInfo + time;
        String temp = hoursData.getTemp() + "℃";
        holder.Time.setText(time);
        holder.temp.setText(temp);
        ChangeIcon.changeIcon(holder.Icon, Integer.parseInt(hoursData.getIcon()));
    }

    public void clearData() {
        hoursDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return hoursDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Time;
        ImageView Icon;
        TextView temp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Time = itemView.findViewById(R.id.tv_hoursTime);
            Icon = itemView.findViewById(R.id.iv_hoursIcon);
            temp = itemView.findViewById(R.id.tv_hoursTemp);
        }
    }

    public static String showTimeInfo(String timeData) {
        String timeInfo = null;
        int time = 0;

        if (timeData == null || timeData.equals("")) {
            timeInfo = "获取失败";
        } else {
            time = Integer.parseInt(timeData.trim().substring(0, 2));
            if (time >= 0 && time <= 6) {
                timeInfo = "凌晨";
            } else if (time > 6 && time < 12) {
                timeInfo = "上午";
            } else if (time == 12) {
                timeInfo = "中午";
            } else if (time >= 13 && time <= 18) {
                timeInfo = "下午";
            } else if (time > 18 && time <= 24) {
                timeInfo = "晚上";
            } else {
                timeInfo = "未知";
            }
        }


        return timeInfo;
    }

}

