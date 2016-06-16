package com.example.gulei.myapplication.ui.gallery;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageViewHolder implements Parcelable {

	private String image_path;//图片路径
	private String parentPathName;//
	private long image_Id;


	public ImageViewHolder(){}

	protected ImageViewHolder(Parcel in) {
		image_path = in.readString();
		parentPathName = in.readString();
		image_Id = in.readLong();
	}

	public static final Creator<ImageViewHolder> CREATOR = new Creator<ImageViewHolder>() {
		@Override
		public ImageViewHolder createFromParcel(Parcel in) {
			return new ImageViewHolder(in);
		}

		@Override
		public ImageViewHolder[] newArray(int size) {
			return new ImageViewHolder[size];
		}
	};

	public long getImage_Id() {
		return image_Id;
	}
	public void setImage_Id(long image_Id) {
		this.image_Id = image_Id;
	}
	public String getImage_path() {
		return image_path;
	}
	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
	public String getParentPathName() {
		return parentPathName;
	}
	public void setParentPathName(String parentPathName) {
		this.parentPathName = parentPathName;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(image_path);
		dest.writeString(parentPathName);
		dest.writeLong(image_Id);
	}
}
