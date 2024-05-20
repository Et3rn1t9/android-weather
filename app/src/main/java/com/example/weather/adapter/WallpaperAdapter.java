package com.example.weather.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.Preview;
import com.example.weather.R;
import com.example.weather.data.Wallpaper;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder>{

    private Context mcontext;
    private List<Wallpaper> mWallpaperList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wallpaper mwallpaper = mWallpaperList.get(position);
        holder.wallpaper.setImageResource(mwallpaper.getImageId());
//        Glide.with(mcontext).
//                load(mwallpaper.getImageId()).
//                transform(new RoundedCorners(60)).
//                into(holder.wallpaper);
        //为图片设置点击事件
        holder.wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Wallpaper wallpaper = mWallpaperList.get(position);
                Intent intent = new Intent(mcontext, Preview.class);   //调用活动“ChangeWallpaper”
                intent.putExtra("wallpaper", wallpaper.getImageId());
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWallpaperList.size();
    }

    public WallpaperAdapter(Context contxt, List<Wallpaper> wallpaperList){
        mcontext = contxt;
        mWallpaperList = wallpaperList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView wallpaper;
        public ViewHolder(@NonNull View view) {
            super(view);
            wallpaper = (ImageView) view.findViewById(R.id.wallpaper);
        }
    }
}
