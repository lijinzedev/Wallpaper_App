package com.wallpaper.anime.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.wallpaper.anime.Application;
import com.wallpaper.anime.EventBus.SimpleEventBus;
import com.wallpaper.anime.R;
import com.wallpaper.anime.db.Picture;
import com.wallpaper.anime.fragment.Picture_Fragment;
import com.wallpaper.anime.util.FileUtil;
import com.wallpaper.anime.util.NetworkUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class PictureActivity extends BaseActivity {
    private ViewPager mViewPager;
    private static final String TAG = "PictureActivity";
    private String imageUrl;
    private LinearLayout control_layuout;
    FloatingActionButton fab;
    private Picture picture;
    private int collectflag; //收藏标签
    private static final String URL = "URL";
    private static final String LIST = "LIST";
    List<String> mlist = new ArrayList<>();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTranslucent();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_picture);
        EventBus.getDefault().register(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        collectflag = getIntent().getIntExtra("collect", 0);
        control_layuout = findViewById(R.id.control_layuout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collect(view);
            }
        });


        mViewPager = findViewById(R.id.vp_picture);
        intiData();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
//                if (position == 0) {
//                    EventBus.getDefault().post(
//                            new SimpleEventBus(1, "update"));
//                }
                String url = mlist.get(position);

                return Picture_Fragment.getInstance(url);
            }

            @Override
            public int getCount() {
                return mlist.size();
            }
        });
        if (control_layuout.getVisibility() == View.VISIBLE) {

            control_layuout.setVisibility(View.GONE);
            fab.hide();
        } else {
            control_layuout.setVisibility(View.VISIBLE);
            fab.show();
        }

        //Record last browse location
        for(int i=0;i<mlist.size();i++){
            if(mlist.get(i).equals(imageUrl)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }


    public void download(View view) {
        if (NetworkUtil.isNetworkAvailable(this))
            requestPermission(imageUrl, 1);
        else Toast.makeText(PictureActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
    }

    public void setWallpaper(View view) {
        if (NetworkUtil.isNetworkAvailable(this))
            requestPermission(imageUrl, 2);
        else Toast.makeText(PictureActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
    }

    public void collect(View view) {

        requestPermission(imageUrl, 4);
    }

    public void share(View view) {
        if (NetworkUtil.isNetworkAvailable(this))
            requestPermission(imageUrl, 3);
        else Toast.makeText(PictureActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
    }

    private void requestPermission(String url, int flag) {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        baseTask(url, flag);
                    } else {
                        Toast.makeText(PictureActivity.this, "没有存储权限，没法进行下一步了", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void MySetWallPaper(String path) {
        WallpaperManager mWallManager = WallpaperManager.getInstance(Application.getInstance());
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            mWallManager.setBitmap(bitmap);
            Toast.makeText(this, "主题壁纸设置成功，赶快去体验一下吧！", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void baseTask(final String url, int flag) {

        new AsyncTask<Void, Integer, File>() {

            @Override
            protected File doInBackground(Void... params) {
                File file = null;
                File destFile = null;
                try {
                    FutureTarget<File> future = Glide
                            .with(PictureActivity.this)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                    file = future.get();
                    // 首先保存图片
                    File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
                    File appDir = null;
                    String fileName = "test";
                    if (flag == 1) {
                        appDir = new File(pictureFolder, "AcgClub/AcgClub下载");
                        fileName = System.currentTimeMillis() + ".jpg";
                    } else if (flag == 2) {
                        appDir = new File(pictureFolder, "AcgClub/AcgClub缓存");
                        fileName = System.currentTimeMillis() + "";
                    } else if (flag == 3) {
                        appDir = new File(pictureFolder, "AcgClub/AcgClub缓存");
                        fileName = System.currentTimeMillis() + "";
                    }
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }

                    destFile = new File(appDir, fileName);
                    FileUtil.copy(file, destFile);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                return destFile;
            }

            @Override
            protected void onPostExecute(File destFile) {
                if (flag == 1) {
                    // 通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(destFile.getPath()))));
                    Toast.makeText(PictureActivity.this, "已保存", Toast.LENGTH_SHORT).show();
                } else if (flag == 2) {
                    MySetWallPaper(destFile.getAbsolutePath());

                } else if (flag == 3) {
                    Log.i(TAG, "onPostExecute: " + getUriFromFile(destFile));
                    Intent share_intent = new Intent();
                    share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                    share_intent.setType("image/*");  //设置分享内容的类型
                    share_intent.putExtra(Intent.EXTRA_STREAM, getUriFromFile(destFile));
                    //创建分享的Dialog
                    share_intent = Intent.createChooser(share_intent, "分享到");
                    startActivity(share_intent);
                } else if (flag == 4) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this, R.style.DialogIOS);
                    View view = LayoutInflater.from(PictureActivity.this).inflate(R.layout.dialog_choosepage, null);
                    TextView cancel = view.findViewById(R.id.choosepage_cancel);
                    TextView sure = view.findViewById(R.id.choosepage_sure);
                    final EditText tag = view.findViewById(R.id.tag);
                    //    final EditText lable = view.findViewById(R.id.lable);
                    final Dialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setContentView(view);
                    //使editext可以唤起软键盘
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(PictureActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String thetag = tag.getText().toString();
                            //       String thelable = lable.getText().toString();

                            picture = new Picture();
                            picture.setUrl(imageUrl);
                            if ("".equals(thetag)) {
                                picture.setTag("love");
                            } else {

                                picture.setTag(thetag);
                            }
                            picture.save();
                            Toast.makeText(PictureActivity.this, "已收藏", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        }
                    });
                }

            }


            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        }.execute();
    }

    /**
     * 获取本地文件的uri
     *
     * @param file
     * @return
     */
    public static Uri getUriFromFile(File file) {
        Uri imageUri = null;
        if (file != null && file.exists() && file.isFile()) {
//            imageUri = Uri.fromFile(file);
            imageUri = FileProvider.getUriForFile(Application.getInstance(), "com.wallpaper.anime.fileprovider", file);
        }
        return imageUri;
    }

    public void changeVisible(View view) {
        if (control_layuout.getVisibility() == View.VISIBLE) {

            control_layuout.setVisibility(View.GONE);
            fab.hide();
        } else {
            control_layuout.setVisibility(View.VISIBLE);
            if (collectflag == 11)
                fab.hide();
            else
                fab.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//           finishAfterTransition();
//        }else
        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    private void intiData() {
        //   get  the  value  of  the  intent  tag
        imageUrl = getIntent().getStringExtra(URL);
        mlist = (List<String>) getIntent().getSerializableExtra(LIST);

    }

    //config intent
    public static Intent newIntent(Context context, String url, List<String> fruitList) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(LIST, (Serializable) fruitList);
        return intent;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEventBus event) {
        if (event.getId() == 2) {
            Log.w(TAG, "onEventMainThread: "+"PictureActivity接收总线" );
            Snackbar.make(toolbar, R.string.re_success, Snackbar.LENGTH_SHORT).show();
            intiData();
            mViewPager.getAdapter().notifyDataSetChanged();
        }
    }
}
