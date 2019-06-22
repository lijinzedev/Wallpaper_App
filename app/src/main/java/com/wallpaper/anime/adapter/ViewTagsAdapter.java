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
import com.wallpaper.anime.db.SimpleTitleTip;
import com.wallpaper.anime.glide.GlideApp;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moxun on 16/3/4.
 */
public class ViewTagsAdapter extends TagsAdapter {
    ImageView imageView;
    TextView tag;
    List<SimpleTitleTip> list = new ArrayList<>();
    Activity activity;

    public ViewTagsAdapter( Activity activity) {
        this.activity = activity;
        list=LitePal.findAll(SimpleTitleTip.class);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_item_view, parent, false);
        imageView = view.findViewById(R.id.iv_3d);
        tag = view.findViewById(R.id.tag_3d);
        GlideApp.with(activity).load(R.drawable.img_avatar_01).into(imageView);
        tag.setText(list.get(position).getTip());
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
