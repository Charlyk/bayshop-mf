package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/14/16.
 */
public class LivePackage extends Package {
    private String mCreatedDate;

    private LivePackage() {

    }

    protected LivePackage(Parcel in) {
        super(in);
        mCreatedDate = in.readString();
    }

    public static final Creator<LivePackage> CREATOR = new Creator<LivePackage>() {
        @Override
        public LivePackage createFromParcel(Parcel source) {
            return new LivePackage(source);
        }

        @Override
        public LivePackage[] newArray(int size) {
            return new LivePackage[size];
        }
    };

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mCreatedDate);
    }

    public static class Builder extends Package.Builder {
        private String mCreatedDate;

        public Builder createdDate(String createdDate) {
            mCreatedDate = createdDate;
            return this;
        }

        public LivePackage build() {
            LivePackage livePackage = new LivePackage();
            livePackage.mCreatedDate = this.mCreatedDate;
            livePackage.mCodeNumber = this.mCodeNumber;
            livePackage.mId = this.mId;
            livePackage.mName = this.mName;
            livePackage.mRealWeght = this.mRealWeght;
            livePackage.mDeposit = this.mDeposit;
            livePackage.mProducts = this.mProducts;
            livePackage.mCurrency = this.mCurrency;
            return livePackage;
        }
    }
}
