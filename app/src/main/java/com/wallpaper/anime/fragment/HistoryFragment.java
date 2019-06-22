package com.wallpaper.anime.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wallpaper.anime.EventBus.SimpleEventBus;
import com.wallpaper.anime.R;
import com.wallpaper.anime.adapter.TimeLineAdapter;
import com.wallpaper.anime.db.PictureHistory;
import com.wallpaper.anime.timeline_util.OrderStatus;
import com.wallpaper.anime.timeline_util.Orientation;
import com.wallpaper.anime.timeline_util.TimeLineModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends Fragment {

    private View view;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private List<PictureHistory> historyList = new ArrayList<>();
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private static final String TAG = "HistoryFragment";
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     //设置日期格式


    public HistoryFragment() {
        EventBus.getDefault().register(this);
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding, getActivity());
        //查询数据库中的历史记录
        historyList = LitePal.findAll(PictureHistory.class);
        for (int i = historyList.size() - 1; i >= 0; i--) {
            mDataList.add(new TimeLineModel(historyList.get(i).getUrl(), historyList.get(i).getData(), OrderStatus.ACTIVE));
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.histroy_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setHasFixedSize(true);
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding, getActivity());
        mRecyclerView.setAdapter(mTimeLineAdapter);
        return view;
    }

//    private void setDataListItems(String s) {
//        mDataList.add(new TimeLineModel(s, df.format(new Date()), OrderStatus.INACTIVE));
//        mTimeLineAdapter.notifyItemInserted(mDataList.size());
//    }

    /**
     * notifyItemInserted(int position): 列表position位置添加一条数据时可以调用，伴有动画效果
     * 插入完成的时候，第一个不会自动出现在recycleView的顶部，这个时候你需要自己手动将recycleView滑动到顶部， recycleView.scrollToPosition(0) ；
     */
    private void setDataListItems(String s) {
        int flag = checkExist(mDataList, s);
        if (flag == -1) {
            PictureHistory pictureHistory = new PictureHistory();
            pictureHistory.setData(df.format(new Date()));
            pictureHistory.setUrl(s);
            Log.d(TAG, "setDataListItems: " + "中存储数据");
            pictureHistory.save();
            mDataList.add(0, new TimeLineModel(s, df.format(new Date()), OrderStatus.ACTIVE));
        } else {
            PictureHistory ph = LitePal.where("url=?", s).findFirst(PictureHistory.class);
            ph.delete();
            //存储 最新的数据
            PictureHistory pictureHistory = new PictureHistory();
            pictureHistory.setData(df.format(new Date()));
            pictureHistory.setUrl(mDataList.get(flag).getMessage());
            pictureHistory.save();
            mDataList.remove(flag);
            if (flag >= 0) {
                mTimeLineAdapter.notifyItemRemoved(flag);
            }
            Log.d(TAG, "checkExist: " + "中存储数据");
            mDataList.add(0, new TimeLineModel(s, df.format(new Date()), OrderStatus.ACTIVE));

        }
        mTimeLineAdapter.notifyItemInserted(0);
        if (mRecyclerView != null)
            mRecyclerView.getLayoutManager().scrollToPosition(0);

    }

    private int checkExist(List<TimeLineModel> mDataList, String s) {
        int flag = -1;
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getMessage().equals(s)) {
                flag = i;
                return flag;
            }
        }
        return flag;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEventBus event) {
        if (event.getId() == 3) {
            Log.d(TAG, "onEventMainThread:" + "接受到消息" + event.getString());
            setDataListItems(event.getString());
//            mTimeLineAdapter.notifyItemInserted(0);
//            mRecyclerView.getLayoutManager().scrollToPosition(0);
        }

    }


    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }
}
