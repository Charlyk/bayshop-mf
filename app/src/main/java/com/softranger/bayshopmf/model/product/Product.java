package com.softranger.bayshopmf.model.product;

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
    private String mOrderStorageId;
    private String mWeight;
    private String mDeclarationId;
    private int mID;
    private ArrayList<Photo> mImages;
    private boolean mIsCheckInProgress;
    private boolean mIsPhotoInProgress;
    private String mCheckComment;
    private String mPhotoComment;

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
        mOrderStorageId = in.readString();
        mWeight = in.readString();
        mDeclarationId = in.readString();
        mID = in.readInt();
        in.readList(mImages, Integer.class.getClassLoader());
        mIsCheckInProgress = in.readByte() != 0;
        mIsPhotoInProgress = in.readByte() != 0;
        mCheckComment = in.readString();
        mPhotoComment = in.readString();
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

    public String getOrderStorageId() {
        return mOrderStorageId;
    }

    public void setOrderStorageId(String orderStorageId) {
        mOrderStorageId = orderStorageId;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        mWeight = weight;
    }

    public String getDeclarationId() {
        return mDeclarationId;
    }

    public void setDeclarationId(String declarationId) {
        mDeclarationId = declarationId;
    }

    public boolean isCheckInProgress() {
        return mIsCheckInProgress;
    }

    public void setCheckInProgress(boolean checkInProgress) {
        mIsCheckInProgress = checkInProgress;
    }

    public boolean isPhotoInProgress() {
        return mIsPhotoInProgress;
    }

    public void setPhotoInProgress(boolean photoInProgress) {
        mIsPhotoInProgress = photoInProgress;
    }

    public String getCheckComment() {
        return mCheckComment;
    }

    public void setCheckComment(String checkComment) {
        mCheckComment = checkComment;
    }

    public String getPhotoComment() {
        return mPhotoComment;
    }

    public void setPhotoComment(String photoComment) {
        mPhotoComment = photoComment;
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
        dest.writeString(mOrderStorageId);
        dest.writeString(mWeight);
        dest.writeString(mDeclarationId);
        dest.writeInt(mID);
        dest.writeList(mImages);
        dest.writeByte((byte) (mIsCheckInProgress ? 1 : 0));
        dest.writeByte((byte) (mIsPhotoInProgress ? 1 : 0));
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
        private String mOrderStorageId;
        private String mWeight;
        private String mDeclarationId;
        private int mID;
        private ArrayList<Photo> mImages;
        private boolean mIsCheckInProgress;
        private boolean mIsPhotoInProgress;
        private String mCheckComment;
        private String mPhotoComment;

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

        public Builder orderStorageId(String orderStorageId) {
            mOrderStorageId = orderStorageId;
            return this;
        }

        public Builder weight(String weight) {
            mWeight = weight;
            return this;
        }

        public Builder declarationId(String declarationId) {
            mDeclarationId = declarationId;
            return this;
        }

        public Builder isCheckInProgress(boolean isCheckInProgress) {
            mIsCheckInProgress = isCheckInProgress;
            return this;
        }

        public Builder isPhotoInProgress(boolean isPhotoInProgress) {
            mIsPhotoInProgress = isPhotoInProgress;
            return this;
        }

        public Builder checkComment(String checkComment) {
            mCheckComment = checkComment;
            return this;
        }

        public Builder photoComment(String photoComment) {
            mPhotoComment = photoComment;
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
            product.mOrderStorageId = this.mOrderStorageId;
            product.mWeight = this.mWeight;
            product.mDeclarationId = this.mDeclarationId;
            product.mIsCheckInProgress = this.mIsCheckInProgress;
            product.mIsPhotoInProgress = this.mIsPhotoInProgress;
            product.mPhotoComment = this.mPhotoComment;
            product.mCheckComment = this.mCheckComment;
            return product;
        }
    }
}
