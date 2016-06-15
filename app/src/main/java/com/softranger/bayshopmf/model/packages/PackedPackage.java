package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

import java.lang.*;

/**
 * Created by macbook on 6/14/16.
 */
public class PackedPackage extends Package {
    private String mPackedTime;
    private int mPercentage;

    private PackedPackage() {

    }

    protected PackedPackage(Parcel in) {
        super(in);
        mPackedTime = in.readString();
        mPercentage = in.readInt();
    }

    public static final Creator<PackedPackage> CREATOR = new Creator<PackedPackage>() {
        @Override
        public PackedPackage createFromParcel(Parcel source) {
            return new PackedPackage(source);
        }

        @Override
        public PackedPackage[] newArray(int size) {
            return new PackedPackage[size];
        }
    };

    public String getPackedTime() {
        return mPackedTime;
    }

    public void setPackedTime(String packedTime) {
        mPackedTime = packedTime;
    }

    public int getPercentage() {
        return mPercentage;
    }

    public void setPercentage(int percentage) {
        mPercentage = percentage;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mPackedTime);
        dest.writeInt(mPercentage);
    }

    public static class Builder extends Package.Builder {
        private String mPackedTime;
        private int mPercentage;

        public Builder packedTime(String packedTime) {
            mPackedTime = packedTime;
            return this;
        }

        public Builder percentage(int percentage) {
            mPercentage = percentage;
            return this;
        }

        public PackedPackage build() {
            PackedPackage packedPackage = new PackedPackage();
            packedPackage.mPackedTime = this.mPackedTime;
            packedPackage.mName = this.mName;
            packedPackage.mId = this.mId;
            packedPackage.mRealWeght = this.mRealWeght;
            packedPackage.mCodeNumber = this.mCodeNumber;
            packedPackage.mPercentage = this.mPercentage;
            packedPackage.mDeposit = this.mDeposit;
            packedPackage.mTrackingNumber = this.mTrackingNumber;
            packedPackage.mShippingMethod = this.mShippingMethod;
            packedPackage.mAddress = this.mAddress;
            packedPackage.mProducts = this.mProducts;
            packedPackage.mCurrency = this.mCurrency;
            return packedPackage;
        }
    }
}
