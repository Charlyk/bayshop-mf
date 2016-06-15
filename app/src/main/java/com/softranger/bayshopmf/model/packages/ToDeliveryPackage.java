package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

import java.lang.*;

/**
 * Created by macbook on 6/14/16.
 */
public class ToDeliveryPackage extends Package {

    private ToDeliveryPackage() {

    }

    protected ToDeliveryPackage(Parcel in) {
        super(in);
    }

    public static final Creator<ToDeliveryPackage> CREATOR = new Creator<ToDeliveryPackage>() {
        @Override
        public ToDeliveryPackage createFromParcel(Parcel source) {
            return new ToDeliveryPackage(source);
        }

        @Override
        public ToDeliveryPackage[] newArray(int size) {
            return new ToDeliveryPackage[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static class Builder extends Package.Builder {

        public ToDeliveryPackage build() {
            ToDeliveryPackage toDeliveryPackage = new ToDeliveryPackage();
            toDeliveryPackage.mCodeNumber = this.mCodeNumber;
            toDeliveryPackage.mRealWeght = this.mRealWeght;
            toDeliveryPackage.mId = this.mId;
            toDeliveryPackage.mName = this.mName;
            toDeliveryPackage.mDeposit = this.mDeposit;
            toDeliveryPackage.mTrackingNumber = this.mTrackingNumber;
            toDeliveryPackage.mShippingMethod = this.mShippingMethod;
            toDeliveryPackage.mAddress = this.mAddress;
            toDeliveryPackage.mProducts = this.mProducts;
            toDeliveryPackage.mCurrency = this.mCurrency;
            return toDeliveryPackage;
        }
    }
}
