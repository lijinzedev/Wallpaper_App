package com.wallpaper.anime.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.wallpaper.anime.MyApplication;
import com.wallpaper.anime.R;
import com.wallpaper.anime.activity.PictureActivity;
import com.wallpaper.anime.cardtest.CardAdapterHelper;
import com.wallpaper.anime.glide.GlideApp;
import com.wallpaper.anime.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;


/**
 * Created by jameson on 8/30/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<String> mList = new ArrayList<>();
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private Activity activity;

    public CardAdapter(List<String> mList, Activity activity) {
        this.mList = mList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_card_item, parent, false);
        final ViewHolder holder = new ViewHolder(itemView);

        mCardAdapterHelper.onCreateViewHolder(parent, itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    activity.startActivity(PictureActivity.newIntent(activity, mList.get(position), mList, position));
                } else Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            }
        });
        GlideApp.with(MyApplication.mContext)
                .asBitmap()
                .transition(BitmapTransitionOptions.withCrossFade())
                .load(mList.get(position))
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

    }


}
