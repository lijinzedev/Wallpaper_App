package com.wallpaper.anime.minterface;


import com.wallpaper.anime.bean.CdnPictureBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;



public interface CdnPictureApi {
    @GET("index.php")
        //http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByCategory&cid=${分类ID}&start=${从第几张图开始}&count=${每次加载的数量}&from=360chrome
    Call<CdnPictureBean> call(@Query("c") String c, @Query("a") String a, @Query("cid") int cid, @Query("start") int start, @Query("count") int count, @Query("from") String from);
}
