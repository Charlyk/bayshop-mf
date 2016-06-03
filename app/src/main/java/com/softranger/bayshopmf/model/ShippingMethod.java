package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 5/26/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ShippingMethod implements Parcelable {
    private int mId;
    private String mName;
    private String mEstimatedTime;
    private double mMaxWeight;
    private double mCalculatedPrice;
    private int mRank;
    private String mCurrency;
    private String mDescription;

    private ShippingMethod() {
    }

    protected ShippingMethod(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mEstimatedTime = in.readString();
        mMaxWeight = in.readDouble();
        mCalculatedPrice = in.readDouble();
        mRank = in.readInt();
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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getEstimatedTime() {
        return mEstimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        mEstimatedTime = estimatedTime;
    }

    public double getMaxWeight() {
        return mMaxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        mMaxWeight = maxWeight;
    }

    public double getCalculatedPrice() {
        return mCalculatedPrice;
    }

    public void setCalculatedPrice(double calculatedPrice) {
        mCalculatedPrice = calculatedPrice;
    }

    public int getRank() {
        return mRank;
    }

    public void setRank(int rank) {
        mRank = rank;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mEstimatedTime);
        dest.writeDouble(mMaxWeight);
        dest.writeDouble(mCalculatedPrice);
        dest.writeInt(mRank);
        dest.writeString(mCurrency);
        dest.writeString(mDescription);
    }


    public static class Builder {
        private int mId;
        private String mName;
        private String mEstimatedTime;
        private double mMaxWeight;
        private double mCalculatedPrice;
        private int mRank;
        private String mCurrency;
        private String mDescription;

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder mEstimatedTime(String estimatedTime) {
            mEstimatedTime = estimatedTime;
            return this;
        }

        public Builder maxWeight(double maxWeight) {
            mMaxWeight = maxWeight;
            return this;
        }

        public Builder calculatedPrice(double calculatedPrice) {
            mCalculatedPrice = calculatedPrice;
            return this;
        }

        public Builder rank(int rank) {
            mRank = rank;
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
            shippingMethod.mId = this.mId;
            shippingMethod.mName = this.mName;
            shippingMethod.mEstimatedTime = this.mEstimatedTime;
            shippingMethod.mMaxWeight = this.mMaxWeight;
            shippingMethod.mCalculatedPrice = this.mCalculatedPrice;
            shippingMethod.mRank = this.mRank;
            shippingMethod.mCurrency = this.mCurrency;
            shippingMethod.mDescription = this.mDescription;
            return shippingMethod;
        }
    }
}
