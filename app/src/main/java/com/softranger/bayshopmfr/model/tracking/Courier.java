package com.softranger.bayshopmfr.model.tracking;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 12/23/16, 12, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Courier implements Parcelable {
    @JsonProperty("name") private String mName;
    @JsonProperty("title") private String mTitle;

    public Courier() {

    }

    protected Courier(Parcel in) {
        mName = in.readString();
        mTitle = in.readString();
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

    public String getName() {
        return mName;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mTitle);
    }
}
