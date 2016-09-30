package com.softranger.bayshopmf.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 9/29/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class AddressesList implements Parcelable {

    @JsonProperty("selectedAddress") private Address mSelectedAddress;
    @JsonProperty("addresses") private ArrayList<Address> mAddresses;

    protected AddressesList(Parcel in) {
        mSelectedAddress = in.readParcelable(Address.class.getClassLoader());
        mAddresses = in.createTypedArrayList(Address.CREATOR);
    }

    public static final Creator<AddressesList> CREATOR = new Creator<AddressesList>() {
        @Override
        public AddressesList createFromParcel(Parcel in) {
            return new AddressesList(in);
        }

        @Override
        public AddressesList[] newArray(int size) {
            return new AddressesList[size];
        }
    };

    public Address getSelectedAddress() {
        return mSelectedAddress;
    }

    public ArrayList<Address> getAddresses() {
        return mAddresses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mSelectedAddress, flags);
        dest.writeTypedList(mAddresses);
    }
}
