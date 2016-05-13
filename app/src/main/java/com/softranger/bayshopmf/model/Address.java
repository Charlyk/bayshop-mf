package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class Address implements Parcelable {
    private String mClientName;
    private String mStreet;
    private String mBuildingNumber;
    private String mCity;
    private String mCountry;
    private String mPostalCode;
    private String mPhoneNumber;

    private Address() {

    }

    protected Address(Parcel in) {
        mClientName = in.readString();
        mStreet = in.readString();
        mBuildingNumber = in.readString();
        mCity = in.readString();
        mCountry = in.readString();
        mPostalCode = in.readString();
        mPhoneNumber = in.readString();
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public String getClientName() {
        return mClientName;
    }

    public void setClientName(String clientName) {
        mClientName = clientName;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public String getBuildingNumber() {
        return mBuildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        mBuildingNumber = buildingNumber;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getPostalCode() {
        return mPostalCode;
    }

    public void setPostalCode(String postalCode) {
        mPostalCode = postalCode;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mClientName);
        dest.writeString(mStreet);
        dest.writeString(mBuildingNumber);
        dest.writeString(mCity);
        dest.writeString(mCountry);
        dest.writeString(mPostalCode);
        dest.writeString(mPhoneNumber);
    }

    public static class Builder {
        private String mClientName;
        private String mStreet;
        private String mBuildingNumber;
        private String mCity;
        private String mCountry;
        private String mPostalCode;
        private String mPhoneNumber;

        public Builder clientName(String clientName) {
            mClientName = clientName;
            return this;
        }

        public Builder street(String street) {
            mStreet = street;
            return this;
        }

        public Builder buildingNumber(String buildingNumber) {
            mBuildingNumber = buildingNumber;
            return this;
        }

        public Builder city(String city) {
            mCity = city;
            return this;
        }

        public Builder country(String country) {
            mCountry = country;
            return this;
        }

        public Builder postalCode(String postalCode) {
            mPostalCode = postalCode;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            mPhoneNumber = phoneNumber;
            return this;
        }

        public Address build() {
            Address address = new Address();
            address.mClientName = this.mClientName;
            address.mStreet = this.mStreet;
            address.mBuildingNumber = this.mBuildingNumber;
            address.mCity = this.mCity;
            address.mCountry = this.mCountry;
            address.mPostalCode = this.mPostalCode;
            address.mPhoneNumber = this.mPhoneNumber;
            return address;
        }
    }
}
