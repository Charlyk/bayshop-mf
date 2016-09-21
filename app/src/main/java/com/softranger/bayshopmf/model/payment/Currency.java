package com.softranger.bayshopmf.model.payment;

import android.support.annotation.DrawableRes;

/**
 * Created by Eduard Albu on 6/30/16, 06, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class Currency {
    private static final String USD = "USD", EURO = "Euro", GBP = "GBP", ALL = "All";
    private String mName;
    private String mSymbol;
    private CurrencyType mCurrencyType;
    @DrawableRes private int mIconId;

    public Currency(String name, int iconId) {
        mName = name;
        mIconId = iconId;
    }

    public String getName() {
        return mName;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }

    public CurrencyType getCurrencyType() {
        return mCurrencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        mCurrencyType = currencyType;
    }

    public enum CurrencyType {
        USD, Euro, GBP, All;

        public static CurrencyType getCurrencyType(String name) {
            switch (name) {
                case Currency.USD: return USD;
                case Currency.EURO: return Euro;
                case Currency.GBP: return GBP;
                default: return All;
            }
        }
    }
}
