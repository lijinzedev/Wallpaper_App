package com.wallpaper.anime.db;

import com.wallpaper.anime.dragview.Tip;

import org.litepal.crud.LitePalSupport;

public class SimpleTitleTip  extends LitePalSupport implements Tip {
    private int id;
    private String tip;
    private boolean flag;
    private  String tipid;
    private  int pos;
    public int getPos() {
        return pos;
    }
    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getTipid() {
        return tipid;
    }

    public void setTipid(String tipid) {
        this.tipid = tipid;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public int getId() {
        return id;
    }

    public SimpleTitleTip(int id, String tip) {
        this.id = id;
        this.tip = tip;
    }

    public SimpleTitleTip() {
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "tip:"+ tip;
    }
}
