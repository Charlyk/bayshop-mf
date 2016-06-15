package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

import java.lang.*;

/**
 * Created by macbook on 6/14/16.
 */
public class SentPackage extends Package {
    private String mSentTime;

    private SentPackage() {

    }

    protected SentPackage(Parcel in) {
        super(in);
        mSentTime = in.readString();
    }

    public static final Creator<SentPackage> CREATOR = new Creator<SentPackage>() {
        @Override
        public SentPackage createFromParcel(Parcel source) {
            return new SentPackage(source);
        }

        @Override
        public SentPackage[] newArray(int size) {
            return new SentPackage[size];
        }
    };

    public String getSentTime() {
        return mSentTime;
    }

    public void setSentTime(String sentTime) {
        mSentTime = sentTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mSentTime);
    }

    public static class Builder extends Package.Builder {
        private String mSentTime;

        public Builder sentTime(String sentTime) {
            mSentTime = sentTime;
            return this;
        }

        public SentPackage build() {
            SentPackage sentPackage = new SentPackage();
            sentPackage.mSentTime = this.mSentTime;
            sentPackage.mCodeNumber = this.mCodeNumber;
            sentPackage.mRealWeght = this.mRealWeght;
            sentPackage.mId = this.mId;
            sentPackage.mName = this.mName;
            sentPackage.mDeposit = this.mDeposit;
            sentPackage.mTrackingNumber = this.mTrackingNumber;
            sentPackage.mShippingMethod = this.mShippingMethod;
            sentPackage.mAddress = this.mAddress;
            sentPackage.mProducts = this.mProducts;
            sentPackage.mCurrency = this.mCurrency;
            return sentPackage;
        }
    }
}
