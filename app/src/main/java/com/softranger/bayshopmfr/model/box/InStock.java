package com.softranger.bayshopmfr.model.box;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class InStock extends Box {
    @JsonProperty("remainingDays") private int mRemainingDays;
    @JsonProperty("isDeclarationFilled") private int mDeclarationFilled;
    @JsonProperty("separationRequest") private int mSeparationRequest;
    @JsonProperty("free_days_storage")
    private int mFreeStorage;
    @JsonProperty("isHeldByProhibition")
    private int mIsHeldByProhibition;
    @JsonProperty("volume_weight")
    private double mVolumeWeight;

    @JsonIgnore private boolean mIsSelected;

    public InStock() {
        // empty constructor for jackson
    }

    public InStock(Parcel in) {
        super(in);
        mRemainingDays = in.readInt();
        mDeclarationFilled = in.readInt();
        mSeparationRequest = in.readInt();
        mIsHeldByProhibition = in.readInt();
        mFreeStorage = in.readInt();
        mVolumeWeight = in.readDouble();
    }

    public static final Creator<InStock> CREATOR = new Creator<InStock>() {
        @Override
        public InStock createFromParcel(Parcel parcel) {
            return new InStock(parcel);
        }

        @Override
        public InStock[] newArray(int i) {
            return new InStock[i];
        }
    };

    public int getRemainingDays() {
        return mRemainingDays;
    }

    public int getSeparationRequest() {
        return mSeparationRequest;
    }

    public int getIsHeldByProhibition() {
        return mIsHeldByProhibition;
    }

    @JsonIgnore
    public void setSeparationRequest(int separationRequest) {
        mSeparationRequest = separationRequest;
    }

    public int getDeclarationFilled() {
        return mDeclarationFilled;
    }

    @JsonIgnore
    public void setDeclarationFilled(int declarationFilled) {
        mDeclarationFilled = declarationFilled;
    }

    @JsonSetter("declarationFilled")
    public void setDeclarationFilled(boolean declarationFilled) {
        mDeclarationFilled = declarationFilled ? 1 : 0;
    }

    public int getFreeStorage() {
        return mFreeStorage;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public double getVolumeWeight() {
        return mVolumeWeight;
    }

    public void setVolumeWeight(double volumeWeight) {
        mVolumeWeight = volumeWeight;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(mRemainingDays);
        parcel.writeInt(mDeclarationFilled);
        parcel.writeInt(mSeparationRequest);
        parcel.writeInt(mIsHeldByProhibition);
        parcel.writeInt(mFreeStorage);
        parcel.writeDouble(mVolumeWeight);
    }
}
