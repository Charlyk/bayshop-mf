package com.softranger.bayshopmfr.model.product;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by macbook on 6/18/16.
 */
public class Insurance implements Parcelable {
    private String mCurrency;
    private double mCommission;
    private double mShippingCost;
    private double mTotalPriceBoxes;
    private boolean mIsInsuranceSelected;
    private boolean mIsInsuranceAvailable;
    private double mDeclarationTotalPrice;

    private Insurance() {

    }

    protected Insurance(Parcel in) {
        mCurrency = in.readString();
        mCommission = in.readDouble();
        mShippingCost = in.readDouble();
        mTotalPriceBoxes = in.readDouble();
        mIsInsuranceSelected = in.readByte() != 0;
        mIsInsuranceAvailable = in.readByte() != 0;
        mDeclarationTotalPrice = in.readDouble();
    }

    public static final Creator<Insurance> CREATOR = new Creator<Insurance>() {
        @Override
        public Insurance createFromParcel(Parcel in) {
            return new Insurance(in);
        }

        @Override
        public Insurance[] newArray(int size) {
            return new Insurance[size];
        }
    };

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public double getCommission() {
        return mCommission;
    }

    public void setCommission(double commission) {
        mCommission = commission;
    }

    public double getShippingCost() {
        return mShippingCost;
    }

    public void setShippingCost(double shippingCost) {
        mShippingCost = shippingCost;
    }

    public double getTotalPriceBoxes() {
        return mTotalPriceBoxes;
    }

    public void setTotalPriceBoxes(double totalPriceBoxes) {
        mTotalPriceBoxes = totalPriceBoxes;
    }

    public boolean isInsuranceSelected() {
        return mIsInsuranceSelected;
    }

    public void setInsuranceSelected(boolean insuranceSelected) {
        mIsInsuranceSelected = insuranceSelected;
    }

    public boolean isInsuranceAvailable() {
        return mIsInsuranceAvailable;
    }

    public void setInsuranceAvailable(boolean insuranceAvailable) {
        mIsInsuranceAvailable = insuranceAvailable;
    }

    public double getDeclarationTotalPrice() {
        return mDeclarationTotalPrice;
    }

    public void setDeclarationTotalPrice(double declarationTotalPrice) {
        mDeclarationTotalPrice = declarationTotalPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCurrency);
        dest.writeDouble(mCommission);
        dest.writeDouble(mShippingCost);
        dest.writeDouble(mTotalPriceBoxes);
        dest.writeByte((byte) (mIsInsuranceSelected ? 1 : 0));
        dest.writeByte((byte) (mIsInsuranceAvailable ? 1 : 0));
        dest.writeDouble(mDeclarationTotalPrice);
    }

    public static class Builder {
        private String mCurrency;
        private double mCommission;
        private double mShippingCost;
        private double mTotalPriceBoxes;
        private boolean mIsInsuranceSelected;
        private boolean mIsInsuranceAvailable;
        private double mDeclarationTotalPrice;

        public Builder currency(String currency) {
            mCurrency = currency;
            return this;
        }

        public Builder commission(double commission) {
            mCommission = commission;
            return this;
        }

        public Builder shippingCost(double shippingCost) {
            mShippingCost = shippingCost;
            return this;
        }

        public Builder totalPriceBoxes(double totalPriceBoxes) {
            mTotalPriceBoxes = totalPriceBoxes;
            return this;
        }

        public Builder isInsuranceSelected(boolean isInsuranceSelected) {
            mIsInsuranceSelected = isInsuranceSelected;
            return this;
        }

        public Builder isInsuranceAvailable(boolean isInsuranceAvailable) {
            mIsInsuranceAvailable = isInsuranceAvailable;
            return this;
        }

        public Builder declarationTotalPrice(double declarationTotalPrice) {
            mDeclarationTotalPrice = declarationTotalPrice;
            return this;
        }

        public Insurance build() {
            Insurance insurance = new Insurance();
            insurance.mCurrency = this.mCurrency;
            insurance.mCommission = this.mCommission;
            insurance.mShippingCost = this.mShippingCost;
            insurance.mTotalPriceBoxes = this.mTotalPriceBoxes;
            insurance.mIsInsuranceSelected = this.mIsInsuranceSelected;
            insurance.mIsInsuranceAvailable = this.mIsInsuranceAvailable;
            insurance.mDeclarationTotalPrice = this.mDeclarationTotalPrice;
            return insurance;
        }
    }
}
