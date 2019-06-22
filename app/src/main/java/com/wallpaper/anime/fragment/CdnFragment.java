package com.wallpaper.anime.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wallpaper.anime.R;
import com.wallpaper.anime.bean.CdnBean_item;
import com.wallpaper.anime.dragview.EasyTipDragView;
import com.wallpaper.anime.db.SimpleTitleTip;
import com.wallpaper.anime.dragview.TipDataModel;
import com.wallpaper.anime.dragview.TipItemView;
import com.wallpaper.anime.minterface.CndItemApi;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 360图片 展示碎片
 */
public class CdnFragment extends android.support.v4.app.Fragment {


    private List<SimpleTitleTip> simpleTitleTips = new ArrayList<>();
    private EasyTipDragView easyTipDragView;
    private String baseurl = "http://cdn.apc.360.cn/";
    private static final String TAG = "CdnFragment";
    private boolean flag = true;
    TextView tv;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        tv.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cdn_fragment, null);
        requestdata();
        easyTipDragView = (EasyTipDragView) view.findViewById(R.id.easy_tip_drag_view);
        //设置已包含的标签数据
        easyTipDragView.setAddData(TipDataModel.getAddTips());
        //设置可以添加的标签数据
        easyTipDragView.setDragData(TipDataModel.getDragTips());
        //在easyTipDragView处于非编辑模式下点击item的回调（编辑模式下点击item作用为删除item）
        easyTipDragView.setSelectedListener(new TipItemView.OnSelectedListener() {
            @Override
            public void onTileSelected(SimpleTitleTip entity, int position, View view) {

                Toast.makeText(getActivity(), entity.getTipid(), Toast.LENGTH_SHORT).show();

            }
        });
        //设置每次数据改变后的回调（例如每次拖拽排序了标签或者增删了标签都会回调）
        easyTipDragView.setDataResultCallback(new EasyTipDragView.OnDataChangeResultCallback() {
            @Override
            public void onDataChangeResult(ArrayList<SimpleTitleTip> tips) {
                for (SimpleTitleTip tip : tips) {

                    Log.d(TAG, "原始回调数据" + tip.getPos() + tip.getTip());

                }
                int i = 1;

                List<SimpleTitleTip> simpleTitleTips = LitePal.order("pos").where("flag=?", "0").find(SimpleTitleTip.class);

                Map<String, SimpleTitleTip> tempmaps = new HashMap<>();

                for (SimpleTitleTip simpleTitleTip : simpleTitleTips) {

                    tempmaps.put(simpleTitleTip.getTipid(), simpleTitleTip);

                }

                for (SimpleTitleTip simpleTitleTip : tips) {

                    if (tempmaps.containsKey(simpleTitleTip.getTipid())) {
                        simpleTitleTip.setFlag(true);
                    }
                    simpleTitleTip.setPos(i++);

                    simpleTitleTip.updateAll("tipid=?", simpleTitleTip.getTipid());
                }

                List<SimpleTitleTip> simpleTitleTipList = LitePal.findAll(SimpleTitleTip.class);

                Map<String, SimpleTitleTip> tipHashMap = new HashMap<>();

                for (SimpleTitleTip simpleTitleTip : tips) {

                    tipHashMap.put(simpleTitleTip.getTipid(), simpleTitleTip);
                }

                for (SimpleTitleTip simpleTitleTip : simpleTitleTipList) {

                    if (!tipHashMap.containsKey(simpleTitleTip.getTipid())) {

                        simpleTitleTip.setToDefault("flag");

                        simpleTitleTip.updateAll("tipid=?", simpleTitleTip.getTipid());

                    }
                }
            }
        });
        //设置点击“确定”按钮后最终数据的回调
        easyTipDragView.setOnCompleteCallback(new EasyTipDragView.OnCompleteCallback() {
            @Override
            public void onComplete(ArrayList<SimpleTitleTip> tips) {
                /**
                 * 数据回调
                 */
                int i = 1;

                List<SimpleTitleTip> simpleTitleTips = LitePal.order("pos").where("flag=?", "0").find(SimpleTitleTip.class);

                Map<String, SimpleTitleTip> tempmaps = new HashMap<>();

                for (SimpleTitleTip simpleTitleTip : simpleTitleTips) {

                    tempmaps.put(simpleTitleTip.getTipid(), simpleTitleTip);

                }

                for (SimpleTitleTip simpleTitleTip : tips) {

                    if (tempmaps.containsKey(simpleTitleTip.getTipid())) {

                        simpleTitleTip.setFlag(true);

                    }

                    simpleTitleTip.setPos(i++);

                    simpleTitleTip.updateAll("tipid=?", simpleTitleTip.getTipid());
                }

                List<SimpleTitleTip> simpleTitleTipList = LitePal.findAll(SimpleTitleTip.class);

                Map<String, SimpleTitleTip> tipHashMap = new HashMap<>();

                for (SimpleTitleTip simpleTitleTip : tips) {

                    tipHashMap.put(simpleTitleTip.getTipid(), simpleTitleTip);
                }

                for (SimpleTitleTip simpleTitleTip : simpleTitleTipList) {

                    if (!tipHashMap.containsKey(simpleTitleTip.getTipid())) {

                        simpleTitleTip.setToDefault("flag");

                        simpleTitleTip.updateAll("tipid=?", simpleTitleTip.getTipid());

                    }
                }

            }
        });
        tv = getActivity().findViewById(R.id.dingyue);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv.getText().equals("订阅")) {
                    easyTipDragView.open();
                    tv.setText("完成");
                } else {
                    //完成关闭，回调数据
                    easyTipDragView.dragTipAdapter.cancelEditingStatus();
                    if (easyTipDragView.completeCallback != null) {
                        easyTipDragView.completeCallback.onComplete(easyTipDragView.lists);
                    }
                    easyTipDragView.close();
                    tv.setText("订阅");
                }
            }
        });
        return view;
    }

    private void requestdata() {
        simpleTitleTips = LitePal.findAll(SimpleTitleTip.class);
        Map<String, SimpleTitleTip> tempmaps = new HashMap<>();
        for (SimpleTitleTip simpleTitleTip : simpleTitleTips) {
            tempmaps.put(simpleTitleTip.getTipid(), simpleTitleTip);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CndItemApi cndItemApi = retrofit.create(CndItemApi.class);
        Call<CdnBean_item> call = cndItemApi.CDN_BEAN_ITEM_CALL();
        call.enqueue(new Callback<CdnBean_item>() {
            @Override
            public void onResponse(Call<CdnBean_item> call, Response<CdnBean_item> response) {
                int i;
                if (response.isSuccessful()) {
                    CdnBean_item cdnBean_item = response.body();
                    for (CdnBean_item.DataBean dataBean : cdnBean_item.getData()) {
                        SimpleTitleTip simpleTitleTip = new SimpleTitleTip();
                        simpleTitleTip.setTip(dataBean.getName());
                        simpleTitleTip.setTipid(dataBean.getId());
                        if (dataBean.getId().equals("36"))
                            simpleTitleTip.setFlag(true);
                        simpleTitleTips.add(simpleTitleTip);
                    }
                    for (SimpleTitleTip simpleTitleTip : simpleTitleTips) {
                        if (!tempmaps.containsKey(simpleTitleTip.getTipid())) {
                            simpleTitleTip.save();
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<CdnBean_item> call, Throwable t) {

            }
        });
    }

    public void toast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}
