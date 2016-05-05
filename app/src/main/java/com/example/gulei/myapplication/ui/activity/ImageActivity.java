package com.example.gulei.myapplication.ui.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.base.BaseActivity;
import com.example.gulei.myapplication.ui.view.fresco.photoview.HackyViewPager;
import com.example.gulei.myapplication.ui.view.fresco.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends BaseActivity {
    private HackyViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        initDefaultHeader("图片");
        initView();
    }
    private void initView(){
        mViewPager = (HackyViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
    }
    private class SamplePagerAdapter extends PagerAdapter {

        List<String> datas = new ArrayList<String>();
        public SamplePagerAdapter(){
            datas.add("res:///" + R.mipmap.image_1);
            datas.add("res:///" + R.mipmap.image_2);
            datas.add("res:///" + R.mipmap.image_3);
            datas.add("res:///" + R.mipmap.image_4);
            datas.add("res:///" + R.mipmap.image_5);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setImageUri(datas.get(position));

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
