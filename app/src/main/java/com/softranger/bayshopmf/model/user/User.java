package com.softranger.bayshopmf.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmf.model.address.Country;
import com.softranger.bayshopmf.model.address.CountryCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by macbook on 6/29/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Parcelable {
    @JsonProperty("uid") private String mUserId;
    @JsonProperty("access_token") private String mToken;
    @JsonProperty("id") private String mId;
    @JsonProperty("name") private String mFirstName;
    @JsonProperty("surname") private String mLastName;
    @JsonProperty("countryId") private int mCountryId;
    @JsonProperty("phoneCode") private String mPhoneCode;
    @JsonProperty("phone") private String mPhoneNumber;
    @JsonProperty("languageId")
    private int mLanguageId;
    @JsonIgnore private String mLanguageName;
    @JsonIgnore private String mCountryName;
    @JsonProperty("countries") private ArrayList<Country> mCountries;
    @JsonIgnore private ArrayList<Language> mLanguages;
    @JsonProperty("languages")
    private HashMap<Integer, String> mLanguagesMap;
    @JsonProperty("phoneFormats") private ArrayList<CountryCode> mCountryCodes;
    @JsonProperty("balance")
    private HashMap<String, Double> mBalanceMap;

    private User() {

    }

    protected User(Parcel in) {
        mUserId = in.readString();
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
        mToken = in.readString();
        mId = in.readString();
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

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
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

    public String getFullName() {
        return mFirstName + " " + mLastName;
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
        mLanguages = new ArrayList<>();
        for (Map.Entry<Integer, String> set : mLanguagesMap.entrySet()) {
            Language language = new Language.Builder()
                    .id(set.getKey())
                    .name(set.getValue())
                    .build();
            mLanguages.add(language);
        }
        return mLanguages;
    }

    public ArrayList<CountryCode> getCountryCodes() {
        return mCountryCodes;
    }

    public void setCountryCodes(ArrayList<CountryCode> countryCodes) {
        mCountryCodes = countryCodes;
    }

    public String getToken() {
        return mToken;
    }

    public String getId() {
        return mId;
    }

    public HashMap<String, Double> getBalanceMap() {
        return mBalanceMap;
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
        dest.writeString(mToken);
        dest.writeString(mId);
    }
}
