package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/14/16.
 */
public class InProcessing extends PUSParcel {

    public InProcessing() {

    }

    protected InProcessing(Parcel in) {
        super(in);
    }

    public static final Creator<InProcessing> CREATOR = new Creator<InProcessing>() {
        @Override
        public InProcessing createFromParcel(Parcel source) {
            return new InProcessing(source);
        }

        @Override
        public InProcessing[] newArray(int size) {
            return new InProcessing[size];
        }
    };

    public int getPercentage() {
        return mPercentage;
    }

    public void setPercentage(int percentage) {
        mPercentage = percentage;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}
