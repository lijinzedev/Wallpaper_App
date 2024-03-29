package com.wallpaper.anime.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;

import com.wallpaper.anime.MyApplication;
import com.wallpaper.anime.R;

import com.wallpaper.anime.cardtest.CardItemTouchHelperCallback;
import com.wallpaper.anime.cardtest.CardLayoutManager;
import com.wallpaper.anime.db.Picture;
import com.wallpaper.anime.glide.GlideApp;
import com.wallpaper.anime.util.NetworkUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


import me.yuqirong.cardswipelayout.CardConfig;
import me.yuqirong.cardswipelayout.OnSwipeListener;

import static org.litepal.LitePalApplication.getContext;

public class CollectActivity extends BaseActivity {
    private static final String TAG = "CollectActivity";
    private List<Picture> list = new ArrayList<>();
    private List<Picture> bufferlist = new ArrayList<>();
    private List<Bitmap> bitmaps = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardItemTouchHelperCallback cardCallback;
    private ImageView blurringView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.collect_activity);
        // list = LitePal.findAll(Picture.class);
        blurringView = findViewById(R.id.image_view);
        list = LitePal.order("id desc").limit(5).find(Picture.class);
        if (list.size() != 0) {
            for (Picture picture : list) {
                bufferlist.add(picture);
            }
        }


        Log.d(TAG, "onCreate: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "onCreate: " + list.get(i).getUrl());
        }
        //显示搜索框控件
        SearchView searchView = (SearchView) findViewById(R.id.serachview);
        //设置查询提示字符串
        searchView.setQueryHint("请输入搜索内容");
        //设置搜索图标是否显示在搜索框内
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                list.clear();
                list = LitePal.where("tag=?", s).find(Picture.class);
                if (list.size() != 0) {
                    for (Picture picture : list) {
                        bufferlist.add(picture);
                    }
                }

                recyclerView.getAdapter().notifyDataSetChanged();
                cardCallback.dataChange(list);
                if (list == null) {
                    Toast.makeText(CollectActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                } // Toast.makeText(CollectActivity.this, "aaaaaa", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        initData();
        initView();

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new MyAdapter());
        cardCallback = new CardItemTouchHelperCallback(recyclerView.getAdapter(), list);
        cardCallback.setOnSwipedListener(new OnSwipeListener<Picture>() {

            @Override
            public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
                MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
                viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);
                if (direction == CardConfig.SWIPING_LEFT) {
                    myHolder.dislikeImageView.setAlpha(Math.abs(ratio));
                } else if (direction == CardConfig.SWIPING_RIGHT) {
                    myHolder.likeImageView.setAlpha(Math.abs(ratio));
                } else {
                    myHolder.dislikeImageView.setAlpha(0f);
                    myHolder.likeImageView.setAlpha(0f);
                }
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, Picture o, int direction) {
                MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
                viewHolder.itemView.setAlpha(1f);
                myHolder.dislikeImageView.setAlpha(0f);
                myHolder.likeImageView.setAlpha(0f);
                //       Toast.makeText(CollectActivity.this, direction == CardConfig.SWIPED_LEFT ? "swiped left" : "swiped right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipedClear() {
                //   Toast.makeText(CollectActivity.this, "data clear", Toast.LENGTH_SHORT).show();
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, 3000L);
            }

        });
        final ItemTouchHelper touchHelper = new ItemTouchHelper(cardCallback);
        final CardLayoutManager cardLayoutManager = new CardLayoutManager(recyclerView, touchHelper);
        recyclerView.setLayoutManager(cardLayoutManager);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initData() {

        if (bufferlist.size() != 0) {
            for (Picture picture : bufferlist) {
                list.add(picture);
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collect_card_item, parent, false);
            final MyViewHolder holder = new MyViewHolder(view);
            view.setOnClickListener((view1) -> {
                holder.avatarImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picture url = list.get(viewType);
                        if (NetworkUtil.isNetworkAvailable(getContext())) {
                            Intent intent = new Intent(getContext(), PictureActivity.class);
                            intent.putExtra("IMGURL", url.getUrl());
                            intent.putExtra("collect", 11);
                            startActivity(intent);
                        } else Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                    }
                });


            });
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder == null) {
                return;
            }
            Picture imageUrl = list.get(holder.getAdapterPosition());
            ((MyViewHolder) holder).tag.setText(imageUrl.getTag());
            ((MyViewHolder) holder).lable.setText(imageUrl.getLabel());
//            ((MyViewHolder) holder).avatarImageView.setTag( position);
//             Object tag= ((MyViewHolder) holder).avatarImageView.getTag();
////            SimpleTarget target = new SimpleTarget<Bitmap>() {
////                @Override
////                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
////                    ((MyViewHolder) holder).avatarImageView.setImageBitmap(resource);
////                }
////            };
//            if (tag!=null&&(int) tag!= position) {
//                //如果tag不是Null,并且同时tag不等于当前的position。
//                //说明当前的viewHolder是复用来的
//                //Cancel any pending loads Glide may have for the view
//                //and free any resources that may have been loaded for the view.
//                Log.d(TAG, "onBindViewHolder: "+"数据复用");
//                Glide.with(CollectActivity.this).clear(((MyViewHolder) holder).avatarImageView);
//            }
            GlideApp.with(MyApplication.mContext)
                    .asBitmap()
                    .placeholder(R.drawable.white)
                    .error(R.drawable.white)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .load(imageUrl.getUrl())
                    .into(((MyViewHolder) holder).avatarImageView);

            GlideApp.with(MyApplication.mContext)
                    .asBitmap()
                    .placeholder(R.drawable.white)
                    .error(R.drawable.white)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .load(imageUrl.getUrl())
                    .into(blurringView);
//            Drawable drawable = blurringView.getDrawable();
//            BitmapDrawable bd = (BitmapDrawable) drawable;
//            Bitmap bm = bd.getBitmap();
//
//            blurringView.setImageBitmap(BlurBitmapUtils.getBlurBitmap(CollectActivity.this,bm,15));
//            ViewSwitchUtils.startSwitchBackgroundAnim(blurringView,bm);


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView avatarImageView;
            ImageView likeImageView;
            ImageView dislikeImageView;
            TextView tag;
            TextView lable;

            MyViewHolder(View itemView) {
                super(itemView);
                tag = itemView.findViewById(R.id.tag);
                lable = itemView.findViewById(R.id.lable);
                avatarImageView = (ImageView) itemView.findViewById(R.id.iv_avatar);
                likeImageView = (ImageView) itemView.findViewById(R.id.iv_like);
                dislikeImageView = (ImageView) itemView.findViewById(R.id.iv_dislike);
            }

        }
    }

    public void back(View view) {


        finish();

    }
}
