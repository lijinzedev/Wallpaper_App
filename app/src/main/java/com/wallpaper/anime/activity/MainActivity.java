package com.wallpaper.anime.activity;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wallpaper.anime.R;
import com.wallpaper.anime.db.SimpleTitleTip;
import com.wallpaper.anime.fragment.AcgFragment;
import com.wallpaper.anime.fragment.CdnFragment;
import com.wallpaper.anime.fragment.CollectFragment;
import com.wallpaper.anime.fragment.Fragment_for3d;
import com.wallpaper.anime.fragment.HistoryFragment;
import com.wallpaper.anime.menu.DrawerAdapter;
import com.wallpaper.anime.menu.DrawerItem;
import com.wallpaper.anime.menu.SimpleItem;
import com.wallpaper.anime.menu.SpaceItem;
import com.wallpaper.slidingrootnav.SlidingRootNav;
import com.wallpaper.slidingrootnav.SlidingRootNavBuilder;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
    private static final int POS_DASHBOARD = 0;
    private static final int POS_ACCOUNT = 1;
    private static final int POS_MESSAGES = 2;
    private static final int POS_CART = 3;
    private static final int POS_3D = 4;
    private static final int POS_LOGOUT = 5;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav slidingRootNav;
    private Map<String, Fragment> map = new HashMap<>();
    private FragmentManager fragmentManager;

    private Fragment currentFragment = new Fragment();
    private List<Fragment> fragments = new ArrayList<>();
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    private int currentIndex = 0;
    private TextView tv;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.dingyue);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        // LitePal.deleteAll(PictureHistory.class);
        List<SimpleTitleTip> simpleTitleTips = LitePal.findAll(SimpleTitleTip.class);

        if (savedInstanceState != null) { // “内存重启”时调用
            //获取“内存重启”时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT, 0);
            //注意，添加顺序要跟下面添加的顺序一样！！！！

            fragments.removeAll(fragments);
            fragments.add(fragmentManager.findFragmentByTag(0 + ""));
            fragments.add(fragmentManager.findFragmentByTag(1 + ""));
            fragments.add(fragmentManager.findFragmentByTag(2 + ""));
            fragments.add(fragmentManager.findFragmentByTag(3 + ""));
            fragments.add(fragmentManager.findFragmentByTag(4 + ""));

            //恢复fragment页面
            restoreFragment();
        } else {
            //正常启动时调用
            fragments.add(AcgFragment.createAcgFragment());
            fragments.add(CollectFragment.createAcgFragment());
            fragments.add(new HistoryFragment());
            fragments.add(new CdnFragment());
            fragments.add(new Fragment_for3d());
        }
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();
        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();
        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_ACCOUNT),
                createItemFor(POS_MESSAGES),
                createItemFor(POS_CART),
                createItemFor(POS_3D),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);
        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        adapter.setSelected(POS_DASHBOARD);
    }

    @Override
    public void onItemSelected(int position) {
        switch (position) {

            case POS_LOGOUT:
                finish();
                break;
            case POS_DASHBOARD:
                currentIndex = 0;
                tv.setVisibility(View.GONE);
                break;
            case POS_ACCOUNT:
                currentIndex = 1;
                tv.setVisibility(View.GONE);
                break;
            case POS_MESSAGES:
                currentIndex = 2;
                tv.setVisibility(View.GONE);
                break;
            case POS_CART:
                currentIndex = 3;
                tv.setVisibility(View.VISIBLE);
                break;
            case POS_3D:
                currentIndex = 4;
                tv.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        showFragment();
//
//        if (position == POS_LOGOUT) {
//
//        } else if (position == POS_DASHBOARD) {
//            if (map.containsKey("acg")) {
//                showFragment(map.get("acg"));
//            } else {
//                Log.d(TAG, "onItemSelected: " + "fragment被new了出来");
//                slidingRootNav.closeMenu();
//                acgFragment = AcgFragment.createAcgFragment();
//                map.put("acg", acgFragment);
//                showFragment(map.get("acg"));
//            }

//            if (map.containsKey("collect")) {
//                showFragment(map.get("collect"));
//
//            } else {
//                Log.d(TAG, "onItemSelected: " + "collectfragment被new了出来");
//                slidingRootNav.closeMenu();
//               collectFragment = CollectFragment.createAcgFragment();
//                map.put("collect", collectFragment);
//                showFragment(map.get("collect"));
//            }
//        }
//
    }

    //
//    private void showFragment(android.support.v4.app.Fragment fragment) {
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, fragment)
//                .commit();
//
//    }
    private void showFragment() {

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //如果之前没有添加过
        if (!fragments.get(currentIndex).isAdded()) {
            transaction
                    .hide(currentFragment)
                    .add(R.id.container, fragments.get(currentIndex), "" + currentIndex);  //第三个参数为添加当前的fragment时绑定一个tag

        } else {
            transaction
                    .hide(currentFragment)
                    .show(fragments.get(currentIndex));
        }
        currentFragment = fragments.get(currentIndex);
        transaction.commit();
    }

    private void restoreFragment() {
        FragmentTransaction mBeginTreansaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {
            if (i == currentIndex) {
                mBeginTreansaction.show(fragments.get(i));
            } else {
                mBeginTreansaction.hide(fragments.get(i));
            }
        }
        mBeginTreansaction.commit();
        //把当前显示的fragment记录下来
        currentFragment = fragments.get(currentIndex);
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.bule))
                .withSelectedTextTint(color(R.color.bule));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        outState.putInt(CURRENT_FRAGMENT, currentIndex);
        super.onSaveInstanceState(outState);
    }


}
