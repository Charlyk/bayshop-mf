package com.softranger.bayshopmf.model.box;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class AwaitingArrival extends Box {

    @JsonProperty("status") private String mTrackingStatus;
    @JsonProperty("url") private String mUrl;
    @JsonProperty("storage") private String mStorage;

    public AwaitingArrival() {
        // empty constructor for jackson
    }

    public AwaitingArrival(Parcel in) {
        super(in);
        mTrackingStatus = in.readString();
        mUrl = in.readString();
        mStorage = in.readString();
    }

    public static final Creator<AwaitingArrival> CREATOR = new Creator<AwaitingArrival>() {
        @Override
        public AwaitingArrival createFromParcel(Parcel parcel) {
            return new AwaitingArrival(parcel);
        }

        @Override
        public AwaitingArrival[] newArray(int i) {
            return new AwaitingArrival[i];
        }
    };

    public String getTrackingStatus() {
        return mTrackingStatus;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getStorage() {
        return mStorage;
    }

    @JsonSetter("productUrl")
    public void setProductUrl(String url) {
        mUrl = url;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mTrackingStatus);
        parcel.writeString(mUrl);
        parcel.writeString(mStorage);
    }
}
