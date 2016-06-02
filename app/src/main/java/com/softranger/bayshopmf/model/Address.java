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
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mStreet;
    private String mCity;
    private String mCountry;
    private String mPostalCode;
    private String mPhoneNumber;
    private String mPhoneCode;
    private int mCountryId;
    private int mId;

    private Address() {

    }

    protected Address(Parcel in) {
        mClientName = in.readString();
        mFirstName = in.readString();
        mLastName = in.readString();
        mEmail = in.readString();
        mStreet = in.readString();
        mCity = in.readString();
        mCountry = in.readString();
        mPostalCode = in.readString();
        mPhoneNumber = in.readString();
        mPhoneCode = in.readString();
        mCountryId = in.readInt();
        mId = in.readInt();
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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhoneCode() {
        return mPhoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        mPhoneCode = phoneCode;
    }

    public int getCountryId() {
        return mCountryId;
    }

    public void setCountryId(int countryId) {
        mCountryId = countryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mClientName);
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeString(mEmail);
        dest.writeString(mStreet);
        dest.writeString(mCity);
        dest.writeString(mCountry);
        dest.writeString(mPostalCode);
        dest.writeString(mPhoneNumber);
        dest.writeString(mPhoneCode);
        dest.writeInt(mCountryId);
        dest.writeInt(mId);
    }

    public static class Builder {
        private String mClientName;
        private String mFirstName;
        private String mLastName;
        private String mEmail;
        private String mStreet;
        private String mCity;
        private String mCountry;
        private String mPostalCode;
        private String mPhoneNumber;
        private String mPhoneCode;
        private int mCountryId;
        private int mId;

        public Builder clientName(String clientName) {
            mClientName = clientName;
            return this;
        }

        public Builder firstName(String firstName) {
            mFirstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            mLastName = lastName;
            return this;
        }

        public Builder email(String email) {
            mEmail = email;
            return this;
        }

        public Builder street(String street) {
            mStreet = street;
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

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder phoneCode(String phoneCode) {
            mPhoneCode = phoneCode;
            return this;
        }

        public Builder countryId(int countryId) {
            mCountryId = countryId;
            return this;
        }

        public Address build() {
            Address address = new Address();
            address.mClientName = this.mClientName;
            address.mFirstName = this.mFirstName;
            address.mLastName = this.mLastName;
            address.mEmail = this.mEmail;
            address.mStreet = this.mStreet;
            address.mCity = this.mCity;
            address.mCountry = this.mCountry;
            address.mPostalCode = this.mPostalCode;
            address.mPhoneNumber = this.mPhoneNumber;
            address.mPhoneCode = this.mPhoneCode;
            address.mCountryId = this.mCountryId;
            address.mId = this.mId;
            return address;
        }
    }
}
