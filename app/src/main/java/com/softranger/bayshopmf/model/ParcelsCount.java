package com.softranger.bayshopmf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.softranger.bayshopmf.util.Constants;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by Eduard Albu on 9/21/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParcelsCount {
    @JsonProperty("live") private int mLive;
    @JsonProperty("processing") private int mProcessing;
    @JsonProperty("held-by-damage") private int mHeldByDamage;
    @JsonProperty("held-by-prohibition") private int mHeldByProhibition;
    @JsonProperty("packed") private int mPacked;
    @JsonProperty("dept") private int mDept;
    @JsonProperty("sent") private int mSent;
    @JsonProperty("customs-held") private int mCustomsHeld;
    @JsonProperty("local-depo") private int mLocalDepot;
    @JsonProperty("taken-to-delivery") private int mTakenToDelivery;
    @JsonProperty("received") private int mReceived;
    @JsonProperty("mf") private int mMF;
    @JsonProperty("waitingMf") private int mWaitingMF;

    public int getLive() {
        return mLive;
    }

    public int getProcessing() {
        return mProcessing;
    }

    public int getHeldByDamage() {
        return mHeldByDamage;
    }

    public int getHeldByProhibition() {
        return mHeldByProhibition;
    }

    public int getPacked() {
        return mPacked;
    }

    public int getDept() {
        return mDept;
    }

    public int getSent() {
        return mSent;
    }

    public int getCustomsHeld() {
        return mCustomsHeld;
    }

    public int getLocalDepot() {
        return mLocalDepot;
    }

    public int getTakenToDelivery() {
        return mTakenToDelivery;
    }

    public int getReceived() {
        return mReceived;
    }

    public int getMF() {
        return mMF;
    }

    public int getWaitingMF() {
        return mWaitingMF;
    }

    public HashMap<String, Integer> getCountersMap() throws IllegalAccessException {
        Field[] fields = ParcelsCount.class.getDeclaredFields();
        HashMap<String, Integer> countersMap = new HashMap<>();

        int parcelsCount = 0;
        for (Field field : fields) {
            String status = CountersName.getByStringName(field.getName()).counterName();
            int value = field.getInt(this);

            if (status.equals(Constants.ParcelStatus.AWAITING_ARRIVAL) || status.equals(Constants.ParcelStatus.IN_STOCK) ||
                    status.equals(Constants.ParcelStatus.RECEIVED)) {
                countersMap.put(status, value);
            } else {
                parcelsCount = parcelsCount + value;
            }
        }

        countersMap.put(Constants.PARCELS, parcelsCount);

        return countersMap;
    }

    private enum CountersName {

        mSent("sent"),
        mDept("dept"),
        mCustomsHeld("customs-held"),
        mHeldByProhibition("held-by-prohibition"),
        mTakenToDelivery("taken-to-delivery"),
        mLive("live"),
        mReceived("received"),
        mLocalDepot("local-depo"),
        mHeldByDamage("held-by-damage"),
        mWaitingMF("waitingMf"),
        mPacked("packed"),
        mProcessing("processing"),
        mMF("mf"),
        mUnknown("unknown");

        private String mCounterName;

        CountersName(String counterName) {
            mCounterName = counterName;
        }

        public String counterName() {
            return mCounterName;
        }

        public static CountersName getByStringName(String counterName) {
            for (CountersName name : CountersName.values()) {
                if (name.name().equals(counterName)) return name;
            }

            return mUnknown;
        }
    }
}
