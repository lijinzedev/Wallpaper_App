package com.wallpaper.anime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.wallpaper.anime.R;
import com.wallpaper.anime.glide.GlideApp;
import com.wallpaper.anime.util.ResMsg;

import java.util.ArrayList;
import java.util.List;



public class BasePictureAdapter extends RecyclerView.Adapter<BasePictureAdapter.ViewHolder> {

    private static final String TAG = "BasePictureAdapter";
    private Context mContext;
    private BasePictureAdapter.OnItemClickListener listener;
    protected boolean isScrolling = false;
    private RecyclerView recyclerView;
    private List<String> urlList=new ArrayList<>();
    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }
    public BasePictureAdapter(Context mContext, RecyclerView recyclerView, List<String> urlList, BasePictureAdapter.OnItemClickListener listener) {
        this.mContext = mContext;
        this.urlList = urlList;
        this.listener = listener;
        this.recyclerView = recyclerView;

    }

    public interface OnItemClickListener {
        void onItemClick(String url, int position);

        void onItemLongClick(String url, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_base_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(view1 -> {
            String url = urlList.get(holder.getAdapterPosition());
            listener.onItemClick(url, holder.getAdapterPosition());
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
        if (!TextUtils.isEmpty(imageUrl) && !isScrolling) {
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(imageUrl)
                    .placeholder(R.drawable.bg_white).
                     transition(BitmapTransitionOptions.withCrossFade()) //淡入淡出动画
                    .into(holder.parallaxImageView);
        } else {

            holder.parallaxImageView.setImageBitmap(ResMsg.bmp);
        }

  //      ParallaxImageRecyclerViewHelper.Companion.setup(recyclerView, holder.parallaxImageView);
//        GlideApp.with(mContext).load(url).into(holder.parallaxImageView);
//        diskCacheStrategy(DiskCacheStrategy.NONE)

    }

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
