package com.example.gulei.myapplication.ui.gallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.example.gulei.myapplication.Config;
import com.example.gulei.myapplication.GApplication;
import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.utils.AppManager;
import com.example.gulei.myapplication.common.utils.PrintUtils;
import com.example.gulei.myapplication.common.utils.ThreadPoolUtil;
import com.example.gulei.myapplication.ui.base.BaseActivity;
import com.example.gulei.myapplication.ui.view.HeadLayout;
import com.example.gulei.myapplication.ui.view.cropper.CropImageActivity;
import com.jcodecraeer.xrecyclerview.DividerGridItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GalleryActivity extends BaseActivity implements
		GalleryPopupWindow.OnImageDirSelected, GalleryAdapter.ImageListener {

	private GalleryManager galleryManager = GalleryManager.initGalleryManager();
	private List<String> selectedPictures = galleryManager.getSelectedPictures();
	private List<ImageViewHolder> allPictures = galleryManager.getAllPictures();

	private ProgressDialog mProgressDialog;
	public final static String EXTRA_IMAGE_URLS = "image_urls";
	public final static String EXTRA_IMAGE_TYPE = "image_type";
	public final static String EXTRA_IMAGE_NUM = "image_num";
	public final static String EXTRA_CAMERA = "image_camera";
	public static final int PHOTO_PREVIEW = 1001;
	public static final int SINGLE_TO_CLIP = 0x1001;
	private RecyclerView mGirdView;
	private GalleryAdapter mAdapter;
	private LinearLayout mBottomLy;
	private LinearLayout mPreviewLy;
	private TextView mChooseDir;
	private TextView mTextCount;
	private int type; //这里是一些可能出现的情况的类型，暂时没用到
	private GalleryPopupWindow mListImageDirPopupWindow;
	private Integer selectNum;
	private GalleryHandler mGalleryHandler;
	private String localCameraPath = "";
	private List<GalleryImageFloder> mGalleryImageFloders = new ArrayList<GalleryImageFloder>();
	private final int TO_CAMERA_CAPTURE = 0x000001;
	private final int TO_CLIP_IMAGE = 0x000002;
	private boolean mHasCamera = GApplication.getInstance().hasCamera();
	public void handleMessage(List<ImageViewHolder> imgs, List<GalleryImageFloder> imageFloders) {
		if(mProgressDialog!=null && mProgressDialog.isShowing() && !isFinishing())
			mProgressDialog.dismiss();
		if(imgs!=null){
			allPictures.addAll(imgs);
		}
		updateImageViewHolders(allPictures, true);
		updateGalleryImageFloders(imageFloders);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		if(mProgressDialog!=null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		galleryManager.clearData();
		mGalleryHandler = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		mGalleryHandler = new GalleryHandler(this);
		initData(savedInstanceState);
		initView();
		initGalleryView();
		initListDirPopupWindw();
		initEvent();
		getImages();
		updateSelectedNum(selectedPictures.size());
	}
	private void initData(Bundle savedInstanceState){
		selectNum = getIntent().getIntExtra(EXTRA_IMAGE_NUM, 1);
		type = getIntent().getIntExtra(EXTRA_IMAGE_TYPE, SINGLE_TO_CLIP);
		if(savedInstanceState==null){
			initGalleryManager(getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS));
		}else{
			localCameraPath = savedInstanceState.getString("localCameraPath", "");
			initGalleryManager(savedInstanceState.getStringArrayList("selectedPictures"));
		}
	}
	private void initGalleryManager(List<String> selectedPictures){
		if(selectedPictures!=null && selectedPictures.size()>0){
			this.selectedPictures.addAll(selectedPictures);
		}
	}
	private void submit() {
		if(mAdapter == null ||mAdapter.getSelectedImage() == null || mAdapter.getSelectedImage().size() == 0){
			PrintUtils.showToast("请先选择图片");
			return;
		}
//		if (type == HiConfig.MULTIPLE_TO_GALLERY) {
//			if(mAdapter.getmSelectedImage().size() == 1 && PhotoUtil.isNormaiImage(mAdapter.getmSelectedImage().get(0), 0.25))
//			{
//				processPhoto(mAdapter.getmSelectedImage().get(0));
//			}
//			else
//			{
//				submitMulPics();
//			}
//		}
//		if (type == HiConfig.SIGNILE_TO_GALLERY) {
//
//			if (mAdapter.getmSelectedImage() != null && mAdapter.getmSelectedImage().size() == 1) {
//				Intent intent = new Intent();
//				intent.putExtra("selectPic", mAdapter.getmSelectedImage().get(0));
//				setResult(RESULT_OK, intent);
//				AppManager.getAppManager().finishActivity(this);
//			}
//		}
		if (type == SINGLE_TO_CLIP) {
			if (mAdapter.getSelectedImage().size() == 1) {
				startImageAction(mAdapter.getSelectedImage().get(0));
			}else{
				PrintUtils.showToast("只能选择一张图片哟");
			}
		}
	}

//	private void processPhoto(String imagePath)
//	{
//		Intent intent = new Intent(GalleryActivity.this, PhotoProcessActivity.class);
//		intent.putExtra("path", imagePath);
//		intent.putExtra("type", PhotoProcessActivity.PHOTO_PROCESS_PUBLISH_SINGLE_TYPE);
//		GalleryActivity.this.startActivityForResult(intent, HiConfig.PHOTO_SINGLE_PROCESS);
//	}

//	private void submitSinglePic(String str)
//	{
//		Intent intent = new Intent();
//		if(StringUtil.isNullOrEmpty(str))
//		{
//			str = mAdapter.getmSelectedImage().get(0);
//		}
//		intent.putExtra("selectPic", new String[]{str});
//		setResult(RESULT_OK, intent);
//		AppManager.getAppManager().finishActivity(this);
//	}

//	private void submitMulPics()
//	{
//		Intent intent = new Intent();
//		if (mAdapter.getmSelectedImage() != null)
//		{
//			intent.putExtra("selectPic", mAdapter.getmSelectedImage().toArray(new String[mAdapter.getmSelectedImage().size()]));
//		}
//		setResult(RESULT_OK, intent);
//		AppManager.getAppManager().finishActivity(this);
//	}

//	private void initSelectedPicture(Bundle savedInstanceState) {
//		if(savedInstanceState == null) {
//			ArrayList<ImageViewHolder> pictures = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_URLS);
//			if(select_pics!=pictures){
//				if (pictures != null) {
//					select_pics = pictures;
//				}
//			}
//			if(select_pics==null){
//				select_pics = new ArrayList<>();
//			}
//		} else {
//			select_pics = savedInstanceState.getParcelable("select_pics");
//			if(select_pics == null) {
//				select_pics = new ArrayList<>();
//			}
//		}
//	}

	private void initGalleryView() {
		mAdapter = new GalleryAdapter(this, null, selectNum,selectedPictures,mScreenWidth);
		mAdapter.openLoadAnimation();//加载动画
		GridLayoutManager layoutManager = new GridLayoutManager(this,3);
		layoutManager.setOrientation(GridLayoutManager.VERTICAL);
		mGirdView.setLayoutManager(layoutManager);
		mGirdView.addItemDecoration(new DividerGridItemDecoration(this));
		mGirdView.setAdapter(mAdapter);
		mAdapter.setImageListener(this);
	}
	
	
	/**
	 * 为View绑定数据
	 */
	private void updateImageViewHolders(List<ImageViewHolder> imgs, boolean isAllPics) {
		if(imgs == null) {
			return;
		}
		if (imgs.size() == 0) {
			PrintUtils.showToast("未发现图片资源");
			return;
		}
		mAdapter.notifyDataReset(imgs);
	};

	private void updateGalleryImageFloders(List<GalleryImageFloder> galleryImageFloders) {
		if(galleryImageFloders != null && galleryImageFloders.size() > 0) {
			mGalleryImageFloders.clear();
			mGalleryImageFloders.addAll(galleryImageFloders);
			mListImageDirPopupWindow.updatePopWindow();
		}
	}
	
	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new GalleryPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mGalleryImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.gallery_list_dir, null),mHasCamera);

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			PrintUtils.showToast("未发现图片资源");
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在扫描...");
		GalleryRunnable galleryRunnable = new GalleryRunnable(mGalleryHandler, mHasCamera);
		ThreadPoolUtil.execute(galleryRunnable);
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (RecyclerView) findViewById(R.id.rv_photos);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mBottomLy = (LinearLayout) findViewById(R.id.id_bottom_ly);
		mTextCount = (TextView)findViewById(R.id.tv_count);
		mPreviewLy = (LinearLayout)findViewById(R.id.ll_show_big);
		initTitleAndRightText("选择图片", "确定", new HeadLayout.OnRightClickListener() {
			@Override
			public void onClick() {
				submit();
			}
		});
	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mChooseDir.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mListImageDirPopupWindow
//						.setAnimationStyle(R.style.gallery_anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
		mPreviewLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(allPictures==null || allPictures.size()==0 ||
						(allPictures.size()==1 && "photo".equals(allPictures.get(0).getImage_path()))){
					PrintUtils.showToast("未发现图片");
					return;
				}
				Intent intent = new Intent(GalleryActivity.this, GalleryPreviewActivity.class);
				intent.putExtra(GalleryPreviewActivity.EXTRA_IMAGE_INDEX, 0);
				intent.putExtra(GalleryPreviewActivity.EXTRA_IMAGE_SELECT_MAX, selectNum);
				startActivityForResult(intent, PHOTO_PREVIEW);
			}
		});
	}

	@Override
	public void selected(GalleryImageFloder floder, int pos) {
		List<ImageViewHolder> imageViewHolders = floder.getFileList();
		if(imageViewHolders != null && imageViewHolders.size() > 0) {
			if(pos == 0) {
				updateImageViewHolders(imageViewHolders, true);
			} else {
				updateImageViewHolders(imageViewHolders, false);
			}
			mChooseDir.setText(floder.getName());
		}
		mListImageDirPopupWindow.dismiss();
	}

	@Override
	public void onSelectImageFromCamera() {
		try {
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File dir = new File(Config.PHOTO_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
			localCameraPath = file.getPath();
			Uri imageUri = Uri.fromFile(file);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(openCameraIntent, TO_CAMERA_CAPTURE);
		}catch(Exception e){
			PrintUtils.showToast("图片加载失败");
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case TO_CAMERA_CAPTURE:// 当取到值的时候才上传path路径下的图片到服务器
					if (TextUtils.isEmpty(localCameraPath)) {
						return;
					}
					try {
						Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
						scanIntent.setData(Uri.fromFile(new File(localCameraPath)));
						sendBroadcast(scanIntent);
					} catch (Exception e) {

					}
					mAdapter.getSelectedImage().add(localCameraPath);
					submit();
					break;
				case  TO_CLIP_IMAGE:
					setResult(RESULT_OK, data);
					AppManager.getAppManager().finishActivity(this);
					break;
//				case HiConfig.PHOTO_SINGLE_PROCESS:
//					if(data==null){
//						return;
//					}
//					String str = data.getStringExtra("path");
//					submitSinglePic(str);
//					break;
				case PHOTO_PREVIEW:
					mAdapter.notifyDataSetChanged();
					submit();
					break;
			}
		}else{
			if(requestCode == PHOTO_PREVIEW ){
				mAdapter.notifyDataSetChanged();
				updateSelectedNum(selectedPictures.size());
			}
		}
	}

	@Override
	public void onSelectImage(int count) {
		updateSelectedNum(count);
	}

	private void startImageAction(String path) {
		Intent intent = new Intent(this, CropImageActivity.class);
		intent.setData(Uri.parse("file://"+path));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://"+Config.PHOTO_DIR+File.separator+ System.currentTimeMillis()+".jpg"));
		startActivityForResult(intent, TO_CLIP_IMAGE);
	}
	private void updateSelectedNum(int count){
		mTextCount.setText(getString(R.string.viewpager_indicator, count, selectNum));
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		//这里只存了这两个，其他的是用intent那个获取的
		savedInstanceState.putString("localCameraPath", localCameraPath);
		savedInstanceState.putStringArrayList("selectedPictures",(ArrayList) selectedPictures);
		super.onSaveInstanceState(savedInstanceState);
	}
}
