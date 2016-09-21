package com.softranger.bayshopmf.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmf.util.SpinnerObj;

/**
 * Created by macbook on 6/17/16.
 */
public class Country implements Parcelable, SpinnerObj {
    @JsonProperty("id") int mId;
    @JsonProperty("title") String mName;
    @JsonProperty("code") String mCode;

    private Country() {

    }

    protected Country(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mCode = in.readString();
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

    @Override
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mCode);
    }

    public static class Builder {
        private int mId;
        private String mName;
        private String mCode;

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder code(String code) {
            mCode = code;
            return this;
        }

        public Country build() {
            Country country = new Country();
            country.mId = this.mId;
            country.mName = this.mName;
            country.mCode = this.mCode;
            return country;
        }
    }
}
