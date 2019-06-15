package com.wallpaper.anime.util;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.race604.drawable.wave.WaveDrawable;
import com.wallpaper.anime.Application;
import com.wallpaper.anime.R;

import java.io.InputStream;

import static org.litepal.LitePalApplication.getContext;

public class ResMsg {
  public   static   Bitmap bmp;
    static {

        Resources r = getContext().getResources();
        @SuppressLint("ResourceType") InputStream is = r.openRawResource(R.drawable.white);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
         bmp = bmpDraw.getBitmap();
    }

    private static  WaveDrawable INSTATCE =new WaveDrawable(Application.mContext,R.drawable.shalou);
    public static WaveDrawable getINSTATCE(){
        INSTATCE.setWaveSpeed(2);
        INSTATCE.setIndeterminate(true);
        return INSTATCE;
    }
    private ResMsg(){
    }

}
