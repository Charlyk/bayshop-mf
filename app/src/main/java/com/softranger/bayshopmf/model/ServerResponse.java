package com.softranger.bayshopmf.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class ServerResponse<T> {
    @JsonProperty("code") int mCode;
    @JsonProperty("error") boolean mError;
    @JsonProperty("message") String mMessage;
    @JsonProperty("data") T mData;

    public int getCode() {
        return mCode;
    }

    public boolean isError() {
        return mError;
    }

    public String getMessage() {
        return mMessage;
    }

    public T getData() {
        return mData;
    }
}
