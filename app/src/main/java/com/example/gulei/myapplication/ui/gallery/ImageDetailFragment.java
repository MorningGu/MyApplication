package com.example.gulei.myapplication.ui.gallery;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.ui.view.fresco.photoview.PhotoView;
import com.example.gulei.myapplication.ui.view.fresco.photoview.PhotoViewAttacher;


public class ImageDetailFragment extends Fragment {
	public static final int LOCALPICTURE = 1,WEBPICTURE = 0;
	private String mImageUrl;
	private PhotoView photoView;
	private int type; // 1是本地图片，0是网络图片
	private FinishFragmentListener finishListener;
	private double density = 1;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();
		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	public static ImageDetailFragment newInstance(String imageUrl, int imgType) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		args.putInt("type", imgType);
		f.setArguments(args);
		return f;
	}

	public static ImageDetailFragment newInstance(String imageUrl, int imgType,
												  FinishFragmentListener fListener) {
		final ImageDetailFragment f = new ImageDetailFragment();
		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		args.putInt("type", imgType);
		f.setArguments(args);
		f.setFinishListener(fListener);
		return f;
	}
	

	private void setFinishListener(FinishFragmentListener fListener) {
		this.finishListener = fListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url")
				: null;
		type = getArguments() != null ? getArguments().getInt("type") : null;

		DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_image_detail, container, false);
		photoView = (PhotoView) v.findViewById(R.id.iv_photo);
		photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				if(finishListener!=null){
					finishListener.onFragmentFinished();
				}
			}
		});
		return v;
	}

	
	public interface FinishFragmentListener {
		void onFragmentFinished();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadPicByImageloader();
	}

	private void loadPicByImageloader()
	{
		if(type == LOCALPICTURE) {
			photoView.setImageURI(Uri.parse("file://" + mImageUrl));
		} else {
			photoView.setImageURI(Uri.parse(mImageUrl));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
		//这里没有操作，所以才要注销super，原因是有一个系统bug，只有当要存数据的时候再启用super
	}
}
