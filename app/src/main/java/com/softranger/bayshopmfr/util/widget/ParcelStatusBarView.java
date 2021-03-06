package com.softranger.bayshopmfr.util.widget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;

/**
 * Created by Eduard Albu on 9/14/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class ParcelStatusBarView extends RelativeLayout {

    private Context mContext;
    private LinearLayout mStatusBarHolder;
    private View mStatusIndicator;
    private int mTotalWidth;
    private int mStatusesCount;
    private int mOneStatusWidth;
    private int mCurrentProgress;
    private TextView mStatusName;
    private Interpolator mInterpolator;
    private boolean mIsReady;
    private SparseArray<BarColor> mColors;
    private OnStatusBarReadyListener mOnStatusBarReadyListener;
    private ValueAnimator mIndicatorAnimation;
    private Handler mUpdateHandler;
    private ValueAnimator mLabelAnimation;

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
        mUpdateHandler = new Handler();

        // below are initial statuses for the status bar
        mColors = new SparseArray<BarColor>() {{
            put(0, BarColor.green); // initial state
            put(1, BarColor.green); // processing
            put(2, BarColor.red); // held by prohibition
            put(3, BarColor.red); // held by damage
//        mColors.put(4, BarColor.green); // packing
            put(4, BarColor.green); // awaiting sending
            put(5, BarColor.red); // held due to debt
//        mColors.put(6, BarColor.red); // held by user
            put(6, BarColor.green); // sent
            put(7, BarColor.red); // held by customs
            put(8, BarColor.yellow); // local depot
            put(9, BarColor.green); // in the way
            put(10, BarColor.green); // received
        }};

        mInterpolator = new AccelerateDecelerateInterpolator();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.status_bar_view, this);
        mStatusBarHolder = (LinearLayout) rootView.findViewById(R.id.statusBarHolderLayout);

        mStatusesCount = mColors.size() - 1;
        mStatusBarHolder.measure(MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED);
        mTotalWidth = mStatusBarHolder.getWidth();

        mOneStatusWidth = mTotalWidth / mStatusesCount;

        mStatusIndicator = rootView.findViewById(R.id.statusBarStatusIndicator);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // get holder width
        mTotalWidth = mStatusBarHolder.getWidth();
        // compute the with for one status
        mOneStatusWidth = mTotalWidth / mStatusesCount;
        // get parent left and right sides position
        Rect parentRect = new Rect();
        mStatusBarHolder.getGlobalVisibleRect(parentRect);

        // tell to listeners that we are ready for animations
        if (mOnStatusBarReadyListener != null && !mIsReady) {
            mUpdateHandler.postDelayed(mUpdateRunnable, 200);
        }
        // set this to true so the animations will work
        mIsReady = true;
    }

    /**
     * Runnable to send message to listener about animation ready status
     */
    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mOnStatusBarReadyListener.onStatusBarReady();
        }
    };

    /**
     * Convert dp to pixels
     *
     * @param dp to convert
     * @return value of passed dp in pixels
     */
    private int getPixelsFromDp(int dp) {
        Resources r = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mInterpolator = new AccelerateDecelerateInterpolator();
        mStatusBarHolder = (LinearLayout) findViewById(R.id.statusBarHolderLayout);

        // set the initial maximum statuses
        mStatusesCount = mColors.size() - 1;

        mStatusIndicator = findViewById(R.id.statusBarStatusIndicator);
    }

    /**
     * Change maximum statuses number (default is 9, so status bar will be divided in 9 parts)
     *
     * @param statusesCount new max statuses count
     */
    public void setStatusesCount(int statusesCount) {
        mStatusesCount = statusesCount;
    }

    public void setNewColorsMap(SparseArray<BarColor> colorsMap) {
        mColors.clear();

        for (int i = 0; i < colorsMap.size(); i++) {
            mColors.put(i, colorsMap.valueAt(i));
        }

        // set the initial maximum statuses
        mStatusesCount = mColors.size() - 1;
        // compute the with for one status
        mOneStatusWidth = mTotalWidth / mStatusesCount;
    }

    public void startLoading() {

    }

    public void setStatusNameLabel(TextView statusNameLabel) {
        mStatusName = statusNameLabel;
    }

    public Drawable getBarColor(int progress) {
        switch (mColors.get(progress)) {
            case red:
                return mContext.getResources().getDrawable(R.drawable.red_5dp_corner);
            case green:
                return mContext.getResources().getDrawable(R.drawable.green_5dp_corner);
            case yellow:
                return mContext.getResources().getDrawable(R.drawable.yelow_5dp_corner);
            default:
                return mContext.getResources().getDrawable(R.drawable.gray_5dp_corners);
        }
    }

    public void setProgress(int progress) {
        // check if given progress is not greater then max progress
        // if it is greater just set it equal to max progress
        if (!mIsReady) return;
        if (progress > mStatusesCount) progress = mStatusesCount;
        // set current progress for get method
        mCurrentProgress = progress;

        // update status indicator and text view backgrounds based on status color
        if (mStatusName != null) mStatusName.setBackgroundDrawable(getBarColor(progress));
        switch (mColors.get(progress)) {
            case red:
                mStatusIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.red_status_bg));
                if (mStatusName != null) mStatusName.setBackgroundDrawable(getBarColor(progress));
                break;
            case green:
                mStatusIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.green_status_bg));
                break;
            case yellow:
                mStatusIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yellow_status_bg));
                break;
            case gray:
                mStatusIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.status_bar_bg));
                break;
        }

        // compute the width which we should get at the end
        float toWidth = mOneStatusWidth * progress;
        // get indicator current width
        float fromWidth = mStatusIndicator.getLayoutParams().width;
        // if we have already max width just stop here
        if ((fromWidth + toWidth) >= mTotalWidth && progress == mStatusesCount) {
            toWidth = mTotalWidth;
        }
        // create a value animator to animate the indicator progress
        mIndicatorAnimation = ValueAnimator.ofFloat(0, toWidth);
        mIndicatorAnimation.addUpdateListener(mIndicatorAnimatorListener);

        // create animator for label if label is not null
        if (mStatusName != null) {
            mLabelAnimation = ValueAnimator.ofFloat(0f, 1f);
            mLabelAnimation.addUpdateListener(mLabelAnimationListener);
        }

        // add all created animators to a set and play them together
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.setInterpolator(mInterpolator);
        if (mStatusName != null) {
            set.playTogether(mIndicatorAnimation, mLabelAnimation);
        } else {
            set.play(mIndicatorAnimation);
        }
        set.start();
    }

    /**
     * Stop position and width changing animations
     */
    public void stopAnimations() {
        if (mIndicatorAnimation != null) mIndicatorAnimation.cancel();
    }

    public void setOnStatusBarReadyListener(OnStatusBarReadyListener onStatusBarReadyListener) {
        mOnStatusBarReadyListener = onStatusBarReadyListener;
    }

    /**
     * Get the current progress
     *
     * @return how many positions of the indicator were changed
     */
    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    /**
     * Chenge a color for any status in the lost or add a new status
     *
     * @param position of the status in the list
     * @param color    for that position as {@link BarColor}
     */
    public void setColor(int position, BarColor color) {
        mColors.put(position, color);
    }

    /**
     * Change the animation interpolator
     *
     * @param interpolator new interpolator for animations
     */
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * Listener below will update the width of the progress indicator
     */
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

    private ValueAnimator.AnimatorUpdateListener mLabelAnimationListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (mStatusName != null) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mStatusName.setAlpha(animatedValue);
            }
        }
    };

    /**
     * Used to set status indicator and text background color
     */
    public enum BarColor {
        green, red, yellow, gray
    }

    public interface OnStatusBarReadyListener {
        void onStatusBarReady();
    }
}
