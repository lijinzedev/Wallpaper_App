package com.wallpaper.anime.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxun.tagcloudlib.view.TagsAdapter;
import com.wallpaper.anime.R;
import com.wallpaper.anime.activity.CdnActivity;
import com.wallpaper.anime.db.SimpleTitleTip;
import com.wallpaper.anime.glide.GlideApp;
import com.wallpaper.anime.util.ResMsg;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moxun on 16/3/4.
 */
public class ViewTagsAdapter extends TagsAdapter {
    ImageView imageView;
    List<SimpleTitleTip> list = new ArrayList<>();
    Activity activity;
    TextView tv;
    private SimpleTitleTip[] tips = {

            new SimpleTitleTip(36, "4K专区"),
            new SimpleTitleTip(6, "美女模特"),
            new SimpleTitleTip(30, "爱情美图"),
            new SimpleTitleTip(9, "风景大片"),
            new SimpleTitleTip(15, "小清新"),
            new SimpleTitleTip(26, "动漫卡通"),
            new SimpleTitleTip(11, "明星风尚"),
            new SimpleTitleTip(14, "萌宠动物"),
            new SimpleTitleTip(5, "游戏壁纸"),
            new SimpleTitleTip(12, "汽车天下"),
            new SimpleTitleTip(7, "影视剧照"),
            new SimpleTitleTip(22, "军事天地"),
            new SimpleTitleTip(13, "节日美图"),
            new SimpleTitleTip(16, "劲爆体育"),
            new SimpleTitleTip(18, "BABY秀"),
            new SimpleTitleTip(35, "文字控"),
            new SimpleTitleTip(10, "炫酷风尚"),
            new SimpleTitleTip(26, "月历风尚")

    };

    public ViewTagsAdapter(Activity activity) {
        this.activity = activity;
        list = LitePal.findAll(SimpleTitleTip.class);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_item_view, parent, false);
        imageView = view.findViewById(R.id.iv_3d);
        tv = view.findViewById(R.id.tv_for_3d);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(CdnActivity.getIntent(context, tips[position].getId()));
            }
        });
        switch (position) {
            case 0:
                GlideApp.with(activity).load(ResMsg.gaoqing).into(imageView);
                tv.setText(tips[0].getTip());
                break;
            case 1:
                GlideApp.with(activity).load(ResMsg.mote).into(imageView);
                tv.setText(tips[1].getTip());
                break;
            case 2:
                GlideApp.with(activity).load(ResMsg.aiqing).into(imageView);
                tv.setText(tips[2].getTip());
                break;
            case 3:
                GlideApp.with(activity).load(ResMsg.fengjing).into(imageView);
                tv.setText(tips[3].getTip());
                break;
            case 4:
                GlideApp.with(activity).load(ResMsg.xiaoqingxin).into(imageView);
                tv.setText(tips[4].getTip());
                break;
            case 5:
                GlideApp.with(activity).load(ResMsg.dongmankatong).into(imageView);
                tv.setText(tips[5].getTip());
                break;
            case 6:
                GlideApp.with(activity).load(ResMsg.mingxing).into(imageView);
                tv.setText(tips[6].getTip());
                break;
            case 7:
                GlideApp.with(activity).load(ResMsg.mengchong).into(imageView);
                tv.setText(tips[7].getTip());
                break;
            case 8:
                GlideApp.with(activity).load(ResMsg.youxi).into(imageView);
                tv.setText(tips[8].getTip());
                break;
            case 9:
                GlideApp.with(activity).load(ResMsg.qiche).into(imageView);
                tv.setText(tips[9].getTip());
                break;
            case 10:
                GlideApp.with(activity).load(ResMsg.yingshijuzhao).into(imageView);
                tv.setText(tips[10].getTip());
                break;
            case 11:
                GlideApp.with(activity).load(ResMsg.junshi).into(imageView);
                tv.setText(tips[11].getTip());
                break;
            case 12:
                GlideApp.with(activity).load(ResMsg.jieri).into(imageView);
                tv.setText(tips[12].getTip());
                break;
            case 13:
                GlideApp.with(activity).load(ResMsg.tiyu).into(imageView);
                tv.setText(tips[13].getTip());
                break;
            case 14:
                GlideApp.with(activity).load(ResMsg.babyshow).into(imageView);
                tv.setText(tips[14].getTip());
                break;
            case 15:
                GlideApp.with(activity).load(ResMsg.wenzikong).into(imageView);
                tv.setText(tips[15].getTip());
                break;
            case 16:
                GlideApp.with(activity).load(ResMsg.shishang).into(imageView);
                tv.setText(tips[16].getTip());
                break;
            case 17:
                GlideApp.with(activity).load(ResMsg.yueli).into(imageView);
                tv.setText(tips[17].getTip());
                break;
        }

        return view;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getPopularity(int position) {
        return position;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }
}
