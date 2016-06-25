package com.softranger.bayshopmf.model.packages;

import android.os.Parcel;
import android.os.Parcelable;

import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.model.ShippingMethod;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/31/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class InForming implements Parcelable {
    private int mId;
    private String mGeneralDescription;
    private String mCodeNumber;
    private String mName;
    private int mWeight;
    private String mCreatedDate;
    private String mUid;
    private boolean mHasBattery;
    private ArrayList<InStockItem> mItems;
    private String mDeposit;
    private Address mAddress;
    private ShippingMethod mShippingMethod;
    private ArrayList<Product> mProducts;
    private boolean mNeedInsurance;
    private double mGoodsPrice;
    private double mTotalPrice;
    private double mShippingPrice;
    private double mInsurancePrice;
    private double mDeclarationPrice;
    private String mCurrency;
    private boolean mAdditionalPackage;
    private boolean mSentOnUserAlert;
    private boolean mLocalDelivery;

    private InForming() {

    }

    protected InForming(Parcel in) {
        mId = in.readInt();
        mGeneralDescription = in.readString();
        mCodeNumber = in.readString();
        mName = in.readString();
        mWeight = in.readInt();
        mCreatedDate = in.readString();
        mUid = in.readString();
        mHasBattery = in.readByte() != 0;
        mItems = in.createTypedArrayList(InStockItem.CREATOR);
        mDeposit = in.readString();
        mAddress = in.readParcelable(Address.class.getClassLoader());
        mShippingMethod = in.readParcelable(ShippingMethod.class.getClassLoader());
        mProducts = in.createTypedArrayList(Product.CREATOR);
        mNeedInsurance = in.readByte() != 0;
        mGoodsPrice = in.readDouble();
        mTotalPrice = in.readDouble();
        mShippingPrice = in.readDouble();
        mInsurancePrice = in.readDouble();
        mDeclarationPrice = in.readDouble();
        mCurrency = in.readString();
        mAdditionalPackage = in.readByte() != 0;
        mSentOnUserAlert = in.readByte() != 0;
        mLocalDelivery = in.readByte() != 0;
    }

    public static final Creator<InForming> CREATOR = new Creator<InForming>() {
        @Override
        public InForming createFromParcel(Parcel in) {
            return new InForming(in);
        }

        @Override
        public InForming[] newArray(int size) {
            return new InForming[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getGeneralDescription() {
        return mGeneralDescription;
    }

    public void setGeneralDescription(String generalDescription) {
        mGeneralDescription = generalDescription;
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

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public boolean isHasBattery() {
        return mHasBattery;
    }

    public void setHasBattery(boolean hasBattery) {
        mHasBattery = hasBattery;
    }

    public ArrayList<InStockItem> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<InStockItem> items) {
        mItems = items;
    }

    public String getDeposit() {
        return mDeposit;
    }

    public void setDeposit(String deposit) {
        mDeposit = deposit;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    public Address getAddress() {
        return mAddress;
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

    public boolean isNeedInsurance() {
        return mNeedInsurance;
    }

    public void setNeedInsurance(boolean needInsurance) {
        mNeedInsurance = needInsurance;
    }

    public double getGoodsPrice() {
        return mGoodsPrice;
    }

    public void setGoodsPrice(double goodsPrice) {
        mGoodsPrice = goodsPrice;
    }

    public double getTotalPrice() {
        return mTotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        mTotalPrice = totalPrice;
    }

    public double getShippingPrice() {
        return mShippingPrice;
    }

    public void setShippingPrice(double shippingPrice) {
        mShippingPrice = shippingPrice;
    }

    public double getInsurancePrice() {
        return mInsurancePrice;
    }

    public void setInsurancePrice(double insurancePrice) {
        mInsurancePrice = insurancePrice;
    }

    public double getDeclarationPrice() {
        return mDeclarationPrice;
    }

    public void setDeclarationPrice(double declarationPrice) {
        mDeclarationPrice = declarationPrice;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public boolean isAdditionalPackage() {
        return mAdditionalPackage;
    }

    public void setAdditionalPackage(boolean additionalPackage) {
        mAdditionalPackage = additionalPackage;
    }

    public boolean isSentOnUserAlert() {
        return mSentOnUserAlert;
    }

    public void setSentOnUserAlert(boolean sentOnUserAlert) {
        mSentOnUserAlert = sentOnUserAlert;
    }

    public boolean isLocalDelivery() {
        return mLocalDelivery;
    }

    public void setLocalDelivery(boolean localDelivery) {
        mLocalDelivery = localDelivery;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mGeneralDescription);
        dest.writeString(mCodeNumber);
        dest.writeString(mName);
        dest.writeInt(mWeight);
        dest.writeString(mCreatedDate);
        dest.writeString(mUid);
        dest.writeByte((byte) (mHasBattery ? 1 : 0));
        dest.writeTypedList(mItems);
        dest.writeString(mDeposit);
        dest.writeParcelable(mAddress, flags);
        dest.writeParcelable(mShippingMethod, flags);
        dest.writeTypedList(mProducts);
        dest.writeByte((byte) (mNeedInsurance ? 1 : 0));
        dest.writeDouble(mGoodsPrice);
        dest.writeDouble(mTotalPrice);
        dest.writeDouble(mShippingPrice);
        dest.writeDouble(mInsurancePrice);
        dest.writeDouble(mDeclarationPrice);
        dest.writeString(mCurrency);
        dest.writeByte((byte) (mAdditionalPackage ? 1 : 0));
        dest.writeByte((byte) (mSentOnUserAlert ? 1 : 0));
        dest.writeByte((byte) (mLocalDelivery? 1 : 0));
    }


    public static class Builder {
        private int mId;
        private String mGeneralDescription;
        private String mCodeNumber;
        private String mName;
        private int mWeight;
        private String mCreatedDate;
        private String mUid;
        private boolean mHasBattery;
        private ArrayList<InStockItem> mItems;
        private String mDeposit;
        private Address mAddress;
        private ShippingMethod mShippingMethod;
        private ArrayList<Product> mProducts;
        private boolean mNeedInsurance;
        private double mGoodsPrice;
        private double mTotalPrice;
        private double mShippingPrice;
        private double mInsurancePrice;
        private double mDeclarationPrice;
        private String mCurrency;
        private boolean mAdditionalPackage;
        private boolean mSentOnUserAlert;
        private boolean mLocalDelivery;

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder generalDescription(String generalDescription) {
            mGeneralDescription = generalDescription;
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

        public Builder weight(int weight) {
            mWeight = weight;
            return this;
        }

        public Builder createdDate(String createdDate) {
            mCreatedDate = createdDate;
            return this;
        }

        public Builder uid(String uid) {
            mUid = uid;
            return this;
        }

        public Builder hasBattery(int hasBattery) {
            mHasBattery = hasBattery == 1;
            return this;
        }

        public Builder items(ArrayList<InStockItem> items) {
            mItems = items;
            return this;
        }

        public Builder deposit(String deposit) {
            mDeposit = deposit;
            return this;
        }

        public Builder address(Address address) {
            mAddress = address;
            return this;
        }

        public Builder shippingMethod(ShippingMethod method) {
            mShippingMethod = method;
            return this;
        }

        public Builder products(ArrayList<Product> products) {
            mProducts = products;
            return this;
        }

        public Builder needInsurance(boolean needInsurance) {
            mNeedInsurance = needInsurance;
            return this;
        }

        public Builder goodsPrice(double goodsPrice) {
            mGoodsPrice = goodsPrice;
            return this;
        }

        public Builder totalPrice(double totalPrice) {
            mTotalPrice = totalPrice;
            return this;
        }

        public Builder shippingPrice(double shippingPrice) {
            mShippingPrice = shippingPrice;
            return this;
        }

        public Builder insurancePrice(double insurancePrice) {
            mInsurancePrice = insurancePrice;
            return this;
        }

        public Builder declarationPrice(double declarationPrice) {
            mDeclarationPrice = declarationPrice;
            return this;
        }

        public Builder currency(String currency) {
            mCurrency = currency;
            return this;
        }

        public Builder additionalPackage(boolean additionalPackage) {
            mAdditionalPackage = additionalPackage;
            return this;
        }

        public Builder sentOnUserAlert(boolean sentOnUserAlert) {
            mSentOnUserAlert = sentOnUserAlert;
            return this;
        }

        public Builder localDelivery(boolean localDelivery) {
            mLocalDelivery = localDelivery;
            return this;
        }

        public InForming build() {
            InForming inForming = new InForming();
            inForming.mId = this.mId;
            inForming.mGeneralDescription = this.mGeneralDescription;
            inForming.mCodeNumber = this.mCodeNumber;
            inForming.mName = this.mName;
            inForming.mWeight = this.mWeight;
            inForming.mCreatedDate = this.mCreatedDate;
            inForming.mUid = this.mUid;
            inForming.mHasBattery = this.mHasBattery;
            inForming.mItems = this.mItems;
            inForming.mDeposit = this.mDeposit;
            inForming.mAddress = this.mAddress;
            inForming.mShippingMethod = this.mShippingMethod;
            inForming.mProducts = this.mProducts;
            inForming.mNeedInsurance = this.mNeedInsurance;
            inForming.mGoodsPrice = this.mGoodsPrice;
            inForming.mTotalPrice = this.mTotalPrice;
            inForming.mShippingPrice = this.mShippingPrice;
            inForming.mDeclarationPrice = this.mDeclarationPrice;
            inForming.mInsurancePrice = this.mInsurancePrice;
            inForming.mCurrency = this.mCurrency;
            inForming.mAdditionalPackage = this.mAdditionalPackage;
            inForming.mSentOnUserAlert = this.mSentOnUserAlert;
            inForming.mLocalDelivery = this.mLocalDelivery;
            return inForming;
        }
    }
}
