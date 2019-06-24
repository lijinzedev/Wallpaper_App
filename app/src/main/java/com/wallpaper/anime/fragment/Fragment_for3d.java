package com.wallpaper.anime.fragment;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.moxun.tagcloudlib.view.TagCloudView;
import com.wallpaper.anime.R;
import com.wallpaper.anime.adapter.ViewTagsAdapter;

import jp.live2d.asuna_26.LiveWallpaperService;

public class Fragment_for3d extends android.support.v4.app.Fragment{
    private TagCloudView tagCloudView;
    private ViewTagsAdapter textTagsAdapter;
    private Button live2d;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_3d, container, false);
        tagCloudView = (TagCloudView)view.findViewById(R.id.tag_cloud);
        live2d=view.findViewById(R.id.live2d);
        live2d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,new ComponentName(getActivity(),LiveWallpaperService.class));
                startActivity(intent);
            }
        });
        textTagsAdapter = new ViewTagsAdapter(getActivity());
        tagCloudView.setAdapter(textTagsAdapter);
        return view;
    }
}
