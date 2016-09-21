package com.softranger.bayshopmf.util;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

/**
 * Created by Eduard Albu on 5/26/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ColorGroupSectionTitleIndicator extends SectionTitleIndicator<Character> {

    public ColorGroupSectionTitleIndicator(Context context) {
        super(context);
    }

    public ColorGroupSectionTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorGroupSectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(Character addressGroup) {
        // Example of using a single character
        setTitleText(addressGroup + "");

        // Example of using a longer string
        // setTitleText(colorGroup.getName());

        setIndicatorTextColor(Color.parseColor("#ffffff"));
    }

}