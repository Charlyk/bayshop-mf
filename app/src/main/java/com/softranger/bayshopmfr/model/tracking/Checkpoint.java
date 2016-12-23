package com.softranger.bayshopmfr.model.tracking;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Eduard Albu on 12/23/16, 12, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Checkpoint implements Parcelable {
    @JsonProperty("courier") private Courier mCourier;
    @JsonProperty("location") private String mLocation;
    @JsonProperty("status") private String mStatus;
    @JsonProperty("date_time") private Date mDateTime;

    public Checkpoint() {

    }

    protected Checkpoint(Parcel in) {
        mCourier = in.readParcelable(Courier.class.getClassLoader());
        mLocation = in.readString();
        mStatus = in.readString();
        mDateTime = (Date) in.readSerializable();
    }

    public static final Creator<Checkpoint> CREATOR = new Creator<Checkpoint>() {
        @Override
        public Checkpoint createFromParcel(Parcel in) {
            return new Checkpoint(in);
        }

        @Override
        public Checkpoint[] newArray(int size) {
            return new Checkpoint[size];
        }
    };

    public Courier getCourier() {
        return mCourier;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getStatus() {
        return mStatus;
    }

    public Date getDateTime() {
        return mDateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mCourier, i);
        parcel.writeString(mLocation);
        parcel.writeString(mStatus);
        parcel.writeSerializable(mDateTime);
    }
}
