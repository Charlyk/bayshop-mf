package com.softranger.bayshopmf.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.softranger.bayshopmf.util.CountriesDeserializer;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Eduard Albu on 5/13/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class Address implements Parcelable {
    private String mClientName;
    @JsonProperty("id") private int mId;
    @JsonProperty("first_name") private String mFirstName;
    @JsonProperty("last_name") private String mLastName;
    @JsonProperty("shipping_email") private String mEmail;
    @JsonProperty("address") private String mStreet;
    @JsonProperty("city") private String mCity;
    @JsonProperty("state") private String mState;
    @JsonProperty("zip") private String mPostalCode;
    @JsonProperty("shipping_phone_code") private String mPhoneCode;
    @JsonProperty("phone") private String mPhoneNumber;
    @JsonProperty("countryId") private int mCountryId;
    @JsonProperty("country") private String mCountry;

    private boolean mIsInFavorites;

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
        mState = in.readString();
        mId = in.readInt();
        mIsInFavorites = in.readByte() != 0;
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
        return mFirstName + " " + mLastName;
    }

    public String getStreet() {
        return mStreet;
    }

    @JsonSetter("shipping_address")
    public void setStreet(String street) {
        mStreet = street;
    }

    public String getCity() {
        return mCity;
    }

    @JsonSetter("shipping_city")
    public void setCity(String city) {
        mCity = city;
    }

    public String getCountry() {
        return mCountry;
    }

    @JsonSetter("countryTitle")
    public void setCountry(String country) {
        mCountry = country;
    }

    @JsonSetter("shipping_country")
    public void setShippingCountry(String shippingCountry) {
        mCountry = shippingCountry;
    }

    public String getPostalCode() {
        return mPostalCode;
    }

    @JsonSetter("shipping_zip")
    public void setPostalCode(String postalCode) {
        mPostalCode = postalCode;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @JsonSetter("shipping_phone")
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

    @JsonSetter("shipping_first_name")
    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    @JsonSetter("shipping_last_name")
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

    public boolean isInFavorites() {
        return mIsInFavorites;
    }

    public void setInFavorites(boolean inFavorites) {
        mIsInFavorites = inFavorites;
    }

    public String getState() {
        return mState;
    }

    @JsonSetter("shipping_state")
    public void setState(String state) {
        mState = state;
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
        dest.writeString(mState);
        dest.writeInt(mId);
        dest.writeByte((byte) (mIsInFavorites ? 1 : 0));
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
        private int mId = -1;
        private boolean mIsInFavorites;
        private String mState;

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

        public Builder isInFavorites(boolean isInFavorites) {
            mIsInFavorites = isInFavorites;
            return this;
        }

        public Builder state(String state) {
            mState = state;
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
            address.mIsInFavorites = this.mIsInFavorites;
            address.mState = this.mState;
            return address;
        }
    }

    @JsonDeserialize(using = CountriesDeserializer.class)
    public static class AddressCountries implements Parcelable {
        private int mId;
        private String mName;

        protected AddressCountries(Parcel in) {
            mId = in.readInt();
            mName = in.readString();
        }

        public static final Creator<AddressCountries> CREATOR = new Creator<AddressCountries>() {
            @Override
            public AddressCountries createFromParcel(Parcel in) {
                return new AddressCountries(in);
            }

            @Override
            public AddressCountries[] newArray(int size) {
                return new AddressCountries[size];
            }
        };

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mId);
            dest.writeString(mName);
        }
    }
}
