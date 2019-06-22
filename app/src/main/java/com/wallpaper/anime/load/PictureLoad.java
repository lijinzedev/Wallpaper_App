package com.wallpaper.anime.load;

import android.util.Log;

import com.wallpaper.anime.bean.CdnBean_item;
import com.wallpaper.anime.db.SimpleTitleTip;
import com.wallpaper.anime.minterface.CndItemApi;
import com.wallpaper.anime.util.SSLSocketClient;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PictureLoad {
    private static final String TAG = "PictureLoad";

    public static List<SimpleTitleTip> request_item_data(List<SimpleTitleTip> datas, String baseurl) {
        Log.d(TAG, "requestdata: " + "加载数据");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        datas.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CndItemApi cndItemApi = retrofit.create(CndItemApi.class);
        Call<CdnBean_item> call = cndItemApi.CDN_BEAN_ITEM_CALL();
        call.enqueue(new Callback<CdnBean_item>() {
            @Override
            public void onResponse(Call<CdnBean_item> call, Response<CdnBean_item> response) {
                if (response.isSuccessful()) {
                    CdnBean_item cdnBean_item = response.body();
                    Log.d(TAG, "onResponse: " + cdnBean_item.getErrmsg());
                    for (CdnBean_item.DataBean dataBean : cdnBean_item.getData()) {
                        SimpleTitleTip simpleTitleTip = new SimpleTitleTip();
                        simpleTitleTip.setId(Integer.parseInt(dataBean.getId()));
                        simpleTitleTip.setTip(dataBean.getName());
                        Log.d(TAG, "onResponse: " + dataBean.getName());
                        datas.add(simpleTitleTip);
                    }
                    Log.d(TAG, "onCreateView: " + datas.size());
                }
            }

            @Override
            public void onFailure(Call<CdnBean_item> call, Throwable t) {

            }
        });
        if (datas.size() != 0) {
            return datas;

        } else
            return null;
    }
}
