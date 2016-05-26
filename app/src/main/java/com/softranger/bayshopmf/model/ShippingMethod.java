package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 5/26/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ShippingMethod implements Parcelable {
    private String mName;
    private int mPrice;
    private String mCurrency;
    private String mDescription;

    private ShippingMethod() {

    }

    protected ShippingMethod(Parcel in) {
        mName = in.readString();
        mPrice = in.readInt();
        mCurrency = in.readString();
        mDescription = in.readString();
    }

    public static final Creator<ShippingMethod> CREATOR = new Creator<ShippingMethod>() {
        @Override
        public ShippingMethod createFromParcel(Parcel in) {
            return new ShippingMethod(in);
        }

        @Override
        public ShippingMethod[] newArray(int size) {
            return new ShippingMethod[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mPrice);
        dest.writeString(mCurrency);
        dest.writeString(mDescription);
    }

    public static class Builder {
        private String mName;
        private int mPrice;
        private String mCurrency;
        private String mDescription;

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder price(int price) {
            mPrice = price;
            return this;
        }

        public Builder currency(String currency){
            mCurrency = currency;
            return this;
        }

        public Builder description(String description) {
            mDescription = description;
            return this;
        }

        public ShippingMethod build() {
            ShippingMethod shippingMethod = new ShippingMethod();
            shippingMethod.mName = this.mName;
            shippingMethod.mPrice = this.mPrice;
            shippingMethod.mCurrency = this.mCurrency;
            shippingMethod.mDescription = this.mDescription;
            return shippingMethod;
        }
    }
}
