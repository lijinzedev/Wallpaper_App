package com.ljz.acgclub;

import android.content.Context;

import org.litepal.LitePal;


public class Application extends android.app.Application {
    public static Application mContext;

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
