package com.example.gulei.myapplication.ui.view.cropper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.gulei.myapplication.R;
import com.example.gulei.myapplication.common.utils.AppManager;
import com.example.gulei.myapplication.common.utils.PrintUtils;
import com.example.gulei.myapplication.ui.base.BaseActivity;
import com.example.gulei.myapplication.ui.view.HeadLayout;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CropImageActivity extends BaseActivity implements View.OnClickListener{

   private Bitmap mBitmap;
   private Uri mInputPath = null;
   private Uri mOutputPath = null;
   private CropImageView mCropImageView;
   private TextView tv_reset;
   private ImageView iv_rotate;
   private ImageView iv_crop;

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.tv_reset:{
            mCropImageView.reset();
            break;
         }
         case R.id.iv_rotate:{
            mCropImageView.rotate();
            mCropImageView.invalidate();
            break;
         }
         case R.id.iv_crop:{
            mCropImageView.crop();
            break;
         }
      }
   }

   public static class CropParam {
      public int mAspectX = 0;
      public int mAspectY = 0;
      public int mOutputX = 0;
      public int mOutputY = 0;
      public int mMaxOutputX = 0;
      public int mMaxOutputY = 0;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_cropimage);
      initView();
      Intent intent = getIntent();
      Bundle extras = intent.getExtras();
      if (extras == null) {
         setResult(RESULT_CANCELED);
         return;
      }
      mInputPath = intent.getData();
      mOutputPath = extras.getParcelable(MediaStore.EXTRA_OUTPUT);
      if (mInputPath == null || mOutputPath == null) {
         setResult(RESULT_CANCELED);
         finish();
         return;
      }
//      mBitmap = loadBitmap(mInputPath);
      mBitmap = loadBitmapWithInSample(mInputPath);
      if (mBitmap == null) {
         setResult(RESULT_CANCELED);
         finish();
         return;
      }
      mCropImageView.initialize(mBitmap, getCropParam(intent));
   }
   private void initView(){
      mCropImageView = (CropImageView) findViewById(R.id.CropWindow);
      tv_reset = (TextView)findViewById(R.id.tv_reset);
      iv_rotate = (ImageView)findViewById(R.id.iv_rotate);
      iv_crop = (ImageView)findViewById(R.id.iv_crop);
      tv_reset.setOnClickListener(this);
      iv_rotate.setOnClickListener(this);
      iv_crop.setOnClickListener(this);
      initTitleAndRightText("选择图片", "确定", new HeadLayout.OnLeftClickListener() {
         @Override
         public void onClick() {
            setResult(RESULT_CANCELED);
            AppManager.getAppManager().finishActivity();
         }
      }, new HeadLayout.OnRightClickListener() {
         @Override
         public void onClick() {
            new SaveImageTask().execute(mCropImageView.getCropBitmap());
         }
      });
   }
   @Override
   protected void onDestroy() {
      if (mBitmap != null) {
         mBitmap.recycle();
      }
      mCropImageView.destroy();
      super.onDestroy();
   }

   private class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {

      private ProgressDialog mProgressDailog;

      private SaveImageTask() {
         mProgressDailog = new ProgressDialog(CropImageActivity.this);
         mProgressDailog.setCanceledOnTouchOutside(false);
         mProgressDailog.setCancelable(false);
         mProgressDailog.setTitle("保存");
         mProgressDailog.setMessage("正在保存...");
      }

      @Override
      protected void onPreExecute() {
         mProgressDailog.show();
      }

      @Override
      protected void onPostExecute(Boolean result) {
         if (mProgressDailog.isShowing()) {
            mProgressDailog.dismiss();
         }
         setResult(RESULT_OK, new Intent().putExtra(MediaStore.EXTRA_OUTPUT, mOutputPath));
         AppManager.getAppManager().finishActivity();
      }

      @Override
      protected Boolean doInBackground(Bitmap... params) {
         if(params==null && params[0]==null){
            return Boolean.TRUE;
         }
         OutputStream outputStream = null;
         try {
            outputStream = getContentResolver().openOutputStream(mOutputPath);
            if (outputStream != null) {
               params[0].compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
         } catch (IOException e) {
            
         } finally {
            if(params[0]!=null&&!params[0].isRecycled()){
               params[0].recycle();
            }
            closeSilently(outputStream);
         }
         return Boolean.TRUE;
      }
   }

   protected Bitmap loadBitmap(Uri uri) {
      Bitmap bitmap = null;
      InputStream in = null;
      try {
         in = getContentResolver().openInputStream(uri);
         bitmap = BitmapFactory.decodeStream(in);
         in.close();
      } catch (FileNotFoundException e) {
      } catch (IOException e) {
      }catch (OutOfMemoryError e){
         PrintUtils.showToast("内存不足");
      }
      return bitmap;
   }

   protected Bitmap loadBitmapWithInSample(Uri uri) {
      final int MAX_VIEW_SIZE = 1280;
      InputStream in = null;
      try {
//         FileInputStream in = new FileInputStream(uri.getPath());
         in = getContentResolver().openInputStream(uri);
         BitmapFactory.Options o = new BitmapFactory.Options();
         o.inJustDecodeBounds = true;
         BitmapFactory.decodeStream(in, null, o);
         in.close();
         int scale = 1;
         if (o.outWidth > o.outHeight && o.outWidth > MAX_VIEW_SIZE) {// 如果宽度大的话根据宽度固定大小缩放
            scale = (int) (o.outWidth / MAX_VIEW_SIZE);
         } else if (o.outWidth < o.outHeight && o.outHeight > MAX_VIEW_SIZE) {// 如果高度高的话根据宽度固定大小缩放
            scale = (int) (o.outHeight / MAX_VIEW_SIZE);
         }
//         if (o.outHeight > MAX_VIEW_SIZE || o.outWidth > MAX_VIEW_SIZE) {
//            scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_VIEW_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
//         }
         if (scale < 1)
            scale = 1;
         BitmapFactory.Options o2 = new BitmapFactory.Options();
         o2.inPreferredConfig = Bitmap.Config.RGB_565;
         o2.inSampleSize = scale;
         in = getContentResolver().openInputStream(uri);
         Bitmap b = BitmapFactory.decodeStream(in, null, o2);
         in.close();
         return b;
      } catch (FileNotFoundException e) {
         PrintUtils.showToast("图片未找到");
      } catch (IOException e) {
         PrintUtils.showToast("图片载入出错");
      }catch (OutOfMemoryError e){
         PrintUtils.showToast("内存不足");
      }
      return null;
   }

   protected static void closeSilently(Closeable c) {
      if (c == null)
         return;
      try {
         c.close();
      } catch (Throwable t) {
      }
   }

   public static CropParam getCropParam(Intent intent) {
      CropParam params = new CropParam();
      Bundle extras = intent.getExtras();
      if (extras != null) {
         if (extras.containsKey(CropIntent.ASPECT_X) && extras.containsKey(CropIntent.ASPECT_Y)) {
            params.mAspectX = extras.getInt(CropIntent.ASPECT_X);
            params.mAspectY = extras.getInt(CropIntent.ASPECT_Y);
         }
         if (extras.containsKey(CropIntent.OUTPUT_X) && extras.containsKey(CropIntent.OUTPUT_Y)) {
            params.mOutputX = extras.getInt(CropIntent.OUTPUT_X);
            params.mOutputY = extras.getInt(CropIntent.OUTPUT_Y);
         }
         if (extras.containsKey(CropIntent.MAX_OUTPUT_X) && extras.containsKey(CropIntent.MAX_OUTPUT_Y)) {
            params.mMaxOutputX = extras.getInt(CropIntent.MAX_OUTPUT_X);
            params.mMaxOutputY = extras.getInt(CropIntent.MAX_OUTPUT_Y);
         }
      }
      return params;
   }
}
