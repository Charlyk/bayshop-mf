package com.softranger.bayshopmfr.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.softranger.bayshopmfr.R;

/**
 * Created by Eduard Albu on 10/28/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationMessage implements Parcelable {
    @JsonProperty("code")
    private String mCode;
    @JsonProperty("storage_code")
    private String mStorageCode;
    @JsonProperty("title")
    private String mTitle;
    @JsonProperty("message")
    private String mMessage;
    @JsonProperty("id")
    private String mId;
    @JsonIgnore
    private Action mAction;

    public NotificationMessage() {

    }

    protected NotificationMessage(Parcel in) {
        mCode = in.readString();
        mStorageCode = in.readString();
        mTitle = in.readString();
        mMessage = in.readString();
        mId = in.readString();
    }

    public static final Creator<NotificationMessage> CREATOR = new Creator<NotificationMessage>() {
        @Override
        public NotificationMessage createFromParcel(Parcel in) {
            return new NotificationMessage(in);
        }

        @Override
        public NotificationMessage[] newArray(int size) {
            return new NotificationMessage[size];
        }
    };

    public String getCode() {
        return mCode;
    }

    public String getStorageCode() {
        return mStorageCode;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getId() {
        return mId;
    }

    @JsonSetter("action")
    public void setAction(String strAction) {
        mAction = Action.getActionByString(strAction);
    }

    public Action getAction() {
        return mAction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mCode);
        parcel.writeString(mStorageCode);
        parcel.writeString(mTitle);
        parcel.writeString(mMessage);
        parcel.writeString(mId);
    }

    public enum Action {
        mf(R.mipmap.ic_warehouse_usa_30dp, "mf"),
        waitingMf(R.mipmap.ic_awaiting_arrival_30dp, "waitingMf"),
        parcel(R.mipmap.ic_parcels_30dp, "parcel"),
        unknown(R.mipmap.ic_warehouse_usa_30dp, "");

        private String mActionName;
        @DrawableRes
        private int mSmallIcon;

        Action(@DrawableRes int smallIcon, String actionName) {
            mActionName = actionName;
            mSmallIcon = smallIcon;
        }

        @DrawableRes
        public int icon() {
            return mSmallIcon;
        }

        public String getActionName() {
            return mActionName;
        }

        public static Action getActionByString(String strAction) {
            for (Action action : values()) {
                if (action.getActionName().equalsIgnoreCase(strAction)) {
                    return action;
                }
            }
            return unknown;
        }
    }
}
