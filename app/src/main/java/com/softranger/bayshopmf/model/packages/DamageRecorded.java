package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by Eduard Albu on 8/10/16, 08, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class DamageRecorded extends PUSParcel {

    public DamageRecorded() {

    }

    public DamageRecorded(Parcel in) {
        super(in);
    }

    public static final Creator<DamageRecorded> CREATOR = new Creator<DamageRecorded>() {
        @Override
        public DamageRecorded createFromParcel(Parcel source) {
            return new DamageRecorded(source);
        }

        @Override
        public DamageRecorded[] newArray(int size) {
            return new DamageRecorded[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
