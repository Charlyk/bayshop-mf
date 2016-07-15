package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eduard on 28.04.16.
 */
public class InStockItem implements Parcelable {

    protected boolean isSelected;
    protected boolean hasDeclaration;
    protected double mPrice;
    protected double mWeight;
    protected String mName;
    protected String mTrackingNumber;
    protected String mDeposit;
    protected String mParcelId;
    protected int mID;
    protected String mCurrency;

    public InStockItem() {

    }

    protected InStockItem(Parcel in) {
        isSelected = in.readByte() != 0;
        mName = in.readString();
        mTrackingNumber = in.readString();
        hasDeclaration = in.readByte() != 0;
        mParcelId = in.readString();
        mID = in.readInt();
        mPrice = in.readDouble();
        mCurrency = in.readString();
        mWeight = in.readInt();
    }

    public static final Creator<InStockItem> CREATOR = new Creator<InStockItem>() {
        @Override
        public InStockItem createFromParcel(Parcel in) {
            return new InStockItem(in);
        }

        @Override
        public InStockItem[] newArray(int size) {
            return new InStockItem[size];
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        mTrackingNumber = trackingNumber;
    }

    public boolean isHasDeclaration() {
        return hasDeclaration;
    }

    public void setHasDeclaration(boolean hasDeclaration) {
        this.hasDeclaration = hasDeclaration;
    }

    public String getDeposit() {
        return mDeposit;
    }

    public void setDeposit(String deposit) {
        mDeposit = deposit;
    }

    public String getParcelId() {
        return mParcelId;
    }

    public void setParcelId(String parcelId) {
        mParcelId = parcelId;
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public double getWeight() {
        return mWeight;
    }

    public void setWeight(double weight) {
        mWeight = weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeString(mName);
        parcel.writeString(mTrackingNumber);
        parcel.writeByte((byte) (hasDeclaration ? 1 : 0));
        parcel.writeString(mParcelId);
        parcel.writeInt(mID);
        parcel.writeDouble(mPrice);
        parcel.writeString(mCurrency);
        parcel.writeDouble(mWeight);
    }

    public static class Builder {
        protected boolean isSelected;
        protected boolean hasDeclaration;
        protected String mName;
        protected String mTrackingNumber;
        protected String mDeposit;
        protected String mParcelId;
        protected int mID;
        protected double mPrice;
        protected String mCurrency;
        protected double mWeight;

        public Builder isSelected(boolean isSelected) {
            this.isSelected = isSelected;
            return this;
        }

        public Builder hasDeclaration(boolean hasDeclaration) {
            this.hasDeclaration = hasDeclaration;
            return this;
        }

        public Builder name(String name) {
            this.mName = name;
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

        public Builder parcelId(String parcelId) {
            mParcelId = parcelId;
            return this;
        }

        public Builder id(int id) {
            mID = id;
            return this;
        }

        public Builder price(double price) {
            mPrice = price;
            return this;
        }

        public Builder currency(String currency) {
            mCurrency = currency;
            return this;
        }

        public Builder weight(double weight) {
            mWeight = weight;
            return this;
        }

        public InStockItem build() {
            InStockItem inStockItem = new InStockItem();
            inStockItem.setName(mName);
            inStockItem.setTrackingNumber(mTrackingNumber);
            inStockItem.setSelected(this.isSelected);
            inStockItem.setHasDeclaration(this.hasDeclaration);
            inStockItem.setDeposit(mDeposit);
            inStockItem.mParcelId = this.mParcelId;
            inStockItem.mID = this.mID;
            inStockItem.mPrice = this.mPrice;
            inStockItem.mWeight = this.mWeight;
            inStockItem.mCurrency = this.mCurrency;
            return inStockItem;
        }
    }
}
