package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by macbook on 6/30/16.
 */
public class History implements Parcelable {
    private PaymentType mPaymentType;
    private String mComment;
    private String mDate;
    private double mSumm;
    private double mTotalAmmount;
    private String mTransactionId;

    private History() {

    }

    protected History(Parcel in) {
        mComment = in.readString();
        mDate = in.readString();
        mSumm = in.readDouble();
        mTotalAmmount = in.readDouble();
        mTransactionId = in.readString();
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    public PaymentType getPaymentType() {
        return mPaymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        mPaymentType = paymentType;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public double getSumm() {
        return mSumm;
    }

    public void setSumm(double summ) {
        mSumm = summ;
    }

    public double getTotalAmmount() {
        return mTotalAmmount;
    }

    public void setTotalAmmount(double totalAmmount) {
        mTotalAmmount = totalAmmount;
    }

    public String getTransactionId() {
        return mTransactionId;
    }

    public void setTransactionId(String transactionId) {
        mTransactionId = transactionId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mComment);
        dest.writeString(mDate);
        dest.writeDouble(mSumm);
        dest.writeDouble(mTotalAmmount);
        dest.writeString(mTransactionId);
    }

    public static class Builder {
        private PaymentType mPaymentType;
        private String mComment;
        private String mDate;
        private double mSumm;
        private double mTotalAmmount;
        private String mTransactionId;

        public Builder paymentType(String paymentType) {
            switch (paymentType) {
                case "minus":
                    mPaymentType = PaymentType.minus;
                    break;
                case "plus":
                    mPaymentType = PaymentType.plus;
                    break;
            }
            return this;
        }

        public Builder comment(String comment) {
            mComment = comment;
            return this;
        }

        public Builder date(String date) {
            mDate = date;
            return this;
        }

        public Builder summ(double summ) {
            mSumm = summ;
            return this;
        }

        public Builder totalAmmount(double totalAmmount) {
            mTotalAmmount = totalAmmount;
            return this;
        }

        public Builder transactionId(String transactionId) {
            mTransactionId = transactionId;
            return this;
        }

        public History build() {
            History history = new History();
            history.mPaymentType = this.mPaymentType;
            history.mComment = this.mComment;
            history.mDate = this.mDate;
            history.mSumm = this.mSumm;
            history.mTotalAmmount = this.mTotalAmmount;
            history.mTransactionId = this.mTransactionId;
            return history;
        }
    }

    public static enum PaymentType {
        minus, plus
    }
}
