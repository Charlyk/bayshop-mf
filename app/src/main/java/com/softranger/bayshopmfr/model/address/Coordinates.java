package com.softranger.bayshopmfr.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/16/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coordinates implements Parcelable {
    @JsonProperty("latitude") double mLatitude;
    @JsonProperty("longitude") double mLongitude;

    public Coordinates() {
        // empty constructor used by jackson
    }

    protected Coordinates(Parcel in) {
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        @Override
        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(mLatitude);
        parcel.writeDouble(mLongitude);
    }
}
