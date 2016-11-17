package com.softranger.bayshopmfr.model.box;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmfr.util.Constants;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AwaitingArrival extends Box {

    @JsonProperty("url") private String mUrl;
    @JsonProperty("storage") private String mStorage;
    @JsonProperty("service_name")
    private String mTrackingServiceName;
    @JsonProperty("tracking_status")
    private String mStringStatus;

    public AwaitingArrival() {
        // empty constructor for jackson
    }

    public AwaitingArrival(Parcel in) {
        super(in);
        mUrl = in.readString();
        mStorage = in.readString();
        mTrackingServiceName = in.readString();
        mStringStatus = in.readString();
    }

    public static final Creator<AwaitingArrival> CREATOR = new Creator<AwaitingArrival>() {
        @Override
        public AwaitingArrival createFromParcel(Parcel parcel) {
            return new AwaitingArrival(parcel);
        }

        @Override
        public AwaitingArrival[] newArray(int i) {
            return new AwaitingArrival[i];
        }
    };

    public TrackingInfo.TrackingStatus getTrackingStatus() {
        if (mStringStatus == null) return TrackingInfo.TrackingStatus.UNKNOWN;
        for (TrackingInfo.TrackingStatus s : TrackingInfo.TrackingStatus.values()) {
            if (s.name().equalsIgnoreCase(mStringStatus)) {
                return s;
            }
        }
        return TrackingInfo.TrackingStatus.UNKNOWN;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getStorage() {
        return mStorage;
    }

    @Override
    public String getPrice() {
        return Constants.USD_SYMBOL + mPrice;
    }

    @JsonSetter("productUrl")
    public void setProductUrl(String url) {
        mUrl = url;
    }

    public String getTrackingServiceName() {
        return mTrackingServiceName;
    }

    public String getStringStatus() {
        return mStringStatus;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AwaitingArrival &&
                ((AwaitingArrival) obj).getId().equals(this.getId());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mUrl);
        parcel.writeString(mStorage);
        parcel.writeString(mTrackingServiceName);
        parcel.writeString(mStringStatus);
    }

    public void setTrackingStatus(TrackingInfo trackingInfo) {
        mTrackingServiceName = trackingInfo.getServiceName();
        mStringStatus = trackingInfo.getStatus();
    }
}
