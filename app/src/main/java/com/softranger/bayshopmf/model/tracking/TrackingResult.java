package com.softranger.bayshopmf.model.tracking;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Eduard Albu on 10/31/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackingResult implements Parcelable {
    @JsonProperty("id")
    private int mId;
    @JsonProperty("tracking_number")
    private String mTrackingNumber;
    @JsonProperty("courier")
    private Courier mCourier;
    @JsonProperty("is_active")
    private boolean mIsActive;
    @JsonProperty("is_delivered")
    private boolean mIsDelivered;
    @JsonProperty("last_check")
    private Date mDate;
    @JsonProperty("checkpoints")
    private ArrayList<Checkpoint> mCheckpoints;

    public TrackingResult() {

    }

    protected TrackingResult(Parcel in) {
        mId = in.readInt();
        mTrackingNumber = in.readString();
        mCourier = in.readParcelable(Courier.class.getClassLoader());
        mIsActive = in.readByte() != 0;
        mIsDelivered = in.readByte() != 0;
        mCheckpoints = in.createTypedArrayList(Checkpoint.CREATOR);
    }

    public static final Creator<TrackingResult> CREATOR = new Creator<TrackingResult>() {
        @Override
        public TrackingResult createFromParcel(Parcel in) {
            return new TrackingResult(in);
        }

        @Override
        public TrackingResult[] newArray(int size) {
            return new TrackingResult[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public Courier getCourier() {
        return mCourier;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public boolean isDelivered() {
        return mIsDelivered;
    }

    public Date getDate() {
        return mDate;
    }

    public ArrayList<Checkpoint> getCheckpoints() {
        return mCheckpoints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mTrackingNumber);
        parcel.writeParcelable(mCourier, i);
        parcel.writeByte((byte) (mIsActive ? 1 : 0));
        parcel.writeByte((byte) (mIsDelivered ? 1 : 0));
        parcel.writeTypedList(mCheckpoints);
    }
}
