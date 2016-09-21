package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

/**
 * Created by Eduard Albu on 9/14/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class PUSParcel implements Parcelable {
    @JsonProperty("id") String mId;
    @JsonProperty("codeNumber") String mCodeNumber;
    @JsonProperty("fieldTime") String fieldTime;
    @JsonProperty("totalPrice") String mPrice;
    @JsonProperty("currency") String mCurrency;
    @JsonProperty("generalDescription") String mGeneralDescription;
    PUSStatus mParcelStatus;
    String mRealWeight;
    boolean mWasAnimated;
    boolean mIsSelected;

    public PUSParcel() {

    }

    protected PUSParcel(Parcel in) {
        mId = in.readString();
        mCodeNumber = in.readString();
        mRealWeight = in.readString();
        fieldTime = in.readString();
        mPrice = in.readString();
        mCurrency = in.readString();
        mWasAnimated = in.readByte() != 0;
    }

    public static final Creator<PUSParcel> CREATOR = new Creator<PUSParcel>() {
        @Override
        public PUSParcel createFromParcel(Parcel in) {
            return new PUSParcel(in);
        }

        @Override
        public PUSParcel[] newArray(int size) {
            return new PUSParcel[size];
        }
    };

    @JsonSetter("realWeight")
    public void setRealWeight(String realWeight) {
        mRealWeight = realWeight;
    }

    public String getId() {
        return mId;
    }

    public String getCodeNumber() {
        return mCodeNumber;
    }

    public String getRealWeight() {
        return mRealWeight;
    }

    public String getFieldTime() {
        return fieldTime;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public PUSStatus getParcelStatus() {
        return mParcelStatus;
    }

    public String getGeneralDescription() {
        return mGeneralDescription;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public void setParcelStatus(String parcelStatus) {
        for (PUSStatus status : PUSStatus.values()) {
            if (status.toString().equalsIgnoreCase(parcelStatus)) {
                mParcelStatus = status;
            }
        }
    }

    public boolean isWasAnimated() {
        return mWasAnimated;
    }

    public void setWasAnimated(boolean wasAnimated) {
        mWasAnimated = wasAnimated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mCodeNumber);
        parcel.writeString(mRealWeight);
        parcel.writeString(fieldTime);
        parcel.writeString(mPrice);
        parcel.writeString(mCurrency);
        parcel.writeByte((byte) (mWasAnimated ? 1 : 0));
    }

    public enum PUSStatus {
        processing(R.string.processing, "processing", 1),
        held_by_prohibition(R.string.held_by_prohibition, "held-by-prohibition", 2),
        held_by_damage(R.string.held_by_damage, "held-by-damage", 3),
        awaiting_sending(R.string.awaiting_sending, "packed", 4),
        held_due_to_debt(R.string.held_due_to_debt, "dept", 5),
        sent(R.string.sent, "sent", 6),
        held_by_customs(R.string.held_by_customs, "customs-held", 7),
        local_depot(R.string.local_deposit, "local-depo", 8),
        in_the_way(R.string.take_to_delivery, "taken-to-delivery", 9),
        received(R.string.received, "mReceived", 10);

        private final String formatted;
        private final int index;
        @StringRes private int nameStringRes;

        PUSStatus(@StringRes int nameStringId, String formatted, int index) {
            this.formatted = formatted;
            this.index = index;
            this.nameStringRes = nameStringId;
        }

        @Override
        public String toString() {
            return formatted;
        }

        public int index() {
            return index;
        }

        @StringRes
        public int statusName() {
            return nameStringRes;
        }
    }
}
