package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;

/**
 * Created by macbook on 6/14/16.
 */
public class InProcessingPackage extends Package {
    private int mPercentage;
    private String mCreatedDate;
    private String mGoodsPrice;
    private String mCustomsClearance;
    private String mShippingPrice;
    private String mTotalPrice;

    private InProcessingPackage() {

    }

    protected InProcessingPackage(Parcel in) {
        super(in);
        mPercentage = in.readInt();
        mCreatedDate = in.readString();
        mGoodsPrice = in.readString();
        mCustomsClearance = in.readString();
        mShippingPrice = in.readString();
        mTotalPrice = in.readString();
    }

    public static final Creator<InProcessingPackage> CREATOR = new Creator<InProcessingPackage>() {
        @Override
        public InProcessingPackage createFromParcel(Parcel source) {
            return new InProcessingPackage(source);
        }

        @Override
        public InProcessingPackage[] newArray(int size) {
            return new InProcessingPackage[size];
        }
    };

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    public int getPercentage() {
        return mPercentage;
    }

    public void setPercentage(int percentage) {
        mPercentage = percentage;
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mPercentage);
        dest.writeString(mCreatedDate);
        dest.writeString(mGoodsPrice);
        dest.writeString(mCustomsClearance);
        dest.writeString(mShippingPrice);
        dest.writeString(mTotalPrice);
    }

    public static class Builder extends Package.Builder {
        private int mPercentage;
        private String mCreatedDate;
        private String mGoodsPrice;
        private String mCustomsClearance;
        private String mShippingPrice;
        private String mTotalPrice;

        public Builder percentage(int percentage) {
            mPercentage = percentage;
            return this;
        }

        public Builder createdDate(String createdDate) {
            mCreatedDate = createdDate;
            return this;
        }

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

        public InProcessingPackage build() {
            InProcessingPackage inProcessingPackage = new InProcessingPackage();
            inProcessingPackage.mCreatedDate = this.mCreatedDate;
            inProcessingPackage.mRealWeght = this.mRealWeght;
            inProcessingPackage.mPercentage = this.mPercentage;
            inProcessingPackage.mName = this.mName;
            inProcessingPackage.mCodeNumber = this.mCodeNumber;
            inProcessingPackage.mId = this.mId;
            inProcessingPackage.mDeposit = this.mDeposit;
            inProcessingPackage.mGoodsPrice = this.mGoodsPrice;
            inProcessingPackage.mCustomsClearance = this.mCustomsClearance;
            inProcessingPackage.mShippingPrice = this.mShippingPrice;
            inProcessingPackage.mTotalPrice = this.mTotalPrice;
            inProcessingPackage.mTrackingNumber = this.mTrackingNumber;
            inProcessingPackage.mShippingMethod = this.mShippingMethod;
            inProcessingPackage.mAddress = this.mAddress;
            inProcessingPackage.mProducts = this.mProducts;
            inProcessingPackage.mCurrency = this.mCurrency;
            return inProcessingPackage;
        }
    }
}
