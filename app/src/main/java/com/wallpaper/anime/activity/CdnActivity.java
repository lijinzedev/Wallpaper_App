package com.wallpaper.anime.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.wallpaper.anime.R;
import com.wallpaper.anime.adapter.ListBaseAdapter;
import com.wallpaper.anime.adapter.SuperViewHolder;
import com.wallpaper.anime.bean.CdnPictureBean;
import com.wallpaper.anime.entily.CdnPicture_Bean;
import com.wallpaper.anime.glide.GlideApp;
import com.wallpaper.anime.minterface.CdnPictureApi;
import com.wallpaper.anime.util.Constant;
import com.wallpaper.anime.util.NetworkUtil;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.litepal.LitePalApplication.getContext;

public class CdnActivity extends BaseActivity {

    private static String baseurl = "http://wallpaper.apc.360.cn/";
    /**
     * 服务器端一共多少条数据
     */
    private static final int TOTAL_COUNTER = 34;

    /**
     * 每一页展示多少条数据
     */
    private static final int REQUEST_COUNT = 10;

    /**
     * 已经获取到多少条数据了
     */
    private static int mCurrentCounter = 0;
    private LRecyclerView mRecyclerView = null;
    private DataAdapter mDataAdapter = null;
    private PreviewHandler mHandler = new PreviewHandler(CdnActivity.this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private boolean isRefresh = false;
    private List<CdnPicture_Bean> list = new ArrayList<>();
    int id;
    private static final String TAG = "CdnActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_cdn);
        init_toolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (LRecyclerView) findViewById(R.id.list);
        mDataAdapter = new DataAdapter(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        //setLayoutManager
        id = getIntent().getIntExtra("id", 11);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //防止item位置互换
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentCounter = 0;
                mDataAdapter.clear();
                requestData();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mCurrentCounter < TOTAL_COUNTER) {
                    // loading more
                    requestData();
                } else {
                    //the end
                    mRecyclerView.setNoMore(true);
                }
            }
        });
        mRecyclerView.refresh();
    }

    private void init_toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        String title = getIntent().getStringExtra("title");
        toolbar.setTitle(title);
        toolbar.getBackground().setAlpha(200);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addItems(List<CdnPicture_Bean> list) {
        mDataAdapter.addAll(list);
        mCurrentCounter += list.size();
    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {
        private WeakReference<CdnActivity> ref;

        PreviewHandler(CdnActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CdnActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case -1:
                    int currentSize = activity.mDataAdapter.getItemCount();
                    activity.addItems(list);
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    break;
                case -2:
                    activity.notifyDataSetChanged();
                    break;
                case -3:
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    activity.mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
                        @Override
                        public void reload() {
                            requestData();
                        }
                    });
                    break;
            }
        }
    }

    /**
     * 模拟请求网络
     */
    private void requestData() {

        new Thread() {

            @Override
            public void run() {
                super.run();

                //模拟一下网络请求失败的情况
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    list.clear();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(baseurl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    CdnPictureApi api = retrofit.create(CdnPictureApi.class);
                    //http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByCategory&cid=${分类ID}&start=${从第几张图开始}&count=${每次加载的数量}&from=360chrome
                    Call<CdnPictureBean> call = api.call("WallPaper", "getAppsByCategory", id, Constant.getRandomPage("START"), Constant.getRandomPage("CDN"), "360chrome");
                    call.enqueue(new Callback<CdnPictureBean>() {
                        @Override
                        public void onResponse(Call<CdnPictureBean> call, Response<CdnPictureBean> response) {
                            for (CdnPictureBean.DataBean dataBean : response.body().getData()) {
                                Log.d(TAG, "onResponse: " + dataBean.getUtag());
                                CdnPicture_Bean cdnPicture_bean = new CdnPicture_Bean();
                                cdnPicture_bean.url = dataBean.getUrl();

                                cdnPicture_bean.Height = new Random().nextInt(1000);

                                if (cdnPicture_bean.Height < 100) {
                                    cdnPicture_bean.Height += 850;
                                }

                                list.add(cdnPicture_bean);
                                mHandler.sendEmptyMessage(-1);
                            }
                        }

                        @Override
                        public void onFailure(Call<CdnPictureBean> call, Throwable t) {

                        }
                    });


                } else {
                    mHandler.sendEmptyMessage(-3);
                }
            }
        }.start();
    }

    private class DataAdapter extends ListBaseAdapter<CdnPicture_Bean> {

        List<String> list = new ArrayList<>();

        public void changeData() {
            for (CdnPicture_Bean cdnPictureBean : mDataList) {
                list.add(cdnPictureBean.url);
            }

        }

        public DataAdapter(Context context) {
            super(context);
        }


        @Override
        public int getLayoutId() {
            return R.layout.cdn_card_item;
        }

        @Override
        public void onBindItemHolder(SuperViewHolder holder, int position) {
            changeData();
            CardView cardView = holder.getView(R.id.card_view);
            ImageView imageView = holder.getView(R.id.info_text);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), PictureActivity.class);
                    intent.putExtra("URL", list.get(position));
                    intent.putExtra("LIST", (Serializable) list);
                    intent.putExtra("postion", position);
                    startActivity(intent);
//                    Toast.makeText(mContext, "点击"+position, Toast.LENGTH_SHORT).show();
                }
            });
            CdnPicture_Bean cdnPicture_bean = mDataList.get(position);
            GlideApp.with(CdnActivity.this).
                    asDrawable().placeholder(R.drawable.bg_black).
                    transition(DrawableTransitionOptions.withCrossFade()). //淡入淡出动画
                    load(cdnPicture_bean.url).centerCrop().into(imageView);
            //cardView.getLayoutParams().height = cdnPicture_bean.Height;

        }

    }


    //config intent
    public static Intent getIntent(Context context, int id, String title) {
        Intent intent = new Intent(context, CdnActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        return intent;
    }
}
