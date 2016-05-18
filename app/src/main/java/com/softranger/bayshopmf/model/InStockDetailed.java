package com.softranger.bayshopmf.model;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/4/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InStockDetailed extends InStockItem {

    private ArrayList<Photo> mPhotoUrls;
    private String mDate;
    private String mWeight;
    private String mPrice;
    private String mDescription;
    private String mCurency;
    private int mPhotoInProgress;
    private int mCheckInProgress;

    private InStockDetailed() {

    }

    public InStockDetailed(Parcel in) {
        super(in);
        in.readList(mPhotoUrls, Photo.class.getClassLoader());
        mDate = in.readString();
        mWeight = in.readString();
        mPrice = in.readString();
        mDescription = in.readString();
        mCurency = in.readString();
        mPhotoInProgress = in.readInt();
        mCheckInProgress = in.readInt();
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeList(mPhotoUrls);
        parcel.writeString(mDate);
        parcel.writeString(mWeight);
        parcel.writeString(mPrice);
        parcel.writeString(mDescription);
        parcel.writeString(mCurency);
        parcel.writeInt(mPhotoInProgress);
        parcel.writeInt(mCheckInProgress);
    }

    public static class Builder extends InStockItem.Builder {
        private ArrayList<Photo> mPhotoUrls;
        private String mDate;
        private String mWeight;
        private String mPrice;
        private String mCurency;
        private int mPhotoInProgress;
        private int mCheckInProgress;

        public Builder photoUrls(ArrayList<Photo> photoUrls) {
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

        public InStockDetailed build() {
            InStockDetailed itemDetailed = new InStockDetailed();
            itemDetailed.setPhotoUrls(mPhotoUrls);
            itemDetailed.setDate(mDate);
            itemDetailed.setPrice(mPrice);
            itemDetailed.setWeight(mWeight);
            itemDetailed.setTrackingNumber(mTrackingNumber);
            itemDetailed.setName(mName);
            itemDetailed.setDeposit(mDeposit);
            itemDetailed.setCurency(mCurency);
            itemDetailed.setHasDeclaration(hasDeclaration);
            itemDetailed.setPhotoInProgress(mPhotoInProgress);
            itemDetailed.setCheckInProgress(mCheckInProgress);
            return itemDetailed;
        }
    }
}
