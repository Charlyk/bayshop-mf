package com.softranger.bayshopmf.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 7/20/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class MailMessage implements Parcelable {
    private String mTitle;
    private String mMessage;
    private String mDate;
    private boolean mIsRead;
    private boolean mIsSelected;

    private MailMessage() {

    }

    protected MailMessage(Parcel in) {
        mTitle = in.readString();
        mMessage = in.readString();
        mDate = in.readString();
        mIsRead = in.readByte() != 0;
        mIsSelected = in.readByte() != 0;
    }

    public static final Creator<MailMessage> CREATOR = new Creator<MailMessage>() {
        @Override
        public MailMessage createFromParcel(Parcel in) {
            return new MailMessage(in);
        }

        @Override
        public MailMessage[] newArray(int size) {
            return new MailMessage[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public boolean isRead() {
        return mIsRead;
    }

    public void setRead(boolean read) {
        mIsRead = read;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mMessage);
        dest.writeString(mDate);
        dest.writeByte((byte) (mIsRead ? 1 : 0));
        dest.writeByte((byte) (mIsSelected ? 1 : 0));
    }

    public static class Builder {
        private String mTitle;
        private String mMessage;
        private String mDate;
        private boolean mIsRead;
        private boolean mIsSelected;

        public Builder title(String title) {
            mTitle = title;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder date(String date) {
            mDate = date;
            return this;
        }

        public Builder isRead(boolean isRead) {
            mIsRead = isRead;
            return this;
        }

        public Builder isSelected(boolean isSelected) {
            mIsSelected = isSelected;
            return this;
        }

        public MailMessage build() {
            MailMessage mailMessage = new MailMessage();
            mailMessage.mTitle = this.mTitle;
            mailMessage.mMessage = this.mMessage;
            mailMessage.mDate = this.mDate;
            mailMessage.mIsRead = this.mIsRead;
            mailMessage.mIsSelected = this.mIsSelected;
            return mailMessage;
        }
    }
}
