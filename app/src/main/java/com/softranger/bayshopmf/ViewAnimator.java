package com.softranger.bayshopmf;

import android.animation.Animator;
import android.view.View;

/**
 * Created by eduard on 28.04.16.
 */
public class ViewAnimator {

    private AnimationListener mAnimationListener;

    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }

    public void flip(final View view) {
        view.animate().rotationYBy(180).setDuration(250).setListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationStarted();
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationFinished();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }
        );
    }



    public interface AnimationListener {
        void onAnimationStarted();

        void onAnimationFinished();
    }
}
