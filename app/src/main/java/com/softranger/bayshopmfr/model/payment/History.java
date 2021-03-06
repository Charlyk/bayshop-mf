package com.softranger.bayshopmfr.model.payment;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmfr.BuildConfig;
import com.softranger.bayshopmfr.util.Imageble;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by macbook on 6/30/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class History implements Parcelable, Imageble {
    private PaymentType mPaymentType;
    private Bitmap mIcon;
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
    @JsonProperty("img_payment")
    private String mImageUrl;

    @JsonCreator
    public static History Create(String json) {
        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        mapper.setDateFormat(dateFormat);
        History history = null;
        try {
            history = mapper.readValue(json, History.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }

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
        mImageUrl = in.readString();
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
    public void setImage(Bitmap bitmap) {
        mIcon = bitmap;
    }

    @Override
    public Bitmap getImage() {
        return mIcon;
    }

    @Override
    public String getImageUrl() {
        if (mImageUrl != null && mImageUrl.contains("http")) return mImageUrl;
        else return BuildConfig.BASE_URL + mImageUrl;
    }

    public String getCommission() {
        return mCommission;
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
        dest.writeString(mImageUrl);
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
