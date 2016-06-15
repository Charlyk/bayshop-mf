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
public class Package implements Parcelable {
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

    protected Package() {

    }

    protected Package(Parcel in) {
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
    }

    public static final Creator<Package> CREATOR = new Creator<Package>() {
        @Override
        public Package createFromParcel(Parcel in) {
            return new Package(in);
        }

        @Override
        public Package[] newArray(int size) {
            return new Package[size];
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
    }

    public static class Builder {
        protected int mId;
        protected String mCodeNumber;
        protected String mName;
        protected int mRealWeght;
        protected String mDeposit;
        protected String mTrackingNumber;
        protected Address mAddress;
        protected ShippingMethod mShippingMethod;
        protected ArrayList<Product> mProducts;
        protected String mCurrency;

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder codeNumber(String codeNumber) {
            mCodeNumber = codeNumber;
            return this;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder realWeight(int realWeght) {
            mRealWeght = realWeght;
            return this;
        }

        public Builder deposit(String deposit) {
            mDeposit = deposit;
            return this;
        }

        public Builder trackingNumber(String trackingNumber) {
            mTrackingNumber = trackingNumber;
            return this;
        }

        public Builder address(Address address) {
            mAddress = address;
            return this;
        }

        public Builder shippingMethod(ShippingMethod shippingMethod) {
            mShippingMethod = shippingMethod;
            return this;
        }

        public Builder products(ArrayList<Product> products) {
            mProducts = products;
            return this;
        }

        public Builder currency(String currency) {
            mCurrency = currency;
            return this;
        }

        public Package build() {
            Package pack = new Package();
            pack.mId = this.mId;
            pack.mCodeNumber = this.mCodeNumber;
            pack.mName = this.mName;
            pack.mRealWeght = this.mRealWeght;
            pack.mDeposit = this.mDeposit;
            pack.mTrackingNumber = this.mTrackingNumber;
            pack.mAddress = this.mAddress;
            pack.mShippingMethod = this.mShippingMethod;
            pack.mProducts = this.mProducts;
            pack.mCurrency = this.mCurrency;
            return pack;
        }
    }
}
