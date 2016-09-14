package com.softranger.bayshopmf.util.widget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;

import java.util.HashMap;

/**
 * Created by Eduard Albu on 9/14/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class ParcelStatusBarView extends RelativeLayout {

    private Context mContext;
    private LinearLayout mStatusBarHolder;
    private View mStatusIndicator;
    private TextView mStatusNameLabel;
    private int mTotalWidth;
    private int mStatusesCount;
    private int mOneStatusWidth;
    private int mCurrentProgress;
    private Interpolator mInterpolator;
    private static float parentLeft;
    private static float parentRight;
    private HashMap<Integer, BarColor> mColors;

    public ParcelStatusBarView(Context context) {
        super(context);
        initializeView(context);
    }

    public ParcelStatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public ParcelStatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(context);
    }

    private void initializeView(Context context) {
        mContext = context;

        mColors = new HashMap<>();
        mColors.put(0, BarColor.green); // initial state
        mColors.put(1, BarColor.green); // processing
        mColors.put(2, BarColor.red); // held by prohibition
        mColors.put(3, BarColor.red); // held by damage
//        mColors.put(4, BarColor.green); // packing
        mColors.put(4, BarColor.green); // awaiting sending
        mColors.put(5, BarColor.red); // held due to debt
//        mColors.put(6, BarColor.red); // held by user
        mColors.put(6, BarColor.green); // sent
        mColors.put(7, BarColor.red); // held by customs
        mColors.put(8, BarColor.green); // local depot
        mColors.put(9, BarColor.green); // in the way;

        mInterpolator = new AccelerateDecelerateInterpolator();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.status_bar_view, this);
        mStatusBarHolder = (LinearLayout) rootView.findViewById(R.id.statusBarHolderLayout);

        mStatusesCount = 9;
        mStatusBarHolder.measure(MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED);
        mTotalWidth = mStatusBarHolder.getWidth();

        mOneStatusWidth = mTotalWidth / mStatusesCount;

        mStatusIndicator = rootView.findViewById(R.id.statusBarStatusIndicator);
        mStatusNameLabel = (TextView) rootView.findViewById(R.id.statusViewNameLabel);

        setProgress(mCurrentProgress, "");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mTotalWidth = mStatusBarHolder.getWidth();

        mOneStatusWidth = mTotalWidth / mStatusesCount;

        Rect parentRect = new Rect();
        mStatusBarHolder.getGlobalVisibleRect(parentRect);
        parentLeft = parentRect.left;
        parentRight = parentRect.right;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mInterpolator = new AccelerateDecelerateInterpolator();
        mStatusBarHolder = (LinearLayout) findViewById(R.id.statusBarHolderLayout);

        mStatusesCount = 9;

        mStatusIndicator = findViewById(R.id.statusBarStatusIndicator);
        mStatusNameLabel = (TextView) findViewById(R.id.statusViewNameLabel);

        setProgress(mCurrentProgress, "");
    }

    public void setStatusesCount(int statusesCount) {
        mStatusesCount = statusesCount;
    }

    public void setProgress(int progress, String statusName) {
        // check if given progress is not greater then max progress
        // if it is greater just set it equal to max progress
        if (progress > mStatusesCount) progress = mStatusesCount;
        // set current progress for get method
        mCurrentProgress = progress;

        switch (mColors.get(progress)) {
            case red:
                mStatusNameLabel.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                mStatusIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.red_status_bg));
                break;
            case green:
                mStatusNameLabel.setTextColor(mContext.getResources().getColor(R.color.colorGreenAction));
                mStatusIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.green_status_bg));
                break;
        }

        // compute the width which we should get at the end
        float toWidth = mOneStatusWidth * progress;
        // get indicator current width
        float fromWidth = mStatusIndicator.getLayoutParams().width;
        // if we have already max width just stop here
        if (fromWidth == toWidth) return;
        // create a value animator to animate the indicator progress
        ValueAnimator indicatorAnimator = ValueAnimator.ofFloat(fromWidth, toWidth);
        indicatorAnimator.addUpdateListener(mIndicatorAnimatorListener);

        // create a value animator to move the text view
        float translationX = mStatusNameLabel.getTranslationX();
        ValueAnimator textAnimator = ValueAnimator.ofFloat(translationX, fromWidth); // set from width so the indicator will be at the center of text view
        textAnimator.addUpdateListener(mNameAnimatorListener);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(indicatorAnimator, textAnimator);
        set.setDuration(500);
        set.setInterpolator(mInterpolator);
        set.start();
        // set stetus title
        mStatusNameLabel.setText(statusName);
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setColor(int position, BarColor color) {
        mColors.put(position, color);
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    private ValueAnimator.AnimatorUpdateListener mIndicatorAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float animatedValue = (float) valueAnimator.getAnimatedValue();
            // set the with for indicator each time this method is called
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mStatusIndicator.getLayoutParams();
            layoutParams.width = (int) animatedValue;
            mStatusIndicator.setLayoutParams(layoutParams);
        }
    };

    private ValueAnimator.AnimatorUpdateListener mNameAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float animatedValue = (float) valueAnimator.getAnimatedValue();
            // get text rectangle
            Rect textRect = new Rect();
            mStatusNameLabel.getGlobalVisibleRect(textRect);
            // check if the rectangle is not at the edge of the screen
            if (textRect.right < parentRight && textRect.left >= parentLeft) {
                mStatusNameLabel.setTranslationX(animatedValue);
            }
        }
    };

    public enum BarColor {
        green, red
    }
}
