package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

import java.lang.*;

/**
 * Created by macbook on 6/14/16.
 */
public class Received extends PUSParcel {

    public Received() {

    }

    protected Received(Parcel in) {
        super(in);
    }

    public static final Creator<Received> CREATOR = new Creator<Received>() {
        @Override
        public Received createFromParcel(Parcel source) {
            return new Received(source);
        }

        @Override
        public Received[] newArray(int size) {
            return new Received[size];
        }
    };

    public String getReceivedTime() {
        return mReceivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        mReceivedTime = receivedTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
