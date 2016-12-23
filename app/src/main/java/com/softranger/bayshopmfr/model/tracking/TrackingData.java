package com.softranger.bayshopmfr.model.tracking;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Eduard Albu on 12/23/16, 12, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class TrackingData implements Parcelable {
    @JsonProperty("service_name") private String mServiceName;
    @JsonProperty("tracking_status") private String mStringStatus;
    @JsonProperty("checkpoints") private ArrayList<Checkpoint> mCheckpoints;
    @JsonProperty("sent_time") private Date mSentTime;
    @JsonProperty("update_time") private Date mUpdateTime;

    protected TrackingData(Parcel in) {
        mServiceName = in.readString();
        mStringStatus = in.readString();
        mCheckpoints = in.createTypedArrayList(Checkpoint.CREATOR);
        mSentTime = (Date) in.readSerializable();
        mUpdateTime = (Date) in.readSerializable();
    }

    public static final Creator<TrackingData> CREATOR = new Creator<TrackingData>() {
        @Override
        public TrackingData createFromParcel(Parcel in) {
            return new TrackingData(in);
        }

        @Override
        public TrackingData[] newArray(int size) {
            return new TrackingData[size];
        }
    };

    public String getServiceName() {
        return mServiceName;
    }

    public String getStringStatus() {
        return mStringStatus;
    }

    public ArrayList<Checkpoint> getCheckpoints() {
        return mCheckpoints;
    }

    public Date getSentTime() {
        return mSentTime;
    }

    public Date getUpdateTime() {
        return mUpdateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mServiceName);
        parcel.writeString(mStringStatus);
        parcel.writeTypedList(mCheckpoints);
        parcel.writeSerializable(mSentTime);
        parcel.writeSerializable(mUpdateTime);
    }
}
