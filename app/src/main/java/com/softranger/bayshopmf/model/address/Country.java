package com.softranger.bayshopmf.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmf.util.SpinnerObj;

/**
 * Created by macbook on 6/17/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Parcelable, SpinnerObj {
    @JsonProperty("id") private int mId;
    @JsonProperty("title") private String mName;
    @JsonProperty("code") private String mCode;
    @JsonProperty("storageId")
    private String mStorageId;
    @JsonProperty("flag")
    private String mFlagUrl;
    @JsonProperty("rank")
    private int mRank;
    @JsonProperty("remoteId")
    private String mRemoteId;
    @JsonProperty("forShipping")
    private int mForShipping;

    private Country() {

    }

    protected Country(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mCode = in.readString();
        mStorageId = in.readString();
        mFlagUrl = in.readString();
        mRank = in.readInt();
        mRemoteId = in.readString();
        mForShipping = in.readInt();
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

    public String getStorageId() {
        return mStorageId;
    }

    public String getFlagUrl() {
        return mFlagUrl;
    }

    public int getRank() {
        return mRank;
    }

    public String getRemoteId() {
        return mRemoteId;
    }

    public int getForShipping() {
        return mForShipping;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeString(mCode);
        parcel.writeString(mStorageId);
        parcel.writeString(mFlagUrl);
        parcel.writeInt(mRank);
        parcel.writeString(mRemoteId);
        parcel.writeInt(mForShipping);
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
