package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 9/16/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class Box implements Parcelable {

    @JsonProperty("id") String mId;
    @JsonProperty("uid") String mUid;
    @JsonProperty("title") String mTitle;
    @JsonProperty("price") String mPrice;
    @JsonProperty("quantity") int mQuantity;
    @JsonProperty("weight") String mWeight;
    @JsonProperty("photos") ArrayList<Photo> mPhotos;

    public Box() {

    }

    protected Box(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mTitle = in.readString();
        mPrice = in.readString();
        mQuantity = in.readInt();
        mWeight = in.readString();
        mPhotos = in.createTypedArrayList(Photo.CREATOR);
    }

    public static final Creator<Box> CREATOR = new Creator<Box>() {
        @Override
        public Box createFromParcel(Parcel in) {
            return new Box(in);
        }

        @Override
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };

    public String getId() {
        return mId;
    }

    public String getUid() {
        return mUid;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPrice() {
        return mPrice;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public String getWeight() {
        return mWeight;
    }

    public ArrayList<Photo> getPhotos() {
        return mPhotos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mUid);
        parcel.writeString(mTitle);
        parcel.writeString(mPrice);
        parcel.writeInt(mQuantity);
        parcel.writeString(mWeight);
        parcel.writeTypedList(mPhotos);
    }
}
