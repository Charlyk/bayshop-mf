package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Eduard Albu on 9/29/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shipper implements Parcelable {
    @JsonProperty("id") private String mId;
    @JsonProperty("systemName") private String mSystemName;
    @JsonProperty("storageId") private String mStorageId;
    @JsonProperty("haveInsurance") private int mHaveInsurance;
    @JsonProperty("active") private int mActive;
    @JsonProperty("trackingUrl") private String mTrackingUrl;
    @JsonProperty("type") private String mFormula;
    @JsonProperty("stepWeight") private int mStepWeight;
    @JsonProperty("stepPrice") private double mStepPrice;
    @JsonProperty("minQuantity") private double mMinQuantity;
    @JsonProperty("maxQuantity") private double mMaxQuantity;
    @JsonProperty("maxVolume") private double mMaxVolume;
    @JsonProperty("volumeDelete") private int mVolumeDelete;
    @JsonProperty("useVolumeDelete") private int mUseVolumeDelete;
    @JsonProperty("isFollowByTracking") private int mIsFollowByTracking;
    @JsonProperty("rank") private int mRank;
    @JsonProperty("haveCurrier") private int mHaveCourier;
    @JsonProperty("countryId")
    private int mCountryId;
    @JsonProperty("remoteId") private String mRemoteId;
    @JsonProperty("isManualSend") private int mIsManualSend;
    @JsonProperty("useMinimalPrice") private int mUseMinimalPrice;
    @JsonProperty("allowSendDebt") private int mAllowSendDebt;
    @JsonProperty("shipperId") private String mShipperId;
    @JsonProperty("langId") private String mLangId;
    @JsonProperty("title") private String mTitle;
    @JsonProperty("description") private String mDescription;
    @JsonProperty("time") private String mTime;
    @JsonProperty("realWeight") private int mRealWeight;
    @JsonProperty("disabled") private boolean mIsDisabled;
    @JsonProperty("volumeWeight") private int mVolumeWeight;
    @JsonProperty("shipperMeasureId") private String mShipperMeasureId;
    @JsonProperty("calculatedPrice") private double mCalculatedPrice;

    public Shipper() {
        // empty constructor for jackson
    }

    protected Shipper(Parcel in) {
        mId = in.readString();
        mSystemName = in.readString();
        mStorageId = in.readString();
        mHaveInsurance = in.readInt();
        mActive = in.readInt();
        mTrackingUrl = in.readString();
        mFormula = in.readString();
        mStepWeight = in.readInt();
        mStepPrice = in.readDouble();
        mMinQuantity = in.readDouble();
        mMaxQuantity = in.readDouble();
        mMaxVolume = in.readDouble();
        mVolumeDelete = in.readInt();
        mUseVolumeDelete = in.readInt();
        mIsFollowByTracking = in.readInt();
        mRank = in.readInt();
        mHaveCourier = in.readInt();
        mCountryId = in.readInt();
        mRemoteId = in.readString();
        mIsManualSend = in.readInt();
        mUseMinimalPrice = in.readInt();
        mAllowSendDebt = in.readInt();
        mShipperId = in.readString();
        mLangId = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mTime = in.readString();
        mRealWeight = in.readInt();
        mIsDisabled = in.readByte() != 0;
        mVolumeWeight = in.readInt();
        mShipperMeasureId = in.readString();
        mCalculatedPrice = in.readDouble();
    }

    public static final Creator<Shipper> CREATOR = new Creator<Shipper>() {
        @Override
        public Shipper createFromParcel(Parcel in) {
            return new Shipper(in);
        }

        @Override
        public Shipper[] newArray(int size) {
            return new Shipper[size];
        }
    };

    @JsonIgnore
    public Shipper setId(String id) {
        mId = id;
        return this;
    }

    public String getId() {
        return mId;
    }

    public String getSystemName() {
        return mSystemName;
    }

    public String getStorageId() {
        return mStorageId;
    }

    public int getHaveInsurance() {
        return mHaveInsurance;
    }

    public int getActive() {
        return mActive;
    }

    public String getTrackingUrl() {
        return mTrackingUrl;
    }

    public String getFormula() {
        return mFormula;
    }

    public int getStepWeight() {
        return mStepWeight;
    }

    public double getStepPrice() {
        return mStepPrice;
    }

    public double getMinQuantity() {
        return mMinQuantity;
    }

    public double getMaxQuantity() {
        return mMaxQuantity;
    }

    public double getMaxVolume() {
        return mMaxVolume;
    }

    public int getVolumeDelete() {
        return mVolumeDelete;
    }

    public int getUseVolumeDelete() {
        return mUseVolumeDelete;
    }

    public int getIsFollowByTracking() {
        return mIsFollowByTracking;
    }

    public int getRank() {
        return mRank;
    }

    public int getHaveCourier() {
        return mHaveCourier;
    }

    public int getCountryId() {
        return mCountryId;
    }

    public String getRemoteId() {
        return mRemoteId;
    }

    public int getIsManualSend() {
        return mIsManualSend;
    }

    public int getUseMinimalPrice() {
        return mUseMinimalPrice;
    }

    public int getAllowSendDebt() {
        return mAllowSendDebt;
    }

    public String getShipperId() {
        return mShipperId;
    }

    public String getLangId() {
        return mLangId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTime() {
        return mTime;
    }

    public int getRealWeight() {
        return mRealWeight;
    }

    public boolean isDisabled() {
        return mIsDisabled;
    }

    public int getVolumeWeight() {
        return mVolumeWeight;
    }

    public String getShipperMeasureId() {
        return mShipperMeasureId;
    }

    public double getCalculatedPrice() {
        return mCalculatedPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mSystemName);
        dest.writeString(mStorageId);
        dest.writeInt(mHaveInsurance);
        dest.writeInt(mActive);
        dest.writeString(mTrackingUrl);
        dest.writeString(mFormula);
        dest.writeInt(mStepWeight);
        dest.writeDouble(mStepPrice);
        dest.writeDouble(mMinQuantity);
        dest.writeDouble(mMaxQuantity);
        dest.writeDouble(mMaxVolume);
        dest.writeInt(mVolumeDelete);
        dest.writeInt(mUseVolumeDelete);
        dest.writeInt(mIsFollowByTracking);
        dest.writeInt(mRank);
        dest.writeInt(mHaveCourier);
        dest.writeInt(mCountryId);
        dest.writeString(mRemoteId);
        dest.writeInt(mIsManualSend);
        dest.writeInt(mUseMinimalPrice);
        dest.writeInt(mAllowSendDebt);
        dest.writeString(mShipperId);
        dest.writeString(mLangId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mTime);
        dest.writeInt(mRealWeight);
        dest.writeByte((byte) (mIsDisabled ? 1 : 0));
        dest.writeInt(mVolumeWeight);
        dest.writeString(mShipperMeasureId);
        dest.writeDouble(mCalculatedPrice);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Shipper && getId().equals(((Shipper) obj).getId());
    }
}
