package com.wallpaper.anime.bean;

import java.util.List;

public class CdnBean_item {

    /**
     * errno : 0
     * errmsg : 正常
     * consume : 1
     * total : 18
     * data : [{"id":"36","name":"4K专区","order_num":"110","tag":"","create_time":"2015-12-08 13:50:44"},{"id":"6","name":"美女模特","order_num":"100","tag":"","create_time":"2011-10-29 17:49:27"},{"id":"30","name":"爱情美图","order_num":"99","tag":"","create_time":"2012-11-23 10:49:25"},{"id":"9","name":"风景大片","order_num":"98","tag":"","create_time":"2011-11-02 16:33:34"},{"id":"15","name":"小清新","order_num":"85","tag":"","create_time":"2011-12-15 18:47:03"},{"id":"26","name":"动漫卡通","order_num":"84","tag":"","create_time":"2012-07-27 17:17:42"},{"id":"11","name":"明星风尚","order_num":"83","tag":"","create_time":"2011-11-02 17:38:58"},{"id":"14","name":"萌宠动物","order_num":"75","tag":"","create_time":"2011-12-15 18:23:27"},{"id":"5","name":"游戏壁纸","order_num":"74","tag":"","create_time":"2011-10-29 17:49:12"},{"id":"12","name":"汽车天下","order_num":"72","tag":"","create_time":"2011-12-13 18:59:40"},{"id":"10","name":"炫酷时尚","order_num":"70","tag":"","create_time":"2011-11-02 17:10:53"},{"id":"29","name":"月历壁纸","order_num":"69","tag":"","create_time":"2012-11-23 09:19:54"},{"id":"7","name":"影视剧照","order_num":"68","tag":"","create_time":"2011-11-02 15:22:39"},{"id":"13","name":"节日美图","order_num":"67","tag":"节日 端午 中秋 元旦 圣诞 清明 情人 春节 新年 2012","create_time":"2011-12-14 18:47:32"},{"id":"22","name":"军事天地","order_num":"14","tag":"","create_time":"2012-05-29 15:10:04"},{"id":"16","name":"劲爆体育","order_num":"12","tag":"","create_time":"2011-12-30 11:37:49"},{"id":"18","name":"BABY秀","order_num":"10","tag":"","create_time":"2012-03-28 23:52:39"},{"id":"35","name":"文字控","order_num":"9","tag":"","create_time":"2014-09-25 18:35:57"}]
     */

    private String errno;
    private String errmsg; //返回信息
    private String consume;
    private String total;  //类型数量
    private List<DataBean> data;

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getConsume() {
        return consume;
    }

    public void setConsume(String consume) {
        this.consume = consume;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 36
         * name : 4K专区
         * order_num : 110
         * tag :
         * create_time : 2015-12-08 13:50:44
         */

        private String id;
        private String name;
        private String order_num;
        private String tag;
        private String create_time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrder_num() {
            return order_num;
        }

        public void setOrder_num(String order_num) {
            this.order_num = order_num;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }
}
