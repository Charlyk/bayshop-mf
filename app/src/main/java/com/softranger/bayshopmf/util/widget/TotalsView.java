package com.softranger.bayshopmf.util.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;

import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Eduard Albu on 9/23/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class TotalsView extends RelativeLayout implements View.OnClickListener {

    @SuppressWarnings("unused")
    private RelativeLayout mTotalsLayout;
    private Toolbar mToolbar;

    private TextView mWeightLabel;
    private TextView mPriceLabel;

    private OnCreateParcelClickListener mOnCreateParcelClickListener;

    private String mCurrency;

    private static double totalPrice;
    private static double totalWeight;

    public TotalsView(Context context) {
        super(context);
        initializeView(context);
    }

    public TotalsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public TotalsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public TotalsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.totals_layout, this);
        mCurrency = "$";
        mTotalsLayout = (RelativeLayout) view.findViewById(R.id.totalsLayout);
        mToolbar = (Toolbar) view.findViewById(R.id.totalsViewToolbar);
        mToolbar.setNavigationOnClickListener(this);
        mWeightLabel = (TextView) view.findViewById(R.id.totalsViewWeightLabel);
        mPriceLabel = (TextView) view.findViewById(R.id.totalsViewPriceLabel);
    }

    @SuppressWarnings("unused")
    public void setYFraction(final float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
    }

    @SuppressWarnings("unused")
    public float getYFraction() {
        if (getHeight() == 0) {
            return 0;
        }
        return getTranslationY() / getHeight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTotalsLayout = (RelativeLayout) findViewById(R.id.totalsLayout);
        mToolbar = (Toolbar) findViewById(R.id.totalsViewToolbar);
        mToolbar.setNavigationOnClickListener(this);
        mWeightLabel = (TextView) findViewById(R.id.totalsViewWeightLabel);
        mPriceLabel = (TextView) findViewById(R.id.totalsViewPriceLabel);
        mCurrency = "$";
    }

    public void setOnCreateParcelClickListener(OnCreateParcelClickListener onCreateParcelClickListener) {
        mOnCreateParcelClickListener = onCreateParcelClickListener;
    }

    public void toggleOnClick() {
        if (mOnCreateParcelClickListener != null) {
            mOnCreateParcelClickListener.onCreateParcelClick();
        }
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public void increasePrice(double increaseWith) {
        totalPrice = totalPrice + increaseWith;
        setNewValue(mPriceLabel, mCurrency + totalPrice);
    }

    public void decreasePrice(double decreaseWith) {
        totalPrice = totalPrice - decreaseWith;
        setNewValue(mPriceLabel, mCurrency + totalPrice);
    }

    public void increaseWeight(double increaseWith) {
        totalWeight = totalWeight + increaseWith;
        setNewValue(mWeightLabel, totalWeight + "kg");
    }

    public void decreaseWeight(double decreaseWith) {
        totalWeight = totalWeight - decreaseWith;
        setNewValue(mWeightLabel, totalWeight + "kg");
    }

    public void resetTotals() {
        totalWeight = 0;
        totalPrice = 0;
    }

    public void transform(boolean showToolbar) {
        // Determine center
        final int x = (getRight() - getLeft()) / 2;
        final int y = (getBottom() - getTop()) / 2;
        // Determine radius sizes
        final int containerWidth = getWidth() / 2;
        final int containerHeight = getHeight() / 2;
        final int maxRadius = (int) Math.sqrt((containerWidth * containerWidth) +
                (containerHeight * containerHeight));
        final int startingRadius;
        final int finalRadius;
        if (showToolbar) {
            startingRadius = 0;
            finalRadius = maxRadius;
            mToolbar.setVisibility(View.VISIBLE);
        } else {
            startingRadius = maxRadius;
            finalRadius = 0;
        }

        // Animate
        try {
            final Animator animator = ViewAnimationUtils.createCircularReveal(mToolbar, x,
                    y, startingRadius, finalRadius);
            if (!showToolbar) {
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mToolbar.setVisibility(View.GONE);
                    }
                });
            }
            animator.setDuration(600);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        } catch (IllegalStateException e) {
            Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage());
        }
    }

    private void setNewValue(final TextView forTextView, final String newText) {
        float scaleY = forTextView.getScaleY();
        float scaleX = forTextView.getScaleX();

        ValueAnimator.AnimatorUpdateListener scaleXupdateListener = animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            forTextView.setScaleX(animatedValue);
        };

        ValueAnimator.AnimatorUpdateListener scaleYupdateListener = animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            forTextView.setScaleY(animatedValue);
        };

        ValueAnimator scaleDownYanimation = ValueAnimator.ofFloat(scaleY, 0.1f);
        ValueAnimator scaleDownXanimation = ValueAnimator.ofFloat(scaleX, 0.1f);
        scaleDownXanimation.addUpdateListener(scaleXupdateListener);
        scaleDownYanimation.addUpdateListener(scaleYupdateListener);

        ValueAnimator scaleUpYanimation = ValueAnimator.ofFloat(0.1f, scaleY);
        ValueAnimator scaleUpXanimation = ValueAnimator.ofFloat(0.1f, scaleX);
        scaleUpXanimation.addUpdateListener(scaleXupdateListener);
        scaleUpYanimation.addUpdateListener(scaleYupdateListener);

        final AnimatorSet upSet = new AnimatorSet();
        upSet.setDuration(150);
        upSet.playTogether(scaleUpXanimation, scaleUpYanimation);
        upSet.setInterpolator(new DecelerateInterpolator());

        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                forTextView.setText(newText);
                upSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

        AnimatorSet downSet = new AnimatorSet();
        downSet.setDuration(150);
        downSet.addListener(animatorListener);
        downSet.playTogether(scaleDownXanimation, scaleDownYanimation);
        downSet.setInterpolator(new AccelerateInterpolator());
        downSet.start();
    }

    @Override
    public void onClick(View v) {
        if (mOnCreateParcelClickListener != null) {
            mOnCreateParcelClickListener.onNavIconClick();
        }
    }

    public interface OnCreateParcelClickListener {
        void onCreateParcelClick();
        void onNavIconClick();
    }
}
