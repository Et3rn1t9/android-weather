package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.weather.util.SpfUtil;
import com.facebook.stetho.Stetho;

public class Preview extends AppCompatActivity implements View.OnClickListener {

    private PopupWindow mPopupWindow;
    private int wallpaper;
    private String imagePath;
    public static final String KEY_WALLPAPER = "wallpaper_id";
    public static final String KEY_IMAGEPATH = "image_path";
    public static final String WALLPAPER_CHOOSE = "sourceofwallpaper";
    public static final int SOUCREOFLOCAL = 0;
    public static final int SOUCREOFSYSTEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Stetho.initializeWithDefaults(this);
        setStatusBar();

        findViewById(R.id.back_func).setOnClickListener(this);
        findViewById(R.id.wallpaper_apply).setOnClickListener(this);

        handleIntent(getIntent());
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void handleIntent(Intent intent) {
        imagePath = intent.getStringExtra("imagePath");
        wallpaper = intent.getIntExtra("wallpaper", 0);

        if (imagePath != null) {
            setLocalBackground();
        } else {
            setBackground();
        }
    }

    private void setLocalBackground() {
        LinearLayout layout = findViewById(R.id.preview);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        Drawable drawable = new BitmapDrawable(bitmap);
        layout.setBackground(drawable);
    }

    private void setBackground() {
        LinearLayout layout = findViewById(R.id.preview);
        layout.setBackgroundResource(wallpaper);
    }

    @Override
    public void onClick(View view) {
        //设置主页背景图片
        if (view.getId() == R.id.wallpaper_apply) {
            saveWallpaperAndNavigate();
        }
        //返回按钮
        else if (view.getId() == R.id.back_func) {
            finish();
        }
    }

    private void saveWallpaperAndNavigate() {
        if (imagePath == null) {
            SpfUtil.saveInt(this, WALLPAPER_CHOOSE, SOUCREOFSYSTEM);
            SpfUtil.saveInt(this, KEY_WALLPAPER, wallpaper);
            Intent intent = new Intent(Preview.this, MainActivity.class);
            startActivity(intent);
        } else {
            SpfUtil.saveInt(this, WALLPAPER_CHOOSE, SOUCREOFLOCAL);
            SpfUtil.saveString(this, KEY_IMAGEPATH, imagePath);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
        }

    }
}
