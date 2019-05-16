package net;

import okhttp3.OkHttpClient;

public class NetworkUtils {

    private static volatile OkHttpClient mInstance;

    public static OkHttpClient getInstance(){
        if (mInstance == null){
            synchronized (NetworkUtils.class){
                if (mInstance == null){
                    mInstance = new OkHttpClient();
                }
            }
        }
        return mInstance;
    }
}
