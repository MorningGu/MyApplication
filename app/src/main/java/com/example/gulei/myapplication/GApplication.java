package com.example.gulei.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.example.gulei.myapplication.common.utils.DeviceUuidFactory;

import com.example.gulei.myapplication.common.utils.ImageLoaderUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.analytics.AnalyticsConfig;

import java.lang.reflect.Field;

/**
 * Created by gulei on 2016/4/29 0029.
 */
public class GApplication extends Application {

    private static GApplication mInstance;
    //设备id工厂
    private DeviceUuidFactory deviceUuidFactory;

    private boolean isDebug = false;

    private Boolean hasCamera = null;
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    private void init(){
        //内存分析工具
        LeakCanary.install(this);
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        AnalyticsConfig.enableEncrypt(true);
        mInstance = this;
        deviceUuidFactory = new DeviceUuidFactory(this.getApplicationContext());
        ImageLoaderUtils.init(this);//这里是一个初始化
        initOkHttp();
        initDebug();
    }

    /**
     * 判断是否有相机
     * @return
     */
    public boolean hasCamera(){
        if(hasCamera==null){
            PackageManager pm = getPackageManager();
            // FEATURE_CAMERA - 后置相机
            // FEATURE_CAMERA_FRONT - 前置相机
            if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                    && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
//                Log.i("camera", "non-support");
                hasCamera = false;
            } else {
//                Log.i("camera", "support");
                hasCamera = true;
            }
        }
        return hasCamera;
    }
    /**
     * 初始化是否是debug
     */
    private void initDebug(){
        ApplicationInfo appInfo = null;
        try {
            appInfo = GApplication.getInstance().getPackageManager()
                    .getApplicationInfo(GApplication.getInstance().getPackageName(),
                            PackageManager.GET_META_DATA);
            isDebug =  !appInfo.metaData.getBoolean("IS_RELEASE");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是不是debug
     * @return
     */
    public boolean isDebug(){
        return isDebug;
    }
    /**
     * 初始化okHttp
     */
    private void initOkHttp(){
//        HttpHeaders headers = new HttpHeaders();
//        headers.put("commonHeaderKey1", "commonHeaderValue1");    //所有的 header 都 不支持 中文
//        headers.put("commonHeaderKey2", "commonHeaderValue2");
//        HttpParams params = new HttpParams();
//        params.put("commonParamsKey1", "commonParamsValue1");     //所有的 params 都 支持 中文
//        params.put("commonParamsKey2", "这里支持中文参数");
        //必须调用初始化
        OkHttpUtils.init(this);
        //以下都不是必须的，根据需要自行选择
        OkHttpUtils.getInstance()//
//                .debug("OkHttpUtils")                                              //是否打开调试
                .setConnectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                 //全局的写入超时时间
                .setCacheMode(CacheMode.DEFAULT) ;                                  //全局的缓存模式
//                .addCommonHeaders(headers)                                         //设置全局公共头
//                .addCommonParams(params);                                          //设置全局公共参数
    }
    /**
     * 得到Application实例
     * @return
     */
    public static GApplication getInstance() {
        return mInstance;
    }
    /**
     * 获取设备号
     *
     * @return
     */
    public String getDeviceId() {
        return deviceUuidFactory.getDeviceUuid();
    }

    /**
     * 获取当前系统版本号
     * @return
     */
    public int getAPIVersion() {
        int APIVersion;
        try {
            APIVersion = android.os.Build.VERSION.SDK_INT;
        } catch (NumberFormatException e) {
            APIVersion = 0;
        }
        return APIVersion;
    }
    /**
     * 获取状态栏高度
     */
    public int getBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;// 默认为38，貌似大部分是这样的

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }
    /**
     * 获取的是铃声的Uri
     */
    public static Uri getDefaultRingtoneUri(Context ctx, int type) {
        return RingtoneManager.getActualDefaultRingtoneUri(ctx, type);
    }

    Vibrator vibrator;

    /**
     *  输入震动毫秒数
     */
    public void setVibrate() {
        if (vibrator == null) {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }
        vibrator.vibrate(500);
    }
    /**
     * 播放铃声
     */
    MediaPlayer mMediaPlayer;
    public MediaPlayer getMediaPlayer() {
        if (mMediaPlayer != null){
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = MediaPlayer.create(
                this,
                getDefaultRingtoneUri(this,
                        RingtoneManager.TYPE_NOTIFICATION));
        return mMediaPlayer;
    }

}
