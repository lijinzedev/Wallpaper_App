package com.wallpaper.anime.db;

import org.litepal.crud.LitePalSupport;

public class Picture extends LitePalSupport {
    private int id;
    private String url;

    private String tag;    //收藏标签
    private String label; //名称
    private String love;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }

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


}
