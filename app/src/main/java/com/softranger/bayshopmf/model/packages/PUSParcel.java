package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;
import android.os.Parcelable;

import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.model.ShippingMethod;

import java.util.ArrayList;

/**
 * Created by macbook on 6/14/16.
 */
public class PUSParcel implements Parcelable {
    protected int mId;
    protected String mCodeNumber;
    protected String mName;
    protected int mRealWeght; // in grams
    protected String mDeposit;
    protected String mTrackingNumber;
    protected Address mAddress;
    protected ShippingMethod mShippingMethod;
    protected ArrayList<Product> mProducts;
    protected String mCurrency;
    protected String mStatus;
    protected double mTotalPrice;
    protected double mDeliveryPrice;
    protected double mInsuranceCommission;
    protected String mCreated;
    protected double mCostumsClearance;
    protected int mPercentage;
    protected String mPackedTime;
    protected String mLocalDepotTime;
    protected String mReceivedTime;
    protected String mSentTime;
    protected String mProhibitionHeldReason;
    protected String mTakenToDeliveryTime;
    protected String mCustomsHeldTime;

    public PUSParcel() {

    }

    protected PUSParcel(Parcel in) {
        mId = in.readInt();
        mCodeNumber = in.readString();
        mName = in.readString();
        mRealWeght = in.readInt();
        mDeposit = in.readString();
        mTrackingNumber = in.readString();
        mAddress = in.readParcelable(Address.class.getClassLoader());
        mShippingMethod = in.readParcelable(ShippingMethod.class.getClassLoader());
        in.readTypedList(mProducts, Product.CREATOR);
        mCurrency = in.readString();
        mStatus = in.readString();
        mTotalPrice = in.readDouble();
        mDeliveryPrice = in.readDouble();
        mInsuranceCommission = in.readDouble();
        mCreated = in.readString();
        mCostumsClearance = in.readDouble();
        mPercentage = in.readInt();
        mTrackingNumber = in.readString();
        mPackedTime = in.readString();
        mLocalDepotTime = in.readString();
        mReceivedTime = in.readString();
        mSentTime = in.readString();
        mProhibitionHeldReason = in.readString();
        mTakenToDeliveryTime = in.readString();
        mCustomsHeldTime = in.readString();
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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCodeNumber() {
        return mCodeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        mCodeNumber = codeNumber;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getRealWeght() {
        return mRealWeght;
    }

    public void setRealWeght(int realWeght) {
        mRealWeght = realWeght;
    }

    public String getDeposit() {
        return mDeposit;
    }

    public void setDeposit(String deposit) {
        mDeposit = deposit;
    }

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        mTrackingNumber = trackingNumber;
    }

    public Address getAddress() {
        return mAddress;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    public ShippingMethod getShippingMethod() {
        return mShippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        mShippingMethod = shippingMethod;
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    public void setProducts(ArrayList<Product> products) {
        mProducts = products;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public double getTotalPrice() {
        return mTotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        mTotalPrice = totalPrice;
    }

    public double getDeliveryPrice() {
        return mDeliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        mDeliveryPrice = deliveryPrice;
    }

    public double getInsuranceCommission() {
        return mInsuranceCommission;
    }

    public void setInsuranceCommission(double insuranceCommission) {
        mInsuranceCommission = insuranceCommission;
    }

    public String getCreated() {
        return mCreated;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    public double getCostumsClearance() {
        return mCostumsClearance;
    }

    public void setCostumsClearance(double costumsClearance) {
        mCostumsClearance = costumsClearance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mCodeNumber);
        dest.writeString(mName);
        dest.writeInt(mRealWeght);
        dest.writeString(mDeposit);
        dest.writeString(mTrackingNumber);
        dest.writeParcelable(mAddress, flags);
        dest.writeParcelable(mShippingMethod, flags);
        dest.writeTypedList(mProducts);
        dest.writeString(mCurrency);
        dest.writeString(mStatus);
        dest.writeDouble(mTotalPrice);
        dest.writeDouble(mDeliveryPrice);
        dest.writeDouble(mInsuranceCommission);
        dest.writeString(mCreated);
        dest.writeDouble(mCostumsClearance);
        dest.writeInt(mPercentage);
        dest.writeString(mTrackingNumber);
        dest.writeString(mPackedTime);
        dest.writeString(mLocalDepotTime);
        dest.writeString(mReceivedTime);
        dest.writeString(mSentTime);
        dest.writeString(mProhibitionHeldReason);
        dest.writeString(mTakenToDeliveryTime);
        dest.writeString(mCustomsHeldTime);
    }

    public static class Builder<T extends PUSParcel> {
        private T mParcel;

        public Builder(T parcel) {
            this.mParcel = parcel;
        }

        public Builder<T> id(int id) {
            mParcel.mId = id;
            return this;
        }

        public Builder<T> codeNumber(String codeNumber) {
            mParcel.mCodeNumber = codeNumber;
            return this;
        }

        public Builder<T> name(String name) {
            mParcel.mName = name;
            return this;
        }

        public Builder<T> realWeight(int realWeght) {
            mParcel.mRealWeght = realWeght;
            return this;
        }

        public Builder<T> deposit(String deposit) {
            mParcel.mDeposit = deposit;
            return this;
        }

        public Builder<T> trackingNumber(String trackingNumber) {
            mParcel.mTrackingNumber = trackingNumber;
            return this;
        }

        public Builder<T> address(Address address) {
            mParcel.mAddress = address;
            return this;
        }

        public Builder<T> shippingMethod(ShippingMethod shippingMethod) {
            mParcel.mShippingMethod = shippingMethod;
            return this;
        }

        public Builder<T> products(ArrayList<Product> products) {
            mParcel.mProducts = products;
            return this;
        }

        public Builder<T> currency(String currency) {
            mParcel.mCurrency = currency;
            return this;
        }

        public Builder<T> status(String status) {
            mParcel.mStatus = status;
            return this;
        }

        public Builder<T> totalPrice(double totalPrice) {
            mParcel.mTotalPrice = totalPrice;
            return this;
        }

        public Builder<T> deliveryPrice(double deliveryPrice) {
            mParcel.mDeliveryPrice = deliveryPrice;
            return this;
        }

        public Builder<T> insuranceCommission(double insuranceCommission) {
            mParcel.mInsuranceCommission = insuranceCommission;
            return this;
        }

        public Builder<T> created(String created) {
            mParcel.mCreated = created;
            return this;
        }

        public Builder<T> costumsClearance(double costumsClearance) {
            mParcel.mCostumsClearance = costumsClearance;
            return this;
        }

        public Builder<T> percentage(int percentage) {
            mParcel.mPercentage = percentage;
            return this;
        }

        public Builder<T> packedTime(String packedTime) {
            mParcel.mPackedTime = packedTime;
            return this;
        }

        public Builder<T> localDepotTime(String localDepotTime) {
            mParcel.mLocalDepotTime = localDepotTime;
            return this;
        }

        public Builder<T> receivedTime(String receivedTime) {
            mParcel.mReceivedTime = receivedTime;
            return this;
        }

        public Builder<T> sentTime(String sentTime) {
            mParcel.mSentTime = sentTime;
            return this;
        }

        public Builder<T> prohibitionHeldReason(String prohibitionHeldReason) {
            mParcel.mProhibitionHeldReason = prohibitionHeldReason;
            return this;
        }

        public Builder<T> takenToDeliveryTime(String takenToDeliveryTime) {
            mParcel.mTakenToDeliveryTime = takenToDeliveryTime;
            return this;
        }

        public Builder<T> customsHeldTime(String customsHeldTime) {
            mParcel.mCustomsHeldTime = customsHeldTime;
            return this;
        }

        public T build() {
            return mParcel;
        }
    }
}
