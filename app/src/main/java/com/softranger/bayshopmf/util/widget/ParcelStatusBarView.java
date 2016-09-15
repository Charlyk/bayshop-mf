package com.softranger.bayshopmf.util.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.PUSParcel;

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
    private boolean mIsReady;
    private HashMap<Integer, BarColor> mColors;
    private OnStatusBarReadyListener mOnStatusBarReadyListener;
    private ValueAnimator mIndicatorAnimation;
    private ValueAnimator mTextAnimation;
    private float mToWidth;

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

        if (mOnStatusBarReadyListener != null && !mIsReady) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnStatusBarReadyListener.onStatusBarReady();
                }
            }, 500);
        }

        mIsReady = true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mInterpolator = new AccelerateDecelerateInterpolator();
        mStatusBarHolder = (LinearLayout) findViewById(R.id.statusBarHolderLayout);

        mStatusesCount = 9;

        mStatusIndicator = findViewById(R.id.statusBarStatusIndicator);
        mStatusNameLabel = (TextView) findViewById(R.id.statusViewNameLabel);
    }

    public void setStatusesCount(int statusesCount) {
        mStatusesCount = statusesCount;
    }

    public void setProgress(PUSParcel pusParcel) {
        // check if given progress is not greater then max progress
        // if it is greater just set it equal to max progress
        int progress = pusParcel.getParcelStatus().index();
        if (progress > mStatusesCount) progress = mStatusesCount;
        // set current progress for get method
        mCurrentProgress = progress;

        switch (mColors.get(progress)) {
            case red:
                mStatusIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.red_status_bg));
                mStatusNameLabel.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.status_red_bg));
                break;
            case green:
                mStatusIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.green_status_bg));
                mStatusNameLabel.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.status_green_bg));
                break;
        }

        // compute the width which we should get at the end
        mToWidth = mOneStatusWidth * progress;
        // get indicator current width
        float fromWidth = mStatusIndicator.getLayoutParams().width;
        // if we have already max width just stop here
        if (fromWidth == mToWidth) return;
        // create a value animator to animate the indicator progress
        mIndicatorAnimation = ValueAnimator.ofFloat(0, mToWidth);
        mIndicatorAnimation.addUpdateListener(mIndicatorAnimatorListener);
        mIndicatorAnimation.setDuration(500);
        mIndicatorAnimation.addListener(mAnimationListener);
        mIndicatorAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mIndicatorAnimation.start();

        // set stetus title
        mStatusNameLabel.setText(pusParcel.getParcelStatus().statusName());
        pusParcel.setWasAnimated(true);
    }

    public void stopAnimations() {
        if (mIndicatorAnimation != null) mIndicatorAnimation.cancel();
        if (mTextAnimation != null) mTextAnimation.cancel();
    }

    public void setOnStatusBarReadyListener(OnStatusBarReadyListener onStatusBarReadyListener) {
        mOnStatusBarReadyListener = onStatusBarReadyListener;
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
            // check if the rectangle is not at the edge of the screen
            if (animatedValue >= parentLeft && animatedValue <= parentRight) {
                Rect rect = new Rect();
                mStatusNameLabel.getGlobalVisibleRect(rect);
                mStatusNameLabel.setX(animatedValue - (rect.width() / 2));
            }
        }
    };

    private Animator.AnimatorListener mAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            Rect textRect = new Rect();
            Rect indicatorRect = new Rect();
            mStatusIndicator.getGlobalVisibleRect(indicatorRect);
            mStatusNameLabel.getGlobalVisibleRect(textRect);
            float halfTextWidth = textRect.centerX();
            mTextAnimation = ValueAnimator.ofFloat(halfTextWidth, mToWidth);
            mTextAnimation.addUpdateListener(mNameAnimatorListener);
            mTextAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            mTextAnimation.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    public enum BarColor {
        green, red
    }

    public interface OnStatusBarReadyListener {
        void onStatusBarReady();
    }
}
