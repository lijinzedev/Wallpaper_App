package com.wallpaper.anime.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.wallpaper.anime.EventBus.SimpleEventBus;
import com.wallpaper.anime.R;
import com.wallpaper.anime.activity.PictureActivity;
import com.wallpaper.anime.adapter.BasePictureAdapter;
import com.wallpaper.anime.bean.AcgBean;
import com.wallpaper.anime.minterface.AcgApi;
import com.wallpaper.anime.util.Constant;
import com.wallpaper.anime.util.NetworkUtil;
import com.wallpaper.anime.util.SSLSocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import martinbzdqsm.com.parallaxscrollimageview_master.ParallaxRecyclerViewController;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class BaseFragment extends Fragment implements BasePictureAdapter.OnItemClickListener, View.OnClickListener {
    private ParallaxRecyclerViewController mParallaxRecyclerViewController;
    private static final String TAG = "BaseFragment";
    private List<String> url_string_list = new ArrayList<>();
    private RecyclerView recyclerView;
    private BasePictureAdapter adapter;
    private String category;
    private View view;

    private SwipeRefreshLayout swipeRefresh;
//    private int curpage = 1;

    //Fragment的View加载完毕的标记
    private boolean isViewCreated;

    //Fragment对用户可见的标记
    private boolean isUIVisible;

    public static BaseFragment newInstance(String category) {
        Bundle args = new Bundle();
        BaseFragment fragment = new BaseFragment();
        args.putString("CATEGORY", category);
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = inflater.inflate(R.layout.fragment_base, null);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            isUIVisible = true;
            lazyLoad();
        } else {
            isUIVisible = false;
        }
    }

    private void lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreated && isUIVisible) {
                //数据加载完毕,恢复标记,防止重复加载
                isViewCreated = false;
                isUIVisible = false;
                requestData();
        }
    }

    void init(View v) {
        assert getArguments() != null;

        recyclerView = v.findViewById(R.id.base_recyclerview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //
        mParallaxRecyclerViewController = new ParallaxRecyclerViewController(linearLayoutManager, R.id.base_fragment_item_image);
        recyclerView.addOnScrollListener(mParallaxRecyclerViewController);
        ParallaxRecyclerViewController mParallaxRecyclerViewController = new ParallaxRecyclerViewController(linearLayoutManager, R.id.base_fragment_item_image);
        recyclerView.addOnScrollListener(mParallaxRecyclerViewController);


        adapter = new BasePictureAdapter(getContext(), recyclerView, url_string_list, this);
        recyclerView.setAdapter(adapter);
        FloatingActionButton floatingActionButton = v.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //get message
                initView(view);
            }
        }
    };

    private void initView(View v) {
        category = getArguments().getString("CATEGORY");
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.base_recyclerview);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BasePictureAdapter(getContext(), recyclerView, url_string_list, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        AnimationSet set = new AnimationSet(false);
        WindowManager wm = getActivity().getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();
        Animation animation = new TranslateAnimation(0, 0, height, 0); //translateanimation
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateInterpolator(1.0F));
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);//set order；
        controller.setDelay(0.2f);//set LayoutAnimationController；
        recyclerView.setLayoutAnimation(controller);   //set animation
        // 外部对RecyclerView设置监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // 查看源码可知State有三种状态：SCROLL_STATE_IDLE（静止）、SCROLL_STATE_DRAGGING（上升）、SCROLL_STATE_SETTLING（下落）
                if (newState == SCROLL_STATE_IDLE) { // 滚动静止时才加载图片资源，极大提升流畅度
                    adapter.setScrolling(false);
                    adapter.notifyDataSetChanged(); // notify调用后onBindViewHolder会响应调用
                } else
                    adapter.setScrolling(true);
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        recyclerView.setAdapter(adapter);
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
        FloatingActionButton floatingActionButton = v.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
    }

    public void requestData() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            int curpage = Constant.getRandomPage(category);
            Log.i(TAG, "requestData: " + "加载数据" + curpage + "链表长度: " + url_string_list.size());
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
            builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
            url_string_list.clear();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.rabtman.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build();
            AcgApi acgApi = retrofit.create(AcgApi.class);
            Call<AcgBean> call = acgApi.ACG_BEAN_CALL(category, curpage);
            call.enqueue(new Callback<AcgBean>() {
                @Override
                public void onResponse(@NonNull Call<AcgBean> call, @NonNull Response<AcgBean> response) {
                    if (response.isSuccessful()) {
                        AcgBean acgBean = response.body();
                        assert acgBean != null;
                        for (AcgBean.DataBean dataBean : acgBean.getData()) {
                            //urlList.add(dataBean);
                            url_string_list.addAll(dataBean.getImgUrls());
                        }
                        Log.d(TAG, "onResponse: " + "链表长度: " + url_string_list.size());
                        swipeRefresh.setRefreshing(false);
                        //  adapter.notifyDataSetChanged();
                        mhandler.sendEmptyMessage(1);
                        EventBus.getDefault().post(
                                new SimpleEventBus(2, "end"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AcgBean> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        url_string_list.clear();

        adapter.notifyDataSetChanged();
        requestData();
    }


    @Override
    public void onItemClick(String url, int position) {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            startActivity(PictureActivity.newIntent(getActivity(), url_string_list.get(position), url_string_list, position));
            getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        } else Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(String url, int position) {
//        Toast.makeText(getContext(), imageUrl, Toast.LENGTH_SHORT).show();
//        requestPermission(imageUrl);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEventBus event) {

        if (event.getId() == 1) {
            Log.w(TAG, "onEventMainThread: " + "BaseFragment 刷新");
            Snackbar.make(swipeRefresh, R.string.reing, Snackbar.LENGTH_SHORT).show();
            requestData();
        }
    }

}
