package com.wallpaper.anime.db;

import org.litepal.crud.LitePalSupport;

public class PictureHistory extends LitePalSupport {


    private int id;
    private String url;
    private String data;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
