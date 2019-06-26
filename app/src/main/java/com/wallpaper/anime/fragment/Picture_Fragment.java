package com.wallpaper.anime.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.ldoublem.loadingviewlib.LVCircular;
import com.ldoublem.loadingviewlib.view.LVGhost;
import com.ortiz.touchview.TouchImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wallpaper.anime.EventBus.SimpleEventBus;
import com.wallpaper.anime.MyApplication;
import com.wallpaper.anime.R;
import com.wallpaper.anime.db.Picture;
import com.wallpaper.anime.glide.GlideApp;
import com.wallpaper.anime.util.FileUtil;
import com.wallpaper.anime.util.NetworkUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 */

public class Picture_Fragment extends Fragment {
    @BindView(R.id.floattin_set)
    FloatingActionButton floattinSet;
    @BindView(R.id.floattin_down)
    FloatingActionButton floattinDown;
    @BindView(R.id.floattin_share)
    FloatingActionButton floattinShare;
    @BindView(R.id.floattin_star)
    FloatingActionButton floattinStar;
    @BindView(R.id.menu_labels_right)
    FloatingActionMenu menuLabelsRight;
    @BindView(R.id.lv_ghost)
    LVGhost lvGhost;

    LVCircular mLVCircular;

    private Unbinder unbinder;
    private static final String ARG_STRING = "Picture_Fragment";
    private boolean sendflag = true; //EventBus 中发送消息次数控制
    private boolean hidden = false;
    private boolean isViewCreated = false;
    String imageUrl;
    private static final String TAG = "Picture_Fragment";

    public static Picture_Fragment getInstance(String args) {
        Picture_Fragment fra = new Picture_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_STRING, args);
        fra.setArguments(bundle);
        return fra;
    }

    //onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        imageUrl = bundle.getString(ARG_STRING);
        if (hidden) {
            EventBus.getDefault().register(this);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        unbinder = ButterKnife.bind(this, view);
        floattinSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabelsRight.close(true);
                setWallpaper(v);
            }
        });
        floattinDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabelsRight.close(true);
                download(v);
            }
        });
        floattinShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabelsRight.close(true);
                share(v);
            }
        });
        floattinStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabelsRight.close(true);

                collect(v);
            }
        });
        TouchImageView touchImageView = (TouchImageView) view.findViewById(R.id.picture_big);


        /*
         * Note :the photoview is a picture zoom view
         *but I used Glide to load pictures,and I have a problem
         *Glide has cache policy,causing picture loading to be abnormal
         * so i delete the photoview and find a new; please waitting for me;
         * */

        lvGhost.setVisibility(View.VISIBLE);
        lvGhost.setViewColor(Color.WHITE);
        lvGhost.setHandColor(Color.BLACK);
        lvGhost.startAnim();
        new Thread(() -> {
            try {
                Bitmap bitmap = GlideApp.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .submit().get();
                if (getActivity() != null)
                    getActivity().runOnUiThread(() -> {

                        touchImageView.setImageBitmap(bitmap);
                        menuLabelsRight.setVisibility(View.VISIBLE);
                        lvGhost.setVisibility(View.GONE);
                        lvGhost.stopAnim();
                    });
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return view;
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEventBus event) {

    }

    /**
     * 判断当前的碎片是否可见
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.d(ARG_STRING, "onHiddenChanged: " + "当前可见");
            this.hidden = true;
            lazyLoad();
        } else {
            this.hidden = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lazyLoad();
    }

    private void lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (hidden && isViewCreated) {
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            hidden = false;
            EventBus.getDefault().post(
                    new SimpleEventBus(3, imageUrl));
            Log.d(ARG_STRING, ARG_STRING + "发送消息");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    public void setWallpaper(View view) {
        if (NetworkUtil.isNetworkAvailable(getActivity()))
            requestPermission(imageUrl, 2);
        else Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
    }

    private void requestPermission(String url, int flag) {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        baseTask(url, flag);
                    } else {
                        Toast.makeText(getActivity(), "没有存储权限，没法进行下一步了", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void collect(View view) {
        requestPermission(imageUrl, 4);
    }

    public void download(View view) {
        if (NetworkUtil.isNetworkAvailable(getActivity()))
            requestPermission(imageUrl, 1);
        else Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
    }

    public void share(View view) {
        if (NetworkUtil.isNetworkAvailable(getActivity()))
            requestPermission(imageUrl, 3);
        else Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
    }

    public void MySetWallPaper(String path) {
        WallpaperManager mWallManager = WallpaperManager.getInstance(MyApplication.getInstance());
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            mWallManager.setBitmap(bitmap);

            Toast.makeText(getActivity(), "主题壁纸设置成功，赶快去体验一下吧！", Toast.LENGTH_SHORT).show();


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
                            .with(getActivity())
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
                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(destFile.getPath()))));
                    Toast.makeText(getActivity(), "已保存", Toast.LENGTH_SHORT).show();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogIOS);
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_choosepage, null);
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
                            Toast.makeText(getActivity(), "取消收藏", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String thetag = tag.getText().toString();
                            //       String thelable = lable.getText().toString();
                            Picture picture;
                            picture = new Picture();
                            picture.setUrl(imageUrl);
                            if ("".equals(thetag)) {
                                picture.setTag("love");
                            } else {

                                picture.setTag(thetag);
                            }
                            picture.save();
                            Toast.makeText(getActivity(), "已收藏", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(
                                    new SimpleEventBus(4, ""));
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
            imageUri = FileProvider.getUriForFile(MyApplication.getInstance(), "com.wallpaper.anime.fileprovider", file);
        }
        return imageUri;
    }
}
