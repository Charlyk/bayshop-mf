package com.softranger.bayshopmf.model.payment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by Eduard Albu on 8/11/16, 08, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class PaymentMethod implements Parcelable {
    private String mName;
    private String mCommission;
    private String mCurrency;
    private String mAmount;
    @DrawableRes private int mImage;

    private PaymentMethod() {

    }

    protected PaymentMethod(Parcel in) {
        mName = in.readString();
        mCommission = in.readString();
        mCurrency = in.readString();
        mImage = in.readInt();
        mAmount = in.readString();
    }

    public static final Creator<PaymentMethod> CREATOR = new Creator<PaymentMethod>() {
        @Override
        public PaymentMethod createFromParcel(Parcel in) {
            return new PaymentMethod(in);
        }

        @Override
        public PaymentMethod[] newArray(int size) {
            return new PaymentMethod[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int image) {
        mImage = image;
    }

    public String getCommission() {
        return mCommission;
    }

    public void setCommission(String commission) {
        mCommission = commission;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mCommission);
        dest.writeString(mCurrency);
        dest.writeInt(mImage);
    }

    public static class Builder {
        private String mName;
        private String mCommission;
        private String mCurrency;
        private String mAmount;
        @DrawableRes private int mImage;

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder commission(String commission) {
            mCommission = commission;
            return this;
        }

        public Builder currency(String currency) {
            mCurrency = currency;
            return this;
        }

        public Builder image(@DrawableRes int imageId) {
            mImage = imageId;
            return this;
        }

        public Builder amount(String amount) {
            mAmount = amount;
            return this;
        }

        public PaymentMethod build() {
            PaymentMethod method = new PaymentMethod();
            method.mName = this.mName;
            method.mCommission = this.mCommission;
            method.mCurrency = this.mCurrency;
            method.mImage = this.mImage;
            method.mAmount = this.mAmount;
            return method;
        }
    }
}
