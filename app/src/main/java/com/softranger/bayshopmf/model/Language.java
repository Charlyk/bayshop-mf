package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.softranger.bayshopmf.util.SpinnerObj;

/**
 * Created by macbook on 6/29/16.
 */
public class Language implements Parcelable, SpinnerObj {

    private int mId;
    private String mName;

    private Language() {

    }

    protected Language(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
    }

    public static final Creator<Language> CREATOR = new Creator<Language>() {
        @Override
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };

    @Override
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
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

    public static class Builder {
        private int mId;
        private String mName;

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Language build() {
            Language language = new Language();
            language.mId = this.mId;
            language.mName = this.mName;
            return language;
        }
    }
}
