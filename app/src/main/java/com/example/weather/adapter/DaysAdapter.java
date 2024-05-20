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
import com.example.weather.data.DaysData;
import com.example.weather.util.ChangeIcon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {

    private List<DaysData> daysDataList;

    public DaysAdapter(List<DaysData> daysDataList) {
        this.daysDataList = daysDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DaysAdapter.ViewHolder holder, int position) {
        DaysData daysData = daysDataList.get(position);
        holder.tvDate.setText(daysData.getFxDate().substring(5));
        holder.tvWeek.setText(checkDate(daysData.getFxDate()));
        holder.tvMax.setText(daysData.getTempMax() + "℃");
        holder.tvMin.setText(daysData.getTempMin() + "℃/");
        ChangeIcon.changeIcon(holder.ivIcon, Integer.parseInt(daysData.getIconDay()));
    }

    @Override
    public int getItemCount() {
        return daysDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvWeek;
        TextView tvMax;
        TextView tvMin;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_daysDate);
            tvWeek = itemView.findViewById(R.id.tv_week);
            tvMax = itemView.findViewById(R.id.tv_daysMax);
            tvMin = itemView.findViewById(R.id.tv_daysMin);
            ivIcon = itemView.findViewById(R.id.iv_daysIcon);
        }
    }
    public void clearData() {
        daysDataList.clear();
        notifyDataSetChanged();
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
                String[] weekdays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
                result = weekdays[dayOfWeek - 1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



}
