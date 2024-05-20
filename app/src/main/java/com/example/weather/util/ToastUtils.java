package com.example.weather.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast mToast;

    public static void showToast(Context context, String message) {
        if (mToast != null) {
            // 取消之前的 Toast
            mToast.cancel();
        }
        // 创建并显示新的 Toast
        mToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}