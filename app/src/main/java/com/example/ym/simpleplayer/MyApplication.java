package com.example.ym.simpleplayer;

import android.app.Application;
import android.content.Context;
import android.content.pm.ProviderInfo;

/**
 * Created by YM on 2017/3/15.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static  Context getContext(){
        return context;
    }
}
