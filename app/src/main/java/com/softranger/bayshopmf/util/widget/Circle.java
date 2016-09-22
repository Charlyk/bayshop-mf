package com.softranger.bayshopmf.util.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;

import com.softranger.bayshopmf.R;

/**
 * Created by Eduard Albu on 9/22/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class Circle extends View {

    private static final int START_ANGLE_POINT = 270; // start from top

    private final Paint paint;
    private static RectF rect;

    private final int STROKE_WIDTH = 10;
    private Context mContext;

    @ColorRes
    private static int strokeColor = R.color.colorGreenAction;

    private float angle;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        //Circle color
        mContext = context;
        paint.setColor(mContext.getResources().getColor(strokeColor));

        //Initial Angle (optional, it can be zero)
        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (rect == null) {
            int width = getWidth();
            int height = getHeight();
            int halfStrokeWidht = STROKE_WIDTH / 2;
            rect = new RectF(STROKE_WIDTH, STROKE_WIDTH, width - halfStrokeWidht, height - halfStrokeWidht);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setStrokeColor(@ColorRes int strokeColor) {
        Circle.strokeColor = strokeColor;
        paint.setColor(mContext.getResources().getColor(strokeColor));
    }
}
