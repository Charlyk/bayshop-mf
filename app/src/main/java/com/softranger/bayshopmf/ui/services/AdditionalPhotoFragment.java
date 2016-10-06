package com.softranger.bayshopmf.ui.services;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdditionalPhotoFragment extends ParentFragment {

    public static final String ACTION_PHOTO_IN_PROCESSING = "ACTION PHOTO IN PROCESSING";
    public static final String ACTION_CANCEL_PHOTO_REQUEST = "ACTION CANCEL PHOTO REQUEST";
    private static final String ID_ARG = "id argument";
    private static final String PREORDER_ARG = "is preorder argument";
    private static final String STATUS_ARG = "status argument";

    @BindView(R.id.check_product_commentInput)
    EditText mCommentInput;
    @BindView(R.id.check_product_confirmBtn)
    Button mConfirmButton;

    private Unbinder mUnbinder;
    private MainActivity mActivity;
    private String mId;
    private boolean mIsInProgress;
    private boolean mIsPreorder;

    private Call<ServerResponse> mCall;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_additional_photo, container, false);
        mActivity = (MainActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);
        mId = getArguments().getString(ID_ARG);
        mIsInProgress = getArguments().getBoolean(STATUS_ARG);
        mIsPreorder = getArguments().getBoolean(PREORDER_ARG);

        if (mIsInProgress) {
            mConfirmButton.setText(getString(R.string.cancel_request));
            mConfirmButton.setBackgroundColor(mActivity.getResources().getColor(R.color.colorAccent));
        }

        return view;
    }

    @OnClick(R.id.additionalPhotoDetailsButton)
    void showDetails() {

    }

    @OnClick(R.id.check_product_confirmBtn)
    void sendRequest() {
        String comment = String.valueOf(mCommentInput.getText());
        mActivity.toggleLoadingProgress(true);
        if (!mIsInProgress && !mIsPreorder) {
            mCall = Application.apiInterface().requestServiceForInStock(mId, Constants.Api.OPTION_PHOTO, 10, comment);
            mCall.enqueue(mResponseCallback);
        } else if (mIsPreorder) {
            mCall = Application.apiInterface().requestPhotoForAwaiting(mId, mIsInProgress ? 0 : 1, comment);
            mCall.enqueue(mResponseCallback);
        } else {
            mActivity.toggleLoadingProgress(false);
            Intent intent = new Intent(ACTION_CANCEL_PHOTO_REQUEST);
            mActivity.sendBroadcast(intent);
            mActivity.onBackPressed();
        }
    }

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.toggleLoadingProgress(false);
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
        public void onFailure(ServerResponse errorData) {
            Log.e("AdditionalPhoto: ", errorData.getMessage());
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.additional_photo);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.additional_photos;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
        mActivity.hideKeyboard();
    }
}
