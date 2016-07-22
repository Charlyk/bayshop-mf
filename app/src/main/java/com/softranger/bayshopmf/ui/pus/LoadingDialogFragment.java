package com.softranger.bayshopmf.ui.pus;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.CountingRequestBody;
import com.softranger.bayshopmf.util.ViewAnimator;

/**
 * Created by Eduard Albu on 7/22/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class LoadingDialogFragment extends DialogFragment implements View.OnClickListener,
        CountingRequestBody.ProgressListener {

    private static final int ROTATE = 1, FINISH = 2;
    private static final String ACTION_ARG = "action argument";
    private ProgressBar mProgressBar;
    private ImageView mLogoImage;
    private Button mCancelBtn;
    private ViewAnimator mViewAnimator;
    private OnDoneListener mOnDoneListener;
    private int mAction;

    /**
     * It is used in done callback to distinguish actions
     * you can pass any integer here, it does not depend on it
     * @param action any integer you want, it will be returned in done method
     */
    public static LoadingDialogFragment newInstance(int action) {
        Bundle args = new Bundle();
        args.putInt(ACTION_ARG, action);
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mProgressBar = (ProgressBar) view.findViewById(R.id.dialogProgressBar);
        mProgressBar.setMax(100);

        mAction = getArguments().getInt(ACTION_ARG);

        mViewAnimator = new ViewAnimator();

        mLogoImage = (ImageView) view.findViewById(R.id.dialogImageView);
        mCancelBtn = (Button) view.findViewById(R.id.dialogCancelButton);
        mCancelBtn.setOnClickListener(this);
        rotateImage();
        return view;
    }

    private void rotateImage() {
        mImageHandler.postDelayed(mRunnable, 1000);
    }

    public void setOnDoneListener(OnDoneListener onDoneListener) {
        mOnDoneListener = onDoneListener;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mProgressBar.getProgress() < 100) {
                mImageHandler.sendEmptyMessage(ROTATE);
            } else {
                mImageHandler.sendEmptyMessage(FINISH);
            }
        }
    };

    private Handler mImageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ROTATE:
                    mViewAnimator.flip(mLogoImage);
                    mProgressBar.setInterpolator(new AccelerateDecelerateInterpolator());
                    mProgressBar.setProgress(mProgressBar.getProgress() + 10);
                    rotateImage();
                    break;
                case FINISH:
                    if (mOnDoneListener != null) {
                        mOnDoneListener.onDone(mAction);
                        dismiss();
                    } else {
                        mLogoImage.setImageResource(R.mipmap.ic_check_35dp);
                        mCancelBtn.setText("Done");
                        mCancelBtn.setTextColor(getResources().getColor(R.color.colorGreenAction));
                    }
                    break;

            }
        }
    };

    @Override
    public void onClick(View v) {
        mImageHandler.removeCallbacks(mRunnable);
        dismiss();
    }

    @Override
    public void onRequestProgress(long bytesWritten, long contentLength) {

    }

    public interface OnDoneListener {
        void onDone(int action);
    }
}
