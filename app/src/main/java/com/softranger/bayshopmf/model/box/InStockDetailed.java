package com.softranger.bayshopmf.model.box;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/22/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class InStockDetailed extends InStock {
    @JsonProperty("countryCode") private String mCountryCode;
    @JsonProperty("currency") private String mCurrency;
    @JsonProperty("photosInProgress") private int mPhotosInProgress;
    @JsonProperty("checkProductInProgress") private boolean mCheckInProgress;
    @JsonProperty("separationRequested") private boolean mSeparationRequested;
    @JsonProperty("additionalPhotosRequested") private boolean mAdditionalPhotoRequested;
    @JsonProperty("status") private String mStatus;
    @JsonProperty("storage") private String mStorage;

    private InStockDetailed() {

    }

    protected InStockDetailed(Parcel in) {
        super(in);
        mCountryCode = in.readString();
        mCurrency = in.readString();
        mPhotosInProgress = in.readInt();
        mCheckInProgress = in.readByte() != 0;
        mSeparationRequested = in.readByte() != 0;
        mAdditionalPhotoRequested = in.readByte() != 0;
        mStatus = in.readString();
        mStorage = in.readString();
    }

    public static final Creator<InStockDetailed> CREATOR = new Creator<InStockDetailed>() {
        @Override
        public InStockDetailed createFromParcel(Parcel in) {
            return new InStockDetailed(in);
        }

        @Override
        public InStockDetailed[] newArray(int size) {
            return new InStockDetailed[size];
        }
    };

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public int getPhotosInProgress() {
        return mPhotosInProgress;
    }

    public boolean isCheckInProgress() {
        return mCheckInProgress;
    }

    public boolean isSeparationRequested() {
        return mSeparationRequested;
    }

    public boolean isAdditionalPhotoRequested() {
        return mAdditionalPhotoRequested;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getStorage() {
        return mStorage;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mCountryCode);
        dest.writeString(mCurrency);
        dest.writeInt(mPhotosInProgress);
        dest.writeByte((byte) (mCheckInProgress ? 1 : 0));
        dest.writeByte((byte) (mSeparationRequested ? 1 : 0));
        dest.writeByte((byte) (mAdditionalPhotoRequested ? 1 : 0));
        dest.writeString(mStatus);
        dest.writeString(mStorage);
    }
}
