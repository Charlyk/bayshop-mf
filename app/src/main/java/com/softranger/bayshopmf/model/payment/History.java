package com.softranger.bayshopmf.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Date;

/**
 * Created by macbook on 6/30/16.
 */
public class History implements Parcelable {
    private PaymentType mPaymentType;
    @JsonProperty("comment")
    private String mComment;
    @JsonProperty("created")
    private Date mDate;
    @JsonProperty("method_commission")
    private String mMethodCommission;
    @JsonProperty("commission")
    private String mCommission;
    @JsonProperty("summ")
    private double mSumm;
    @JsonProperty("total_amount")
    private double mTotalAmmount;
    @JsonProperty("trid")
    private String mTransactionId;
    @JsonProperty("sign")
    private String mCurrency;

    private History() {

    }

    protected History(Parcel in) {
        mComment = in.readString();
        mMethodCommission = in.readString();
        mCommission = in.readString();
        mSumm = in.readDouble();
        mTotalAmmount = in.readDouble();
        mTransactionId = in.readString();
        mCurrency = in.readString();
        mDate = (Date) in.readSerializable();
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

    @JsonSetter("type")
    public void setPaymentType(String paymentType) {
        mPaymentType = PaymentType.toType(paymentType);
    }

    public PaymentType getPaymentType() {
        return mPaymentType;
    }

    public String getComment() {
        return mComment;
    }

    public Date getDate() {
        return mDate;
    }

    public String getMethodCommission() {
        return mMethodCommission;
    }

    public String getCommissionl() {
        return mCommission;
    }

    public double getSumm() {
        return mSumm;
    }

    public double getTotalAmmount() {
        return mTotalAmmount;
    }

    public String getTransactionId() {
        return mTransactionId;
    }

    public String getCurrency() {
        return mCurrency;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mComment);
        dest.writeString(mMethodCommission);
        dest.writeString(mCommission);
        dest.writeDouble(mSumm);
        dest.writeDouble(mTotalAmmount);
        dest.writeString(mTransactionId);
        dest.writeString(mCurrency);
        dest.writeSerializable(mDate);
    }

    public enum PaymentType {
        minus("minus"), plus("plus"), unknown("unknown");

        private String mStringType;

        PaymentType(String stringType) {
            mStringType = stringType;
        }

        public static PaymentType toType(String stringType) {
            for (PaymentType type : PaymentType.values()) {
                if (type.mStringType.equalsIgnoreCase(stringType)) {
                    return type;
                }
            }
            return unknown;
        }
    }
}
