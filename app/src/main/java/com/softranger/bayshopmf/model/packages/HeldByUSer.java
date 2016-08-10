package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by Eduard Albu on 8/10/16, 08, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class HeldByUser extends PUSParcel {

    public HeldByUser() {

    }

    public HeldByUser(Parcel in) {
        super(in);
    }

    public static final Creator<HeldByUser> CREATOR = new Creator<HeldByUser>() {
        @Override
        public HeldByUser createFromParcel(Parcel source) {
            return new HeldByUser(source);
        }

        @Override
        public HeldByUser[] newArray(int size) {
            return new HeldByUser[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
