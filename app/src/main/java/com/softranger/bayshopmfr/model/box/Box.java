package com.softranger.bayshopmfr.model.box;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmfr.model.product.Photo;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Eduard Albu on 9/16/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Box implements Parcelable, Comparable<Box> {

    @JsonProperty("id") private String mId;
    @JsonProperty("uid") private String mUid;
    @JsonProperty("title") private String mTitle;
    @JsonProperty("tracking") private String mTracking;
    @JsonProperty("price")
    protected String mPrice;
    @JsonProperty("quantity") private int mQuantity;
    @JsonProperty("weight") private String mWeight;
    @JsonProperty("photos") private ArrayList<Photo> mPhotos;
    @JsonProperty("createdDate") private Date mCreatedDate;

    public Box() {
        // empty constructor for jackson
    }

    public Box(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mTitle = in.readString();
        mPrice = in.readString();
        mQuantity = in.readInt();
        mWeight = in.readString();
        mPhotos = in.createTypedArrayList(Photo.CREATOR);
        mTracking = in.readString();
        mCreatedDate = (Date) in.readSerializable();
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

    public void setId(String id) {
        mId = id;
    }

    public String getUid() {
        return mUid;
    }

    @JsonSetter("packageName")
    public void setPackageName(String title) {
        mTitle = title;
    }

    @JsonSetter("barCode")
    public void setBacCode(String bacCode) {
        mUid = bacCode;
    }

    @JsonSetter("packagePrice")
    public void setPackagePrice(int packagePrice) {
        mPrice = String.valueOf(packagePrice);
    }

    @JsonIgnore
    public void setPackagePrice(String packagePrice) {
        mPrice = packagePrice;
    }

    @JsonSetter("trackingNumber")
    public void setTrackingNumber(String trackingNumber) {
        mTracking = trackingNumber;
    }

    @JsonIgnore
    public void setTitle(String title) {
        mTitle = title;
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

    public String getTracking() {
        return mTracking;
    }

    public Date getCreatedDate() {
        return mCreatedDate;
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
        parcel.writeString(mTracking);
        parcel.writeSerializable(mCreatedDate);
    }

    @Override
    public int compareTo(@NonNull Box box) {
        return box.getCreatedDate().compareTo(getCreatedDate());
    }
}
