package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 5/12/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InProcessingProduct implements Parcelable {
    private String mParcelId;
    private String mProductName;
    private String mCreatedDate;
    private String mWeight;
    private int mProcessingProgress;
    private String mDeposit;

    private InProcessingProduct(){

    }

    protected InProcessingProduct(Parcel in) {
        mParcelId = in.readString();
        mProductName = in.readString();
        mCreatedDate = in.readString();
        mWeight = in.readString();
        mProcessingProgress = in.readInt();
        mDeposit = in.readString();
    }

    public static final Creator<InProcessingProduct> CREATOR = new Creator<InProcessingProduct>() {
        @Override
        public InProcessingProduct createFromParcel(Parcel in) {
            return new InProcessingProduct(in);
        }

        @Override
        public InProcessingProduct[] newArray(int size) {
            return new InProcessingProduct[size];
        }
    };

    public String getParcelId() {
        return mParcelId;
    }

    public void setParcelId(String parcelId) {
        mParcelId = parcelId;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        mWeight = weight;
    }

    public int getProcessingProgress() {
        return mProcessingProgress;
    }

    public void setProcessingProgress(int processingProgress) {
        mProcessingProgress = processingProgress;
    }

    public String getDeposit() {
        return mDeposit;
    }

    public void setDeposit(String deposit) {
        mDeposit = deposit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mParcelId);
        dest.writeString(mProductName);
        dest.writeString(mCreatedDate);
        dest.writeString(mWeight);
        dest.writeInt(mProcessingProgress);
        dest.writeString(mDeposit);
    }

    public static class Builder {
        private String mParcelId;
        private String mProductName;
        private String mCreatedDate;
        private String mWeight;
        private int mProcessingProgress;
        private String mDeposit;

        public Builder parcelId(String parcelId) {
            mParcelId = parcelId;
            return this;
        }

        public Builder productName(String productName) {
            mProductName = productName;
            return this;
        }

        public Builder createdDate(String createdDate) {
            mCreatedDate = createdDate;
            return this;
        }

        public Builder weight(String weight) {
            mWeight = weight;
            return this;
        }

        public Builder processingProgress(int processingProgress) {
            mProcessingProgress = processingProgress;
            return this;
        }

        public Builder deposit(String deposit) {
            mDeposit = deposit;
            return this;
        }

        public InProcessingProduct build() {
            InProcessingProduct processingProduct = new InProcessingProduct();
            processingProduct.mParcelId = this.mParcelId;
            processingProduct.mProductName = this.mProductName;
            processingProduct.mCreatedDate = this.mCreatedDate;
            processingProduct.mWeight = this.mWeight;
            processingProduct.mProcessingProgress = this.mProcessingProgress;
            processingProduct.mDeposit = this.mDeposit;
            return processingProduct;
        }
    }
}
