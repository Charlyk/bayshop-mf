package com.softranger.bayshopmf.model;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/4/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InStockDetailed extends InStockItem {

    private ArrayList<String> mPhotoUrls;
    private String mDate;
    private String mWeight;
    private String mPrice;
    private String mDescription;

    private InStockDetailed() {

    }

    public InStockDetailed(Parcel in) {
        super(in);
        in.readStringList(mPhotoUrls);
        mDate = in.readString();
        mWeight = in.readString();
        mPrice = in.readString();
        mDescription = in.readString();
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

    public ArrayList<String> getPhotoUrls() {
        return mPhotoUrls;
    }

    public void setPhotoUrls(ArrayList<String> photoUrls) {
        mPhotoUrls = photoUrls;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        mWeight = weight;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeStringList(mPhotoUrls);
        parcel.writeString(mDate);
        parcel.writeString(mWeight);
        parcel.writeString(mPrice);
        parcel.writeString(mDescription);
    }

    public static class Builder extends InStockItem.Builder {
        private ArrayList<String> mPhotoUrls;
        private String mDate;
        private String mWeight;
        private String mPrice;

        public Builder photoUrls(ArrayList<String> photoUrls) {
            mPhotoUrls = photoUrls;
            return this;
        }

        public Builder date(String date) {
            mDate = date;
            return this;
        }

        public Builder weight(String weight) {
            mWeight = weight;
            return this;
        }

        public Builder price(String price) {
            mPrice = price;
            return this;
        }

        public InStockDetailed build() {
            InStockDetailed itemDetailed = new InStockDetailed();
            itemDetailed.setPhotoUrls(mPhotoUrls);
            itemDetailed.setDate(mDate);
            itemDetailed.setPrice(mPrice);
            itemDetailed.setWeight(mWeight);
            itemDetailed.setTrackingNumber(mTrackingNumber);
            itemDetailed.setName(mName);
            itemDetailed.setDeposit(mDeposit);
            return itemDetailed;
        }
    }
}
