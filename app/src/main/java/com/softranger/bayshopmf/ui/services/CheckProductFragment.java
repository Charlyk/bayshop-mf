package com.softranger.bayshopmf.ui.services;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckProductFragment extends Fragment implements View.OnClickListener {

    public static final String ACTION_CHECK_IN_PROCESSING = "ACTION CHECK IN PROCESSING";
    public static final String ACTION_CANCEL_CHECK_PRODUCT = "ACTION CANCEL CHECK PRODUCT";
    private static final String ID_ARG = "id argument";
    private static final String STATUS_ARG = "status argument";

    private EditText mCommentInput;
    private Button mLeaveComment;
    private MainActivity mActivity;
    private String mId;
    private boolean mIsInprogress;
    private Button mConfirmButton;

    public CheckProductFragment() {
        // Required empty public constructor
    }

    public static CheckProductFragment newInstance(String id, boolean isInProgress) {
        Bundle args = new Bundle();
        args.putString(ID_ARG, id);
        args.putBoolean(STATUS_ARG, isInProgress);
        CheckProductFragment fragment = new CheckProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_product, container, false);
        mActivity = (MainActivity) getActivity();
        mCommentInput = (EditText) view.findViewById(R.id.check_product_commentInput);
        mLeaveComment = (Button) view.findViewById(R.id.check_product_leaveCommentBtn);
        mConfirmButton = (Button) view.findViewById(R.id.check_product_confirmBtn);
        mId = getArguments().getString(ID_ARG);
        mIsInprogress = getArguments().getBoolean(STATUS_ARG);

        if (mIsInprogress) {
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
                if (!mIsInprogress) {
                    RequestBody body = new FormBody.Builder()
                            .add("id", mId)
                            .add("request", Constants.Api.OPTION_CHECK)
                            .add("comments", String.valueOf(mCommentInput.getText()))
                            .build();
                    ApiClient.getInstance().sendRequest(body, Constants.Api.urlAdditionalPhoto(), mHandler);
                    mActivity.toggleLoadingProgress(true);
                } else {
                    Intent intent = new Intent(ACTION_CANCEL_CHECK_PRODUCT);
                    mActivity.sendBroadcast(intent);
                    mActivity.onBackPressed();
                }
                break;

            }
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (error) {
                            Snackbar.make(mConfirmButton, message, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(ACTION_CHECK_IN_PROCESSING);
                            mActivity.sendBroadcast(intent);
                            mActivity.onBackPressed();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mConfirmButton, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mConfirmButton, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mConfirmButton, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };
}
