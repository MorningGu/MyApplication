package com.example.gulei.myapplication.ui.gallery;

import java.util.ArrayList;
import java.util.List;

public class GalleryImageFloder {

	/**
	 * 第一张图片的路径
	 */
	private String firstImagePath;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 文件夹List
	 */
	private List<ImageViewHolder> fileList;

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}
	public int getCount() {
		return getFileList().size();
	}

	public List<ImageViewHolder> getFileList() {
		if(fileList == null){
			fileList = new ArrayList<ImageViewHolder>();
		}
		return fileList;
	}

	public void setFileList(List<ImageViewHolder> fileList) {
		this.fileList = fileList;
	}

}
