package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/14/16.
 */
public class CustomsHeldPackage extends Package {

    private CustomsHeldPackage() {

    }

    protected CustomsHeldPackage(Parcel in) {
        super(in);
    }

    public static final Creator<CustomsHeldPackage> CREATOR = new Creator<CustomsHeldPackage>() {
        @Override
        public CustomsHeldPackage createFromParcel(Parcel source) {
            return new CustomsHeldPackage(source);
        }

        @Override
        public CustomsHeldPackage[] newArray(int size) {
            return new CustomsHeldPackage[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static class Builder extends Package.Builder {

        public CustomsHeldPackage build() {
            CustomsHeldPackage customsHeldPackage = new CustomsHeldPackage();
            customsHeldPackage.mName = this.mName;
            customsHeldPackage.mId = this.mId;
            customsHeldPackage.mRealWeght = this.mRealWeght;
            customsHeldPackage.mCodeNumber = this.mCodeNumber;
            customsHeldPackage.mDeposit = this.mDeposit;
            customsHeldPackage.mTrackingNumber = this.mTrackingNumber;
            customsHeldPackage.mShippingMethod = this.mShippingMethod;
            customsHeldPackage.mAddress = this.mAddress;
            customsHeldPackage.mProducts = this.mProducts;
            customsHeldPackage.mCurrency = this.mCurrency;
            return customsHeldPackage;
        }
    }
}
