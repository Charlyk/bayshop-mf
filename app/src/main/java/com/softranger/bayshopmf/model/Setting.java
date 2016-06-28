package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by macbook on 6/28/16.
 */
public class Setting implements Parcelable {
    private String mTitle;
    @DrawableRes private int mIconId;
    private SettingType mSettingType;


    public Setting(String title, int iconId) {
        mTitle = title;
        mIconId = iconId;
    }

    protected Setting(Parcel in) {
        mTitle = in.readString();
        mIconId = in.readInt();
    }

    public static final Creator<Setting> CREATOR = new Creator<Setting>() {
        @Override
        public Setting createFromParcel(Parcel in) {
            return new Setting(in);
        }

        @Override
        public Setting[] newArray(int size) {
            return new Setting[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public int getIconId() {
        return mIconId;
    }

    public SettingType getSettingType() {
        return mSettingType;
    }

    public void setSettingType(SettingType settingType) {
        mSettingType = settingType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeInt(mIconId);
    }

    public static enum SettingType {
        USER_DATA, ADDRESSES, CHANGE_PASSWORD, REGIONAL_SETTINGS, AUTO_PACKAGING,
        NOTIFICATIONS, SUBSCRIBE, LOG_OUT;

        public static SettingType getSettingType(int position) {
            switch (position) {
                case 0: return USER_DATA;
                case 1: return ADDRESSES;
                case 2: return CHANGE_PASSWORD;
                case 3: return REGIONAL_SETTINGS;
                case 4: return AUTO_PACKAGING;
                case 5: return NOTIFICATIONS;
                case 6: return SUBSCRIBE;
                default: return LOG_OUT;
            }
        }
    }
}
