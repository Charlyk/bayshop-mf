package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

import java.lang.*;

/**
 * Created by macbook on 6/14/16.
 */
public class Sent extends PUSParcel {

    public Sent() {

    }

    protected Sent(Parcel in) {
        super(in);
    }

    public static final Creator<Sent> CREATOR = new Creator<Sent>() {
        @Override
        public Sent createFromParcel(Parcel source) {
            return new Sent(source);
        }

        @Override
        public Sent[] newArray(int size) {
            return new Sent[size];
        }
    };

    public String getSentTime() {
        return mSentTime;
    }

    public void setSentTime(String sentTime) {
        mSentTime = sentTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
