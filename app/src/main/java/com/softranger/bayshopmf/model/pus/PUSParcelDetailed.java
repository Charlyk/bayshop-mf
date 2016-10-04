package com.softranger.bayshopmf.model.pus;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmf.model.product.ShippingMethod;
import com.softranger.bayshopmf.model.address.Address;
import com.softranger.bayshopmf.model.address.Coordinates;
import com.softranger.bayshopmf.model.box.Box;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 9/16/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class PUSParcelDetailed extends PUSParcel {

    @JsonProperty("created")
    private String mCreated;
    @JsonProperty("sentTime")
    private String mSentTime;
    @JsonProperty("packedTime")
    private String mPackedTime;
    @JsonProperty("receivedTime")
    private String mReceivedTime;
    @JsonProperty("takenToDeliveryTime")
    private String mTakenToDeliveryTime;
    @JsonProperty("customsHeldTime")
    private String mCustomsHeldTime;
    @JsonProperty("localDepoTime")
    private String mLocalDepoTime;
    @JsonProperty("heldByProhibitionTime")
    private String mHeldByProhibitionTime;
    @JsonProperty("heldByDamageTime")
    private String mHeldByDamageTime;
    @JsonProperty("customsCause")
    private String mCustomsCause;
    @JsonProperty("totalPrice")
    private String mTotalPrice;
    @JsonProperty("deliveryPrice")
    private String mDeliveryPrice;
    @JsonProperty("status")
    private String mStringStatus;
    @JsonProperty("shipping")
    private ShippingMethod mShippingMethod;
    @JsonProperty("address")
    private Address mAddress;
    @JsonProperty("boxes")
    private ArrayList<Box> mBoxes;
    @JsonProperty("insuranceCommission")
    private String mInsuranceCommission;
    @JsonProperty("percent")
    private int mPercent;
    @JsonProperty("tracking")
    private String mTrackingNum;
    @JsonProperty("trackingUrl")
    private String mTrackingUrl;
    @JsonProperty("coordinates")
    private Coordinates mCoordinates;
    @JsonProperty("signature")
    private String mSignatureUrl;

    public PUSParcelDetailed() {

    }

    @Override
    public String getId() {
        return mId;
    }

    @JsonSetter("uid")
    public void setUid(String uid) {
        mCodeNumber = uid;
    }

    protected PUSParcelDetailed(Parcel in) {
        super(in);
        mCreated = in.readString();
        mSentTime = in.readString();
        mPackedTime = in.readString();
        mReceivedTime = in.readString();
        mTakenToDeliveryTime = in.readString();
        mCustomsHeldTime = in.readString();
        mLocalDepoTime = in.readString();
        mHeldByProhibitionTime = in.readString();
        mHeldByDamageTime = in.readString();
        mCustomsCause = in.readString();
        mTotalPrice = in.readString();
        mDeliveryPrice = in.readString();
        mStringStatus = in.readString();
        mShippingMethod = in.readParcelable(ShippingMethod.class.getClassLoader());
        mAddress = in.readParcelable(Address.class.getClassLoader());
        mBoxes = in.createTypedArrayList(Box.CREATOR);
        mInsuranceCommission = in.readString();
        mPercent = in.readInt();
        mTrackingNum = in.readString();
        mTrackingUrl = in.readString();
        mCoordinates = in.readParcelable(Coordinates.class.getClassLoader());
        mSignatureUrl = in.readString();
    }

    public static final Creator<PUSParcelDetailed> CREATOR = new Creator<PUSParcelDetailed>() {
        @Override
        public PUSParcelDetailed createFromParcel(Parcel in) {
            return new PUSParcelDetailed(in);
        }

        @Override
        public PUSParcelDetailed[] newArray(int size) {
            return new PUSParcelDetailed[size];
        }
    };

    public String getCreated() {
        return mCreated;
    }

    public String getSentTime() {
        return mSentTime;
    }

    public String getPackedTime() {
        return mPackedTime;
    }

    public String getReceivedTime() {
        return mReceivedTime;
    }

    public String getTakenToDeliveryTime() {
        return mTakenToDeliveryTime;
    }

    public String getCustomsHeldTime() {
        return mCustomsHeldTime;
    }

    public String getLocalDepoTime() {
        return mLocalDepoTime;
    }

    public String getHeldByProhibitionTime() {
        return mHeldByProhibitionTime;
    }

    public String getHeldByDamageTime() {
        return mHeldByDamageTime;
    }

    public String getCustomsCause() {
        return mCustomsCause;
    }

    public String getTotalPrice() {
        return mTotalPrice;
    }

    public String getDeliveryPrice() {
        return mDeliveryPrice;
    }

    @JsonSetter("status")
    public void setStringStatus(String stringStatus) {
        setParcelStatus(stringStatus);
        mStringStatus = stringStatus;
    }

    public String getStringStatus() {
        return mStringStatus;
    }

    public ShippingMethod getShippingMethod() {
        return mShippingMethod;
    }

    public Address getAddress() {
        return mAddress;
    }

    public ArrayList<Box> getBoxes() {
        return mBoxes;
    }

    public String getInsuranceCommission() {
        return mInsuranceCommission;
    }

    public int getPercent() {
        return mPercent;
    }



    public String getTrackingNum() {
        return mTrackingNum;
    }

    public String getTrackingUrl() {
        return mTrackingUrl;
    }

    public Coordinates getCoordinates() {
        return mCoordinates;
    }

    public String getSignatureUrl() {
        return mSignatureUrl;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mCreated);
        parcel.writeString(mSentTime);
        parcel.writeString(mPackedTime);
        parcel.writeString(mReceivedTime);
        parcel.writeString(mTakenToDeliveryTime);
        parcel.writeString(mCustomsHeldTime);
        parcel.writeString(mLocalDepoTime);
        parcel.writeString(mHeldByProhibitionTime);
        parcel.writeString(mHeldByDamageTime);
        parcel.writeString(mCustomsCause);
        parcel.writeString(mTotalPrice);
        parcel.writeString(mDeliveryPrice);
        parcel.writeString(mStringStatus);
        parcel.writeParcelable(mShippingMethod, i);
        parcel.writeParcelable(mAddress, i);
        parcel.writeTypedList(mBoxes);
        parcel.writeString(mInsuranceCommission);
        parcel.writeInt(mPercent);
        parcel.writeString(mTrackingNum);
        parcel.writeString(mTrackingUrl);
        parcel.writeParcelable(mCoordinates, i);
        parcel.writeString(mSignatureUrl);
    }
}
