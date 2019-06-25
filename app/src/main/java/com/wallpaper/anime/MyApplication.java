package com.wallpaper.anime;

import android.content.SharedPreferences;

import com.wallpaper.anime.util.ResMsg;

import org.litepal.LitePal;


public class MyApplication extends android.app.Application {
    public static MyApplication mContext;
    static {
        ResMsg resMsg;
    }
    private static MyApplication application = null;
    private static String ShareName = " THEONE";
    public static SharedPreferences getSharedPreferences(){
        // 这里保存的视频路径是在服务里获取，所以这里一定要用：Context.MODE_MULTI_PROCESS
        SharedPreferences sharedPreferences = getInstance().getSharedPreferences(ShareName,application.MODE_MULTI_PROCESS);
        return sharedPreferences;
    }
    public static MyApplication  getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        application = this;
        LitePal.initialize(application);
    }
}
