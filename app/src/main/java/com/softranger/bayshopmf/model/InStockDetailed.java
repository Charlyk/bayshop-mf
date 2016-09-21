package com.softranger.bayshopmf.model;

import android.os.Parcel;

import com.softranger.bayshopmf.model.product.Photo;
import com.softranger.bayshopmf.model.product.Product;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/4/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InStockDetailed extends InStockItem {

    private ArrayList<Photo> mPhotoUrls;
    private ArrayList<Product> mProducts;
    private String mDate;
    private String mDescription;
    private String mCurency;
    private int mPhotoInProgress;
    private int mCheckInProgress;

    public InStockDetailed() {

    }

    public InStockDetailed(Parcel in) {
        super(in);
        in.readList(mPhotoUrls, Photo.class.getClassLoader());
        mDate = in.readString();
        mDescription = in.readString();
        mCurency = in.readString();
        mPhotoInProgress = in.readInt();
        mCheckInProgress = in.readInt();
        in.readList(mProducts, Product.class.getClassLoader());
    }

    public static final Creator<InStockDetailed> CREATOR = new Creator<InStockDetailed>() {
        @Override
        public InStockDetailed createFromParcel(Parcel source) {
            return new InStockDetailed(source);
        }

        @Override
        public InStockDetailed[] newArray(int size) {
            return new InStockDetailed[size];
        }
    };

    public ArrayList<Photo> getPhotoUrls() {
        return mPhotoUrls;
    }

    public void setPhotoUrls(ArrayList<Photo> photoUrls) {
        mPhotoUrls = photoUrls;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCurency() {
        return mCurency;
    }

    public void setCurency(String curency) {
        mCurency = curency;
    }

    public int getPhotoInProgress() {
        return mPhotoInProgress;
    }

    public void setPhotoInProgress(int photoInProgress) {
        mPhotoInProgress = photoInProgress;
    }

    public int getCheckInProgress() {
        return mCheckInProgress;
    }

    public void setCheckInProgress(int checkInProgress) {
        mCheckInProgress = checkInProgress;
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    public void setProducts(ArrayList<Product> products) {
        mProducts = products;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeList(mPhotoUrls);
        parcel.writeString(mDate);
        parcel.writeString(mDescription);
        parcel.writeString(mCurency);
        parcel.writeInt(mPhotoInProgress);
        parcel.writeInt(mCheckInProgress);
        parcel.writeList(mProducts);
    }

    public static class Builder extends InStockItem.Builder {
        private ArrayList<Photo> mPhotoUrls;
        private String mDate;
        private String mCurency;
        private int mPhotoInProgress;
        private int mCheckInProgress;
        private ArrayList<Product> mProducts;

        public Builder photoUrls(ArrayList<Photo> photoUrls) {
            mPhotoUrls = photoUrls;
            return this;
        }

        public Builder date(String date) {
            mDate = date;
            return this;
        }

        public Builder price(double price) {
            mPrice = price;
            return this;
        }

        public Builder curency(String curency) {
            mCurency = curency;
            return this;
        }

        public Builder photoInProgress(int photoInProgress) {
            mPhotoInProgress = photoInProgress;
            return this;
        }

        public Builder checkInProgress(int checkInProgress) {
            mCheckInProgress = checkInProgress;
            return this;
        }

        public Builder products(ArrayList<Product> products) {
            mProducts = products;
            return this;
        }

        public InStockDetailed build() {
            InStockDetailed itemDetailed = new InStockDetailed();
            itemDetailed.setPhotoUrls(this.mPhotoUrls);
            itemDetailed.setDate(this.mDate);
            itemDetailed.setPrice(this.mPrice);
            itemDetailed.setTrackingNumber(this.mTrackingNumber);
            itemDetailed.setName(this.mName);
            itemDetailed.setDeposit(this.mDeposit);
            itemDetailed.setParcelId(this.mParcelId);
            itemDetailed.setCurency(this.mCurency);
            itemDetailed.setHasDeclaration(this.hasDeclaration);
            itemDetailed.setPhotoInProgress(this.mPhotoInProgress);
            itemDetailed.setCheckInProgress(this.mCheckInProgress);
            itemDetailed.setProducts(this.mProducts);
            itemDetailed.setID(this.mID);
            itemDetailed.setWeight(mWeight);
            return itemDetailed;
        }
    }
}
