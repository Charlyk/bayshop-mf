package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 6/1/16, 06, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class PUSItem implements Parcelable {
    private int mId;
    private String mUid;
    private boolean mHasBattery;
    private ArrayList<InStockItem> mItems;

    private PUSItem() {

    }

    protected PUSItem(Parcel in) {
        mId = in.readInt();
        mUid = in.readString();
        mHasBattery = in.readByte() != 0;
        mItems = in.createTypedArrayList(InStockItem.CREATOR);
    }

    public static final Creator<PUSItem> CREATOR = new Creator<PUSItem>() {
        @Override
        public PUSItem createFromParcel(Parcel in) {
            return new PUSItem(in);
        }

        @Override
        public PUSItem[] newArray(int size) {
            return new PUSItem[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public boolean isHasBattery() {
        return mHasBattery;
    }

    public void setHasBattery(boolean hasBattery) {
        mHasBattery = hasBattery;
    }

    public ArrayList<InStockItem> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<InStockItem> items) {
        mItems = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mUid);
        dest.writeByte((byte) (mHasBattery ? 1 : 0));
        dest.writeTypedList(mItems);
    }

    public static class Builder {
        private int mId;
        private String mUid;
        private boolean mHasBattery;
        private ArrayList<InStockItem> mItems;

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder uid(String uid) {
            mUid = uid;
            return this;
        }

        public Builder hasBattery(int hasBattery) {
            mHasBattery = hasBattery == 1;
            return this;
        }

        public Builder items(ArrayList<InStockItem> items) {
            mItems = items;
            return this;
        }

        public PUSItem build() {
            PUSItem pusItem = new PUSItem();
            pusItem.mId = this.mId;
            pusItem.mUid = this.mUid;
            pusItem.mHasBattery = this.mHasBattery;
            pusItem.mItems = this.mItems;
            return pusItem;
        }
    }
}
