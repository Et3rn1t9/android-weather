package com.example.weather.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.data.DaysData;
import com.example.weather.util.ChangeIcon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DaysMoreAdapter extends RecyclerView.Adapter<DaysMoreAdapter.ViewHolder> {

    private List<DaysData> daysDataList;

    public DaysMoreAdapter(List<DaysData> daysDataList) {
        this.daysDataList = daysDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daysmore, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DaysMoreAdapter.ViewHolder holder, int position) {
        DaysData daysData = daysDataList.get(position);
        holder.tv_daysMoreDate1.setText(checkDate(daysData.getFxDate()));
        holder.tv_daysMoreDate2.setText(daysData.getFxDate().substring(5));
        holder.tv_daysMoreMin.setText(daysData.getTempMin() + "℃");
        holder.tv_daysMoreMax.setText(daysData.getTempMax() + "℃");
        holder.tv_dayWind360.setText(daysData.getWind360Day());
        holder.tv_dayWindDir.setText(daysData.getWindDirDay());
        holder.tv_dayWindScale.setText(daysData.getWindScaleDay());
        holder.tv_dayWindSpeed.setText(daysData.getWindSpeedDay());

        holder.tv_nightWind360.setText(daysData.getWind360Night());
        holder.tv_nightWindDir.setText(daysData.getWindDirNight());
        holder.tv_nightWindScale.setText(daysData.getWindScaleDay());
        holder.tv_nightWindSpeed.setText(daysData.getWindSpeedNight());

        holder.tv_cloud.setText(daysData.getCloud());
        holder.tv_uvIndex.setText(daysData.getUvIndex());
        holder.tv_vis.setText(daysData.getVis());
        holder.tv_precip.setText(daysData.getPrecip());
        holder.tv_humidity.setText(daysData.getHumidity());
        holder.tv_pressure.setText(daysData.getPressure());

        holder.tv_dayText.setText(daysData.getTextDay());
        holder.tv_nightText.setText(daysData.getTextNight());
        ChangeIcon.changeIcon(holder.iv_dayIcon, Integer.parseInt(daysData.getIconDay()));
        ChangeIcon.changeIcon(holder.iv_nightIcon, Integer.parseInt(daysData.getIconNight()));
    }

    @Override
    public int getItemCount() {
        return daysDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_daysMoreDate1, tv_daysMoreDate2, tv_daysMoreMin, tv_dayWind360, tv_daysMoreMax, tv_dayWindDir, tv_dayWindScale, tv_dayWindSpeed,tv_dayText;
        TextView tv_nightText,tv_nightWind360, tv_nightWindDir, tv_nightWindScale, tv_nightWindSpeed, tv_cloud, tv_uvIndex, tv_vis, tv_precip, tv_humidity, tv_pressure;
        ImageView iv_dayIcon, iv_nightIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_daysMoreDate1 = itemView.findViewById(R.id.tv_daysMoreDate1);
            tv_daysMoreDate2 = itemView.findViewById(R.id.tv_daysMoreDate2);
            tv_daysMoreMin = itemView.findViewById(R.id.tv_daysMoreMin);
            tv_daysMoreMax = itemView.findViewById(R.id.tv_daysMoreMax);
            tv_dayWind360 = itemView.findViewById(R.id.tv_dayWind360);
            tv_dayWindDir = itemView.findViewById(R.id.tv_dayWindDir);
            tv_dayWindScale = itemView.findViewById(R.id.tv_dayWindScale);
            tv_dayWindSpeed = itemView.findViewById(R.id.tv_dayWindSpeed);
            tv_nightWind360 = itemView.findViewById(R.id.tv_nightWind360);
            tv_nightWindDir = itemView.findViewById(R.id.tv_nightWindDir);
            tv_nightWindScale = itemView.findViewById(R.id.tv_nightWindScale);
            tv_nightWindSpeed = itemView.findViewById(R.id.tv_nightWindSpeed);
            tv_cloud = itemView.findViewById(R.id.tv_cloud);
            tv_uvIndex = itemView.findViewById(R.id.tv_uvIndex);
            tv_vis = itemView.findViewById(R.id.tv_vis);
            tv_precip = itemView.findViewById(R.id.tv_precip);
            tv_humidity = itemView.findViewById(R.id.tv_humidity);
            tv_pressure = itemView.findViewById(R.id.tv_pressure);
            iv_dayIcon = itemView.findViewById(R.id.iv_dayIcon);
            iv_nightIcon = itemView.findViewById(R.id.iv_nightIcon);
            tv_dayText = itemView.findViewById(R.id.tv_dayText);
            tv_nightText =itemView.findViewById(R.id.tv_nightText);
        }
    }

    private String checkDate(String dateString) {
        String result = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // 将时间部分设置为0，只保留日期
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);

            if (calendar.equals(today)) {
                result = "今天";
            } else if (calendar.equals(tomorrow)) {
                result = "明天";
            } else {
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                String[] weekdays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
                result = weekdays[dayOfWeek - 1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
