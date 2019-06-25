package com.wallpaper.anime.util;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import com.race604.drawable.wave.WaveDrawable;
import com.wallpaper.anime.MyApplication;
import com.wallpaper.anime.R;
import java.io.InputStream;
import static org.litepal.LitePalApplication.getContext;

public class ResMsg {
    public static Bitmap bmp;
    public static Bitmap gaoqing = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.gaoqing);
    public static Bitmap mote = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mote);
    public static Bitmap aiqing = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.aiqing);
    public static Bitmap fengjing = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.fengjing);
    public static Bitmap xiaoqingxin = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.xiaoqingxin);
    public static Bitmap dongmankatong = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.dongmankatong);
    public static Bitmap mingxing = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mingxing);
    public static Bitmap mengchong = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mengchong);
    public static Bitmap youxi = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.youxi);
    public static Bitmap qiche = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.qiche);
    public static Bitmap yingshijuzhao = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.yingshijuzhao);
    public static Bitmap junshi = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.junshi);
    public static Bitmap jieri = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.jieri);
    public static Bitmap tiyu = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.tiyu);
    public static Bitmap babyshow = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.babyshow);
    public static Bitmap wenzikong = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wenzikong);
    public static Bitmap shishang = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.shishang);
    public static Bitmap yueli = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.yueli);

    static {

        Resources r = getContext().getResources();
        @SuppressLint("ResourceType") InputStream is = r.openRawResource(R.drawable.white);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        bmp = bmpDraw.getBitmap();

    }

    private static WaveDrawable INSTATCE = new WaveDrawable(MyApplication.mContext, R.drawable.shalou);

    public static WaveDrawable getINSTATCE() {
        INSTATCE.setWaveSpeed(2);
        INSTATCE.setIndeterminate(true);
        return INSTATCE;
    }

    private ResMsg() {
    }

}
