package com.example.gulei.myapplication.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gulei.myapplication.Config;
import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.base.BaseActivity;
import com.example.gulei.myapplication.mvp.presenter.TempPresenter;
import com.example.gulei.myapplication.ui.activity.image.ImageActivity;
import com.example.gulei.myapplication.ui.view.fresco.FImageView;
import com.example.gulei.myapplication.ui.view.fresco.FrescoImageLoader;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    List<String> list = new ArrayList<String>();
    private XRecyclerView mRecyclerView;
    private ImageView emptyView;
    private MyAdapter mAdapter;
    private TempPresenter tempPresenter = new TempPresenter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDefaultHeader("主页面");
        initData();
        initView();
        tempPresenter.method("sds");

    }
    private void initView(){
        mRecyclerView = (XRecyclerView)findViewById(R.id.recyclerview);
        emptyView = (ImageView)findViewById(R.id.iv_empty);
        mAdapter = new MyAdapter(this,list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setEmptyView(emptyView);
        mRecyclerView.setAdapter(mAdapter);
    }
    private void initData(){
        list.add("图片处理");
//        list.add("");
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private  List<String> datas = null;
        private Context mContext;
        public MyAdapter(Context context, List<String> datas) {
            mContext = context;
            this.datas = datas;
        }
        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
            return new ViewHolder(view);
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.mTextView.setText(datas.get(position));
            FrescoImageLoader.getInstance().displayImage("res:///"+R.mipmap.image_5,viewHolder.mImageView,
                    getResources().getDrawable(R.mipmap.ic_launcher), Config.IMAGE_SMALL,Config.IMAGE_SMALL);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(position);
                }
            });
        }
        private void onItemClick(int position){
            switch (position){
                case 0:{
                    Intent intent = new Intent(mContext, ImageActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }
        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public FImageView mImageView;
            public ViewHolder(View view){
                super(view);
                mTextView = (TextView) view.findViewById(R.id.text);
                mImageView = (FImageView)view.findViewById(R.id.fiv);
            }

        }
    }
}
