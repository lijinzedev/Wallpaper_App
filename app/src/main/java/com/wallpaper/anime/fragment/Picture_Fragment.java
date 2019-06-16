package com.wallpaper.anime.fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ortiz.touchview.TouchImageView;
import com.wallpaper.anime.R;
import com.wallpaper.anime.glide.GlideApp;

import java.util.concurrent.ExecutionException;

/**
 * Created by surine on 2017/5/23.
 */

public class Picture_Fragment extends Fragment {

    private static final String ARG_STRING = "Picture_Fragment";
    String url;
    public static Picture_Fragment getInstance(String args) {
        Picture_Fragment fra = new Picture_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_STRING,args);
        fra.setArguments(bundle);
        return fra;
    }

    //onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        url = bundle.getString(ARG_STRING);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R. layout.fragment_picture, container, false);
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
                if (getActivity()!=null)
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
}
