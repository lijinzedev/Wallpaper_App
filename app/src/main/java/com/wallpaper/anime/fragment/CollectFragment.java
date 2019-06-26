package com.wallpaper.anime.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.wallpaper.anime.MyApplication;
import com.wallpaper.anime.EventBus.SimpleEventBus;
import com.wallpaper.anime.R;
import com.wallpaper.anime.activity.PictureActivity;
import com.wallpaper.anime.adapter.CardAdapter;
import com.wallpaper.anime.cardtest.CardItemTouchHelperCallback;
import com.wallpaper.anime.cardtest.CardScaleHelper;
import com.wallpaper.anime.db.Picture;
import com.wallpaper.anime.glide.GlideApp;
import com.wallpaper.anime.util.BlurBitmapUtils;
import com.wallpaper.anime.util.NetworkUtil;
import com.wallpaper.anime.util.ViewSwitchUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CollectFragment extends Fragment {
    private static final String TAG = "CollectFragment";
    private List<Picture> list = new ArrayList<>();
    private List<Picture> bufferlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardItemTouchHelperCallback cardCallback;
    private ImageView mBlurView;
    private View view;
    private Runnable mBlurRunnable;
    private List<String> url_string_list = new ArrayList<>();
    private CardScaleHelper mCardScaleHelper = null;
    private int mLastPos = -1;
    CardAdapter adapter;

    public static CollectFragment createAcgFragment() {
        CollectFragment fragment = new CollectFragment();
        return fragment;
    }

    public CollectFragment() {
        EventBus.getDefault().register(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new CardAdapter(url_string_list, getActivity());
        view = inflater.inflate(R.layout.collect_fragment, null);
        mBlurView = view.findViewById(R.id.image_view);
        list.clear();
        bufferlist.clear();
        url_string_list.clear();
        list = LitePal.order("id desc").limit(5).find(Picture.class);
        if (list.size() != 0) {
            for (Picture picture : list) {
                bufferlist.add(picture);
                url_string_list.add(picture.getUrl());
            }
        }

        //显示搜索框控件
        SearchView searchView = (SearchView) view.findViewById(R.id.serachview);
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
                if (list == null && getActivity() != null) {
                    Toast.makeText(getActivity(), "搜索失败", Toast.LENGTH_SHORT).show();
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

        return view;
    }

//    private void initView() {
//        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(new CollectFragment.MyAdapter());
//        cardCallback = new CardItemTouchHelperCallback(recyclerView.getAdapter(), list);
//        cardCallback.setOnSwipedListener(new OnSwipeListener<Picture>() {
//
//            @Override
//            public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
//                CollectFragment.MyAdapter.MyViewHolder myHolder = (CollectFragment.MyAdapter.MyViewHolder) viewHolder;
//                viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);
//                if (direction == CardConfig.SWIPING_LEFT) {
//                    myHolder.dislikeImageView.setAlpha(Math.abs(ratio));
//                } else if (direction == CardConfig.SWIPING_RIGHT) {
//                    myHolder.likeImageView.setAlpha(Math.abs(ratio));
//                } else {
//                    myHolder.dislikeImageView.setAlpha(0f);
//                    myHolder.likeImageView.setAlpha(0f);
//                }
//            }
//
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, Picture o, int direction) {
//                CollectFragment.MyAdapter.MyViewHolder myHolder = (CollectFragment.MyAdapter.MyViewHolder) viewHolder;
//                viewHolder.itemView.setAlpha(1f);
//                myHolder.dislikeImageView.setAlpha(0f);
//                myHolder.likeImageView.setAlpha(0f);
//                //       Toast.makeText(CollectActivity.this, direction == CardConfig.SWIPED_LEFT ? "swiped left" : "swiped right", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSwipedClear() {
//                //   Toast.makeText(CollectActivity.this, "data clear", Toast.LENGTH_SHORT).show();
//                recyclerView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        initData();
//                        recyclerView.getAdapter().notifyDataSetChanged();
//                    }
//                }, 3000L);
//            }
//
//        });
//        final ItemTouchHelper touchHelper = new ItemTouchHelper(cardCallback);
//        final CardLayoutManager cardLayoutManager = new CardLayoutManager(recyclerView, touchHelper);
//        recyclerView.setLayoutManager(cardLayoutManager);
//        touchHelper.attachToRecyclerView(recyclerView);
//    }


    private void initView() {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);
        // mRecyclerView绑定scale效果
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(0);
        mCardScaleHelper.attachToRecyclerView(recyclerView);
        initBlurBackground();
    }

    private void initBlurBackground() {
        mBlurView = (ImageView) view.findViewById(R.id.blurView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange();
                }
            }
        });
        if (list.size() != 0) {
            notifyBackgroundChange();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEventBus event) {
        if (event.getId() == 4) {
            list.clear();
            bufferlist.clear();
            url_string_list.clear();
            list = LitePal.order("id desc").limit(5).find(Picture.class);
            if (list.size() != 0) {
                for (Picture picture : list) {
                    bufferlist.add(picture);
                    url_string_list.add(picture.getUrl());
                    if (adapter!=null)
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void notifyBackgroundChange() {
        if (mLastPos == mCardScaleHelper.getCurrentItemPos()) return;
        mLastPos = mCardScaleHelper.getCurrentItemPos();
        final String resId = url_string_list.get(mCardScaleHelper.getCurrentItemPos());
        mBlurView.removeCallbacks(mBlurRunnable);
        mBlurRunnable = new Runnable() {
            @Override
            public void run() {
                new Thread(() -> {
                    try {
                        Bitmap bitmap = GlideApp.with(getActivity())
                                .asBitmap()
                                .load(resId)
                                .submit().get();
                        if (getActivity() != null)
                            getActivity().runOnUiThread(() -> {

                                ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), bitmap, 20));
                            });
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }).start();
            }
        };
        mBlurView.postDelayed(mBlurRunnable, 500);
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
            final CollectFragment.MyAdapter.MyViewHolder holder = new CollectFragment.MyAdapter.MyViewHolder(view);
            view.setOnClickListener((view1) -> {
                holder.avatarImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picture url = list.get(holder.getAdapterPosition());
                        if (NetworkUtil.isNetworkAvailable(getContext())) {
                            startActivity(PictureActivity.newIntent(getActivity(), url.getUrl(), url_string_list, holder.getAdapterPosition()));
                        } else Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                    }
                });


            });
            return new CollectFragment.MyAdapter.MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder == null) {
                return;
            }
            Picture imageUrl = list.get(holder.getAdapterPosition());
            ((CollectFragment.MyAdapter.MyViewHolder) holder).tag.setText(imageUrl.getTag());
            ((CollectFragment.MyAdapter.MyViewHolder) holder).lable.setText(imageUrl.getLabel());
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
                    .into(((CollectFragment.MyAdapter.MyViewHolder) holder).avatarImageView);
//
//            GlideApp.with(MyApplication.mContext)
//                    .asBitmap()
//                    .placeholder(R.drawable.white)
//                    .error(R.drawable.white)
//                    .transition(BitmapTransitionOptions.withCrossFade())
//                    .load(imageUrl.getUrl())
//                    .into(blurringView);
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
}
