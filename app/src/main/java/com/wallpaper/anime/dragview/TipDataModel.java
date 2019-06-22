package com.wallpaper.anime.dragview;

import android.util.Log;

import com.wallpaper.anime.db.SimpleTitleTip;

import org.litepal.LitePal;

import java.util.List;

/**
 */
public class TipDataModel {
    private static final String TAG = "TipDataModel";
    private static SimpleTitleTip[] dragTips = {
            new SimpleTitleTip(36, "4K专区"),
            new SimpleTitleTip(6, "美女模特"),
            new SimpleTitleTip(30, "爱情美图"),
            new SimpleTitleTip(9, "风景大片")};
    private static SimpleTitleTip[] addTips = {
            new SimpleTitleTip(15, "小清新"),
            new SimpleTitleTip(26, "动漫卡通"),
            new SimpleTitleTip(11, "明星风尚"),
            new SimpleTitleTip(14, "萌宠动物"),
            new SimpleTitleTip(5, "游戏壁纸"),
            new SimpleTitleTip(12, "汽车天下"),
            new SimpleTitleTip(7, "影视剧照"),
            new SimpleTitleTip(13, "节日美图"),
            new SimpleTitleTip(22, "军事天地"),
            new SimpleTitleTip(16, "劲爆体育"),
            new SimpleTitleTip(18, "BABY秀"),
            new SimpleTitleTip(35, "文字控")
    };


    public static List<SimpleTitleTip> getDragTips() {
        //true 为1
        List<SimpleTitleTip> result = LitePal.order("pos").where("flag=?", "1").find(SimpleTitleTip.class);
        //  List<SimpleTitleTip> result = LitePal.findAll(SimpleTitleTip.class);
        for (SimpleTitleTip simpleTitleTip : result) {
            Log.d(TAG, "getDragTips: " + simpleTitleTip.getPos()+simpleTitleTip.getTip());

        }
        return result;
    }

    public static List<SimpleTitleTip> getAddTips() {
        List<SimpleTitleTip> result = LitePal.order("pos").where("flag=?", "0").find(SimpleTitleTip.class);
//          List<SimpleTitleTip> result = LitePal.findAll(SimpleTitleTip.class);

        return result;
    }
}
