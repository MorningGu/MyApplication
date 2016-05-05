/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.gulei.myapplication.ui.view.fresco;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import com.example.gulei.myapplication.Config;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;


/**
 * Desction:fresco image loader
 * Author:pengjianbo
 * Date:15/12/24 下午9:34
 */
public class FrescoImageLoader {
    private static FrescoImageLoader mInstance;
    private Context context;

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context){
        if(mInstance==null){
            mInstance = new FrescoImageLoader(context);
        }
    }

    /**
     * 获取FrescoImageLoader单例
     * @return
     */
    public static FrescoImageLoader getInstance(){
        return mInstance;
    }
    public FrescoImageLoader(Context context) {
        this(context, Bitmap.Config.RGB_565);
    }
    public FrescoImageLoader(Context context, Bitmap.Config config) {
        this.context = context;
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),"Moe Studio"))
                .setBaseDirectoryName(Config.CACHE_DIR)
                .setMaxCacheSize(Config.CACHE_SIZE_DISK)//磁盘缓存
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setBitmapsConfig(config)
                .build();
        Fresco.initialize(context,imagePipelineConfig);
    }

    /**
     * 展示图片
     * @param imageView
     * @param defaultDrawable
     * @param width
     * @param height
     */
    public void displayImage(String uri, final FImageView imageView, final Drawable defaultDrawable, int width, int height) {
        Resources resources = context.getResources();
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(resources)
                .setFadeDuration(300)
                .setPlaceholderImage(defaultDrawable)
                .setFailureImage(defaultDrawable)
//                .setProgressBarImage(new ProgressBarDrawable())
                .build();

        final DraweeHolder<GenericDraweeHierarchy> draweeHolder = DraweeHolder.create(hierarchy, context);
        imageView.setOnImageViewListener(new FImageView.OnImageViewListener() {
            @Override
            public void onDetach() {
                draweeHolder.onDetach();
            }

            @Override
            public void onAttach() {
                draweeHolder.onAttach();
            }

            @Override
            public boolean verifyDrawable(Drawable dr) {
                if (dr == draweeHolder.getHierarchy().getTopLevelDrawable()) {
                    return true;
                }
                return false;
            }

            @Override
            public void onDraw(Canvas canvas) {
                Drawable drawable = draweeHolder.getHierarchy().getTopLevelDrawable();
                if (drawable == null) {
                    imageView.setImageDrawable(defaultDrawable);
                } else {
                    imageView.setImageDrawable(drawable);
                }
            }
        });

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(uri))
                .setResizeOptions(new ResizeOptions(width, height))//图片目标大小
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(draweeHolder.getController())
                .setImageRequest(imageRequest)
                .build();
        draweeHolder.setController(controller);
    }

}
