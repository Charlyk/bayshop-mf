package com.softranger.bayshopmf.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduard Albu on 7/21/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ChatMessage implements Parcelable {
    private String mDate;
    private String mMessage;
    private String mAuthor;
    private MessageType mMessageType;

    private ChatMessage() {

    }

    protected ChatMessage(Parcel in) {
        mDate = in.readString();
        mMessage = in.readString();
        mAuthor = in.readString();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public MessageType getMessageType() {
        return mMessageType;
    }

    public void setMessageType(MessageType messageType) {
        mMessageType = messageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDate);
        dest.writeString(mMessage);
        dest.writeString(mAuthor);
    }

    public static class Builder {
        private String mDate;
        private String mMessage;
        private String mAuthor;
        private MessageType mMessageType;

        public Builder date(String date) {
            mDate = date;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder author(String author) {
            mAuthor = author;
            return this;
        }

        public Builder messageType(MessageType messageType) {
            mMessageType = messageType;
            return this;
        }

        public ChatMessage build() {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.mDate = this.mDate;
            chatMessage.mMessage = this.mMessage;
            chatMessage.mAuthor = this.mAuthor;
            chatMessage.mMessageType = this.mMessageType;
            return chatMessage;
        }
    }

    public enum MessageType {
        income, outgoing
    }
}
