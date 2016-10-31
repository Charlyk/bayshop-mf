package com.softranger.bayshopmfr.model.chat;

import com.softranger.bayshopmfr.util.SpinnerObj;

/**
 * Created by Eduard Albu on 7/21/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class MailCategory implements SpinnerObj {

    private String mName;
    private int mId;

    public MailCategory(String name, int id) {
        mName = name;
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int getId() {
        return mId;
    }
}
