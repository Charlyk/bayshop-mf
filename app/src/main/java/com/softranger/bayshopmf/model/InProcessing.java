package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 5/31/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InProcessing implements Parcelable {
    private int mId;
    private String mCodeNumber;
    private String mName;
    private String mCreatedDate;
    private int mWeight;
    private int mProgress;

    private InProcessing() {

    }

    protected InProcessing(Parcel in) {
        mId = in.readInt();
        mCodeNumber = in.readString();
        mName = in.readString();
        mWeight = in.readInt();
        mProgress = in.readInt();
    }

    public static final Creator<InProcessing> CREATOR = new Creator<InProcessing>() {
        @Override
        public InProcessing createFromParcel(Parcel in) {
            return new InProcessing(in);
        }

        @Override
        public InProcessing[] newArray(int size) {
            return new InProcessing[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCodeNumber() {
        return mCodeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        mCodeNumber = codeNumber;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mCodeNumber);
        dest.writeString(mName);
        dest.writeInt(mWeight);
        dest.writeInt(mProgress);
    }

    public static class Builder {
        private int mId;
        private String mCodeNumber;
        private String mName;
        private int mWeight;
        private int mProgress;
        private String mCreatedDate;

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder codeNumber(String codeNumber) {
            mCodeNumber = codeNumber;
            return this;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder weight(int weight) {
            mWeight = weight;
            return this;
        }

        public Builder progress(int progress) {
            mProgress = progress;
            return this;
        }

        public Builder createdDate(String createdDate) {
            mCreatedDate = createdDate;
            return this;
        }

        public InProcessing build() {
            InProcessing inProcessing = new InProcessing();
            inProcessing.mId = this.mId;
            inProcessing.mCodeNumber = this.mCodeNumber;
            inProcessing.mName = this.mName;
            inProcessing.mWeight = this.mWeight;
            inProcessing.mProgress = this.mProgress;
            inProcessing.mCreatedDate = this.mCreatedDate;
            return inProcessing;
        }
    }
}
