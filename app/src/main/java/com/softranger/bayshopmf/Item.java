package com.softranger.bayshopmf;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eduard on 28.04.16.
 */
public class Item implements Parcelable {

    private boolean isSelected;

    public Item() {

    }

    protected Item(Parcel in) {
        isSelected = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }
}
