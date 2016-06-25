package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/14/16.
 */
public class LocalDepot extends PUSParcel {

    public LocalDepot() {

    }

    protected LocalDepot(Parcel in) {
        super(in);
    }

    public static final Creator<LocalDepot> CREATOR = new Creator<LocalDepot>() {
        @Override
        public LocalDepot createFromParcel(Parcel source) {
            return new LocalDepot(source);
        }

        @Override
        public LocalDepot[] newArray(int size) {
            return new LocalDepot[size];
        }
    };

    public String getLocalDepotTime() {
        return mLocalDepotTime;
    }

    public void setLocalDepotTime(String localDepotTime) {
        mLocalDepotTime = localDepotTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
