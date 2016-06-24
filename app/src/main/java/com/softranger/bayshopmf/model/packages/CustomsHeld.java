package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/14/16.
 */
public class CustomsHeld extends PUSParcel {

    private CustomsHeld() {

    }

    protected CustomsHeld(Parcel in) {
        super(in);
    }

    public static final Creator<CustomsHeld> CREATOR = new Creator<CustomsHeld>() {
        @Override
        public CustomsHeld createFromParcel(Parcel source) {
            return new CustomsHeld(source);
        }

        @Override
        public CustomsHeld[] newArray(int size) {
            return new CustomsHeld[size];
        }
    };

    public String getCustomsHeldTime() {
        return mCustomsHeldTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
