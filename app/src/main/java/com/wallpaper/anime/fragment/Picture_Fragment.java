package com.wallpaper.anime.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ortiz.touchview.TouchImageView;
import com.wallpaper.anime.EventBus.SimpleEventBus;
import com.wallpaper.anime.R;
import com.wallpaper.anime.glide.GlideApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ExecutionException;

/**
 */

public class Picture_Fragment extends Fragment {

    private static final String ARG_STRING = "Picture_Fragment";
    private boolean sendflag = true; //EventBus 中发送消息次数控制
    private boolean hidden = false;
    private boolean isViewCreated = false;
    String url;


    public static Picture_Fragment getInstance(String args) {
        Picture_Fragment fra = new Picture_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_STRING, args);
        fra.setArguments(bundle);
        return fra;
    }

    //onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        url = bundle.getString(ARG_STRING);
        if (hidden) {
            EventBus.getDefault().register(this);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        TouchImageView touchImageView = (TouchImageView) view.findViewById(R.id.picture_big);


        /*
         * Note :the photoview is a picture zoom view
         *but I used Glide to load pictures,and I have a problem
         *Glide has cache policy,causing picture loading to be abnormal
         * so i delete the photoview and find a new; please waitting for me;
         * */
//
        new Thread(() -> {
            try {
                Bitmap bitmap = GlideApp.with(this)
                        .asBitmap()
                        .load(url)
                        .submit().get();
                if (getActivity() != null)
                    getActivity().runOnUiThread(() -> {
                        touchImageView.setImageBitmap(bitmap);
                    });
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return view;
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEventBus event) {

    }

    /**
     * 判断当前的碎片是否可见
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.d(ARG_STRING, "onHiddenChanged: " + "当前可见");
            this.hidden = true;
            lazyLoad();
        } else {
            this.hidden = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lazyLoad();
    }

    private void lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (hidden && isViewCreated) {
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            hidden = false;
            EventBus.getDefault().post(
                    new SimpleEventBus(3, url));
            Log.d(ARG_STRING, ARG_STRING + "发送消息");
        }
    }


    @Override
    public void onPause() {

        super.onPause();
    }
}
