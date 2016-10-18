package com.softranger.bayshopmf.model.pus;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 10/18/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class CourierContacts implements Parcelable {
    @JsonProperty("title")
    private String mTitle;
    @JsonProperty("phone")
    private String mPhone;

    public CourierContacts() {

    }

    protected CourierContacts(Parcel in) {
        mTitle = in.readString();
        mPhone = in.readString();
    }

    public static final Creator<CourierContacts> CREATOR = new Creator<CourierContacts>() {
        @Override
        public CourierContacts createFromParcel(Parcel in) {
            return new CourierContacts(in);
        }

        @Override
        public CourierContacts[] newArray(int size) {
            return new CourierContacts[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getPhone() {
        return mPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPhone);
    }
}
