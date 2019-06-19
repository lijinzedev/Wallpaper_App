package com.wallpaper.anime.fragment;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wallpaper.anime.R;
import com.wallpaper.anime.adapter.ViewPagerAdapter;
import com.wallpaper.anime.glide.GlideCatchUtil;
import com.wallpaper.anime.util.FileUtil;

/**
 *
 * AcgActivity转成 碎片
 */
public class AcgFragment extends android.support.v4.app.Fragment {


    private static final String TAG = "AcgFragment";
    private BottomNavigationView bottomNavigationView;
    private View view;
    private Activity activity;
    MenuItem prevMenuItem;
    private ViewPager viewPager;
    public static AcgFragment createAcgFragment() {
        AcgFragment fragment = new AcgFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        setTranslucent();
        view = inflater.inflate(R.layout.acg_fragmen, null);
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.item_moeimg:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.item_cosplay:
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.item_gamersky:
                            viewPager.setCurrentItem(2);
                            break;
                    }
                    return false;
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 如果想禁止滑动，可以把下面的代码取消注释
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
        setupViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(BaseFragment.newInstance("moeimg"));
        adapter.addFragment(BaseFragment.newInstance("cosplay"));
        adapter.addFragment(BaseFragment.newInstance("gamersky"));
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       getActivity().getMenuInflater().inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clear_cache:
                clearCache();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void clearCache() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("确定清除缓存？");
        builder.setCancelable(true);
        builder.setPositiveButton("确定", (dialog, which) -> {
            final RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            FileUtil.deleteCache();
                            if (GlideCatchUtil.getInstance().clearCacheDiskSelf())
                                showToast("清除完成");
                        } else {
                            showToast("没有存储权限，没法进行下一步了");
                        }
                    });
        });
        builder.setNegativeButton("取消", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

    }

    void setTranslucent() {
//        StatusBarUtil.setTranslucent(this);
        changeStatusBarTextColor(true);
    }
    private void changeStatusBarTextColor(boolean isBlack) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (isBlack) {
               getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
            } else {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//恢复状态栏白色字体
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }
}
