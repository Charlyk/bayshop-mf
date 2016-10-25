package com.softranger.bayshopmf.ui.services;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckProductFragment extends ParentFragment {

    public static final String ACTION_CHECK_IN_PROCESSING = "ACTION CHECK IN PROCESSING";
    public static final String ACTION_CANCEL_CHECK_PRODUCT = "ACTION CANCEL CHECK PRODUCT";
    private static final String PREORDER_ARG = "preorder argument";
    private static final String ID_ARG = "id argument";
    private static final String STATUS_ARG = "status argument";

    private MainActivity mActivity;
    private String mId;
    private boolean mIsInprogress;
    private boolean mIsPreorder;
    private Unbinder mUnbinder;
    private Call<ServerResponse> mCall;

    @BindView(R.id.additionalServiceImage)
    ImageView mServiceImage;
    @BindView(R.id.additionalServiceDescription)
    TextView mDescriptionaLabel;
    @BindView(R.id.additionalServicePrice)
    TextView mPriceLabel;
    @BindView(R.id.additionalServiceCommentInput)
    EditText mCommentInput;
    @BindView(R.id.additionalServiceConfirmBtn)
    Button mConfirmButton;

    public CheckProductFragment() {
        // Required empty public constructor
    }

    public static CheckProductFragment newInstance(String id, boolean isInProgress, boolean preorder) {
        Bundle args = new Bundle();
        args.putString(ID_ARG, id);
        args.putBoolean(STATUS_ARG, isInProgress);
        args.putBoolean(PREORDER_ARG, preorder);
        CheckProductFragment fragment = new CheckProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_additional_services, container, false);
        mActivity = (MainActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);

        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        // set service default views
        mServiceImage.setImageResource(R.mipmap.ic_check_product_250dp);
        mDescriptionaLabel.setText(getString(R.string.check_product_description));
        mPriceLabel.setText(getString(R.string.check_product_cost));

        mId = getArguments().getString(ID_ARG);
        mIsInprogress = getArguments().getBoolean(STATUS_ARG);
        mIsPreorder = getArguments().getBoolean(PREORDER_ARG);

        if (mIsInprogress) {
            mConfirmButton.setText(getString(R.string.cancel_request));
            Drawable redBg = mActivity.getResources().getDrawable(R.drawable.red_button_bg);
            mConfirmButton.setBackgroundDrawable(redBg);
        }

        return view;
    }

    @OnClick(R.id.additionalServiceDetailsBtn)
    void showDetails() {

    }

    @OnClick(R.id.additionalServiceConfirmBtn)
    void sendCheckRequest() {
        String comment = String.valueOf(mCommentInput.getText());
        mActivity.toggleLoadingProgress(true);
        if (!mIsPreorder) {
            mCall = Application.apiInterface().requestParcelVerification(mId, comment, mIsInprogress ? 0 : 1);
            mCall.enqueue(mResponseCallback);
        } else {
            mCall = Application.apiInterface().requestCheckForAwaiting(mId, mIsInprogress ? 0 : 1, comment);
            mCall.enqueue(mResponseCallback);
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                    sendCheckRequest();
                    break;
            }
        }
    };

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.toggleLoadingProgress(false);
            if (mIsInprogress && mIsPreorder) {
                Intent intent = new Intent(ACTION_CANCEL_CHECK_PRODUCT);
                mActivity.sendBroadcast(intent);
                mActivity.onBackPressed();
            } else if (mIsInprogress) {
                Intent intent = new Intent(ACTION_CANCEL_CHECK_PRODUCT);
                mActivity.sendBroadcast(intent);
                mActivity.onBackPressed();
            } else {
                Intent intent = new Intent(ACTION_CHECK_IN_PROCESSING);
                mActivity.sendBroadcast(intent);
                mActivity.onBackPressed();
            }
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Log.e("CheckProduct: ", errorData.getMessage());
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.check_product);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.check_product;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
        mActivity.hideKeyboard();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
