package com.softranger.bayshopmf.ui.services;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdditionalPhotoFragment extends ParentFragment implements View.OnClickListener {

    public static final String ACTION_PHOTO_IN_PROCESSING = "ACTION PHOTO IN PROCESSING";
    public static final String ACTION_CANCEL_PHOTO_REQUEST = "ACTION CANCEL PHOTO REQUEST";
    private static final String ID_ARG = "id argument";
    private static final String PREORDER_ARG = "is preorder argument";
    private static final String STATUS_ARG = "status argument";

    private EditText mCommentInput;
    private Button mLeaveComment;
    private Button mConfirmButton;
    private MainActivity mActivity;
    private String mId;
    private boolean mIsInProgress;
    private boolean mIsPreorder;


    public AdditionalPhotoFragment() {
        // Required empty public constructor
    }

    public static AdditionalPhotoFragment newInstance(String id, boolean isInProgress, boolean isPreorder) {
        Bundle args = new Bundle();
        args.putString(ID_ARG, id);
        args.putBoolean(STATUS_ARG, isInProgress);
        args.putBoolean(PREORDER_ARG, isPreorder);
        AdditionalPhotoFragment fragment = new AdditionalPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_additional_photo, container, false);
        mActivity = (MainActivity) getActivity();
        mCommentInput = (EditText) view.findViewById(R.id.check_product_commentInput);
        mLeaveComment = (Button) view.findViewById(R.id.additionalPhotoleaveCommentBtn);
        ImageButton details = (ImageButton) view.findViewById(R.id.additionalPhotoDetailsButton);
        details.setOnClickListener(this);
        mConfirmButton = (Button) view.findViewById(R.id.check_product_confirmBtn);
        mId = getArguments().getString(ID_ARG);
        mIsInProgress = getArguments().getBoolean(STATUS_ARG);
        mIsPreorder = getArguments().getBoolean(PREORDER_ARG);

        if (mIsInProgress) {
            mConfirmButton.setText(getString(R.string.cancel_request));
            mConfirmButton.setBackgroundColor(mActivity.getResources().getColor(R.color.colorAccent));
            mLeaveComment.setVisibility(View.GONE);
        } else {
            mLeaveComment.setOnClickListener(this);
        }

        mConfirmButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_product_leaveCommentBtn:
                if (mCommentInput.getVisibility() == View.GONE) {
                    mCommentInput.setVisibility(View.VISIBLE);
                    mLeaveComment.setText(getString(R.string.hide_comment));
                } else {
                    mCommentInput.setVisibility(View.GONE);
                    mLeaveComment.setText(getString(R.string.leave_comment));
                }
                break;
            case R.id.check_product_confirmBtn: {
                if (!mIsInProgress && !mIsPreorder) {
                    RequestBody body = new FormBody.Builder()
                            .add("id", mId)
                            .add("request", Constants.Api.OPTION_PHOTO)
                            .add("package", String.valueOf(10)) // 10 is photo quantity id, it may change in the future but now is 10
                            .add("comments", String.valueOf(mCommentInput.getText()))
                            .build();
                    ApiClient.getInstance().postRequest(body, Constants.Api.urlAdditionalPhoto(), mHandler);
                    mActivity.toggleLoadingProgress(true);
                } else if (mIsPreorder){
                    RequestBody body = new FormBody.Builder()
                            .add("photosPackageRequested", String.valueOf(mIsInProgress ? 0 : 1))
                            .add("photosPackageRequestedComments", String.valueOf(mCommentInput.getText()))
                            .build();
                    ApiClient.getInstance().putRequest(body, Constants.Api.urlEditWaitingArrivalItem(mId), mHandler);
                    mActivity.toggleLoadingProgress(true);
                } else {
                    Intent intent = new Intent(ACTION_CANCEL_PHOTO_REQUEST);
                    mActivity.sendBroadcast(intent);
                    mActivity.onBackPressed();
                }
                break;
            }
            case R.id.additionalPhotoDetailsButton: {

                break;
            }
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        if (mIsInProgress && mIsPreorder) {
            Intent intent = new Intent(ACTION_CANCEL_PHOTO_REQUEST);
            mActivity.sendBroadcast(intent);
            mActivity.onBackPressed();
            return;
        }
        Intent intent = new Intent(ACTION_PHOTO_IN_PROCESSING);
        mActivity.sendBroadcast(intent);
        mActivity.onBackPressed();
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }
}
