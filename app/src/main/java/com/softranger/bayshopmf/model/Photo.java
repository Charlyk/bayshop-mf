package com.softranger.bayshopmf.model;

import android.graphics.Bitmap;
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
    private Bitmap mSmallBitmap;
    private Bitmap mBigBitmap;

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
        mSmallImage = "http://md.bay-dev.tk" + smallImage;
    }

    public String getBigImage() {
        return mBigImage;
    }

    public void setBigImage(String bigImage) {
        mBigImage = "http://md.bay-dev.tk" + bigImage;
    }

    public Bitmap getSmallBitmap() {
        return mSmallBitmap;
    }

    public void setSmallBitmap(Bitmap smallBitmap) {
        mSmallBitmap = smallBitmap;
    }

    public Bitmap getBigBitmap() {
        return mBigBitmap;
    }

    public void setBigBitmap(Bitmap bigBitmap) {
        mBigBitmap = bigBitmap;
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
        private Bitmap mSmallBitmap;
        private Bitmap mBigBitmap;

        public Builder smallImage(String smallImage) {
            mSmallImage = smallImage;
            return this;
        }

        public Builder bigImage(String bigImage) {
            mBigImage = bigImage;
            return this;
        }

        public Builder bigBitmap(Bitmap bigBitmap) {
            mBigBitmap = bigBitmap;
            return this;
        }

        public Builder smallBitmap(Bitmap smallBitmap) {
            mSmallBitmap = smallBitmap;
            return this;
        }

        public Photo build() {
            Photo photo = new Photo();
            photo.mSmallImage = "http://md.bay-dev.tk" + this.mSmallImage;
            photo.mBigImage = "http://md.bay-dev.tk" + this.mBigImage;
            photo.mBigBitmap = this.mBigBitmap;
            photo.mSmallBitmap = this.mSmallBitmap;
            return photo;
        }
    }
}
