package com.wallpaper.anime.util;

import com.race604.drawable.wave.WaveDrawable;
import com.wallpaper.anime.Application;
import com.wallpaper.anime.R;

public class ResMsg {
    private static  WaveDrawable INSTATCE =new WaveDrawable(Application.mContext,R.drawable.shalou);
    public static WaveDrawable getINSTATCE(){
        INSTATCE.setWaveSpeed(2);
        INSTATCE.setIndeterminate(true);
        return INSTATCE;
    }
    private ResMsg(){
    }

}
