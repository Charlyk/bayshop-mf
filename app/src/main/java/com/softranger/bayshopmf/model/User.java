package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by macbook on 6/29/16.
 */
public class User implements Parcelable {
    private String mFirstName;
    private String mLastName;
    private int mCountryId;
    private String mPhoneCode;
    private String mPhoneNumber;
    private int mLanguageId;
    private String mLanguageName;
    private String mCountryName;
    private ArrayList<Country> mCountries;
    private ArrayList<Language> mLanguages;
    private ArrayList<CountryCode> mCountryCodes;

    private User() {

    }

    protected User(Parcel in) {
        mFirstName = in.readString();
        mLastName = in.readString();
        mCountryId = in.readInt();
        mPhoneCode = in.readString();
        mPhoneNumber = in.readString();
        mLanguageId = in.readInt();
        mLanguageName = in.readString();
        mCountryName = in.readString();
        in.readTypedList(mCountries, Country.CREATOR);
        in.readTypedList(mLanguages, Language.CREATOR);
        in.readTypedList(mCountryCodes, CountryCode.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public int getCountryId() {
        return mCountryId;
    }

    public void setCountryId(int countryId) {
        mCountryId = countryId;
    }

    public String getPhoneCode() {
        return mPhoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        mPhoneCode = phoneCode;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public int getLanguageId() {
        return mLanguageId;
    }

    public void setLanguageId(int languageId) {
        mLanguageId = languageId;
    }

    public String getLanguageName() {
        return mLanguageName;
    }

    public void setLanguageName(String languageName) {
        mLanguageName = languageName;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public void setCountryName(String countryName) {
        mCountryName = countryName;
    }

    public ArrayList<Country> getCountries() {
        return mCountries;
    }

    public void setCountries(ArrayList<Country> countries) {
        mCountries = countries;
    }

    public ArrayList<Language> getLanguages() {
        return mLanguages;
    }

    public void setLanguages(ArrayList<Language> languages) {
        mLanguages = languages;
    }

    public ArrayList<CountryCode> getCountryCodes() {
        return mCountryCodes;
    }

    public void setCountryCodes(ArrayList<CountryCode> countryCodes) {
        mCountryCodes = countryCodes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeInt(mCountryId);
        dest.writeString(mPhoneCode);
        dest.writeString(mPhoneNumber);
        dest.writeInt(mLanguageId);
        dest.writeString(mLanguageName);
        dest.writeString(mCountryName);
        dest.writeTypedList(mCountries);
        dest.writeTypedList(mLanguages);
        dest.writeTypedList(mCountryCodes);
    }

    public static class Builder {
        private String mFirstName;
        private String mLastName;
        private int mCountryId;
        private String mPhoneCode;
        private String mPhoneNumber;
        private int mLanguageId;
        private String mLanguageName;
        private String mCountryName;
        private ArrayList<Country> mCountries;
        private ArrayList<Language> mLanguages;
        private ArrayList<CountryCode> mCountryCodes;

        public Builder firstName(String firstName) {
            mFirstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            mLastName = lastName;
            return this;
        }

        public Builder countryId(int countryId) {
            mCountryId = countryId;
            return this;
        }

        public Builder phoneCode(String phoneCode) {
            mPhoneCode = phoneCode;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            mPhoneNumber = phoneNumber;
            return this;
        }

        public Builder languageId(int languageId) {
            mLanguageId = languageId;
            return this;
        }

        public Builder languageName(String languageName) {
            mLanguageName = languageName;
            return this;
        }

        public Builder countryName(String countryName) {
            mCountryName = countryName;
            return this;
        }

        public Builder countries(ArrayList<Country> countries) {
            mCountries = countries;
            return this;
        }

        public Builder languages(ArrayList<Language> languages) {
            mLanguages = languages;
            return this;
        }

        public Builder countryCodes(ArrayList<CountryCode> countryCodes) {
            mCountryCodes = countryCodes;
            return this;
        }

        public User build() {
            User user = new User();
            user.mFirstName = this.mFirstName;
            user.mLastName = this.mLastName;
            user.mCountryId = this.mCountryId;
            user.mPhoneCode = this.mPhoneCode;
            user.mPhoneNumber = this.mPhoneNumber;
            user.mLanguageId = this.mLanguageId;
            user.mLanguageName = this.mLanguageName;
            user.mCountryName = this.mCountryName;
            user.mCountries = this.mCountries;
            user.mLanguages = this.mLanguages;
            user.mCountryCodes = this.mCountryCodes;
            return user;
        }
    }
}
