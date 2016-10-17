package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 10/17/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class WarehouseAddress implements Parcelable {
    @JsonProperty("fullName")
    private String mFullName;
    @JsonProperty("countryTitle")
    private String mCountryTitle;
    @JsonProperty("countryCode")
    private String mCountryCode;
    @JsonProperty("state")
    private String mState;
    @JsonProperty("city")
    private String mCity;
    @JsonProperty("zipCode")
    private String mPostalCode;
    @JsonProperty("phone")
    private String mPhone;
    @JsonProperty("address1")
    private String mAddressLine1;
    @JsonProperty("address2")
    private String mAddressLine2;

    public WarehouseAddress() {

    }

    protected WarehouseAddress(Parcel in) {
        mFullName = in.readString();
        mCountryTitle = in.readString();
        mCountryCode = in.readString();
        mState = in.readString();
        mCity = in.readString();
        mPostalCode = in.readString();
        mPhone = in.readString();
        mAddressLine1 = in.readString();
        mAddressLine2 = in.readString();
    }

    public static final Creator<WarehouseAddress> CREATOR = new Creator<WarehouseAddress>() {
        @Override
        public WarehouseAddress createFromParcel(Parcel in) {
            return new WarehouseAddress(in);
        }

        @Override
        public WarehouseAddress[] newArray(int size) {
            return new WarehouseAddress[size];
        }
    };

    public String getFullName() {
        return mFullName;
    }

    public String getCountryTitle() {
        return mCountryTitle;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getState() {
        return mState;
    }

    public String getCity() {
        return mCity;
    }

    public String getPostalCode() {
        return mPostalCode;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getAddressLine1() {
        return mAddressLine1;
    }

    public String getAddressLine2() {
        return mAddressLine2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFullName);
        dest.writeString(mCountryTitle);
        dest.writeString(mCountryCode);
        dest.writeString(mState);
        dest.writeString(mCity);
        dest.writeString(mPostalCode);
        dest.writeString(mPhone);
        dest.writeString(mAddressLine1);
        dest.writeString(mAddressLine2);
    }
}
