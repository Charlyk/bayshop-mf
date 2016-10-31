package com.softranger.bayshopmf.model.tracking;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 10/31/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Courier implements Parcelable {
    @JsonProperty("slug")
    private String mSlug;
    @JsonProperty("name")
    private String mName;
    @JsonProperty("name_alt")
    private String mNameAlt;
    @JsonProperty("country_code")
    private String mCountryCode;

    public Courier() {

    }

    protected Courier(Parcel in) {
        mSlug = in.readString();
        mName = in.readString();
        mNameAlt = in.readString();
        mCountryCode = in.readString();
    }

    public static final Creator<Courier> CREATOR = new Creator<Courier>() {
        @Override
        public Courier createFromParcel(Parcel in) {
            return new Courier(in);
        }

        @Override
        public Courier[] newArray(int size) {
            return new Courier[size];
        }
    };

    public String getSlug() {
        return mSlug;
    }

    public String getName() {
        return mName;
    }

    public String getNameAlt() {
        return mNameAlt;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mSlug);
        parcel.writeString(mName);
        parcel.writeString(mNameAlt);
        parcel.writeString(mCountryCode);
    }
}
