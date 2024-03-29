package com.wallpaper.anime.timeline_util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineModel implements Parcelable {

    private String picture_url;
    private String mDate;
    private OrderStatus mStatus;
    public TimeLineModel() {
    }

    public TimeLineModel(String mMessage, String mDate, OrderStatus mStatus) {
        this.picture_url = mMessage;
        this.mDate = mDate;
        this.mStatus = mStatus;
    }

    public String getMessage() {
        return picture_url;
    }

    public void semMessage(String message) {
        this.picture_url = message;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public OrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.picture_url);
        dest.writeString(this.mDate);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }

    protected TimeLineModel(Parcel in) {
        this.picture_url = in.readString();
        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public static final Parcelable.Creator<TimeLineModel> CREATOR = new Parcelable.Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };
}
