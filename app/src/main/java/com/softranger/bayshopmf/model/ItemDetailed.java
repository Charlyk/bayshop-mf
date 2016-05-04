package com.softranger.bayshopmf.model;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/4/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ItemDetailed extends Item {

    private ArrayList<String> mPhotoUrls;
    private String mDate;
    private String mWeight;
    private String mPrice;

    private ItemDetailed() {

    }

    public ItemDetailed(Parcel in) {
        super(in);
    }

    public static final Creator<ItemDetailed> CREATOR = new Creator<ItemDetailed>() {
        @Override
        public ItemDetailed createFromParcel(Parcel source) {
            return new ItemDetailed(source);
        }

        @Override
        public ItemDetailed[] newArray(int size) {
            return new ItemDetailed[size];
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

    public static class Builder extends Item.Builder {
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

        public ItemDetailed build() {
            ItemDetailed itemDetailed = new ItemDetailed();
            itemDetailed.setPhotoUrls(mPhotoUrls);
            itemDetailed.setDate(mDate);
            itemDetailed.setPrice(mPrice);
            itemDetailed.setWeight(mWeight);
            return itemDetailed;
        }
    }
}
