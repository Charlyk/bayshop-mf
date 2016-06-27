package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

import java.lang.*;

/**
 * Created by macbook on 6/14/16.
 */
public class Packed extends PUSParcel {

    public Packed() {

    }

    protected Packed(Parcel in) {
        super(in);
    }

    public static final Creator<Packed> CREATOR = new Creator<Packed>() {
        @Override
        public Packed createFromParcel(Parcel source) {
            return new Packed(source);
        }

        @Override
        public Packed[] newArray(int size) {
            return new Packed[size];
        }
    };

    public String getPackedTime() {
        return mPackedTime;
    }

    public void setPackedTime(String packedTime) {
        mPackedTime = packedTime;
    }

    public int getPercentage() {
        return mPercentage;
    }

    public void setPercentage(int percentage) {
        mPercentage = percentage;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
