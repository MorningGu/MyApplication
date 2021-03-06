package com.example.gulei.myapplication.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.utils.AppManager;
import com.example.gulei.myapplication.common.utils.LoadingUtils;
import com.example.gulei.myapplication.mvp.iview.IBaseView;
import com.example.gulei.myapplication.ui.view.HeadLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by gulei on 2016/3/10.
 */
public class BaseActivity extends AppCompatActivity implements IBaseView {
    public int mScreenWidth;
    public int mScreenHeight;
    private HeadLayout mHeadLayout;
    LoadingUtils mLoadingUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化屏幕区域
        AppManager.getAppManager().addActivity(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
    }

    public void onResume() {
        super.onResume();
        //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。参数为页面名称，可自定义)
        MobclickAgent.onPageStart(this.getClass().getName());
        //基本的统计
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。参数为页面名称，可自定义
        MobclickAgent.onPageEnd(this.getClass().getName());
        //统计时长
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        AppManager.getAppManager().finishActivity(this);
//    }

    /**
     * 默认的导航栏，左边图片和title
     * @param title
     */
    protected void initDefaultHeader(String title ){
        mHeadLayout = (HeadLayout)findViewById(R.id.common_header);
        if(mHeadLayout!=null){
            mHeadLayout.initView();
            mHeadLayout.initTitleAndLeftImage(title, R.mipmap.ic_launcher, new HeadLayout.OnLeftClickListener() {
                @Override
                public void onClick() {
                    AppManager.getAppManager().finishActivity(BaseActivity.this);
                }
            });
        }
    }
    /**
     * 导航栏，左边图片和title右边文字
     * @param title
     */
    protected void initTitleAndRightText(String title, String rightText, HeadLayout.OnRightClickListener onRightClickListener){
        mHeadLayout = (HeadLayout)findViewById(R.id.common_header);
        if(mHeadLayout!=null){
            mHeadLayout.initView();
            mHeadLayout.initTitleAndImageText(title, R.mipmap.ic_launcher, rightText,new HeadLayout.OnLeftClickListener() {
                @Override
                public void onClick() {
                    AppManager.getAppManager().finishActivity(BaseActivity.this);
                }
            },onRightClickListener);
        }
    }
    /**
     * 导航栏，左边图片和title右边文字
     * @param title
     */
    protected void initTitleAndRightText(String title, String rightText, HeadLayout.OnLeftClickListener onLeftClickListener, HeadLayout.OnRightClickListener onRightClickListener){
        mHeadLayout = (HeadLayout)findViewById(R.id.common_header);
        if(mHeadLayout!=null){
            mHeadLayout.initView();
            mHeadLayout.initTitleAndImageText(title, R.mipmap.ic_launcher, rightText,onLeftClickListener,onRightClickListener);
        }
    }
    @Override
    public void showLoading() {
        if (mLoadingUtil == null) {
            mLoadingUtil = new LoadingUtils(this, true);
        }
        mLoadingUtil.showLoading();
    }

    @Override
    public void dismissLoading() {
        if (mLoadingUtil != null) {
            mLoadingUtil.dismissLoading();
        }
    }
}
