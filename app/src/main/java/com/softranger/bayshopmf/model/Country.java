package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by macbook on 6/17/16.
 */
public class Country implements Parcelable {
    private String mId;
    private String mName;

    private Country() {

    }

    protected Country(Parcel in) {
        mId = in.readString();
        mName = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
    }

    public static class Builder {
        private String mId;
        private String mName;

        public Builder id(String id) {
            mId = id;
            return this;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Country build() {
            Country country = new Country();
            country.mId = this.mId;
            country.mName = this.mName;
            return country;
        }
    }
}
