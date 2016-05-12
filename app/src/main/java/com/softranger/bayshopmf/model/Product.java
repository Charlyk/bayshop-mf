package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 5/10/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class Product implements Parcelable {
    private String mProductId;
    private String mProductName;
    private String mProductUrl;
    private String mProductQuantity;
    private String mProductPrice;
    private String mTrackingNumber;
    private String mDeposit;
    private String mDate;

    private Product() {}

    protected Product(Parcel in) {
        mProductId = in.readString();
        mProductName = in.readString();
        mProductUrl = in.readString();
        mProductQuantity = in.readString();
        mProductPrice = in.readString();
        mTrackingNumber = in.readString();
        mDeposit = in.readString();
        mDate = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getProductName() {
        return mProductName;
    }

    public String getProductUrl() {
        return mProductUrl;
    }

    public String getProductQuantity() {
        return mProductQuantity;
    }

    public String getProductPrice() {
        return mProductPrice;
    }

    public String getProductId() {
        return mProductId;
    }

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public String getDate() {
        return mDate;
    }

    public String getDeposit() {
        return mDeposit;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public void setProductUrl(String productUrl) {
        mProductUrl = productUrl;
    }

    public void setProductQuantity(String productQuantity) {
        mProductQuantity = productQuantity;
    }

    public void setProductPrice(String productPrice) {
        mProductPrice = productPrice;
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public void setTrackingNumber(String trackingNumber) {
        mTrackingNumber = trackingNumber;
    }

    public void setDeposit(String deposit) {
        mDeposit = deposit;
    }

    public void setDate(String date) {
        mDate = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mProductId);
        dest.writeString(mProductName);
        dest.writeString(mProductUrl);
        dest.writeString(mProductQuantity);
        dest.writeString(mProductPrice);
        dest.writeString(mTrackingNumber);
        dest.writeString(mDeposit);
        dest.writeString(mDate);
    }

    public static class Builder {
        private String mProductId;
        private String mProductName;
        private String mProductUrl;
        private String mProductQuantity;
        private String mProductPrice;
        private String mTrackingNumber;
        private String mDeposit;
        private String mDate;

        public Builder productId(String productId) {
            mProductId = productId;
            return this;
        }

        public Builder productName(String productName) {
            mProductName = productName;
            return this;
        }

        public Builder productUrl(String productUrl) {
            mProductUrl = productUrl;
            return this;
        }

        public Builder productQuantity(String productQuantity) {
            mProductQuantity = productQuantity;
            return this;
        }

        public Builder productPrice(String productPrice) {
            mProductPrice = productPrice;
            return this;
        }

        public Builder trackingNumber(String trackingNumber) {
            mTrackingNumber = trackingNumber;
            return this;
        }

        public Builder deposit(String deposit) {
            mDeposit = deposit;
            return this;
        }

        public Builder date(String date) {
            mDate = date;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.mProductId = this.mProductId;
            product.mProductName = this.mProductName;
            product.mProductUrl = this.mProductUrl;
            product.mProductQuantity = this.mProductQuantity;
            product.mProductPrice = this.mProductPrice;
            product.mTrackingNumber = this.mTrackingNumber;
            product.mDeposit = this.mDeposit;
            product.mDate = this.mDate;
            return product;
        }
    }
}
