package com.example.weather.util;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuoteUtil {

    private static final String HITOKOTO_URL = "https://v1.hitokoto.cn?c=i&c=d";
    private static final OkHttpClient client = new OkHttpClient();

    public interface QuoteCallback {
        void onSuccess(String Quote);

        void onFailure(String errorMessage);
    }

    public static void getQuote(final QuoteCallback callback) {
        Request request = new Request.Builder()
                .url(HITOKOTO_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        String Quote = jsonObject.optString("hitokoto") + "   ——" + jsonObject.optString("from");
                        callback.onSuccess(Quote);
                    } catch (Exception e) {
                        callback.onFailure("Failed to parse response");
                    }
                } else {
                    callback.onFailure("Request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Request failed: " + e.getMessage());
            }
        });
    }
}