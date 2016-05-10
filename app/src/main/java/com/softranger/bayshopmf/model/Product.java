package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 5/10/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class Product implements Parcelable {
    private String mProductName;
    private String mProductUrl;
    private String mProductQuantity;
    private String mProductPrice;

    private Product() {}

    protected Product(Parcel in) {
        mProductName = in.readString();
        mProductUrl = in.readString();
        mProductQuantity = in.readString();
        mProductPrice = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mProductName);
        dest.writeString(mProductUrl);
        dest.writeString(mProductQuantity);
        dest.writeString(mProductPrice);
    }

    public static class Builder {
        private String mProductName;
        private String mProductUrl;
        private String mProductQuantity;
        private String mProductPrice;

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

        public Product build() {
            Product product = new Product();
            product.mProductName = this.mProductName;
            product.mProductUrl = this.mProductUrl;
            product.mProductQuantity = this.mProductQuantity;
            product.mProductPrice = this.mProductPrice;
            return product;
        }
    }
}
