package com.softranger.bayshopmf.model.box;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/21/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class AwaitingArrivalDetails extends AwaitingArrival {

    @JsonProperty("currency") private String mCurrency;
    @JsonProperty("verificationPackageRequested") private int mVerificationRequested;
    @JsonProperty("verificationPackageRequestedComments") private String mVerificationComment;
    @JsonProperty("photosPackageRequested") private int mPhotoRequested;
    @JsonProperty("photosPackageRequestedComments") private String mPhotoComment;

    public AwaitingArrivalDetails() {
        // empty constructor for jackson
    }

    public AwaitingArrivalDetails(Parcel in) {
        super(in);
        mCurrency = in.readString();
        mVerificationRequested = in.readInt();
        mVerificationComment = in.readString();
        mPhotoRequested = in.readInt();
        mPhotoComment = in.readString();
    }

    public static final Creator<AwaitingArrivalDetails> CREATOR = new Creator<AwaitingArrivalDetails>() {
        @Override
        public AwaitingArrivalDetails createFromParcel(Parcel source) {
            return new AwaitingArrivalDetails(source);
        }

        @Override
        public AwaitingArrivalDetails[] newArray(int size) {
            return new AwaitingArrivalDetails[size];
        }
    };

    public String getCurrency() {
        return mCurrency;
    }

    public int getVerificationRequested() {
        return mVerificationRequested;
    }

    public String getVerificationComment() {
        return mVerificationComment;
    }

    public int getPhotoRequested() {
        return mPhotoRequested;
    }

    public String getPhotoComment() {
        return mPhotoComment;
    }

    @JsonIgnore
    public void setVerificationRequested(int verificationRequested) {
        mVerificationRequested = verificationRequested;
    }

    @JsonIgnore
    public void setPhotoRequested(int photoRequested) {
        mPhotoRequested = photoRequested;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mCurrency);
        parcel.writeInt(mVerificationRequested);
        parcel.writeString(mVerificationComment);
        parcel.writeInt(mPhotoRequested);
        parcel.writeString(mPhotoComment);
    }
}
