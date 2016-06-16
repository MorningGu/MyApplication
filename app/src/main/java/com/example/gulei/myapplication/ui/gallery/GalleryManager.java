package com.example.gulei.myapplication.ui.gallery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gulei on 2016/6/8 0008.
 * 这是图库里图片管理的类，用来管理选中图片
 */
public class GalleryManager {
    private static GalleryManager manager = null;
    private List<ImageViewHolder> allPictures = new ArrayList<>();
    private List<String> selectedPictures = new ArrayList<>();

    public static GalleryManager getInstance(){
        if(manager==null){
            manager = new GalleryManager();
            return manager;
        }
        return manager;
    }

    /**
     * 初始化数据
     */
    public static GalleryManager initGalleryManager(){
        getInstance().clearData();
        return manager;
    }

    /**
     * 清除数据
     */
    public void clearData(){
        allPictures.clear();
        selectedPictures.clear();
    }
    /**
     * 根据index删除一条数据
     * @param index
     */
    public void removeSelectedPicture(int index){
        selectedPictures.remove(index);
    }

    /**
     * 根据对象删除一条数据
     * @param picture
     */
    public void removeSelectedPicture(String picture){
        selectedPictures.remove(picture);
    }

    /**
     * 添加一个选中
     * @param picture
     */
    public void addSelectedPictures(String picture){
        if(picture==null){
            return;
        }
        selectedPictures.add(picture);
    }

    /**
     * 设置全部图片
     * @param pictures
     */
    public void setAllPictures(List<ImageViewHolder> pictures){
        if(pictures==null || pictures.size()==0){
            return;
        }
        allPictures.addAll(pictures);
    }

    /**
     * 获取全部图片
     * @return
     */
    public List<ImageViewHolder> getAllPictures(){
        return allPictures;
    }

    /**
     * 获取选中图片
     * @return
     */
    public List<String> getSelectedPictures(){
        return selectedPictures;
    }
}
