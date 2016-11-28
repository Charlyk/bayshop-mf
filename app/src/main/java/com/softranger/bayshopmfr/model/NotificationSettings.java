package com.softranger.bayshopmfr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 11/1/16, 11, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationSettings implements Parcelable {
    @JsonProperty("alertOnSystem")
    private int mAlertOnSystem;
    @JsonProperty("obtainSms")
    private int mObtainSms;
    @JsonProperty("obtainGcm")
    private int mObtainGCM;
    @JsonProperty("obtainMails")
    private int mObtainMails;

    public NotificationSettings() {

    }

    protected NotificationSettings(Parcel in) {
        mAlertOnSystem = in.readInt();
        mObtainSms = in.readInt();
        mObtainGCM = in.readInt();
        mObtainMails = in.readInt();
    }

    public static final Creator<NotificationSettings> CREATOR = new Creator<NotificationSettings>() {
        @Override
        public NotificationSettings createFromParcel(Parcel in) {
            return new NotificationSettings(in);
        }

        @Override
        public NotificationSettings[] newArray(int size) {
            return new NotificationSettings[size];
        }
    };

    public int getAlertOnSystem() {
        return mAlertOnSystem;
    }

    public int getObtainSms() {
        return mObtainSms;
    }

    public int getObtainGCM() {
        return mObtainGCM;
    }

    public int getObtainMails() {
        return mObtainMails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mAlertOnSystem);
        parcel.writeInt(mObtainSms);
        parcel.writeInt(mObtainGCM);
        parcel.writeInt(mObtainMails);
    }
}
