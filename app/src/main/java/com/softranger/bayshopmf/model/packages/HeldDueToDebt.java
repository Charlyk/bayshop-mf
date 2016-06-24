package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/24/16.
 */
public class HeldDueToDebt extends PUSParcel {

    private HeldDueToDebt() {

    }

    public HeldDueToDebt(Parcel in) {
        super(in);
    }

    public static final Creator<HeldDueToDebt> CREATOR = new Creator<HeldDueToDebt>() {
        @Override
        public HeldDueToDebt createFromParcel(Parcel source) {
            return new HeldDueToDebt(source);
        }

        @Override
        public HeldDueToDebt[] newArray(int size) {
            return new HeldDueToDebt[size];
        }
    };
}
