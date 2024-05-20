package com.example.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.Manifest;
import android.widget.Toast;

import com.example.weather.adapter.WallpaperAdapter;
import com.example.weather.data.Wallpaper;
import com.example.weather.util.ImageUtil;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

public class ChangeWallpaper extends AppCompatActivity implements View.OnClickListener {

    private List<Wallpaper> wallpaperList = new ArrayList<>();
    private int id;
    private Button wallpaperBtn;
    private PopupWindow mPopupWindow;
    public static final int REQUEST_CODE_CHOOSE = 0;
    private ImageView ivAvatar;
    private Uri imageUri;
    private String imageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        Stetho.initializeWithDefaults(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        Intent intent = getIntent();
//        id = Integer.parseInt(intent.getStringExtra("id"));

        //初始化壁纸数据
        initWallpaper();
        findViewById(R.id.bg_img).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.wallpaper_con);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        WallpaperAdapter adapter = new WallpaperAdapter(this, wallpaperList);
        recyclerView.setAdapter(adapter);
    }

    private void initWallpaper() {
        Wallpaper paper0 = new Wallpaper(R.drawable.wallpaper0);
        wallpaperList.add(paper0);
        Wallpaper paper1 = new Wallpaper(R.drawable.wallpaper1);
        wallpaperList.add(paper1);
        Wallpaper paper2 = new Wallpaper(R.drawable.wallpaper2);
        wallpaperList.add(paper2);
        Wallpaper paper3 = new Wallpaper(R.drawable.wallpaper3);
        wallpaperList.add(paper3);
        Wallpaper paper4 = new Wallpaper(R.drawable.wallpaper4);
        wallpaperList.add(paper4);
        Wallpaper paper5 = new Wallpaper(R.drawable.wallpaper5);
        wallpaperList.add(paper5);
        Wallpaper paper6 = new Wallpaper(R.drawable.wallpaper6);
        wallpaperList.add(paper6);
    }

    @Override
    public void onClick(View view) {
        //打开相册
        if (view.getId() == R.id.bg_img) {
            choosePhoto(view);
        }
        //壁纸返回按钮
        else if (view.getId() == R.id.btn_back) {
            Intent intent = new Intent(ChangeWallpaper.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //打开相册方式实现
    public void choosePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 真正的去打开相册
            openAlbum();
        } else {
            // 去申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE) {

            if (Build.VERSION.SDK_INT < 19) {
                handleImageBeforeOnApi19(data);
            } else {
                handleImageOnApi19(data);
            }
        }
    }

    private void handleImageBeforeOnApi19(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @TargetApi(19)
    private void handleImageOnApi19(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            if (TextUtils.equals(uri.getAuthority(), "com.android.providers.media.documents")) {
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if (TextUtils.equals(uri.getAuthority(), "com.android.providers.downloads.documents")) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://download/public_donwloads"), Long.valueOf(documentId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Intent intent = new Intent(this, Preview.class);   //调用活动“ChangeWallpaper”
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "你还未获得访问相册权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}