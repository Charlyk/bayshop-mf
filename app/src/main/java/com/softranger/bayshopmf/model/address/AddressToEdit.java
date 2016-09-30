package com.softranger.bayshopmf.model.address;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Eduard Albu on 9/30/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class AddressToEdit {
    @JsonProperty("maskFormatsAll") private ArrayList<CountryCode> mCountryCodes;
    @JsonProperty("countries") private Map<String, String> mCountries;
    @JsonProperty("address") private Address mAddress;

    public ArrayList<CountryCode> getCountryCodes() {
        return mCountryCodes;
    }

    public ArrayList<Country> getCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        for (Map.Entry<String, String> set : mCountries.entrySet()) {
            Country country = new Country.Builder()
                    .id(Integer.parseInt(set.getKey()))
                    .name(set.getValue())
                    .build();
            countries.add(country);
        }
        return countries;
    }

    public Address getAddress() {
        return mAddress;
    }
}
