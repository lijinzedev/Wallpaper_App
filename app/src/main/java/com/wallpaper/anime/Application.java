package com.wallpaper.anime;

import android.content.Context;

import com.wallpaper.anime.util.ResMsg;

import org.litepal.LitePal;


public class Application extends android.app.Application {
    public static Application mContext;
    static {
        ResMsg resMsg;
    }
    public static Context getInstance() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        LitePal.initialize(mContext);
    }
}
