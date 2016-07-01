package com.softranger.bayshopmf.util;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Eduard Albu on 7/1/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public abstract class ParentActivity extends AppCompatActivity {

    public abstract void setToolbarTitle(String title, boolean showIcon);

    public abstract void addFragment(Fragment fragment, boolean showAnimation);

    public abstract void toggleLoadingProgress(boolean show);

    public abstract void replaceFragment(Fragment fragment);
}
