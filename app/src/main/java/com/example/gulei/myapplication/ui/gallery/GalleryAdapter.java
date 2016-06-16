package com.example.gulei.myapplication.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.example.gulei.myapplication.Config;
import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.utils.PixelUtil;
import com.example.gulei.myapplication.common.utils.PrintUtils;
import com.example.gulei.myapplication.ui.base.BaseQuickAdapter;
import com.example.gulei.myapplication.ui.base.BaseViewHolder;
import com.example.gulei.myapplication.common.utils.ImageLoaderUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;


public class GalleryAdapter extends BaseQuickAdapter<ImageViewHolder> {

	/**
	 * 用户选择的图片
	 */
	private List<String> mSelectedImage;
	private int picSize;
	private ImageListener imageListener;
	private int mHeight ;
	public GalleryAdapter(Context context, List<ImageViewHolder> data, Integer num,List<String> selectPics, int screenWidth) {
		super(context, R.layout.item_gallery_grid,data);
		if(num != null && num>0) {
			picSize = num;	
		} else {
			picSize = 1;
		}
		mSelectedImage = selectPics;
		mHeight = ((screenWidth - PixelUtil.dp2px(44))/3);
	}

	@Override
	protected void convert(BaseViewHolder helper, final ImageViewHolder item,final int position) {
		final SimpleDraweeView sdv_photo = helper.getView(R.id.sdv_photo);
		final CheckBox mSelect = helper.getView(R.id.id_item_select);
		ViewGroup.LayoutParams lp = helper.getView(R.id.sdv_photo).getLayoutParams();
		lp.height = mHeight;
		sdv_photo.setLayoutParams(lp);
		// 设置图片
		if (item.getImage_path().equals("photo")) {
			mSelect.setVisibility(View.GONE);
			sdv_photo.setClickable(true);
			ImageLoaderUtils.getInstance().displayImage("res:///"+R.mipmap.ic_camera,sdv_photo, Config.IMAGE_SMALL,Config.IMAGE_SMALL);
			sdv_photo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSelectedImage.size() < picSize) {
						if(imageListener!=null){
							imageListener.onSelectImageFromCamera();
						}
					}else{
						PrintUtils.showToast("不能再多了！");
					}
				}
			});
		} else {
			ImageLoaderUtils.getInstance().displayImage("file:///"+item.getImage_path(),sdv_photo, Config.IMAGE_SMALL,Config.IMAGE_SMALL);
			mSelect.setVisibility(View.VISIBLE);
			/**
			 * 已经选择过的图片，显示出选择过的效果
			 */
			if (mSelectedImage.contains(item.getImage_path())) {
				mSelect.setChecked(true);
				sdv_photo.setColorFilter(Color
						.parseColor("#77000000"));
			} else {
				mSelect.setChecked(false);
				sdv_photo.setColorFilter(null);
			}
			mSelect.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSelect.isChecked()) {
						if (!isFileExits(item.getImage_path())) {
							PrintUtils.showToast("该图片已删除");
							mSelect.setChecked(false);
							return;
						}
						if (mSelectedImage.size() < picSize) {
							mSelectedImage.add(item.getImage_path());
							sdv_photo.setColorFilter(Color.parseColor("#77000000"));
							mSelect.setChecked(true);
						} else {
							mSelectedImage.remove(item.getImage_path());
							mSelect.setChecked(false);
							PrintUtils.showToast("不能再多了！");
						}
					} else {
						// 已经选择过该图片
						if (mSelectedImage.contains(item.getImage_path())) {
							sdv_photo.setColorFilter(null);
							mSelect.setChecked(false);
							mSelectedImage.remove(item.getImage_path());
						}
					}
					if(imageListener!=null){
						imageListener.onSelectImage(mSelectedImage.size());
					}
				}
			});
			// 设置ImageView的点击事件
			sdv_photo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,GalleryPreviewActivity.class);
					intent.putExtra(GalleryPreviewActivity.EXTRA_IMAGE_INDEX, position);
					intent.putExtra(GalleryPreviewActivity.EXTRA_IMAGE_SELECT_MAX, picSize);
					((GalleryActivity)mContext).startActivityForResult(intent,GalleryActivity.PHOTO_PREVIEW);
				}
			});
		}
	}

	private Boolean isFileExits(String path) {
		if(!TextUtils.isEmpty(path)){
			File file = new File(path);
			if (file.exists()) {
				return true;
			}
		}
		return false;
	}

	public void setImageListener(ImageListener imageListener) {
		this.imageListener = imageListener;
	}

	public List<String> getSelectedImage() {
		return mSelectedImage;
	}

	public interface ImageListener {

		void onSelectImageFromCamera();

		void onSelectImage(int selectNum);
	}
}
