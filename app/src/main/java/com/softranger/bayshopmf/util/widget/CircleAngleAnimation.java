package com.softranger.bayshopmf.util.widget;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Eduard Albu on 9/22/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class CircleAngleAnimation extends Animation {

    private Circle circle;

    private float oldAngle;
    private float newAngle;

    public CircleAngleAnimation(Circle circle, int newAngle) {
        this.oldAngle = circle.getAngle();
        this.newAngle = newAngle;
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);

        circle.setAngle(angle);
        circle.requestLayout();
    }
}