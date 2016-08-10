package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by Eduard Albu on 8/10/16, 08, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class Packing extends PUSParcel {

    public Packing() {

    }

    protected Packing(Parcel in) {
        super(in);
    }

    public static final Creator<Packing> CREATOR = new Creator<Packing>() {
        @Override
        public Packing createFromParcel(Parcel source) {
            return new Packing(source);
        }

        @Override
        public Packing[] newArray(int size) {
            return new Packing[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
