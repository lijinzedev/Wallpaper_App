package com.wallpaper.anime.minterface;

import com.wallpaper.anime.bean.CdnBean_item;

import retrofit2.Call;
import retrofit2.http.GET;
/**
 *
 * 360 api接口
 */
public interface CndItemApi {

    @GET("index.php?c=WallPaper&a=getAllCategoriesV2&from=360chrome")
    Call<CdnBean_item> CDN_BEAN_ITEM_CALL();
}
