package com.example.gulei.myapplication.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gulei.myapplication.Config;
import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.utils.DialogUtils;
import com.example.gulei.myapplication.ui.base.BaseActivity;
import com.example.gulei.myapplication.mvp.presenter.TempPresenter;
import com.example.gulei.myapplication.ui.base.BaseQuickAdapter;
import com.example.gulei.myapplication.ui.base.BaseViewHolder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    List<String> list = new ArrayList<String>();
    private XRecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private TempPresenter tempPresenter = new TempPresenter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDefaultHeader("主页面");
//        initData();
        initView();
        tempPresenter.method("sds");
        DialogUtils.showDialog(this, "标题", "显示内容", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"点击了确定",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void initView(){
        mRecyclerView = (XRecyclerView)findViewById(R.id.recyclerview);
        mAdapter = new MyAdapter(this,list);
        mAdapter.openLoadAnimation();//加载动画
        mAdapter.setEmptyView(getCustomView(R.layout.layout_empty,null));//数据为空时候布局
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });
        mAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String content = null;
                String status = (String) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.text:
                        content = "name:" + status;
                        break;

                }
                Toast.makeText(MainActivity.this, content, Toast.LENGTH_LONG).show();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnabled(false);
//        mRecyclerView.setEmptyView(emptyView);
        mRecyclerView.setAdapter(mAdapter);
    }
    private View getCustomView(int layoutResId,ViewGroup parent){
        View view = getLayoutInflater().inflate(layoutResId, parent);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                mAdapter.notifyDataChanged(list);
            }
        });
        return view;
    }
    private void initData(){
        list.add("图片处理");
//        list.add("");
    }
    public class MyAdapter extends BaseQuickAdapter<String> {
        public MyAdapter(Context context, List<String> datas) {
            super(context, R.layout.item, datas);
        }
        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.text,item)
                    .setImageUri(R.id.fiv,"res:///"+R.mipmap.image_5,R.mipmap.ic_launcher,Config.IMAGE_SMALL)
                    .setOnClickListener(R.id.text, new OnItemChildClickListener())
                    .setOnClickListener(R.id.fiv, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext,"点击了图片",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
