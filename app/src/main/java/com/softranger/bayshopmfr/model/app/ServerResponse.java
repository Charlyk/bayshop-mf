package com.softranger.bayshopmfr.model.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerResponse<T> {
    @JsonProperty("code")
    private int mCode;
    @JsonProperty("error")
    private boolean mError;
    @JsonProperty("message")
    private String mMessage;
    @JsonProperty("data")
    private T mData;

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
