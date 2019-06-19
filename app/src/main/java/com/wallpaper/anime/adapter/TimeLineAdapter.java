package com.wallpaper.anime.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.vipulasri.timelineview.TimelineView;
import com.wallpaper.anime.R;
import com.wallpaper.anime.activity.PictureView;
import com.wallpaper.anime.glide.GlideApp;
import com.wallpaper.anime.timeline_util.OrderStatus;
import com.wallpaper.anime.timeline_util.Orientation;
import com.wallpaper.anime.timeline_util.TimeLineModel;
import com.wallpaper.anime.util.DateTimeUtils;
import com.wallpaper.anime.util.NetworkUtil;
import com.wallpaper.anime.util.VectorDrawableUtils;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;
    private List<String> urllist = new ArrayList<>();
    private Context mContext;
    private Activity activity;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;
    private static final String TAG = "TimeLineAdapter";

    public TimeLineAdapter(List<TimeLineModel> feedList, Orientation orientation, boolean withLinePadding, Activity context) {
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
        this.mContext = context;
        this.activity = context;
        for (TimeLineModel timeLineModel : feedList) {
            urllist.add(timeLineModel.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view = mLayoutInflater.inflate(R.layout.history_fragment_item, parent, false);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        TimeLineModel timeLineModel = mFeedList.get(position);
        if (position != 0) {
            timeLineModel.setStatus(OrderStatus.COMPLETED);
        }
        Log.d(TAG, "onBindViewHolder: " + "加载图片" + position);
        if (timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if (timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.bule));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.bule));
        }

        if (!timeLineModel.getDate().isEmpty()) {
            holder.mDate.setVisibility(View.VISIBLE);
            holder.mDate.setText(DateTimeUtils.parseDateTime(timeLineModel.getDate(), "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy"));
        } else
            holder.mDate.setVisibility(View.GONE);
        holder.mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    Intent intent = new Intent(getContext(), PictureView.class);
                    intent.putExtra("IMGURL", timeLineModel.getMessage());

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view,"sharedView").toBundle());
//                }
//            }else {
                activity.startActivity(intent);

                } else Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            }
        });
        GlideApp.with(mContext)
                .asBitmap()
                .load(timeLineModel.getMessage())
                .into(holder.mMessage);
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

}