package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by Eduard Albu on 8/10/16, 08, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class AwaitingDeclaration extends PUSParcel {

    public AwaitingDeclaration() {

    }

    protected AwaitingDeclaration(Parcel in) {
        super(in);
    }

    public static final Creator<AwaitingDeclaration> CREATOR = new Creator<AwaitingDeclaration>() {
        @Override
        public AwaitingDeclaration createFromParcel(Parcel source) {
            return new AwaitingDeclaration(source);
        }

        @Override
        public AwaitingDeclaration[] newArray(int size) {
            return new AwaitingDeclaration[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
