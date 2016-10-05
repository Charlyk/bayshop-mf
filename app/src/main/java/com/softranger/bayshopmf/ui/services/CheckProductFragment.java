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

    @BindView(R.id.check_product_commentInput)
    EditText mCommentInput;
    @BindView(R.id.check_product_confirmBtn)
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
        View view = inflater.inflate(R.layout.fragment_check_product, container, false);
        mActivity = (MainActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);

        mId = getArguments().getString(ID_ARG);
        mIsInprogress = getArguments().getBoolean(STATUS_ARG);
        mIsPreorder = getArguments().getBoolean(PREORDER_ARG);

        if (mIsInprogress) {
            mConfirmButton.setText(getString(R.string.cancel_request));
            mConfirmButton.setBackgroundColor(mActivity.getResources().getColor(R.color.colorAccent));
        }

        return view;
    }

    @OnClick(R.id.checkProductDetailsButton)
    void showDetails() {

    }

    @OnClick(R.id.check_product_confirmBtn)
    void sendCheckRequest() {
        String comment = String.valueOf(mCommentInput.getText());
        mActivity.toggleLoadingProgress(true);
        if (!mIsInprogress && !mIsPreorder) {
            mCall = Application.apiInterface().requestServiceForInStock(mId, Constants.Api.OPTION_CHECK, 0, comment);
            mCall.enqueue(mResponseCallback);
        } else if (mIsPreorder) {
            mCall = Application.apiInterface().requestCheckForAwaiting(mId, mIsInprogress ? 0 : 1, comment);
            mCall.enqueue(mResponseCallback);
        } else {
            mActivity.toggleLoadingProgress(false);
            Intent intent = new Intent(ACTION_CANCEL_CHECK_PRODUCT);
            mActivity.sendBroadcast(intent);
            mActivity.onBackPressed();
        }
    }

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.toggleLoadingProgress(false);
            if (mIsInprogress && mIsPreorder) {
                Intent intent = new Intent(ACTION_CANCEL_CHECK_PRODUCT);
                mActivity.sendBroadcast(intent);
                mActivity.onBackPressed();
                return;
            }
            Intent intent = new Intent(ACTION_CHECK_IN_PROCESSING);
            mActivity.sendBroadcast(intent);
            mActivity.onBackPressed();
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
    }
}
