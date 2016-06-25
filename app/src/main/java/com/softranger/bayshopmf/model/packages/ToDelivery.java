package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

import java.lang.*;

/**
 * Created by macbook on 6/14/16.
 */
public class ToDelivery extends PUSParcel {

    public ToDelivery() {

    }

    protected ToDelivery(Parcel in) {
        super(in);
    }

    public static final Creator<ToDelivery> CREATOR = new Creator<ToDelivery>() {
        @Override
        public ToDelivery createFromParcel(Parcel source) {
            return new ToDelivery(source);
        }

        @Override
        public ToDelivery[] newArray(int size) {
            return new ToDelivery[size];
        }
    };

    public String getTakenToDeliveryTime() {
        return mTakenToDeliveryTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}
