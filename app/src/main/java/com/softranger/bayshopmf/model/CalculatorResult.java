package com.softranger.bayshopmf.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmf.model.address.Country;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eduard Albu on 10/27/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculatorResult implements Parcelable {
    @JsonProperty("shipper_countries")
    private ArrayList<Country> mCountries;
    @JsonProperty("countries")
    private HashMap<Intent, String> mCountriesMap;
    @JsonProperty("shippers")
    private ArrayList<Shipper> mShippersl;

    public CalculatorResult() {

    }


    protected CalculatorResult(Parcel in) {
        mCountries = in.createTypedArrayList(Country.CREATOR);
        mShippersl = in.createTypedArrayList(Shipper.CREATOR);
    }

    public static final Creator<CalculatorResult> CREATOR = new Creator<CalculatorResult>() {
        @Override
        public CalculatorResult createFromParcel(Parcel in) {
            return new CalculatorResult(in);
        }

        @Override
        public CalculatorResult[] newArray(int size) {
            return new CalculatorResult[size];
        }
    };

    public ArrayList<Country> getCountries() {
        return mCountries;
    }

    public HashMap<Intent, String> getCountriesMap() {
        return mCountriesMap;
    }

    public ArrayList<Shipper> getShippersl() {
        return mShippersl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(mCountries);
        parcel.writeTypedList(mShippersl);
    }
}
