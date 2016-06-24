package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/24/16.
 */
public class Prohibited extends PUSParcel {

    public Prohibited() {

    }

    public Prohibited(Parcel in) {
        super(in);
    }

    public static final Creator<Prohibited> CREATOR = new Creator<Prohibited>() {
        @Override
        public Prohibited createFromParcel(Parcel source) {
            return new Prohibited(source);
        }

        @Override
        public Prohibited[] newArray(int size) {
            return new Prohibited[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
