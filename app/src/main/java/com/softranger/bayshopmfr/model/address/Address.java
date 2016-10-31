package com.softranger.bayshopmfr.model.address;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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

    public Address() {

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

    @JsonSetter("country_title")
    public void setCountryTitle(String countryTitle) {
        mCountry = countryTitle;
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

    @JsonIgnore
    public Address setId(int id) {
        mId = id;
        return this;
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

    @JsonSetter("country_id")
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Address && getId() == ((Address) obj).getId();
    }

    public static class AddressAction {
        @StringRes
        private int title;
        @DrawableRes
        private int iconId;

        public AddressAction(@StringRes int title, @DrawableRes int iconId) {
            this.title = title;
            this.iconId = iconId;
        }

        @StringRes
        public int getTitle() {
            return title;
        }

        @DrawableRes
        public int getIconId() {
            return iconId;
        }
    }
}
