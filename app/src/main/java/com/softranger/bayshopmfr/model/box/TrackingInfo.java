package com.softranger.bayshopmfr.model.box;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.util.Application;

/**
 * Created by Eduard Albu on 11/17/16, 11, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class TrackingInfo implements Parcelable {
    @JsonProperty("service_name")
    private String mServiceName;
    @JsonProperty("tracking_status")
    private String mStatus;
    @JsonIgnore
    private TrackingStatus mTrackingStatus;

    public TrackingInfo() {

    }

    protected TrackingInfo(Parcel in) {
        mServiceName = in.readString();
    }

    public static final Creator<TrackingInfo> CREATOR = new Creator<TrackingInfo>() {
        @Override
        public TrackingInfo createFromParcel(Parcel in) {
            return new TrackingInfo(in);
        }

        @Override
        public TrackingInfo[] newArray(int size) {
            return new TrackingInfo[size];
        }
    };

    public String getServiceName() {
        return mServiceName;
    }

    public TrackingStatus getTrackingStatus() {
        if (mStatus == null) return TrackingStatus.UNKNOWN;
        for (TrackingStatus s : TrackingStatus.values()) {
            if (s.name().equalsIgnoreCase(mStatus)) {
                return s;
            }
        }
        return TrackingStatus.UNKNOWN;
    }

    public String getStatus() {
        return mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mServiceName);
    }

    public enum TrackingStatus {
        UNKNOWN(R.string.geting_status, 0), TRACKED(R.string.in_transit, 1), DELIVERED(R.string.delivered, 2);

        @StringRes
        private int mTranslatedStatus;
        private int mProgress;

        TrackingStatus(@StringRes int translatedStatus, int progress) {
            mTranslatedStatus = translatedStatus;
            mProgress = progress;
        }

        public String translatedStatus() {
            return Application.getInstance().getString(mTranslatedStatus);
        }

        public int progress() {
            return mProgress;
        }
    }
}
