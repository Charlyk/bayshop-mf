package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/12/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InProcessingParcel extends BayParcel {

    private String mGoodsPrice;
    private String mCustomsClearance;
    private String mShippingPrice;
    private String mTotalPrice;
    private int mProcessingProgress;

    private InProcessingParcel(){

    }

    protected InProcessingParcel(Parcel in) {
        super(in);
        mGoodsPrice = in.readString();
        mCustomsClearance = in.readString();
        mShippingPrice = in.readString();
        mTotalPrice = in.readString();
        mProcessingProgress = in.readInt();
    }

    public static final Creator<InProcessingParcel> CREATOR = new Creator<InProcessingParcel>() {
        @Override
        public InProcessingParcel createFromParcel(Parcel in) {
            return new InProcessingParcel(in);
        }

        @Override
        public InProcessingParcel[] newArray(int size) {
            return new InProcessingParcel[size];
        }
    };

    public String getGoodsPrice() {
        return mGoodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        mGoodsPrice = goodsPrice;
    }

    public String getCustomsClearance() {
        return mCustomsClearance;
    }

    public void setCustomsClearance(String customsClearance) {
        mCustomsClearance = customsClearance;
    }

    public String getShippingPrice() {
        return mShippingPrice;
    }

    public void setShippingPrice(String shippingPrice) {
        mShippingPrice = shippingPrice;
    }

    public String getTotalPrice() {
        return mTotalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        mTotalPrice = totalPrice;
    }

    public int getProcessingProgress() {
        return mProcessingProgress;
    }

    public void setProcessingProgress(int processingProgress) {
        mProcessingProgress = processingProgress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mGoodsPrice);
        dest.writeString(mCustomsClearance);
        dest.writeString(mShippingPrice);
        dest.writeString(mTotalPrice);
        dest.writeInt(mProcessingProgress);
    }

    public static class Builder extends BayParcel.Builder {
        private String mGoodsPrice;
        private String mCustomsClearance;
        private String mShippingPrice;
        private String mTotalPrice;
        private int mProcessingProgress;

        public Builder goodsPrice(String goodsPrice) {
            mGoodsPrice = goodsPrice;
            return this;
        }

        public Builder customsClearance(String customsClearance) {
            mCustomsClearance = customsClearance;
            return this;
        }

        public Builder shippingPrice(String shippingPrice) {
            mShippingPrice = shippingPrice;
            return this;
        }

        public Builder totalPrice(String totalPrice) {
            mTotalPrice = totalPrice;
            return this;
        }

        public Builder processingProgress(int processingProgress) {
            mProcessingProgress = processingProgress;
            return this;
        }

        public InProcessingParcel build() {
            InProcessingParcel processingParcel = new InProcessingParcel();
            processingParcel.mParcelId = this.mParcelId;
            processingParcel.mParcelName = this.mProductName;
            processingParcel.mCreatedDate = this.mCreatedDate;
            processingParcel.mWeight = this.mWeight;
            processingParcel.mDeposit = this.mDeposit;
            processingParcel.mShippingBy = this.mShippingBy;
            processingParcel.mGoodsPrice = this.mGoodsPrice;
            processingParcel.mCustomsClearance = this.mCustomsClearance;
            processingParcel.mShippingPrice = this.mShippingPrice;
            processingParcel.mTotalPrice = this.mTotalPrice;
            processingParcel.mProcessingProgress = this.mProcessingProgress;
            return processingParcel;
        }
    }
}
