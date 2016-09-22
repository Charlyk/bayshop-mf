package com.softranger.bayshopmf.model.box;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 9/22/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class Declaration implements Parcelable {
    @JsonProperty("title") private String mTitle;
    @JsonProperty("declarationItems") private ArrayList<Product> mProducts;

    public Declaration() {

    }

    protected Declaration(Parcel in) {
        mTitle = in.readString();
        mProducts = in.createTypedArrayList(Product.CREATOR);
    }

    public static final Creator<Declaration> CREATOR = new Creator<Declaration>() {
        @Override
        public Declaration createFromParcel(Parcel in) {
            return new Declaration(in);
        }

        @Override
        public Declaration[] newArray(int size) {
            return new Declaration[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<Product> getProducts() {
        if (mProducts == null) mProducts = new ArrayList<>();
        return mProducts;
    }

    @JsonIgnore
    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeTypedList(mProducts);
    }
}
