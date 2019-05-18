package com.ljz.acgclub.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ljz.acgclub.Application;
import com.ljz.acgclub.ParallaxImageRecyclerViewHelper;
import com.ljz.acgclub.R;
import com.ljz.acgclub.activity.CollectActivity;
import com.ljz.acgclub.glide.GlideApp;

import java.util.List;


public class BasePictureAdapter extends RecyclerView.Adapter<BasePictureAdapter.ViewHolder> {

    private static final String TAG = "BasePictureAdapter";
    private Context mContext;
    private BasePictureAdapter.OnItemClickListener listener;
    private List<String> urlList;
    private RecyclerView recyclerView;


    public BasePictureAdapter(Context mContext, RecyclerView recyclerView, List<String> urlList, BasePictureAdapter.OnItemClickListener listener) {
        this.mContext = mContext;
        this.urlList = urlList;
        this.listener = listener;
        this.recyclerView = recyclerView;
    }

    public interface OnItemClickListener {
        void onItemClick(String url, int position, View view);

        void onItemLongClick(String url, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_base_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(view1 -> {
            String url = urlList.get(holder.getAdapterPosition());
            listener.onItemClick(url, holder.getAdapterPosition(), holder.view);
        });
        holder.view.setOnLongClickListener(v -> {
            String url = urlList.get(holder.getAdapterPosition());
            listener.onItemLongClick(url, holder.getAdapterPosition());
            return false;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = urlList.get(holder.getAdapterPosition());
//        SimpleTarget target = new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                holder.parallaxImageView.setImageBitmap(resource);
//            }
//        };
//        GlideApp.with(mContext)
//                .asBitmap()
//                .load(imageUrl)
//                .into(target);
        GlideApp.with(Application.mContext)
                .asBitmap()
                .placeholder(R.drawable.img_avatar_01)
                .error(R.drawable.img_avatar_01)
                .transition(BitmapTransitionOptions.withCrossFade())
                .load(imageUrl)
                .into(holder.parallaxImageView);
        ParallaxImageRecyclerViewHelper.Companion.setup(recyclerView, holder.parallaxImageView);
//        GlideApp.with(mContext).load(url).into(holder.parallaxImageView);
//        diskCacheStrategy(DiskCacheStrategy.NONE)

    }
//  @Override
//  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//      String imageUrl = urlList.get(holder.getAdapterPosition());
//
//      String tag = position + "";
//      holder.parallaxImageView.setTag(tag);
//
//      SimpleTarget target = new SimpleTarget<Bitmap>() {
//          @Override
//          public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//              //根据tag判断是不是需要设置给ImageView
//              if (tag == holder.parallaxImageView.getTag()) {
//                  holder.parallaxImageView.setImageBitmap(resource);
//              }
//          }
//      };
//      GlideApp.with(mContext)
//              .asBitmap()
//              .load(imageUrl)
//              .into(target);
//
////        GlideApp.with(Application.mContext)
////                .asBitmap()
////                .placeholder(R.drawable.bg_withe)
////                .error(R.drawable.bg_withe)
////                .transition(BitmapTransitionOptions.withCrossFade())
////                .load(imageUrl)
////                .into(touchImageView);
//      ParallaxImageRecyclerViewHelper.Companion.setup(recyclerView, holder.parallaxImageView);
////        GlideApp.with(mContext).load(url).into(holder.parallaxImageView);
////        diskCacheStrategy(DiskCacheStrategy.NONE)
//
//  }

    @Override
    public int getItemCount() {
        return urlList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView parallaxImageView;


        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.parallaxImageView = view.findViewById(R.id.base_fragment_item_image);
        }
    }

}
