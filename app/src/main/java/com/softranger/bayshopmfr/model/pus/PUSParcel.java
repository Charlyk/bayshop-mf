package com.softranger.bayshopmfr.model.pus;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmfr.R;

import java.util.Date;

/**
 * Created by Eduard Albu on 9/14/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PUSParcel implements Parcelable {
    @JsonProperty("id") String mId;
    @JsonProperty("codeNumber") String mCodeNumber;
    @JsonProperty("fieldTime")
    private Date fieldTime;
    @JsonProperty("totalPrice") private String mPrice;
    @JsonProperty("currency") private String mCurrency;
    @JsonProperty("generalDescription") private String mGeneralDescription;
    @JsonProperty("rating")
    protected int mRating;
    @JsonProperty("courierContacts")
    private CourierContacts mCourierContacts;
    @JsonProperty("volumeWeight")
    private double mVolumeWeight;

    private PUSStatus mParcelStatus;
    private String mRealWeight;
    private boolean mIsSelected;

    public PUSParcel() {

    }

    protected PUSParcel(Parcel in) {
        mId = in.readString();
        mCodeNumber = in.readString();
        mRealWeight = in.readString();
        fieldTime = (Date) in.readSerializable();
        mPrice = in.readString();
        mCurrency = in.readString();
        mRating = in.readInt();
        mCourierContacts = in.readParcelable(CourierContacts.class.getClassLoader());
        mVolumeWeight = in.readDouble();
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

    public Date getFieldTime() {
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

    public double getVolumeWeight() {
        return mVolumeWeight;
    }

    public CourierContacts getCourierContacts() {
        return mCourierContacts;
    }

    public int getRating() {
        return mRating;
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
        parcel.writeSerializable(fieldTime);
        parcel.writeString(mPrice);
        parcel.writeString(mCurrency);
        parcel.writeInt(mRating);
        parcel.writeParcelable(mCourierContacts, i);
        parcel.writeDouble(mVolumeWeight);
    }

    public enum PUSStatus {
        processing(R.string.processing_warning, R.string.processing, -1, "processing", 1),
        held_by_prohibition(R.string.held_by_prohibition_warning, R.string.held_by_prohibition, R.layout.prohibition_details_header, "held-by-prohibition", 2),
        held_by_damage(R.string.held_by_damage_warning, R.string.held_by_damage, R.layout.damage_recorded_btn, "held-by-damage", 3),
        awaiting_sending(R.string.awaiting_sending_warning, R.string.awaiting_sending, -1, "packed", 4),
        held_due_to_debt(R.string.held_due_to_debt_warning, R.string.held_due_to_debt, -1, "dept", 5),
        sent(R.string.sent_warning, R.string.sent, R.layout.sent_parcels_header_layout, "sent", 6),
        held_by_customs(R.string.held_by_customs, R.string.held_by_customs, -1, "customs-held", 7),
        local_depot(R.string.local_deposit_warning, R.string.local_deposit, -1, "local-depo", 8),
        in_the_way(R.string.take_to_delivery_warning, R.string.take_to_delivery, R.layout.take_to_delivery_btn, "taken-to-delivery", 9),
        received(R.string.received_warning, R.string.received, R.layout.signature_geolocation_btn, "received", 10),
        held_by_user(R.string.held_by_user_warning, R.string.held_by_user, R.layout.held_by_user_btn, "held-by-user", 11),
        awaiting_declaration(R.string.awaiting_declaration_warning, R.string.awaiting_declaration, R.layout.awaiting_declaration_btn, "awaiting-declaration", 12);

        private final String formatted;
        private final int index;
        @StringRes private int nameStringRes;
        @StringRes
        private int warningStringRes;
        @LayoutRes
        private int buttonsLayout;

        PUSStatus(@StringRes int warningStringRes, @StringRes int nameStringId, @LayoutRes int buttonsLayout, String formatted, int index) {
            this.formatted = formatted;
            this.index = index;
            this.nameStringRes = nameStringId;
            this.buttonsLayout = buttonsLayout;
            this.warningStringRes = warningStringRes;
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

        @StringRes
        public int warning() {
            return warningStringRes;
        }

        @LayoutRes
        public int buttonsLayout() {
            return buttonsLayout;
        }
    }
}
