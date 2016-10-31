package com.softranger.bayshopmfr.model.tracking;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmfr.R;

import java.util.Date;

/**
 * Created by Eduard Albu on 10/31/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Checkpoint implements Parcelable {
    @JsonProperty("time")
    private Date mDate;
    @JsonProperty("courier")
    private Courier mCourier;
    @JsonProperty("status_name")
    private String mStatusName;
    @JsonProperty("status_raw")
    private String mStatusRaw;
    @JsonProperty("location_translated")
    private String mLocationTranslated;
    @JsonProperty("location_raw")
    private String mLocationRaw;
    @JsonProperty("location_zip_code")
    private String mLocationZipCode;
    @JsonIgnore
    private CheckpointStatus mCheckpointStatus;

    public Checkpoint() {

    }

    protected Checkpoint(Parcel in) {
        mCourier = in.readParcelable(Courier.class.getClassLoader());
        mStatusName = in.readString();
        mStatusRaw = in.readString();
        mLocationTranslated = in.readString();
        mLocationRaw = in.readString();
        mLocationZipCode = in.readString();
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

    public Date getDate() {
        return mDate;
    }

    public Courier getCourier() {
        return mCourier;
    }

    public String getStatusName() {
        return mStatusName;
    }

    public String getStatusRaw() {
        return mStatusRaw;
    }

    public String getLocationTranslated() {
        return mLocationTranslated;
    }

    public String getLocationRaw() {
        return mLocationRaw;
    }

    public String getLocationZipCode() {
        return mLocationZipCode;
    }

    @JsonSetter("status_code")
    public void setCheckpointStatus(String statusName) {
        mCheckpointStatus = CheckpointStatus.statusFromString(statusName);
    }

    public CheckpointStatus getCheckpointStatus() {
        return mCheckpointStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mCourier, i);
        parcel.writeString(mStatusName);
        parcel.writeString(mStatusRaw);
        parcel.writeString(mLocationTranslated);
        parcel.writeString(mLocationRaw);
        parcel.writeString(mLocationZipCode);
    }

    public enum CheckpointStatus {
        accepted("accepted", R.string.accepted),
        info_recieved("info-recieved", R.string.info_recieved),
        in_transit_left("in_transit_left", R.string.in_transit_left),
        in_transit_arrived("in_transit_arrived", R.string.in_transit_arrived),
        other("other", -1),
        delivered("delivered", R.string.delivered),
        in_transit("in_transit", R.string.in_transit),
        soon_delivery("soon_delivery", R.string.soon_delivery),
        arrived("arrived", R.string.arrived);

        @StringRes
        int statusText;
        String statusCode;

        CheckpointStatus(String statusCode, @StringRes int statusText) {
            this.statusText = statusText;
            this.statusCode = statusCode;
        }

        public static CheckpointStatus statusFromString(String statusCode) {
            for (CheckpointStatus status : values()) {
                if (status.statusCode.equals(statusCode)) {
                    return status;
                }
            }
            return other;
        }

        @StringRes
        public int translatedStatus() {
            return statusText;
        }
    }
}
