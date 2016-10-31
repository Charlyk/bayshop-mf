package com.softranger.bayshopmfr.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eduard Albu on 10/5/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class PaymentHistories {
    @JsonProperty("balanceList")
    private HashMap<String, ArrayList<History>> mHistoriesMap;

    public PaymentHistories() {
        // empty constructor for jackson
    }

    public HashMap<String, ArrayList<History>> getHistoriesMap() {
        return mHistoriesMap;
    }
}
