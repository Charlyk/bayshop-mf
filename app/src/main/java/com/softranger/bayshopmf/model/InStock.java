package com.softranger.bayshopmf.model;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class InStock extends Box {

    @JsonProperty("remainingDays") int mRemainingDays;

    public InStock() {
        // empty constructor for jackson
    }

    public InStock(Parcel in) {
        super(in);
        mRemainingDays = in.readInt();
    }

    public static final Creator<InStock> CREATOR = new Creator<InStock>() {
        @Override
        public InStock createFromParcel(Parcel parcel) {
            return new InStock(parcel);
        }

        @Override
        public InStock[] newArray(int i) {
            return new InStock[i];
        }
    };

    public int getRemainingDays() {
        return mRemainingDays;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(mRemainingDays);
    }
}
