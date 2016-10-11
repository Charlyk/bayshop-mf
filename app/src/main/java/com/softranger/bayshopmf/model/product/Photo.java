package com.softranger.bayshopmf.model.product;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmf.util.Imageble;

/**
 * Created by Eduard Albu on 5/18/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class Photo implements Parcelable, Imageble {
    @JsonProperty("photoThumbnail") String mSmallImage;
    @JsonProperty("photo") String mBigImage;
    Bitmap mSmallBitmap;
    Bitmap mBigBitmap;

    private Photo() {
        // empty constructor for jackson and builder
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

    public String getBigImage() {
        return mBigImage;
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

    @Override
    public void setImage(Bitmap bitmap) {
        mBigBitmap = bitmap;
    }

    @Override
    public Bitmap getImage() {
        return mBigBitmap;
    }

    @Override
    public String getImageUrl() {
        return mBigImage;
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