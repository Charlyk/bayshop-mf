package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

import java.lang.*;

/**
 * Created by macbook on 6/14/16.
 */
public class ReceivedPackage extends Package {
    private String mReceivedTime;

    private ReceivedPackage() {

    }

    protected ReceivedPackage(Parcel in) {
        super(in);
        mReceivedTime = in.readString();
    }

    public static final Creator<ReceivedPackage> CREATOR = new Creator<ReceivedPackage>() {
        @Override
        public ReceivedPackage createFromParcel(Parcel source) {
            return new ReceivedPackage(source);
        }

        @Override
        public ReceivedPackage[] newArray(int size) {
            return new ReceivedPackage[size];
        }
    };

    public String getReceivedTime() {
        return mReceivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        mReceivedTime = receivedTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mReceivedTime);
    }

    public static class Builder extends Package.Builder {
        private String mReceivedTime;

        public Builder receivedTime(String receivedTime) {
            mReceivedTime = receivedTime;
            return this;
        }

        public ReceivedPackage build() {
            ReceivedPackage receivedPackage = new ReceivedPackage();
            receivedPackage.mReceivedTime = this.mReceivedTime;
            receivedPackage.mCodeNumber = this.mCodeNumber;
            receivedPackage.mRealWeght = this.mRealWeght;
            receivedPackage.mId = this.mId;
            receivedPackage.mName = this.mName;
            receivedPackage.mDeposit = this.mDeposit;
            receivedPackage.mTrackingNumber = this.mTrackingNumber;
            receivedPackage.mShippingMethod = this.mShippingMethod;
            receivedPackage.mAddress = this.mAddress;
            receivedPackage.mProducts = this.mProducts;
            receivedPackage.mCurrency = this.mCurrency;
            return receivedPackage;
        }
    }
}
