package com.example.weather.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.data.RainData;

import java.util.List;

public class RainAdapter extends RecyclerView.Adapter<RainAdapter.ViewHolder> {
    private List<RainData> rainDataList;

    public RainAdapter(List<RainData> rainDataList) {
        this.rainDataList = rainDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rain, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String type = null;
        RainData rainData = rainDataList.get(position);
        if ("rain".equals(rainData.getType())) {
            type = "雨";
        } else if ("snow".equals(rainData.getType())) {
            type = "雪";
        }
        String time = rainData.getTime();
        String timeInfo = showTimeInfo(time);
        holder.tvTime.setText(timeInfo + time);
        holder.tvMinRainInfo.setText(rainData.getMinRainInfo() + "   " + type);
    }
    public void clearData() {
        rainDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rainDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvMinRainInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvMinRainInfo = itemView.findViewById(R.id.tv_minRainInfo);
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
