package com.softranger.bayshopmfr.model.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 9/21/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class InStockList {
    @JsonProperty("list") private ArrayList<InStock> mInStocks;

    public ArrayList<InStock> getInStocks() {
        return mInStocks;
    }
}
