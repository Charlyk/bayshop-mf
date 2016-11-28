package com.softranger.bayshopmfr.ui.settings;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassFragment extends ParentFragment {

    @BindView(R.id.changePassCurrentInput)
    TextInputEditText mCurrentPassInput;
    @BindView(R.id.changePassNewInput)
    TextInputEditText mNewPassInput;
    @BindView(R.id.changePassCurrentInputLayout)
    TextInputLayout mCurrentLayout;
    @BindView(R.id.changePassNewInputLayout)
    TextInputLayout mNewLayout;
    @BindView(R.id.changePassFocusIndicator)
    View mFocusIndicator;
    @BindView(R.id.changePassProgressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.changePassSaveButton)
    Button mButton;

    private Unbinder mUnbinder;
    private SettingsActivity mActivity;
    private Call<ServerResponse> mCall;

    public ChangePassFragment() {
        // Required empty public constructor
    }

    public static ChangePassFragment newInstance() {

        Bundle args = new Bundle();

        ChangePassFragment fragment = new ChangePassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_pass, container, false);
        mActivity = (SettingsActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);

        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);
        return view;
    }

    @OnFocusChange({R.id.changePassCurrentInput, R.id.changePassNewInput})
    void onInputFocusChanged(View view, boolean hasFocus) {
        if (!hasFocus) return;
        if (mFocusIndicator.getVisibility() != View.VISIBLE)
            mFocusIndicator.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.changePassCurrentInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mCurrentLayout.getY()).setDuration(300).start();
                break;
            case R.id.changePassNewInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mNewLayout.getY()).setDuration(300).start();
                break;
        }
    }

    private void toggleLoading(boolean show) {
        if (show) {
            mButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.changePassSaveButton)
    void saveNewPassword() {
        String currentPass = String.valueOf(mCurrentPassInput.getText());
        String newPass = String.valueOf(mNewPassInput.getText());

        if (currentPass.length() < 6) {
            mCurrentPassInput.setError(getString(R.string.enter_valid_password));
            return;
        }

        if (newPass.length() < 6) {
            mNewPassInput.setError(getString(R.string.must_contain));
            return;
        }

        mCall = Application.apiInterface().changeUserPassword(currentPass, newPass);
        mCall.enqueue(mResponseCallback);
        toggleLoading(true);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                    saveNewPassword();
                    break;
            }
        }
    };

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.showResultActivity(getString(R.string.password_changed), R.mipmap.ic_forgot_your_password_250dp,
                    getString(R.string.password_has_bee_changed));
            mNewPassInput.setText("");
            mCurrentPassInput.setText("");
            toggleLoading(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            toggleLoading(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            toggleLoading(false);
        }
    };

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        mActivity.onBackPressed();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.change_password);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.change_password;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mActivity.hideKeyboard();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
