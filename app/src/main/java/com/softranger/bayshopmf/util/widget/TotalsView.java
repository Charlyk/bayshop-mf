package com.softranger.bayshopmf.util.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;

/**
 * Created by Eduard Albu on 9/23/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class TotalsView extends RelativeLayout implements View.OnClickListener {

    private Context mContext;

    private TextView mWeightLabel;
    private TextView mPriceLabel;
    private Button mCreateBtn;

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
    public TotalsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeView(context);
    }

    private void initializeView(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.totals_layout, this);
        mCurrency = "";
        mWeightLabel = (TextView) view.findViewById(R.id.totalsViewWeightLabel);
        mPriceLabel = (TextView) view.findViewById(R.id.totalsViewPriceLabel);
        mCreateBtn = (Button) view.findViewById(R.id.totalsViewCreateBtn);
        mCreateBtn.setOnClickListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mWeightLabel = (TextView) findViewById(R.id.totalsViewWeightLabel);
        mPriceLabel = (TextView) findViewById(R.id.totalsViewPriceLabel);
        mCreateBtn = (Button) findViewById(R.id.totalsViewCreateBtn);
        mCreateBtn.setOnClickListener(this);
        mCurrency = "";
    }

    @Override
    public void onClick(View v) {

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

    private void setNewValue(final TextView forTextView, final String newText) {
        float scaleY = forTextView.getScaleY();
        float scaleX = forTextView.getScaleX();

        ValueAnimator.AnimatorUpdateListener scaleXupdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                forTextView.setScaleX(animatedValue);
            }
        };

        ValueAnimator.AnimatorUpdateListener scaleYupdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                forTextView.setScaleY(animatedValue);
            }
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
}
