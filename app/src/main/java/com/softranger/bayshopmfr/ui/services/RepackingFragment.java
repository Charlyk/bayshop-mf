package com.softranger.bayshopmfr.ui.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class RepackingFragment extends ParentFragment {

    public static final String TOGGLE_REPACKING = "com.softranger.bayshopmf.ui.services.TOGGLE_REPACKING";
    private static final String PROGRESS = "com.softranger.bayshopmf.ui.services.IS_IN_PROGRESS";
    private static final String PARCEL = "com.softranger.bayshopmf.ui.services.PARCEL_ID";

    @BindView(R.id.additionalServiceImage)
    ImageView mServiceImage;
    @BindView(R.id.additionalServiceDescription)
    TextView mDescriptionaLabel;
    @BindView(R.id.additionalServiceCommentInput)
    EditText mCommentInput;
    @BindView(R.id.additionalServiceConfirmBtn)
    Button mConfirmButton;

    private Unbinder mUnbinder;
    private ParentActivity mActivity;
    private Call<ServerResponse> mCall;
    private Call<ServerResponse<HashMap<String, Double>>> mPricesCall;

    private String mId;
    private boolean mIsInProgress;

    public RepackingFragment() {
        // Required empty public constructor
    }

    public static RepackingFragment newInstance(boolean isInProgress, String parcelId) {
        Bundle args = new Bundle();
        args.putBoolean(PROGRESS, isInProgress);
        args.putString(PARCEL, parcelId);
        RepackingFragment fragment = new RepackingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_additional_services, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();

        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mId = getArguments().getString(PARCEL);
        mIsInProgress = getArguments().getBoolean(PROGRESS);

        mServiceImage.setImageResource(R.mipmap.ic_repacking_250dp);
        mDescriptionaLabel.setText(getString(R.string.repacking_description));

        if (mIsInProgress) {
            mConfirmButton.setVisibility(View.VISIBLE);
            mConfirmButton.setText(getString(R.string.cancel_request));
            Drawable redBg = mActivity.getResources().getDrawable(R.drawable.red_button_bg);
            mConfirmButton.setBackgroundDrawable(redBg);
        } else {
            mActivity.toggleLoadingProgress(true);
            mPricesCall = Application.apiInterface().getAdditionalServicesPrices();
            mPricesCall.enqueue(mPricesCallback);
        }

        return view;
    }

    private ResponseCallback<HashMap<String, Double>> mPricesCallback = new ResponseCallback<HashMap<String, Double>>() {
        @Override
        public void onSuccess(HashMap<String, Double> data) {
            mConfirmButton.setVisibility(View.VISIBLE);
            if (!mIsInProgress) {
                mConfirmButton.setText(getString(R.string.request_for, data.get(Constants.Services.REPACKING)));
            }
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            mActivity.toggleLoadingProgress(false);
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call<ServerResponse<HashMap<String, Double>>> call, Throwable t) {
            mActivity.toggleLoadingProgress(false);
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @OnClick(R.id.additionalServiceConfirmBtn)
    void makeRepackingRequest() {
        String comment = String.valueOf(mCommentInput.getText());
        mActivity.toggleLoadingProgress(true);
        mCall = Application.apiInterface().requestParcelRepacking(mId, comment, mIsInProgress ? 0 : 1);
        mCall.enqueue(mResponseCallback);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                    makeRepackingRequest();
                    break;
            }
        }
    };

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.toggleLoadingProgress(false);
            Intent toggle = new Intent(TOGGLE_REPACKING);
            toggle.putExtra("enable", mIsInProgress ? 0 : 1);
            mActivity.sendBroadcast(toggle);
            mActivity.onBackPressed();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            t.printStackTrace();
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.repacking);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.repacking;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        if (mPricesCall != null) mPricesCall.cancel();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
