package com.softranger.bayshopmf.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmf.util.Imageble;

/**
 * Created by macbook on 6/17/16.
 */
public class CountryCode implements Parcelable, Imageble {
    @JsonProperty("id") int mId;
    @JsonProperty("countryId") int mCountryId;
    @JsonProperty("code") String mCode;
    @JsonProperty("format") String mFormat;
    @JsonProperty("flag") String mFlagUrl;
    @JsonProperty("countryCode") String mCountryCode;
    @JsonProperty("title") String mName;
    private Bitmap mFlagImage;

    private CountryCode() {

    }

    protected CountryCode(Parcel in) {
        mId = in.readInt();
        mCountryId = in.readInt();
        mCode = in.readString();
        mFormat = in.readString();
        mFlagUrl = in.readString();
        mCountryCode = in.readString();
        mName = in.readString();
        mFlagImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<CountryCode> CREATOR = new Creator<CountryCode>() {
        @Override
        public CountryCode createFromParcel(Parcel in) {
            return new CountryCode(in);
        }

        @Override
        public CountryCode[] newArray(int size) {
            return new CountryCode[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getCountryId() {
        return mCountryId;
    }

    public void setCountryId(int countryId) {
        mCountryId = countryId;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        mFormat = format;
    }

    public void setFlagUrl(String flagUrl) {
        mFlagUrl = "http://md.bay-dev.tk" + flagUrl;;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
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
        dest.writeInt(mCountryId);
        dest.writeString(mCode);
        dest.writeString(mFormat);
        dest.writeString(mFlagUrl);
        dest.writeString(mCountryCode);
        dest.writeString(mName);
        dest.writeParcelable(mFlagImage, flags);
    }

    @Override
    public void setImage(Bitmap bitmap) {
        mFlagImage = bitmap;
    }

    @Override
    public Bitmap getImage() {
        return mFlagImage;
    }

    @Override
    public String getImageUrl() {
        return mFlagUrl;
    }

    public static class Builder {
        private int mId;
        private int mCountryId;
        private String mCode;
        private String mFormat;
        private String mFlagUrl;
        private String mCountryCode;
        private String mName;
        private Bitmap mFlagImage;

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder countryId(int countryId) {
            mCountryId = countryId;
            return this;
        }

        public Builder code(String code) {
            mCode = code;
            return this;
        }

        public Builder format(String format) {
            mFormat = format;
            return this;
        }

        public Builder flagUrl(String flagUrl) {
            mFlagUrl = "http://md.bay-dev.tk" + flagUrl;
            return this;
        }

        public Builder countryCode(String countryCode) {
            mCountryCode = countryCode;
            return this;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder flagImage(Bitmap flagImage) {
            mFlagImage = flagImage;
            return this;
        }

        public CountryCode build() {
            CountryCode countryCode = new CountryCode();
            countryCode.mId = this.mId;
            countryCode.mCountryId = this.mCountryId;
            countryCode.mCode = this.mCode;
            countryCode.mFormat = this.mFormat;
            countryCode.mFlagUrl = this.mFlagUrl;
            countryCode.mCountryCode = this.mCountryCode;
            countryCode.mName = this.mName;
            countryCode.mFlagImage = this.mFlagImage;
            return countryCode;
        }
    }
}
