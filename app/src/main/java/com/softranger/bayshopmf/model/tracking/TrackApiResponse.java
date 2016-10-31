package com.softranger.bayshopmf.model.tracking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Eduard Albu on 10/31/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackApiResponse<T> {

    @JsonProperty("error")
    private String mError;
    @JsonProperty("length")
    private int mLength;
    @JsonProperty("data")
    private T mData;
    @JsonProperty("tracking_number")
    private String mTrackingNumber;
    @JsonIgnore
    private Result mResult;

    @JsonSetter("result")
    public void setResult(String result) {
        mResult = Result.fromString(result);
    }

    public String getError() {
        return mError;
    }

    public int getLength() {
        return mLength;
    }

    public T getData() {
        return mData;
    }

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public Result getResult() {
        return mResult;
    }

    public enum Result {
        success("success"), unsure("unsure"), unknown("unknown"), waiting("waiting"), error("error");

        private String mResult;

        Result(String result) {
            mResult = result;
        }

        public static Result fromString(String result) {
            for (Result r : values()) {
                if (r.mResult.equals(result)) {
                    return r;
                }
            }
            return unknown;
        }
    }
}
