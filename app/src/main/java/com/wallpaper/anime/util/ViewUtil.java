package com.wallpaper.anime.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.AbsListView;

/**
 * ViewUtil
 * <p>
 * Created by jameson on 12/19/15.
 */
public class ViewUtil {
    /**
     * 返回AbsListView scrollY
     *
     * @param view          view
     * @param mHeaderHeight absListView header高度
     * @return
     */
    public static int getScrollY(AbsListView view, int mHeaderHeight) {
        if (view == null) return 0;

        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= view.getChildCount()) {
            headerHeight = mHeaderHeight;
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    /**
     * 返回View的矩阵(减去statusBar高度)
     *
     * @param view view
     * @return
     */
    public static Rect getOnScreenRect(View view) {
        return getOnScreenRect(view, true);
    }

    /**
     * 返回View的矩阵
     *
     * @param view            view
     * @param removeStatusBar 是否包含算StatusBar高度
     * @return
     */
    public static Rect getOnScreenRect(View view, boolean removeStatusBar) {
        Rect rect = new Rect();
        final int[] location = new int[2];
        view.getLocationOnScreen(location);

        int statusBarHeight = 0;
        if (removeStatusBar) {
            Rect windowRect = new Rect();
            view.getWindowVisibleDisplayFrame(windowRect);
            statusBarHeight = windowRect.top;
        }

        rect.set(location[0], location[1] - statusBarHeight, location[0] + view.getWidth(), location[1] - statusBarHeight + view.getHeight());
        return rect;
    }

    /**
     * 获取statusBar高度
     *
     * @param context context
     * @return
     */
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
