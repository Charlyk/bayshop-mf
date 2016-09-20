package com.softranger.bayshopmf.model;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class AwaitingArrival extends Box {

    @JsonProperty("status") String mTrackingStatus;

    public AwaitingArrival() {
        // empty constructor for jackson
    }

    public AwaitingArrival(Parcel in) {
        super(in);
        mTrackingStatus = in.readString();
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mTrackingStatus);
    }
}
