package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/14/16.
 */
public class LocalPackage extends Package {
    private String mLocalDepotTime;

    private LocalPackage() {

    }

    protected LocalPackage(Parcel in) {
        super(in);
        mLocalDepotTime = in.readString();
    }

    public static final Creator<LocalPackage> CREATOR = new Creator<LocalPackage>() {
        @Override
        public LocalPackage createFromParcel(Parcel source) {
            return new LocalPackage(source);
        }

        @Override
        public LocalPackage[] newArray(int size) {
            return new LocalPackage[size];
        }
    };

    public String getLocalDepotTime() {
        return mLocalDepotTime;
    }

    public void setLocalDepotTime(String localDepotTime) {
        mLocalDepotTime = localDepotTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mLocalDepotTime);
    }

    public static class Builder extends Package.Builder {
        private String mLocalDepotTime;

        public Builder localDepotTime(String localDepotTime) {
            mLocalDepotTime = localDepotTime;
            return this;
        }

        public LocalPackage build() {
            LocalPackage localPackage = new LocalPackage();
            localPackage.mLocalDepotTime = this.mLocalDepotTime;
            localPackage.mName = this.mName;
            localPackage.mId = this.mId;
            localPackage.mRealWeght = this.mRealWeght;
            localPackage.mCodeNumber = this.mCodeNumber;
            localPackage.mDeposit = this.mDeposit;
            localPackage.mTrackingNumber = this.mTrackingNumber;
            localPackage.mShippingMethod = this.mShippingMethod;
            localPackage.mAddress = this.mAddress;
            localPackage.mProducts = this.mProducts;
            localPackage.mCurrency = this.mCurrency;
            return localPackage;
        }
    }
}
