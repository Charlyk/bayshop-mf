package com.softranger.bayshopmfr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmfr.model.address.Address;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 9/29/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class CreationDetails implements Parcelable {
    @JsonProperty("currencySign") private String mCurrencySign;
    @JsonProperty("cursCurrent") private double mCursCurrent;
    @JsonProperty("count") private int mCount;
    @JsonProperty("weight") private int mGramsWeight;
    @JsonProperty("declarationPrice") private int mDeclarationPrice;
    @JsonProperty("items") private int[] mItems;
    @JsonProperty("insuranceMin") private int mInsuranceMin;
    @JsonProperty("insuranceMax") private int mInsuranceMax;
    @JsonProperty("insurancePercent") private String mInsurancePercent;
    @JsonProperty("materialsPrice") private int mMaterialsPrice;
    @JsonProperty("errorMsg") private String mErrorMsg;
    @JsonProperty("infoMsg") private String mInfoMsg;
    @JsonProperty("addresses") private ArrayList<Address> mAddresses;
    @JsonProperty("shippers") private ArrayList<Shipper> mShippers;

    public CreationDetails() {
        // empty constructor for jackson
    }

    protected CreationDetails(Parcel in) {
        mCurrencySign = in.readString();
        mCursCurrent = in.readDouble();
        mCount = in.readInt();
        mGramsWeight = in.readInt();
        mDeclarationPrice = in.readInt();
        in.readIntArray(mItems);
        mInsuranceMin = in.readInt();
        mInsuranceMax = in.readInt();
        mInsurancePercent = in.readString();
        mMaterialsPrice = in.readInt();
        mErrorMsg = in.readString();
        mInfoMsg = in.readString();
        mAddresses = in.createTypedArrayList(Address.CREATOR);
        mShippers = in.createTypedArrayList(Shipper.CREATOR);
    }

    public static final Creator<CreationDetails> CREATOR = new Creator<CreationDetails>() {
        @Override
        public CreationDetails createFromParcel(Parcel in) {
            return new CreationDetails(in);
        }

        @Override
        public CreationDetails[] newArray(int size) {
            return new CreationDetails[size];
        }
    };

    public String getCurrencySign() {
        return mCurrencySign;
    }

    public double getCursCurrent() {
        return mCursCurrent;
    }

    public int getCount() {
        return mCount;
    }

    public int getGramsWeight() {
        return mGramsWeight;
    }

    public int getDeclarationPrice() {
        return mDeclarationPrice;
    }

    public int[] getItems() {
        return mItems;
    }

    public int getInsuranceMin() {
        return mInsuranceMin;
    }

    public int getInsuranceMax() {
        return mInsuranceMax;
    }

    public String getInsurancePercent() {
        return mInsurancePercent;
    }

    public int getMaterialsPrice() {
        return mMaterialsPrice;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public String getInfoMsg() {
        return mInfoMsg;
    }

    public ArrayList<Address> getAddresses() {
        return mAddresses;
    }

    public ArrayList<Shipper> getShippers() {
        return mShippers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCurrencySign);
        dest.writeDouble(mCursCurrent);
        dest.writeInt(mCount);
        dest.writeInt(mGramsWeight);
        dest.writeInt(mDeclarationPrice);
        dest.writeIntArray(mItems);
        dest.writeInt(mInsuranceMin);
        dest.writeInt(mInsuranceMax);
        dest.writeString(mInsurancePercent);
        dest.writeInt(mMaterialsPrice);
        dest.writeString(mErrorMsg);
        dest.writeString(mInfoMsg);
        dest.writeTypedList(mAddresses);
        dest.writeTypedList(mShippers);
    }
}
