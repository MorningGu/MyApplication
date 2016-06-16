package com.example.gulei.myapplication.ui.gallery;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.gulei.myapplication.Config;
import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.utils.ImageLoaderUtils;
import com.example.gulei.myapplication.ui.base.BaseQuickAdapter;
import com.example.gulei.myapplication.ui.base.BaseViewHolder;
import com.example.gulei.myapplication.ui.view.fresco.FImageView;
import com.jcodecraeer.xrecyclerview.DividerItemDecoration;

import java.util.List;

public class GalleryPopupWindow extends BasePopupWindow<GalleryImageFloder> {
	private RecyclerView mListDir;
	private boolean mHasCamera;
	private BaseQuickAdapter<GalleryImageFloder> mBaseListAdapter;
	public GalleryPopupWindow(int width, int height, List<GalleryImageFloder> datas, View convertView, boolean mHasCamera) {
		super(convertView, width, height, true, datas);
		this.mHasCamera = mHasCamera;
	}

	@Override
	public void initViews() {
		mListDir = (RecyclerView) findViewById(R.id.rv_dir);
		LinearLayoutManager layoutManager = new LinearLayoutManager(context);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mListDir.setLayoutManager(layoutManager);
		mListDir.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
		mBaseListAdapter = new BaseQuickAdapter<GalleryImageFloder>(context,R.layout.gallery_list_dir_item ,mDatas) {
			@Override
			protected void convert(BaseViewHolder helper, GalleryImageFloder item, final int position) {
				helper.setText(R.id.id_dir_item_name,item.getName())
						.setText(R.id.id_dir_item_count,
								mHasCamera && position == 0?((item.getCount()-1) + "张"):(item.getCount() + "张"));
				ImageLoaderUtils.getInstance().displayImage("file://" + item.getFirstImagePath(),(FImageView) helper.getView(R.id.id_dir_item_image),
						null, Config.IMAGE_SMALL,Config.IMAGE_SMALL);
				helper.getConvertView().setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mImageDirSelected != null) {
							mImageDirSelected.selected(mDatas.get(position), position);
						}
					}
				});

			}
		};
		mListDir.setAdapter(mBaseListAdapter);
	}
	
	public interface OnImageDirSelected {
		void selected(GalleryImageFloder floder, int pos);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents() {}

	@Override
	public void init() {}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params) {}

	public void updatePopWindow() {
		mBaseListAdapter.notifyDataReset(mDatas);
	}
}
