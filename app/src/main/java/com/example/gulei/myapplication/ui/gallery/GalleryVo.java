package com.example.gulei.myapplication.ui.gallery;import java.io.Serializable;import java.util.ArrayList;import java.util.List;public class GalleryVo implements Serializable {	private static final long serialVersionUID = 1L;	private List<ImageViewHolder> mImgs = new ArrayList<ImageViewHolder>();	private List<GalleryImageFloder> mImageFloders = new ArrayList<GalleryImageFloder>();		public GalleryVo(List<ImageViewHolder> imgs, List<GalleryImageFloder> imageFloders)	{		mImgs = imgs;		mImageFloders = imageFloders;	}		public List<ImageViewHolder> getImgs() {		return mImgs;	}	public void setImgs(List<ImageViewHolder> mImgs) {		this.mImgs = mImgs;	}	public List<GalleryImageFloder> getImageFloders() {		return mImageFloders;	}	public void setImageFloders(List<GalleryImageFloder> mImageFloders) {		this.mImageFloders = mImageFloders;	}}