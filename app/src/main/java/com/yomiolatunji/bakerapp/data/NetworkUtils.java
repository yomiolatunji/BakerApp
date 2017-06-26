package com.yomiolatunji.bakerapp.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class NetworkUtils {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    static OkHttpClient client;
    static Context context;

    private NetworkUtils() {

        client = new OkHttpClient();
    }

    public static NetworkUtils newInstance(Context c) {
        context = c;
        return new NetworkUtils();
    }


    public String get(String url) throws IOException {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
