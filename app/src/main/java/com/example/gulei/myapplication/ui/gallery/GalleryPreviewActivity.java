package com.example.gulei.myapplication.ui.gallery;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.utils.AppManager;
import com.example.gulei.myapplication.common.utils.PrintUtils;
import com.example.gulei.myapplication.ui.base.BaseActivity;
import com.example.gulei.myapplication.ui.view.fresco.photoview.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

public class GalleryPreviewActivity extends BaseActivity implements View.OnClickListener{
    private GalleryManager galleryManager = GalleryManager.getInstance();
    private List<String> selectedPictures = galleryManager.getSelectedPictures();
    private List<ImageViewHolder> allPictures = galleryManager.getAllPictures();
    private List<ImageViewHolder> mPictures = new ArrayList<>();

    private static final String STATE_POSITION = "STATE_POSITION";
    private static final String STATE_MAX = "STATE_MAX";
    private static final String STATE_PIC_SELECTED= "STATE_PIC_SELECTED";
    private static final String STATE_PIC_ALL= "STATE_PIC_ALL";

    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_SELECT_MAX = "image_select_max";
    public static final String EXTRA_IMAGE_SELECTED = "SelectGalleryPics";
    public static final String EXTRA_IMAGE_ALL = "AllGalleryPics";

    private RelativeLayout rl_title,rl_bottom;
    private LinearLayout ll_ok,ll_cb;
    private ImageView iv_back;
    private TextView tv_select_index,tv_count_all;
    private CheckBox cb_state;
    private HackyViewPager mPager;

    private ImagePagerAdapter mAdapter;

    private int position;//当前的位置
    private int selectMaxNum; //可以选择的最大数量

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_preview);
        if(initData(savedInstanceState)){
            initView();
        }
    }

    /**
     * 初始化数据
     * @param savedInstanceState
     * @return  数据正常返回true，数据异常返回false
     */
    private boolean initData(Bundle savedInstanceState){
        position = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        selectMaxNum = getIntent().getIntExtra(EXTRA_IMAGE_SELECT_MAX, 0);

        if (savedInstanceState != null) {
            //这里是不保留活动的时候，不保留活动和初次进入是互斥的关系
            position = savedInstanceState.getInt(STATE_POSITION);
            selectMaxNum = savedInstanceState.getInt(STATE_MAX);
            selectedPictures = savedInstanceState.getStringArrayList(STATE_PIC_SELECTED);
            allPictures = savedInstanceState.getParcelableArrayList(STATE_PIC_ALL);
            if(allPictures!=null && allPictures.size()>0){
                mPictures.addAll(allPictures);
                if("photo".equals(mPictures.get(0).getImage_path())){
                    mPictures.remove(0);
                    position-=1;
                    if(position<0){
                        position=0;
                    }
                }
            }
        }else{
            //这里是从外部进入的时候
            if(allPictures!=null && allPictures.size()>0){
                mPictures.addAll(allPictures);
                if("photo".equals(mPictures.get(0).getImage_path())){
                    mPictures.remove(0);
                    position-=1;
                    if(position<0){
                        position=0;
                    }
                }
            }
        }
//        //这里是为了处理一个崩溃，原因不明，但结果是position为108，pictures.size=0
//        if(position>=pictures.size() || position<0){
//            PrintUtils.showToast("数据加载错误");
//            goFinish(false);
//            return false;
//        }
        return true;
    }
    private void initView(){
        mPager = (HackyViewPager) findViewById(R.id.pager);
        tv_select_index = (TextView) findViewById(R.id.tv_select_index);
        tv_count_all = (TextView)findViewById(R.id.tv_count_all);
        rl_title = (RelativeLayout)findViewById(R.id.rl_preview_title);
        rl_bottom = (RelativeLayout)findViewById(R.id.rl_preview_bottom);
        ll_cb = (LinearLayout)findViewById(R.id.ll_cb);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        ll_ok = (LinearLayout)findViewById(R.id.ll_ok);
        cb_state = (CheckBox)findViewById(R.id.cb_state);
        ll_ok.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        ll_cb.setOnClickListener(this);
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mPictures);
        mPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(position);
        mPager.setOffscreenPageLimit(5);
        if (isSelected(mPictures.get(position).getImage_path())) {
            cb_state.setChecked(true);
        } else {
            cb_state.setChecked(false);
        }
        tv_count_all.setText(getString(R.string.viewpager_indicator, position+1, mPager
                .getAdapter().getCount()));
        tv_select_index.setText(getString(R.string.viewpager_indicator, selectedPictures.size(), selectMaxNum));
        if(mPictures==null || mPictures.size()==0){
            return;
        }
        // 更新下标
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                if (isSelected(mPictures.get(arg0).getImage_path())) {
                    cb_state.setChecked(true);
                } else {
                    cb_state.setChecked(false);
                }
                position = arg0;
                tv_count_all.setText(getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount()));
            }

        });
    }
    @Override
    public void onBackPressed() {
        goFinish(false);
    }
    private void goFinish(boolean okOrCancel) {
        if(okOrCancel){
            setResult(RESULT_OK);
        }else{
            setResult(RESULT_CANCELED);
        }
        AppManager.getAppManager().finishActivity(this);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
        outState.putInt(STATE_MAX, selectMaxNum);
        outState.putStringArrayList(STATE_PIC_SELECTED, (ArrayList<String>) selectedPictures);
        outState.putParcelableArrayList(STATE_PIC_ALL,(ArrayList<? extends Parcelable>) allPictures);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPager != null) {
            mPager.destroyDrawingCache();
        }
    }
    private boolean isSelected(String pic){
        if(selectedPictures==null){
            return false;
        }
        if(selectedPictures.contains(pic)){
            return true;
        }
        return false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                goFinish(false);
                break;
            case R.id.ll_ok:
               //进入下一页
                goFinish(true);
                break;
            case R.id.ll_cb:
                if(!cb_state.isChecked()){
                    if(mPictures==null || mPictures.size()==0){
                        PrintUtils.showToast("图片加载失败");
                        cb_state.setChecked(false);
                        return;
                    }
                    if(selectedPictures.size()>=selectMaxNum){
                        PrintUtils.showToast("不能再多了！");
                        cb_state.setChecked(false);
                        return;
                    }
                    selectedPictures.add(mPictures.get(position).getImage_path());
                    tv_select_index.setText(getString(R.string.viewpager_indicator, selectedPictures.size(), selectMaxNum));
                    cb_state.setChecked(true);
                }else{
                    if(selectedPictures==null){
                        cb_state.setChecked(false);
                        return;
                    }
                    if(selectedPictures.contains(mPictures.get(position).getImage_path())){
                        selectedPictures.remove(mPictures.get(position).getImage_path());
                        tv_select_index.setText(getString(R.string.viewpager_indicator, selectedPictures.size(), selectMaxNum));
                        cb_state.setChecked(false);
                    }
                }
                break;
        }
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        private List<ImageViewHolder> mPictures;

        public ImagePagerAdapter(FragmentManager fm, List<ImageViewHolder> pictures) {
            super(fm);
            mPictures = pictures;
        }

        @Override
        public int getCount() {
            return mPictures == null ? 0 : mPictures.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance(
                mPictures.get(position).getImage_path(), ImageDetailFragment.LOCALPICTURE, new ImageDetailFragment.FinishFragmentListener() {
                    @Override
                    public void onFragmentFinished() {
                        if (rl_title.getVisibility() == View.GONE) {
                            rl_title.setVisibility(View.VISIBLE);
                            rl_bottom.setVisibility(View.VISIBLE);
                        } else {
                            rl_title.setVisibility(View.GONE);
                            rl_bottom.setVisibility(View.GONE);
                        }
                    }
                });
        }
    }
}
