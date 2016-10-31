package com.softranger.bayshopmf.model.tracking;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 10/31/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourierService implements Parcelable {
    @JsonProperty("tracking_number")
    private String mTrackingNumber;
    @JsonProperty("tracker_url")
    private String mTrackParcel;
    @JsonProperty("courier")
    private Courier mCourier;

    public CourierService() {

    }

    protected CourierService(Parcel in) {
        mTrackingNumber = in.readString();
        mTrackParcel = in.readString();
        mCourier = in.readParcelable(Courier.class.getClassLoader());
    }

    public static final Creator<CourierService> CREATOR = new Creator<CourierService>() {
        @Override
        public CourierService createFromParcel(Parcel in) {
            return new CourierService(in);
        }

        @Override
        public CourierService[] newArray(int size) {
            return new CourierService[size];
        }
    };

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public String getTrackParcel() {
        return mTrackParcel;
    }

    public Courier getCourier() {
        return mCourier;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTrackingNumber);
        parcel.writeString(mTrackParcel);
        parcel.writeParcelable(mCourier, i);
    }
}
