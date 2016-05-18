package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 5/18/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class Photo implements Parcelable {
    private String mSmallImage;
    private String mBigImage;

    private Photo() {

    }

    protected Photo(Parcel in) {
        mSmallImage = in.readString();
        mBigImage = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getSmallImage() {
        return mSmallImage;
    }

    public void setSmallImage(String smallImage) {
        mSmallImage = smallImage;
    }

    public String getBigImage() {
        return mBigImage;
    }

    public void setBigImage(String bigImage) {
        mBigImage = bigImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSmallImage);
        dest.writeString(mBigImage);
    }

    public static class Builder {
        private String mSmallImage;
        private String mBigImage;

        public Builder smallImage(String smallImage) {
            mSmallImage = smallImage;
            return this;
        }

        public Builder bigImage(String bigImage) {
            mBigImage = bigImage;
            return this;
        }

        public Photo build() {
            Photo photo = new Photo();
            photo.mSmallImage = this.mSmallImage;
            photo.mBigImage = this.mBigImage;
            return photo;
        }
    }
}
