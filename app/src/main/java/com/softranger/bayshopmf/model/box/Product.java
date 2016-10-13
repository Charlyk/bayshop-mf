package com.softranger.bayshopmf.model.box;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Eduard Albu on 9/22/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product implements Parcelable {
    @JsonProperty("title") private String mTitle;
    @JsonProperty("quantity") private String mQuantity;
    @JsonProperty("price") private String mPrice;
    @JsonProperty("url") private String mUrl;
    @JsonProperty("declarationItemId") private String mItemId;
    @JsonProperty("waitingMfId")
    private String mWaitingMfId;

    public Product() {

    }

    protected Product(Parcel in) {
        mTitle = in.readString();
        mQuantity = in.readString();
        mPrice = in.readString();
        mUrl = in.readString();
        mItemId = in.readString();
        mWaitingMfId = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getTitle() {
        if (mTitle == null) return "";
        return mTitle;
    }

    public String getQuantity() {
        if (mQuantity == null) return "";
        return mQuantity;
    }

    public String getPrice() {
        if (mPrice == null) return "";
        return mPrice;
    }

    public String getUrl() {
        if (mUrl == null) return "";
        return mUrl;
    }

    public String getItemId() {
        if (mUrl == null) return "";
        return mItemId;
    }


    @JsonIgnore
    public void setTitle(String title) {
        mTitle = title;
    }

    @JsonIgnore
    public void setQuantity(String quantity) {
        mQuantity = quantity;
    }

    @JsonIgnore
    public void setPrice(String price) {
        mPrice = price;
    }

    @JsonIgnore
    public void setUrl(String url) {
        mUrl = url;
    }

    @JsonSetter("id")
    public void setItemId(String itemId) {
        mItemId = itemId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mQuantity);
        dest.writeString(mPrice);
        dest.writeString(mUrl);
        dest.writeString(mItemId);
        dest.writeString(mWaitingMfId);
    }
}
