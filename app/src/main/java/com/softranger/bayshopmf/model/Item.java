package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eduard on 28.04.16.
 */
public class Item implements Parcelable {

    private boolean isSelected;
    private boolean hasDeclaration;
    private String mName;
    private String mTrackingNumber;

    public Item() {

    }

    protected Item(Parcel in) {
        isSelected = in.readByte() != 0;
        mName = in.readString();
        mTrackingNumber = in.readString();
        hasDeclaration = in.readByte() != 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
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
    }

    public static class Builder {
        private boolean isSelected;
        private boolean hasDeclaration;
        private String mName;
        private String mTrackingNumber;

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

        public Item build() {
            Item item = new Item();
            item.setName(mName);
            item.setTrackingNumber(mTrackingNumber);
            item.setSelected(this.isSelected);
            item.setHasDeclaration(this.hasDeclaration);
            return item;
        }
    }
}
