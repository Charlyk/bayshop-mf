package com.softranger.bayshopmf.model.pus;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmf.model.product.ShippingMethod;
import com.softranger.bayshopmf.model.address.Address;
import com.softranger.bayshopmf.model.address.Coordinates;
import com.softranger.bayshopmf.model.box.Box;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Eduard Albu on 9/16/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PUSParcelDetailed extends PUSParcel {
    @JsonProperty("created")
    private Date mCreated;
    @JsonProperty("sentTime")
    private Date mSentTime;
    @JsonProperty("packedTime")
    private Date mPackedTime;
    @JsonProperty("receivedTime")
    private Date mReceivedTime;
    @JsonProperty("takenToDeliveryTime")
    private Date mTakenToDeliveryTime;
    @JsonProperty("customsHeldTime")
    private Date mCustomsHeldTime;
    @JsonProperty("localDepoTime")
    private Date mLocalDepoTime;
    @JsonProperty("heldByProhibitionTime")
    private Date mHeldByProhibitionTime;
    @JsonProperty("heldByDamageTime")
    private Date mHeldByDamageTime;
    @JsonProperty("customsCause")
    private String mCustomsCause;
    @JsonProperty("price_total")
    private double mTotalPrice;
    @JsonProperty("price_delivery")
    private double mDeliveryPrice;
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
    @JsonProperty("volume_weight")
    private double mVolumeWeight;
    @JsonProperty("service_comment")
    private String mComment;
    @JsonProperty("price_insurance")
    private double mInsurancePrice;
    @JsonProperty("price_declarations")
    private double mDeclarationPrice;
    @JsonProperty("price_boxes")
    private double mBoxesPrice;
    @JsonProperty("price_repacking")
    private double mRepackingPrice;
    @JsonProperty("price_additional_materials")
    private double mAdditionalMaterialsPrice;


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
        mCreated = (Date) in.readSerializable();
        mSentTime = (Date) in.readSerializable();
        mPackedTime = (Date) in.readSerializable();
        mReceivedTime = (Date) in.readSerializable();
        mTakenToDeliveryTime = (Date) in.readSerializable();
        mCustomsHeldTime = (Date) in.readSerializable();
        mLocalDepoTime = (Date) in.readSerializable();
        mHeldByProhibitionTime = (Date) in.readSerializable();
        mHeldByDamageTime = (Date) in.readSerializable();
        mCustomsCause = in.readString();
        mTotalPrice = in.readDouble();
        mDeliveryPrice = in.readDouble();
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
        mVolumeWeight = in.readDouble();
        mComment = in.readString();
        mInsurancePrice = in.readDouble();
        mDeclarationPrice = in.readDouble();
        mBoxesPrice = in.readDouble();
        mRepackingPrice = in.readDouble();
        mAdditionalMaterialsPrice = in.readDouble();
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

    public Date getCreated() {
        return mCreated;
    }

    public Date getSentTime() {
        return mSentTime;
    }

    public Date getPackedTime() {
        return mPackedTime;
    }

    public Date getReceivedTime() {
        return mReceivedTime;
    }

    public Date getTakenToDeliveryTime() {
        return mTakenToDeliveryTime;
    }

    public Date getCustomsHeldTime() {
        return mCustomsHeldTime;
    }

    public Date getLocalDepoTime() {
        return mLocalDepoTime;
    }

    public Date getHeldByProhibitionTime() {
        return mHeldByProhibitionTime;
    }

    public Date getHeldByDamageTime() {
        return mHeldByDamageTime;
    }

    public String getCustomsCause() {
        return mCustomsCause;
    }

    public double getTotalPrice() {
        return mTotalPrice;
    }

    public double getDeliveryPrice() {
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

    public double getVolumeWeight() {
        return mVolumeWeight;
    }

    @JsonSetter("service_rating")
    public void setRating(int rating) {
        mRating = rating;
    }

    public String getComment() {
        return mComment;
    }

    public double getInsurancePrice() {
        return mInsurancePrice;
    }

    public double getDeclarationPrice() {
        return mDeclarationPrice;
    }

    public double getBoxesPrice() {
        return mBoxesPrice;
    }

    public double getRepackingPrice() {
        return mRepackingPrice;
    }

    public double getAdditionalMaterialsPrice() {
        return mAdditionalMaterialsPrice;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeSerializable(mCreated);
        parcel.writeSerializable(mSentTime);
        parcel.writeSerializable(mPackedTime);
        parcel.writeSerializable(mReceivedTime);
        parcel.writeSerializable(mTakenToDeliveryTime);
        parcel.writeSerializable(mCustomsHeldTime);
        parcel.writeSerializable(mLocalDepoTime);
        parcel.writeSerializable(mHeldByProhibitionTime);
        parcel.writeSerializable(mHeldByDamageTime);
        parcel.writeString(mCustomsCause);
        parcel.writeDouble(mTotalPrice);
        parcel.writeDouble(mDeliveryPrice);
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
        parcel.writeDouble(mVolumeWeight);
        parcel.writeString(mComment);
        parcel.writeDouble(mInsurancePrice);
        parcel.writeDouble(mDeclarationPrice);
        parcel.writeDouble(mBoxesPrice);
        parcel.writeDouble(mRepackingPrice);
        parcel.writeDouble(mAdditionalMaterialsPrice);
    }
}
