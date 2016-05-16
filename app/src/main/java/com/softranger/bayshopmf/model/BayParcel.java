package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class BayParcel implements Parcelable {

    protected String mParcelId;
    protected String mParcelName;
    protected String mCreatedDate;
    protected String mWeight;
    protected String mDeposit;
    protected String mShippingBy;
    protected String mTrackingNumber;
    protected String mUrl;
    protected Address mAddress;
    protected ArrayList<Product> mProducts;

    protected BayParcel(){

    }

    protected BayParcel(Parcel in) {
        mParcelId = in.readString();
        mParcelName = in.readString();
        mCreatedDate = in.readString();
        mWeight = in.readString();
        mDeposit = in.readString();
        mShippingBy = in.readString();
        mTrackingNumber = in.readString();
        mUrl = in.readString();
        mAddress = in.readParcelable(Address.class.getClassLoader());
        in.readList(mProducts, Product.class.getClassLoader());
    }

    public static final Creator<BayParcel> CREATOR = new Creator<BayParcel>() {
        @Override
        public BayParcel createFromParcel(Parcel in) {
            return new BayParcel(in);
        }

        @Override
        public BayParcel[] newArray(int size) {
            return new BayParcel[size];
        }
    };

    public String getParcelId() {
        return mParcelId;
    }

    public void setParcelId(String parcelId) {
        mParcelId = parcelId;
    }

    public String getParcelName() {
        return mParcelName;
    }

    public void setParcelName(String parcelName) {
        mParcelName = parcelName;
    }

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        mWeight = weight;
    }

    public String getDeposit() {
        return mDeposit;
    }

    public void setDeposit(String deposit) {
        mDeposit = deposit;
    }

    public String getShippingBy() {
        return mShippingBy;
    }

    public void setShippingBy(String shippingBy) {
        mShippingBy = shippingBy;
    }

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        mTrackingNumber = trackingNumber;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public Address getAddress() {
        return mAddress;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    public void setProducts(ArrayList<Product> products) {
        mProducts = products;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mParcelId);
        dest.writeString(mParcelName);
        dest.writeString(mCreatedDate);
        dest.writeString(mWeight);
        dest.writeString(mDeposit);
        dest.writeString(mShippingBy);
        dest.writeString(mTrackingNumber);
        dest.writeString(mUrl);
        dest.writeParcelable(mAddress, flags);
        dest.writeList(mProducts);
    }

    public static class Builder {
        protected String mParcelId;
        protected String mProductName;
        protected String mCreatedDate;
        protected String mWeight;
        protected String mDeposit;
        protected String mShippingBy;
        protected String mTrackingNumber;
        protected String mUrl;
        protected Address mAddress;
        protected ArrayList<Product> mProducts;

        public Builder parcelId(String parcelId) {
            mParcelId = parcelId;
            return this;
        }

        public Builder productName(String productName) {
            mProductName = productName;
            return this;
        }

        public Builder createdDate(String createdDate) {
            mCreatedDate = createdDate;
            return this;
        }

        public Builder weight(String weight) {
            mWeight = weight;
            return this;
        }

        public Builder deposit(String deposit) {
            mDeposit = deposit;
            return this;
        }

        public Builder shippingBy(String shippingBy) {
            mShippingBy = shippingBy;
            return this;
        }

        public Builder trackingNumber(String trackingNumber) {
            mTrackingNumber = trackingNumber;
            return this;
        }

        public Builder url(String url) {
            mUrl =url;
            return this;
        }

        public Builder address(Address address) {
            mAddress = address;
            return this;
        }

        public Builder products(ArrayList<Product> products) {
            mProducts = products;
            return this;
        }

        public BayParcel build() {
            BayParcel bayParcel = new BayParcel();
            bayParcel.mParcelId = this.mParcelId;
            bayParcel.mParcelName = this.mProductName;
            bayParcel.mCreatedDate = this.mCreatedDate;
            bayParcel.mWeight = this.mWeight;
            bayParcel.mDeposit = this.mDeposit;
            bayParcel.mShippingBy = this.mShippingBy;
            bayParcel.mUrl = this.mUrl;
            bayParcel.mAddress = this.mAddress;
            bayParcel.mProducts = this.mProducts;
            bayParcel.mTrackingNumber = this.mTrackingNumber;
            return bayParcel;
        }
    }
}
