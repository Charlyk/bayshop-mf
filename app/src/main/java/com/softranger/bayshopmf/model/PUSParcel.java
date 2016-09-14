package com.softranger.bayshopmf.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/14/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class PUSParcel {
    @JsonProperty("id") String mId;
    @JsonProperty("codeNumber") String mCodeNumber;
    @JsonProperty("realWeight") String mRealWeight;
    @JsonProperty("fieldTime") String fieldTime;

    private String mPrice;
    private String mCurrency;

    public void setId(String id) {
        mId = id;
    }

    public void setCodeNumber(String codeNumber) {
        mCodeNumber = codeNumber;
    }

    public void setRealWeight(String realWeight) {
        mRealWeight = realWeight;
    }

    public void setFieldTime(String fieldTime) {
        this.fieldTime = fieldTime;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }
}
