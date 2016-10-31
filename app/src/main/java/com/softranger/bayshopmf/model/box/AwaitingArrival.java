package com.softranger.bayshopmf.model.box;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmf.model.tracking.TrackingResult;
import com.softranger.bayshopmf.util.Constants;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class AwaitingArrival extends Box {

    @JsonProperty("status") private String mTrackingStatus;
    @JsonProperty("url") private String mUrl;
    @JsonProperty("storage") private String mStorage;
    @JsonIgnore
    private TrackingResult mTrackingResult;

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

    @Override
    public String getPrice() {
        return Constants.USD_SYMBOL + mPrice;
    }

    @JsonSetter("productUrl")
    public void setProductUrl(String url) {
        mUrl = url;
    }

    public TrackingResult getTrackingResult() {
        return mTrackingResult;
    }

    public void setTrackingResult(TrackingResult trackingResult) {
        mTrackingResult = trackingResult;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AwaitingArrival &&
                ((AwaitingArrival) obj).getId().equals(this.getId());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mTrackingStatus);
        parcel.writeString(mUrl);
        parcel.writeString(mStorage);
    }
}
