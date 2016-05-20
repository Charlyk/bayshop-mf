package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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
    private String mBarcode;
    private String mCurrency;
    private int mID;
    private ArrayList<Photo> mImages;

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
        mBarcode = in.readString();
        mCurrency = in.readString();
        mID = in.readInt();
        in.readList(mImages, Integer.class.getClassLoader());
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

    public ArrayList<Photo> getImages() {
        return mImages;
    }

    public void setImages(ArrayList<Photo> images) {
        mImages = images;
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
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
        dest.writeString(mBarcode);
        dest.writeString(mCurrency);
        dest.writeInt(mID);
        dest.writeList(mImages);
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
        private String mBarcode;
        private String mCurrency;
        private int mID;
        private ArrayList<Photo> mImages;

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

        public Builder images(ArrayList<Photo> images) {
            mImages = images;
            return this;
        }

        public Builder id(int id) {
            mID = id;
            return this;
        }

        public Builder barcode(String barcode) {
            mBarcode = barcode;
            return this;
        }

        public Builder currency(String currency) {
            mCurrency = currency;
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
            product.mImages = this.mImages;
            product.mBarcode = this.mBarcode;
            product.mID = this.mID;
            product.mCurrency = this.mCurrency;
            return product;
        }
    }
}
