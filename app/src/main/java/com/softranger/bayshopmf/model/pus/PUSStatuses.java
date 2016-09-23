package com.softranger.bayshopmf.model.pus;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 9/23/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class PUSStatuses implements Parcelable {
    @JsonProperty("processing") private ArrayList<PUSParcel> mProcessingParcels;
    @JsonProperty("held-by-damage") private ArrayList<PUSParcel> mDamagedParcels;
    @JsonProperty("held-by-prohibition") private ArrayList<PUSParcel> mProhibitedParcels;
    @JsonProperty("packed") private ArrayList<PUSParcel> mPackedParcels;
    @JsonProperty("dept") private ArrayList<PUSParcel> mDeptParcels;
    @JsonProperty("sent") private ArrayList<PUSParcel> mSentParcels;
    @JsonProperty("customs-held") private ArrayList<PUSParcel> mHeldParcels;
    @JsonProperty("local-depo") private ArrayList<PUSParcel> mDepotParcels;
    @JsonProperty("taken-to-delivery") private ArrayList<PUSParcel> mTakenParcels;

    public PUSStatuses() {

    }

    protected PUSStatuses(Parcel in) {
        mProcessingParcels = in.createTypedArrayList(PUSParcel.CREATOR);
        mDamagedParcels = in.createTypedArrayList(PUSParcel.CREATOR);
        mProhibitedParcels = in.createTypedArrayList(PUSParcel.CREATOR);
        mPackedParcels = in.createTypedArrayList(PUSParcel.CREATOR);
        mDeptParcels = in.createTypedArrayList(PUSParcel.CREATOR);
        mSentParcels = in.createTypedArrayList(PUSParcel.CREATOR);
        mHeldParcels = in.createTypedArrayList(PUSParcel.CREATOR);
        mDepotParcels = in.createTypedArrayList(PUSParcel.CREATOR);
        mTakenParcels = in.createTypedArrayList(PUSParcel.CREATOR);
    }

    public static final Creator<PUSStatuses> CREATOR = new Creator<PUSStatuses>() {
        @Override
        public PUSStatuses createFromParcel(Parcel in) {
            return new PUSStatuses(in);
        }

        @Override
        public PUSStatuses[] newArray(int size) {
            return new PUSStatuses[size];
        }
    };

    public ArrayList<PUSParcel> getProcessingParcels() {
        if (mProcessingParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mProcessingParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.IN_PROCESSING);
        }
        return mProcessingParcels;
    }

    public ArrayList<PUSParcel> getDamagedParcels() {
        if (mDamagedParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mProcessingParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.IN_PROCESSING);
        }
        return mDamagedParcels;
    }

    public ArrayList<PUSParcel> getProhibitedParcels() {
        if (mProhibitedParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mProhibitedParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.HELD_BY_PROHIBITION);
        }
        return mProhibitedParcels;
    }

    public ArrayList<PUSParcel> getPackedParcels() {
        if (mPackedParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mPackedParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.PACKED);
        }
        return mPackedParcels;
    }

    public ArrayList<PUSParcel> getDeptParcels() {
        if (mDeptParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mDeptParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.DEPT);
        }
        return mDeptParcels;
    }

    public ArrayList<PUSParcel> getSentParcels() {
        if (mSentParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mSentParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.SENT);
        }
        return mSentParcels;
    }

    public ArrayList<PUSParcel> getHeldParcels() {
        if (mHeldParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mHeldParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.CUSTOMS_HELD);
        }
        return mHeldParcels;
    }

    public ArrayList<PUSParcel> getDepotParcels() {
        if (mDepotParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mDepotParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.LOCAL_DEPO);
        }
        return mDepotParcels;
    }

    public ArrayList<PUSParcel> getTakenParcels() {
        if (mTakenParcels == null) return new ArrayList<>();
        for (PUSParcel pusParcel : mTakenParcels) {
            pusParcel.setParcelStatus(Constants.ParcelStatus.TAKEN_TO_DELIVERY);
        }
        return mTakenParcels;
    }

    public ArrayList<PUSParcel> getAllParcels() {
        ArrayList<PUSParcel> pusParcels = new ArrayList<>();
        pusParcels.addAll(getProcessingParcels());
        pusParcels.addAll(getDamagedParcels());
        pusParcels.addAll(getProhibitedParcels());
        pusParcels.addAll(getPackedParcels());
        pusParcels.addAll(getDeptParcels());
        pusParcels.addAll(getSentParcels());
        pusParcels.addAll(getHeldParcels());
        pusParcels.addAll(getDepotParcels());
        pusParcels.addAll(getTakenParcels());
        return pusParcels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mProcessingParcels);
        dest.writeTypedList(mDamagedParcels);
        dest.writeTypedList(mProhibitedParcels);
        dest.writeTypedList(mPackedParcels);
        dest.writeTypedList(mDeptParcels);
        dest.writeTypedList(mSentParcels);
        dest.writeTypedList(mHeldParcels);
        dest.writeTypedList(mDepotParcels);
        dest.writeTypedList(mTakenParcels);
    }
}
